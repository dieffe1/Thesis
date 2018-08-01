package persistence.dao;

import java.util.List;

import model.Project;
import model.User;
import persistence.UserCredential;

public interface UserDao extends Dao {

	public void save(User user);

	public User findByPrimaryKey(String username);

	public User findByMail(String mail);
	
	public List<String> findAll(Project project);

	public void updateMail(User user);

	public void updateImage(User user);

	public void delete(User user);
	
	public void setPassword(User user, String password);
	
	public UserCredential findByPrimaryKeyCredential(String username);
	
}
