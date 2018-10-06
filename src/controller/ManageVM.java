package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Project;

@SuppressWarnings("serial")
public class ManageVM extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
		String pathSeparator = File.separator;
		BufferedReader readUser = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("whoami").getInputStream()));
		String user = readUser.readLine();
		String currentPath = "/home/" + user + "/InstanText/" + project.getName() + pathSeparator;	

		
		String VM_address = getVirtualMachineAddress();
		System.out.println("VM Address: " + VM_address);
		
		// Invio del file jar tramite ssh
		String sendJar = "sshpass -p \"1996\" scp " + currentPath + "myProject.jar InstanText@" + VM_address + ":../../";
		System.out.println("\n- - Sending Jar File . . . \n");
		Process process = Runtime.getRuntime().exec(new String[] {"bash", "-c", sendJar});
		printCommandOutput(process);
		
		resp.getWriter().println(VM_address);
	}

	public String getVirtualMachineAddress() throws IOException {
		String getVMAddress = "VBoxManage guestproperty get Win10 \"/VirtualBox/GuestInfo/Net/0/V4/IP\"";
		Process proc = Runtime.getRuntime().exec(new String[] {"bash", "-c", getVMAddress});
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String s = stdInput.readLine();
		Pattern p = Pattern.compile(".*\\s(.*)");
		Matcher m = p.matcher(s);
		m.find();
		return m.group(1);
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
}
