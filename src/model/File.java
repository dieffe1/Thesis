package model;

public class File 
{
	private Long id;
	private String name;
	private Package packageID;
	private String code;
	private User user;
	
	public File(String name, Package packageName) {
		this.name = name;
		this.packageID = packageName;
		code = "";
		user = null;
	}

	public File(String name, Package packageName,String code) {
		this(name, packageName);
		this.code = code;
	}

	public File() { }

	public void modify(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public Package getPackage() {
		return packageID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPackage(Package packageName) {
		this.packageID = packageName;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString()
	{
		return "id: " + id + ",    name: " + name;
	}
	
}
