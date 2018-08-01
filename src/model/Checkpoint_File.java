package model;

import java.sql.Timestamp;
import java.util.Calendar;

public class Checkpoint_File {

	private String text;
	private Checkpoint checkpoint;
	private Long id;
	private File file;
	private Package pack;
	private User creator;
	private Timestamp date;
	private String description;
	
	public Checkpoint_File(String text, File file, User creator,String destription) {
		this.text = text;
		this.checkpoint = null;
		this.file = file;
		this.creator = creator;
		this.date = new Timestamp(Calendar.getInstance().getTime().getTime());
		this.pack = file.getPackage();
		this.description = destription;
		
		if(description == "")
			this.description = date.toString();
		
	}

	public Checkpoint_File(String text, Checkpoint checkpoint, File file, User creator, String description) {
		this(text,file,creator,description);
		this.checkpoint = checkpoint;
	}

	public Checkpoint_File() {
	}

	public Checkpoint getCheckpoint() {
		return checkpoint;
	}

	public void setCheckpoint(Checkpoint checkpoint) {
		this.checkpoint = checkpoint;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Package getPackage() {
		return pack;
	}

	public void setPackage(Package pack) {
		this.pack = pack;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
