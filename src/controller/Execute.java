package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Project;
import persistence.DAOFactory;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class Execute extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("\n\n############## EXEC");
		
		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
		String pathSeparator = File.separator;
		String path = req.getServletContext().getRealPath("") + project.getName() + pathSeparator;

		// Creo la cartella dove verranno memorizzati i .java e .class nella home dell'utente
		Runtime.getRuntime().exec("mkdir InstanText");
		Runtime.getRuntime().exec("mv " + path + " InstanText");
		BufferedReader readUser = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("whoami").getInputStream()));
		String user = readUser.readLine();
		String currentPath = "/home/" + user + "/InstanText/" + project.getName() + pathSeparator;	

		//Verifica dei file .class
		if(!existCompiledFiles(project.getName(), user)) {
			resp.getWriter().println("compile");
			return;
		}
		
		// Utilizzo findString per trovare la classe che contiene il main
		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		model.File mainClass = fileDao.findString(project.getId(), "public static void main(String[] args)").get(0);

		// Creo il manifest necessario a creare il jar
		String pathManifest = currentPath + "MANIFEST.MF";
		String createManifest = "echo -e 'Class-Path: .\nMain-Class: " + 
					mainClass.getPackage().getName() + "." + mainClass.getName() + "' >" + pathManifest;
		
		System.out.println("\n- - Creating MANIFEST.MF\n");
		Process process = Runtime.getRuntime().exec(new String[] {"bash", "-c", createManifest});
		printCommandOutput(process);
	
		// Creo ed eseguo uno script bash per creare il file jar (ho la necessità di eseguirlo da bin/)
		String createScriptFile = "echo -e 'cd " + currentPath + "bin/ && jar -cvmf ../MANIFEST.MF ../myProject.jar .' >" 
											+ currentPath + "createJar.sh && bash " + currentPath + "createJar.sh"; 
		System.out.println("\n- - Creating JAR file\n");
		process = Runtime.getRuntime().exec(new String[] {"bash", "-c", createScriptFile});
		printCommandOutput(process);
		
		// Avvio la VM
		String startVM = "VBoxManage startvm Win10 --type headless";
		System.out.println("\n- - Starting Virtual Machine . . . \n");
		process = Runtime.getRuntime().exec(startVM);
		printCommandOutput(process);
	}
	
	public void printCommandOutput(Process proc) {
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		String s = null;
		try {
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public boolean existCompiledFiles(String project, String user) throws IOException {
		Process proc = Runtime.getRuntime().exec("ls /home/" + user + "/InstanText");
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		
		String s = null;
		while ((s = stdInput.readLine()) != null) 
			if(s.equals(project))
				return true;
		return false;
	}
	
	public void waitVMStarting(Thread t) {
		System.out.print("0%...");
		int time = 0;
		while(time < 10) {
			try {
				time++;
				System.out.print(time*10 + "%...");
				t.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println();
	}
	
	
}
