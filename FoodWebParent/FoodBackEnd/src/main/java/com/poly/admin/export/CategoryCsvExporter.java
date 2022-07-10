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

import com.poly.common.entity.Category;

public class CategoryCsvExporter extends AbstractExporter {
	public void export(List<Category> listCategories, HttpServletResponse resp) throws IOException {
		super.setResponseHeader(resp, "text/csv", ".csv", "categories");
		
		Writer writer = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8);
		writer.write("\uFEFF");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeaders = {"Category ID", "Tên", "Alias", "Trạng thái"};
		String[] fieldMapping = {"id", "name", "alias", "enabled"};
		csvWriter.writeHeader(csvHeaders);
		
		for (Category category: listCategories) {
			csvWriter.write(category, fieldMapping);
		}
		
		csvWriter.close();
	}
}
