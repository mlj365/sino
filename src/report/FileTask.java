package report;

public class FileTask {

	private String id;
	private String status;
	private String filename;
	
	public FileTask(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileName() {
		return filename;
	}

	public void setFileName(String filename) {
		this.filename = filename;
	}

	public String getId() {
		return id;
	}
}
