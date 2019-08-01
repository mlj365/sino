package foundation.file.download;

import foundation.file.FileType;

public enum ClientAction {

	Open, SaveAs, AsPDF, ASTexT, AsExcel, AsWord, AsPowerPoint, Unknown;

	public static ClientAction valueOfString(String code) {
		if ("Open".equalsIgnoreCase(code)) {
			return Open;
		}
		else if ("SaveAs".equalsIgnoreCase(code)) {
			return SaveAs;
		}
		else if ("AsPDF".equalsIgnoreCase(code)) {
			return AsPDF;
		}
		else if ("ASTexT".equalsIgnoreCase(code)) {
			return ASTexT;
		}
		else if ("AsExcel".equalsIgnoreCase(code)) {
			return AsExcel;
		}
		else if ("AsWord".equalsIgnoreCase(code)) {
			return AsWord;
		}
		else if ("AsPowerPoint".equalsIgnoreCase(code)) {
			return AsPowerPoint;
		}
		
		return Unknown;
	}

	public static ClientAction valueOfFileType(FileType fileType) {
		if (FileType.Excel2007 == fileType) {
			return AsExcel;
		}
		else if (FileType.Excel2003 == fileType) {
			return AsExcel;
		}
		else if (FileType.CSV == fileType) {
			return AsExcel;
		}
		
		return SaveAs;
	}
	
}
