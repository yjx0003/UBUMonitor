package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Section;

public class FillSheetSection extends FillSheetData {

	public FillSheetSection() {
		super("Section");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<Section> sections = Controller.getInstance()
				.getActualCourse()
				.getSections();
		int rowIndex = 1;
		for (Section section:sections) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, section.getId());
			setCellValue(sheet, rowIndex, ++columnIndex, section.getName());
			setCellValue(sheet, rowIndex, ++columnIndex, section.isVisible());
			setCellValue(sheet, rowIndex, ++columnIndex, section.getSectionNumber());
			++rowIndex;
		}
	}

}
