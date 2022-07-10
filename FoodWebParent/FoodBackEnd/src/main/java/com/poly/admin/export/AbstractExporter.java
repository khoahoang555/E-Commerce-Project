package com.poly.admin.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;


public class AbstractExporter {
	public void setResponseHeader(HttpServletResponse resp, String contentType, String extension, String name) throws IOException {
		DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = dateFormater.format(new Date());
		String fileName = name + "_" + timestamp + extension;
		
		resp.setContentType(contentType);
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment;filename=" + fileName;
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader(headerKey, headerValue);
	}
}
