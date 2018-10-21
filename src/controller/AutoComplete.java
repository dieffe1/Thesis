package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import model.File;
import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class AutoComplete extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("\n+++++++++++++ AUTOCOMPLETE INVOKED\n");
		HttpSession session = req.getSession();
		String object = req.getParameter("obj");	
		
		System.out.println("variable: " + object);
		File file = (File) session.getAttribute("file");
		String code = file.getCode();
		
		Pattern p = Pattern.compile("(\\w+)\\s+" + object + ".*");
		Matcher m = p.matcher(code);
		if(!m.find()) {
			System.out.println("Class name not found");
			return;
		} 
		
		String className = m.group(1);
		System.out.println("className " + className);
		
		String completeClassName = null;
		p = Pattern.compile(".*import\\s+(.*)" + className + ";.*");
		m = p.matcher(code);
		
		if(!m.find()) {
			//se la classe è all'interno dello stesso package non ci sarà un import, 
			//controllo se il nome della classe trovata corrisponde ad una all'interno del package
			User user = (User) session.getAttribute("user");
			Project project = (Project) session.getAttribute("project");
			FileDao fileDao = DAOFactory.getInstance().getFileDao();
			Collection<model.File> files = fileDao .findByName(user.getUsername(), project.getName(), file.getPackage().getName()).values();
			
			boolean samePackageFile = false;
			for (File f : files) 
				if(f.getName().equals(className)) {
					samePackageFile = true;
					completeClassName = file.getPackage().getName() + "." + className;
					break;
				}
			
			if(!samePackageFile) {
				System.out.println("Complete class name not found");
				return;	
			}
		}
		
		if(completeClassName == null)
			completeClassName = m.group(1) + className;
		System.out.println("completeClassName " + completeClassName);
		
		
		String filepath = req.getServletContext().getRealPath("");
		Project project = (Project) session.getAttribute("project");
		
		String packageName = file.getPackage().getName();
		String testMain = filepath + project.getName() + "/src/" + packageName + "/testMain.java";
		java.io.File newFile = new java.io.File(testMain);
		newFile.createNewFile();

		String testCode = "package " + packageName + ";\n" + 
				"		 import java.lang.reflect.Method;\n" + 
				"		 public class testMain {\n" + 
				"		 	public static void main(String[] args) {\n" + 
				"				try {\n" + 
				"					Class myClass = Class.forName(\"" + completeClassName + "\");\n" + 
				"					Method[] meths = myClass.getMethods();\n" + 
				"					for(Method M : meths) {\n" + 
				"						System.out.print(M.getName() + \"(\");\n" + 
				"						Class[] par = M.getParameterTypes();\n" + 
				"						for(int i=0; i<par.length; i++) {\n" + 
				"							System.out.print(par[i].getSimpleName());\n" +
				"							if(i < par.length-1)" +
				"								System.out.print(\",\");" +
				"						}\n" +
				"						System.out.println(\") return \" + M.getReturnType().getSimpleName());\n" +
				"					}\n" + 
				"				} catch (ClassNotFoundException e) {\n" + 
				"					e.printStackTrace();\n" + 
				"				}\n" + 
				"			}\n" + 
				"		}";
		
		PrintWriter out = new PrintWriter(newFile);
		out.println(testCode);
		out.flush();
		out.close();
		
		String srcPath = filepath + project.getName() + "/src/";
		String binPath = filepath + project.getName() + "/bin/";
		
		// Utilizzo findString per trovare la classe che contiene il main originale e la elimino
		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		model.File mainClass = fileDao.findString(project.getId(), "public static void main(String[] args)").get(0);
		String deleteMainSrc = "rm " + srcPath + mainClass.getPackage().getName() + "/" + mainClass.getName() + ".java";
		String deleteMainBin = "rm " + binPath + mainClass.getPackage().getName() + "/" + mainClass.getName() + ".class";
		
		printCommandOutput(Runtime.getRuntime().exec(deleteMainSrc));
		printCommandOutput(Runtime.getRuntime().exec(deleteMainBin));
		
		String compile = "javac -sourcepath " + srcPath + " -d " + binPath + " " + 
				srcPath + "/" + packageName + "/testMain.java";
		
		Process proc = Runtime.getRuntime().exec(compile);
		printCommandOutput(proc);

		// Creo la cartella dove verranno memorizzati i .java e .class nella home dell'utente
		String path = req.getServletContext().getRealPath("") + project.getName() + "/";
		Runtime.getRuntime().exec("mkdir InstanText");
		Runtime.getRuntime().exec("rm -r InstanText/" + project.getName());
		printCommandOutput(Runtime.getRuntime().exec("cp -r " + path + " InstanText"));
		BufferedReader readUser = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("whoami").getInputStream()));
		String user = readUser.readLine();
		String currentPath = "/home/" + user + "/InstanText/" + project.getName() + "/";	

		// Creo il manifest necessario a creare il jar
		String pathManifest = currentPath + "MANIFEST.MF";
		String createManifest = "echo -e 'Class-Path: .\nMain-Class: " + 
					packageName + "."+ "testMain' >" + pathManifest;
		
		Process process = Runtime.getRuntime().exec(new String[] {"bash", "-c", createManifest});
		printCommandOutput(process);
	
		// Creo ed eseguo uno script bash per creare il file jar (ho la necessità di eseguirlo da bin/)
		String createScriptFile = "echo -e 'cd " + currentPath + "bin/ && jar -cvmf ../MANIFEST.MF ../testReflection.jar .' >" 
											+ currentPath + "createJar.sh && bash " + currentPath + "createJar.sh"; 
		process = Runtime.getRuntime().exec(new String[] {"bash", "-c", createScriptFile});
		printCommandOutput(process);
		
		newFile.delete();
		
		String s = new JSONArray(returnMethods(Runtime.getRuntime().exec("java -jar InstanText/" + project.getName() + "/testReflection.jar"))).toString();
		resp.getWriter().println(s);
	}

	
	public void printCommandOutput(Process proc) throws IOException {
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		String s = null;
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
		}
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}

	}
	public List<String> returnMethods(Process proc) throws IOException {
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String s = null;
		List<String> metodi = new LinkedList<>();
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
			metodi.add(s);
		}
		return metodi;
	}
}
