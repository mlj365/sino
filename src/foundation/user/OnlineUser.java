package foundation.user;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import foundation.file.FileLock;
import foundation.file.FileLockManager;
import foundation.file.FileProgressor;
import foundation.persist.loader.ObjectLoader;
import foundation.persist.sql.ILoadable;
import foundation.util.Util;

public class OnlineUser implements ILoadable {

	protected int id;
	protected String name;
	protected String englishname;
	protected String password;
	protected String caption;
	protected String rolecode;
	protected String phone;
	protected String type;
	protected String team;
	protected String emp_id;
	protected String emp_name;
	protected String emp_enName;
	protected String emp_title;
	protected String emp_enTitle;
	protected String emp_department;
	protected String emp_enDepartment;
	protected String emp_position;
	protected int activeYear;
	protected int activeMonth;
	protected String orgCode;
	protected String orgName;
	protected String orgEnName;
	protected String orgid;
	protected String defaultRowfilter;
	protected boolean onlyOne;
	protected Map<String, String> rowfilterMap;
	protected FileLockManager fileLockManager;
	
	
	public OnlineUser() {
		rowfilterMap = new HashMap<String, String>();
		fileLockManager = new FileLockManager();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public synchronized String getRowfilter(String data, String template) {
		if (!Util.isEmptyStr(defaultRowfilter)) {
			return defaultRowfilter;
		}
		
		String rowfilter = rowfilterMap.get(data);
		
		if (rowfilter != null) {
			return rowfilter;
		}
		
		if (template == null) {
			return null;
		}
		
		rowfilter = template.replace("@{userid}", String.valueOf(id));
		rowfilter = rowfilter.replace("@{orgid}", orgid);
		
		rowfilterMap.put(data, rowfilter);
		
		return rowfilter;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public boolean isEmpty() {
		return Util.isEmptyStr(id);
	}
	
	public String getId() {
		return String.valueOf(id);
	}

	public String getOrgName() {
		return orgName;
	}

	public String getEnglishname() {
		return englishname;
	}

	public void setEnglishname(String englishname) {
		this.englishname = englishname;
	}

	@Override
	public void load(ResultSet rslt, Object... args) throws Exception {
		onlyOne = true;
		
		ObjectLoader loader = new ObjectLoader("usr", this.getClass());
		loader.setObject(this);
		loader.load(rslt, args);
		
		if (rslt.next()) {
			onlyOne = false;
		}
	}

	public String getOrgId() {
		return orgid;
	}
	
	public String getOrgCode() {
		return orgCode;
	}
	
	public String getRoleCode() {
		return rolecode;
	}

	public boolean isOnlyOne() {
		return onlyOne;
	}
	
	public String getRolecode() {
		return rolecode;
	}
	
	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	public void setRolecode(String rolecode) {
		this.rolecode = rolecode;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	
	public String getOrgEnName() {
		return orgEnName;
	}

	public void setOrgEnName(String orgEnName) {
		this.orgEnName = orgEnName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}

	public String getEmp_enName() {
		return emp_enName;
	}

	public void setEmp_enName(String emp_enName) {
		this.emp_enName = emp_enName;
	}

	public String getEmp_title() {
		return emp_title;
	}

	public void setEmp_title(String emp_title) {
		this.emp_title = emp_title;
	}

	public String getEmp_enTitle() {
		return emp_enTitle;
	}

	public void setEmp_enTitle(String emp_enTitle) {
		this.emp_enTitle = emp_enTitle;
	}

	public String getEmp_department() {
		return emp_department;
	}

	public void setEmp_department(String emp_department) {
		this.emp_department = emp_department;
	}

	public String getEmp_enDepartment() {
		return emp_enDepartment;
	}

	public void setEmp_enDepartment(String emp_enDepartment) {
		this.emp_enDepartment = emp_enDepartment;
	}

	public String getEmp_position() {
		return emp_position;
	}

	public void setEmp_position(String emp_position) {
		this.emp_position = emp_position;
	}

	public int getActiveYear() {
		return activeYear;
	}

	public int getActiveMonth() {
		return activeMonth;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FileProgressor getFileProgress(String code) {
		return fileLockManager.getFileProgress(code);
	}

	public FileLock getFileLock(String name) {
		return fileLockManager.getLock(name);
	}
}
