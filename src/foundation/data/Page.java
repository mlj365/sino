package foundation.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import foundation.variant.IVariantRequestListener;

public class Page implements IVariantRequestListener {  
	
	private static Set<String> parameterNames;
    private int pageSize;  
    private int recordCount;  
    private int pageNo;
    private int total;
  
    static {
    	parameterNames = new HashSet<String>();
    	parameterNames.add("pagesize");
    	parameterNames.add("recordcount");
    	parameterNames.add("pageno");
    	parameterNames.add("beginno"); 
    	parameterNames.add("endno"); 
    	
    	parameterNames.add("page"); 
    	parameterNames.add("rows");
    	
    	parameterNames.add("pagefilter");
    }
    
    public Page(int recordCount) { //默认一页20条，第一页
    	this.pageSize = 20;
    	this.pageNo = 1;
        this.recordCount = recordCount;
        this.total = recordCount;
    }  
    
    public int getBeginRecordNo() { 
    	int recordNo = pageSize * (pageNo - 1) + 1; 
        return recordNo;  
    }  
    
    public int getBeginRecordNo_1() { 
    	int recordNo = getBeginRecordNo();
        return recordNo - 1;  
    } 
    
    public int getEndRecordNo() {  
    	int recordNo = pageSize * pageNo;
        return Math.min(recordNo, recordCount);  
    }  
    
    public int getPageSize() {  
        return pageSize;  
    }  
  
    public int getPageNo() {  
        return pageNo;
    }  
  
    public int getRecordCount() {  
        return recordCount;  
    }  
  
    public int getPageCount() {  
        return (int)Math.ceil(recordCount * 1.0d / pageSize);
    }  
  
    public void setRecordCount(int count) {
    	this.recordCount = count;
    }
  
    public void setPageSize(int value) { 
    	if (value <= 0) {
    		return;
    	}
    	
       	pageSize = value;  
    }  
    
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public void set(String name, String value) {
		name = name.toLowerCase();
		
		if ("pageno".equalsIgnoreCase(name) || "page".equalsIgnoreCase(name)) {
			setPageNo(Integer.parseInt(value));
		}
		else if ("pagesize".equals(name) || "rows".equalsIgnoreCase(name)) {
			setPageSize(Integer.parseInt(value));			
		}
	}
	
	public static boolean containsVarinat(String name) {
		return parameterNames.contains(name);
	}

 	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("size=").append(pageSize).append(",");
		result.append("recordCount=").append(recordCount).append(",");
		result.append("pageNo=").append(pageNo);
		
		return result.toString();
	}

	@Override
	public String getStringValue(String name) {
		if ("pageSize".equalsIgnoreCase(name)) {
			return String.valueOf(pageSize);
		}
		else if ("recordCount".equalsIgnoreCase(name)) {
			return String.valueOf(recordCount);
		}
		else if ("pageNo".equalsIgnoreCase(name)) {
			return String.valueOf(pageNo);
		}
		else if ("beginNo".equalsIgnoreCase(name)) {
			int beginNo = getBeginRecordNo_1();
			return String.valueOf(beginNo);
		}
		else if ("endNo".equalsIgnoreCase(name)) {
			int endNo = getEndRecordNo();
			return String.valueOf(endNo);
		}
		else if ("pageFilter".equalsIgnoreCase(name)) {
			int beginNo = getBeginRecordNo_1();
			int endNo = getEndRecordNo();
			
			return "rownum > " + beginNo + " and rownum <= " + endNo;
		}		
		
		
		return null;
	}

	@Override
	public List<String> getVariantNames() {
		return new ArrayList<String>(parameterNames);
	}

	public static Set<String> getVarinatNameSet() {
		return parameterNames;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}  