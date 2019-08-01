package foundation.file.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

public class HttpResponseWriter {

	private HttpServletResponse response;
	
	public HttpResponseWriter(HttpServletResponse response) {
		this.response = response;
	}
	
	public void write(File file, ClientAction action) throws IOException {
		writeHeader(file, action);
		
		OutputStream outputStream = response.getOutputStream();
		
		try {
			FileInputStream inputStream = new FileInputStream(file);
			try {
				byte[] buffer = new byte[1024]; int numberRead = 0;
				
				while ((numberRead = inputStream.read(buffer)) != -1) {  
					outputStream.write(buffer, 0, numberRead);       
			    }  
			}
			finally {
				inputStream.close();
			}
		}
		finally {
			outputStream.flush();
			outputStream.close();
		}
	}

	private void writeHeader(File file, ClientAction action) throws UnsupportedEncodingException {
		String prefix = "";
		
		if (ClientAction.SaveAs == action) {
			response.setHeader("Content-Type", "application/octet-stream");
			prefix = "attachment;";
		}
		else if (ClientAction.AsExcel == action) {
			response.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;");
			//response.setHeader("Content-Type", "application/vnd.ms-excel;application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;");
		}
		else if (ClientAction.AsWord == action) {
			response.setHeader("Content-Type", "application/msword");
		}		
		else if (ClientAction.AsPowerPoint == action) {
			response.setHeader("Content-Type", "application/x-ppt");
		}
		else if (ClientAction.AsPDF == action) {
			response.setHeader("Content-Type", "application/pdf");
		}		
		else if (ClientAction.ASTexT == action) {
			response.setHeader("Content-Type", "text/plain");
		}		
		
		String filename = prefix + "filename=" + URLEncoder.encode(file.getName(), "UTF-8");
		response.setHeader("Content-Disposition", filename);
	}

	public static void main(String[] args) {
		File file = new File("D:/test/test.txt");
		System.out.println(file.getName());
	}

}
