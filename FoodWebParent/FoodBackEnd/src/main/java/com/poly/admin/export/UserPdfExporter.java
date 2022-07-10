package com.poly.admin.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.poly.common.entity.User;

public class UserPdfExporter extends AbstractExporter {
	public void export(List<User> listUsers, HttpServletResponse resp) throws IOException{
		super.setResponseHeader(resp, "application/pdf", ".pdf", "users");
		
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, resp.getOutputStream());
		
		document.open();
		
		BaseFont courier = BaseFont.createFont("./font/arial.ttf", BaseFont.IDENTITY_H, true);
		
		Font font = new Font(courier, 18, Font.HELVETICA);
		font.setStyle("bold");
		font.setColor(Color.BLUE);
		
		Paragraph paragraph = new Paragraph("Danh Sách Người Dùng", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		
		document.add(paragraph);
		
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(10);
		table.setWidths(new float[] {1.2f, 4.5f, 2.3f, 2.7f, 2.5f, 2.2f});
		
		writeTableHeader(table);
		writeTableData(table, listUsers);
		
		document.add(table);
		
		document.close();
	}
	
	private void writeTableData(PdfPTable table, List<User> listUsers) throws DocumentException, IOException {
		for (User user: listUsers) {
			BaseFont courier = BaseFont.createFont("./font/arial.ttf", BaseFont.IDENTITY_H, true);
			
			Font font = new Font(courier, 12, Font.NORMAL);
			
			Phrase phraseID = new Phrase(String.valueOf(user.getId()), font);
			table.addCell(phraseID);
			
			Phrase phraseEmail = new Phrase(user.getEmail(), font);
			table.addCell(phraseEmail);
			
			Phrase phraseFirstName = new Phrase(user.getFirstName(), font);
			table.addCell(phraseFirstName);
			
			Phrase phraseLastName = new Phrase(user.getLastName(), font);
			table.addCell(phraseLastName);
			
			table.addCell(user.getRoles().toString());
			table.addCell(String.valueOf(user.getEnabled()?"Có":"Không"));
		}
	}
	
	private void writeTableHeader(PdfPTable table) throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.blue);
		cell.setPadding(5);
		
		BaseFont courier = BaseFont.createFont("./font/arial.ttf", BaseFont.IDENTITY_H, true);		
		Font font = new Font(courier, 12, Font.HELVETICA);
		
		font.setColor(Color.WHITE);
		
		cell.setPhrase(new Phrase("ID", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("E-mail", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Họ", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Tên", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Quyền", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Trạng Thái", font));
		table.addCell(cell);
	}
}
