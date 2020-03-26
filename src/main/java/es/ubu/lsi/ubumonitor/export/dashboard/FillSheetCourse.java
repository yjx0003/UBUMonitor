package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Collection;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Course;

public class FillSheetCourse extends FillSheetData {

	public FillSheetCourse() {
		super("Course");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Collection<Course> courses = Controller.getInstance()
				.getDataBase()
				.getCourses()
				.getMap()
				.values();
		int rowIndex = 1;
		for (Course course : courses) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, course.getId());
			setCellValue(sheet, rowIndex, ++columnIndex, course.getFullName());
			setCellValue(sheet, rowIndex, ++columnIndex, course.getShortName());

			++rowIndex;
		}
	}

}
