package foundation.phone;

public class Template {
	
	private String beginSegment;
	private String endSegment;
	
	public Template(String content) {
		if (content == null) {
			return;
		}
		
		int pos = content.indexOf("@{value}");
		
		if (pos < 0) {
			beginSegment = content;
			return;
		}
		
		beginSegment = content.substring(0, pos);
		endSegment = content.substring(pos + "@{value}".length());
	}
	
	public String toContent(String value) {
		if (beginSegment == null) {
			if (endSegment == null) {
				return value;
			}
			
			return value + endSegment;
		}
		else {
			if (endSegment == null) {
				return beginSegment + value;
			}
			
			return beginSegment + value + endSegment;
		}
	}
}
