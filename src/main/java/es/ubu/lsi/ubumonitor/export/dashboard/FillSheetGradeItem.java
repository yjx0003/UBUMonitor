package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.GradeItem;

public class FillSheetGradeItem extends FillSheetData {

	public FillSheetGradeItem() {
		super("Grade Item");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<GradeItem> gradeItems = new HashSet<>();
		int rowIndex = 1;
		
		for (GradeItem gradeItem : gradeItems) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, gradeItem.getId());
			setCellValue(sheet, rowIndex, ++columnIndex, String.format("%2d. %s",rowIndex, gradeItem.getItemname()));
			setCellValue(sheet, rowIndex, ++columnIndex, gradeItem.getLevel());
			CourseModule cm = gradeItem.getModule();
			if (cm != null) {
				setCellValue(sheet, rowIndex, ++columnIndex, cm.getCmid());
			}
			++rowIndex;
		}

	}

}
