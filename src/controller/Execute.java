package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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

		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
//		Project project = DAOFactory.getInstance().getProjectDao().findByPrimaryKey(5l);
		FileDao fileDao = DAOFactory.getInstance().getFileDao();

		Runtime runtime = Runtime.getRuntime();
		String pathSeparator = File.separator;
		String path = req.getServletContext().getRealPath("") + project.getName() + pathSeparator;

//		 CREARE IL MANIFEST CON LA CLASSE MAIN
		
		 String manifestPath = path + "manifest.MF";
		 File file = new File(manifestPath);
		 file.createNewFile();
		
		 model.File f = fileDao.findString(project.getId(), "public static void main(String[] args)").get(0);
		 String main = path + "bin" + pathSeparator + f.getPackage().getName() + pathSeparator + f.getName();
		 PrintWriter out = new PrintWriter(file);
		 out.println("Main-Class: " + main);
		 out.flush();
		 out.close();
		 
		// CREARE UN JAR
		
		 String jarPath = path + project.getName() +".jar";

		 String createJar = "jar cfm " + jarPath + " " + manifestPath + " " + main + ".class";
		
		 Process process = runtime.exec(createJar);
		
		// ESEGUIRE UN JAR
		
		 String runJar = "java -jar " + jarPath;
		 
		 System.out.println(runJar);
		 process = runtime.exec(runJar);
		
		 BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		 BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		
		 // read the output from the command
		
		 System.out.println("Here is the standard output of the command:\n");
		 String ss = "";
		
		 while ((ss = stdInput.readLine()) != null) {
		 System.out.println("stdInput: " + ss);
		 }
		
		 while ((ss = stdError.readLine()) != null) {
		 System.out.println("stdError: " + ss);
		 }
	}
}
