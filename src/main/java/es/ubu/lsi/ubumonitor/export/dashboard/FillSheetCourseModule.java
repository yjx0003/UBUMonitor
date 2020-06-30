package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;

public class FillSheetCourseModule extends FillSheetData{

	public FillSheetCourseModule() {
		super("Course Module");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<CourseModule> modules = Controller.getInstance()
				.getActualCourse()
				.getModules();
		int rowIndex = 1;
		ManageDuplicate manageDuplicate = new ManageDuplicate();
		for (CourseModule courseModule:modules) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, courseModule.getCmid());
			setCellValue(sheet, rowIndex, ++columnIndex, manageDuplicate.getValue(courseModule.getModuleName()));
			setCellValue(sheet, rowIndex, ++columnIndex, courseModule.getModuleType().getModName());
			setCellValue(sheet, rowIndex, ++columnIndex, courseModule.isVisible());
			setCellValue(sheet, rowIndex, ++columnIndex, courseModule.getSection().getId());
			++rowIndex;
		}
		
	}

}
