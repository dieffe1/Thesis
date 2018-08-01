package persistence.dao;

import java.sql.Timestamp;
import java.util.List;


import model.Message;

public interface MessageDao extends Dao {

	public void save(Message message);

	public Message findByPrimaryKey(Long id);

	public List<Message> findAll();
	
	public List<Message> find(Long projectID);

	public List<Message> findLastMessage(Long projectID);
	
	public List<Message> findForTime(Timestamp date, Long projectId);
	
	public Message findOlder(Long projectID, int firstMessage);
	
	public void update(Message message);

	public void delete(Message message);


}
