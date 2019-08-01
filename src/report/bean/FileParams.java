package report.bean;

import java.util.ArrayList;

public class FileParams {
	private String code;
	private String fileExtension;
	private String fileName;
	private ArrayList<Sql> sqls;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	public ArrayList<Sql> getSqls() {
		return sqls;
	}
	public void setSqls(ArrayList<Sql> sqls) {
		this.sqls = sqls;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
    public class Sql
    {
        private String name;
        public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		private String body;
    }

}
