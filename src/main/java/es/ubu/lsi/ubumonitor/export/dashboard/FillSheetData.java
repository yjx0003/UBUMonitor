package es.ubu.lsi.ubumonitor.export.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class FillSheetData {

	protected String sheetName;
	public FillSheetData(String sheetName) {
		this.sheetName = sheetName;
	}
	public void fillData(XSSFWorkbook workbook, CellStyle cellStyle) {
		XSSFSheet sheet = workbook.getSheet(sheetName);
		XSSFTable table = sheet.getTables()
				.get(0);
		fillTable(sheet, cellStyle);
		setArea(table);

		autoSizeCoumns(sheet, table);
	}

	private void autoSizeCoumns(XSSFSheet sheet, XSSFTable table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void setArea(XSSFTable table) {
		table.setArea(new AreaReference(table.getStartCellReference(), new CellReference(Math.max(table.getXSSFSheet()
				.getLastRowNum(), table.getEndRowIndex()), table.getEndColIndex()), SpreadsheetVersion.EXCEL2007));
	}

	protected Cell getCell(Sheet sheet, int rowIndex, int columnIndex, CellType cellType) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);

		}
		Cell cell = row.getCell(columnIndex);
		if (cell == null) {
			cell = row.createCell(columnIndex, cellType);
		}
		return cell;
	}

	protected void setCellValue(Sheet sheet, int rowIndex, int columnIndex, String value) {
		getCell(sheet, rowIndex, columnIndex, CellType.STRING).setCellValue(value);

	}

	protected void setCellValue(Sheet sheet, int rowIndex, int columnIndex, double value) {
		getCell(sheet, rowIndex, columnIndex, CellType.NUMERIC).setCellValue(value);

	}

	protected void setCellValue(Sheet sheet, int rowIndex, int columnIndex, LocalDateTime value, CellStyle cellStyle) {
		Cell cell = getCell(sheet, rowIndex, columnIndex, CellType.BLANK);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);

	}
	
	protected void setCellValue(Sheet sheet, int rowIndex, int columnIndex, LocalDate value, CellStyle cellStyle) {
		Cell cell = getCell(sheet, rowIndex, columnIndex, CellType.BLANK);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);

	}

	protected void setCellValue(Sheet sheet, int rowIndex, int columnIndex, boolean value) {

		getCell(sheet, rowIndex, columnIndex, CellType.BOOLEAN).setCellValue(value);

	}

	protected abstract void fillTable(XSSFSheet sheet, CellStyle dateStyle);

}
