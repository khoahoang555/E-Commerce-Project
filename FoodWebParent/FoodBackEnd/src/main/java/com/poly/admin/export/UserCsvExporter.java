package com.poly.admin.export;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.poly.common.entity.User;

public class UserCsvExporter extends AbstractExporter{
	public void export(List<User> listUsers, HttpServletResponse resp) throws IOException {
		super.setResponseHeader(resp, "text/csv; charset=UTF-8", ".csv");
		
		// Write file csv with format utf-8 bom
		Writer writer = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8);
		writer.write('\uFEFF');
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"User ID", "E-mail", "Họ", "Tên", "Quyền", "Trạng Thái"};
		String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
		csvWriter.writeHeader(csvHeader);
		
		for (User user: listUsers) {
			csvWriter.write(user, fieldMapping);
		}
		
		csvWriter.close();
	}
}
