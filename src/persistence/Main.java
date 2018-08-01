package persistence;

import persistence.JDBC.UtilDao;

public class Main {

	public static void main(String args[])	{
		DAOFactory factory = DAOFactory.getInstance();
		
		UtilDao util = factory.getUtilDao();

		util.dropDatabase();
		util.createDatabase();
		
//		UserDao userDao = factory.getUserDao();
//		ProjectDao projectDao = factory.getProjectDao();
//		PackageDao packageDao = factory.getPackageDao();
//		FileDao fileDao = factory.getFileDao();
//		MessageDao messageDao = factory.getMessageDao();
//		CollaboratorDao collaboratorDao = factory.getCollaboratorDao();
//		
//		User user1 = new User("username", "alessandromarrazzo9@gmail.com");
//		User user2 = new User("css","css@gmail.com");
//		User user3 = new User("nicuola","paletta92@gmail.com");
//
//		userDao.save(user1);
//		userDao.save(user2);
//		userDao.save(user3);
//
//		Project project1 = new Project("rep1", user1);
//		Project project2 = new Project("rep2", user2);
//		Project project3 = new Project("rep3", user3);
//		Project project4 = new Project("rep4", user2);
//		
//		Package package1 = new Package("package1", project1);
//		Package package2 = new Package("package2", project1);
//		Package package3 = new Package("package3", project2);
//		
//		File file1 = new File("file1",package1,"ciao");
//		File file2 = new File("file2",package1);
//		File file3 = new File("file3",package2,"ciao");
//		File file4 = new File("file4",package1,"public class Main {\r\n" + 
//				"\r\n" + 
//				"	public static void main(String args[]) {\r\n\n" + 
//				"		DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.POSTGRESQL);\r\n" + 
//				"		\r\n" + 
//				"		UserDao userDao = factory.getUserDAO();\r\n" + 
//				"		RepositoryDao repositoryDao = factory.getRepositoryDao();\r\n" + 
//				"		PackageDao packageDao = factory.getPackageDao();\r\n" + 
//				"		FileDao fileDao = factory.getFileDao();\r\n" + 
//				"		MessageDao messageDao = factory.getMessaggeDao();\r\n" + 
//				"		ModifyDao modifyDao = factory.getModifyDao();\r\n" + 
//				"		CollaboratorDao collaboratorDao = factory.getCollaboratorDao();\r\n" + 
//				"		CheckpointDao checkpointDao = factory.getCheckpointDao();\r\n" + 
//				"		Checkpoint_FileDao checkpoint_FileDao = factory.getCheckpointFileDao();}");
//		
//		package1.addFile(file1);
//		package1.addFile(file2);
//		package1.addFile(file4);
//		package2.addFile(file3);	//System.out.println("pack " + package1);
//		
//		project1.addPackage(package1);
//		project1.addPackage(package2);
//		project2.addPackage(package3);	//System.out.println("packs " + project1.getPackages());
//		
//		projectDao.save(project1);
//		projectDao.save(project2);
//		projectDao.save(project3);
//		projectDao.save(project4);
//		
//		packageDao.save(package1);
//		packageDao.save(package2);
//		packageDao.save(package3);
//		
//		fileDao.save(file1);
//		fileDao.save(file2);
//		fileDao.save(file3);
//		fileDao.save(file4);
//		
//		projectDao.update(project1);
//		
//		Message message1 = new Message(project1,"ciaoaoao",user1);
//		messageDao.save(message1);
//		
//		Message message2 = new Message(project2,"chelovuoi",user3);
//		messageDao.save(message2);
//		
//		System.out.println(messageDao.findByPrimaryKey(2l));
//		
//		Collaborator coll1 = new Collaborator(user2, project1);
//		Collaborator coll2 = new Collaborator(user1, project2);
//		Collaborator coll3 = new Collaborator(user1, project3);
//		Collaborator coll4 = new Collaborator(user1, project4);
//		
//		collaboratorDao.save(coll1);
//		collaboratorDao.updateStatus(user2.getUsername(), project1.getId());
//		collaboratorDao.save(coll2);
//		collaboratorDao.updateStatus(user1.getUsername(), project2.getId());
//		collaboratorDao.save(coll3);
//		collaboratorDao.save(coll4);
//		
//		System.out.println("collaborators: " + collaboratorDao.find("username"));
//		
//		HashMap<Long, File> files = fileDao.findByProject(1l);
//		for (Long l : files.keySet()) {
//			System.out.println(l);
//		}
	}
}
