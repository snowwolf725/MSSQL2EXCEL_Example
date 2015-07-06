package com.th.hightq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ReportGenerator {

	private List<PrintableDataItem> m_item = new ArrayList<PrintableDataItem>();
	
	private String m_fileName = "";
	
	private String m_templateName = "";
	
	public void setPrintInfo(String _templateName, String _fileName, List<PrintableDataItem> _item) {
		m_templateName = _templateName;
		m_fileName = _fileName;
		m_item = _item;
	}
	
	public void genReport() {
		String filename = m_fileName;
		try {
			FileInputStream input_document = new FileInputStream("Templates/" + m_templateName + ".xls");
			HSSFWorkbook workbook = new HSSFWorkbook(input_document);
			HSSFSheet sheet = workbook.getSheet("Sheet1");
			Row row = null;
			Cell cell = null;
			
			for(PrintableDataItem item : m_item) {
				row = sheet.getRow(item.getRow());
				if(row == null) {
					row = sheet.createRow(item.getRow());
				}
				cell = row.getCell(item.getCol(), Row.RETURN_NULL_AND_BLANK);
				if(cell == null) {
					cell = row.createCell(item.getCol());
				}
				cell.setCellValue(item.getValue());
			}
			input_document.close();
			
			FileOutputStream output_file =new FileOutputStream(new File(filename + ".xls"));
			workbook.write(output_file);
			output_file.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getExcelReportPath() {
		String filename = m_fileName;
		return filename + ".xlsx";
	}
}
