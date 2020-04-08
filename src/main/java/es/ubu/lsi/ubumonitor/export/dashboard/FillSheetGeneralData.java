package es.ubu.lsi.ubumonitor.export.dashboard;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Course;

public class FillSheetGeneralData extends FillSheetData {

	public FillSheetGeneralData() {
		super("General Tables");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		int rowIndex = 0;
		Course course = Controller.getInstance().getActualCourse();
		setCellValue(sheet, ++rowIndex, 1, course.getUniqueComponents().size());
		setCellValue(sheet, ++rowIndex, 1, course.getUniqueComponentsEvents().size());
		setCellValue(sheet, ++rowIndex, 1, course.getUniqueComponents().size());
		setCellValue(sheet, ++rowIndex, 1, course.getSections().size());
		setCellValue(sheet, ++rowIndex, 1, course.getModules().size());
		setCellValue(sheet, ++rowIndex, 1, course.getGradeItems().size());
		setCellValue(sheet, ++rowIndex, 1, course.getModules().stream().filter(m->!m.getActivitiesCompletion().isEmpty()).count());
	}

}
