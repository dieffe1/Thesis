package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import model.File;
import model.Package;
import model.Project;
import persistence.DAOFactory;
import persistence.dao.FileDao;
import persistence.dao.PackageDao;

@SuppressWarnings("serial")
public class Compile extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		System.out.println("########### Compiler");
		HttpSession session = req.getSession();	 
		Project project = (Project) session.getAttribute("project");
		
//		Enumeration<String> a = session.getAttributeNames();
//		while(a.hasMoreElements()) {
//			System.out.println(a.nextElement());
//		}
		
		Runtime runtime = Runtime.getRuntime();
		String pathSeparator = java.io.File.separator;
		
		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		FileDao fileDao = DAOFactory.getInstance().getFileDao();

		HashMap<Long, Package> packages = packageDao.find(project.getId());

		String filepath = req.getServletContext().getRealPath("") + pathSeparator;
		
		String srcProject = filepath + project.getName();
		java.io.File directory = new java.io.File(srcProject);
		directory.mkdir();

		List<String> filepaths = new ArrayList<>();
		java.io.File src = new java.io.File(srcProject, "src");
		src.mkdir();

		System.out.println("src path: " + src.getAbsolutePath());

		java.io.File bin = new java.io.File(srcProject, "bin");
		bin.mkdir();

		System.out.println("bin path: " + bin.getAbsolutePath());

		for (Package pack : packages.values()) {
			String srcPackage = src.getAbsolutePath() + pathSeparator + pack.getName();
			java.io.File dir = new java.io.File(srcPackage);
			dir.mkdir();
			System.out.println("pack " + dir.getAbsolutePath());

			HashMap<Long, File> files = fileDao.find(pack.getId());
			for (File file : files.values()) {
				String srcFile = srcPackage + pathSeparator + file.getName() + ".java";
				java.io.File f = new java.io.File(srcFile);
				f.createNewFile();
				filepaths.add(f.getAbsolutePath());

				PrintWriter out = new PrintWriter(f);
				out.println(file.getCode());
				out.flush();
				out.close();
			}
		}

		String path = src.getAbsolutePath() + pathSeparator;
		
		List<File> list = fileDao.findString(project.getId(), "public static void main(");
		if(list.isEmpty()) {
			resp.getWriter().println("Main class not found!");
			return; 
		}
		File file = list.get(0);
		
		String compile = "javac -sourcepath " + src.getAbsolutePath() + " -d " + bin.getAbsolutePath() + " " + path + file.getPackage().getName() + pathSeparator + file.getName() + ".java";
		Process process = runtime.exec(compile); 

		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));	
		List<String> errors = new LinkedList<>();
		String error = "";

		while ((error = stdError.readLine()) != null) {
			errors.add(error);
		}
		
		if(errors.isEmpty()) {
			resp.getWriter().print("ok");
			return;
		}
		
		for(int i = 0; i<errors.size()-1; i++) { System.out.println("i" + i + errors);
			String[] split = errors.get(i).split(Pattern.quote(pathSeparator));
			if(split.length > 2)
				errors.set(i, split[split.length-2] + pathSeparator + split[split.length-1]);
		}
		
		String files = (new JSONArray(errors).toString());
		resp.getWriter().print(files); 
	}
}
