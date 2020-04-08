package es.ubu.lsi.ubumonitor.export.dashboard;

import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.Controller;

public class FillSheetHomepage extends FillSheetData {

	public FillSheetHomepage() {
		super("Homepage");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		//do nothing
	}
	
	@Override
	public void fillData(XSSFWorkbook workbook, CellStyle cellStyle) {
		XSSFSheet sheet = workbook.getSheet(sheetName);
		setCellValue(sheet, 0, 14, AppInfo.APPLICATION_VERSION);
		setCellValue(sheet, 1, 14, Controller.getInstance().getActualCourse().getFullName());
		setCellValue(sheet, 2, 14, Controller.DATE_TIME_FORMATTER.format(LocalDateTime.now()));
	}

}
