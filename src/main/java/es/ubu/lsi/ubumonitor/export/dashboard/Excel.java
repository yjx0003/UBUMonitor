package es.ubu.lsi.ubumonitor.export.dashboard;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {

	public void createExcel(String path) throws IOException {

		try (XSSFWorkbook workbook = new XSSFWorkbook(
				getClass().getResourceAsStream("/templates/Plantilla modelo relacional.xlsx"));
				FileOutputStream outputStream = new FileOutputStream(path)) {
			CellStyle cellStyle = workbook.createCellStyle();

			cellStyle.setDataFormat((short) 14);
		
			
			
			new FillShetHomepage().fillData(workbook, cellStyle);
			new FillSheetGeneralData().fillData(workbook, cellStyle);
			new FillSheetEnrolledUser().fillData(workbook, cellStyle);
			new FillSheetEnrolledUserCourse().fillData(workbook, cellStyle);
			new FillSheetEnrolledUserGradeItem().fillData(workbook, cellStyle);
			new FillSheetEnrolledUserRole().fillData(workbook, cellStyle);
			new FillSheetEnrolledUserGroup().fillData(workbook, cellStyle);
			new FillSheetActivityCompletion().fillData(workbook, cellStyle);
			new FillSheetCourseModule().fillData(workbook, cellStyle);
			new FillSheetSection().fillData(workbook, cellStyle);
			new FillSheetGradeItem().fillData(workbook, cellStyle);
			new FillSheetRole().fillData(workbook, cellStyle);
			new FillSheetGroup().fillData(workbook, cellStyle);
			new FillSheetCourse().fillData(workbook, cellStyle);
			new FillSheetLog().fillData(workbook, cellStyle);
			workbook.write(outputStream);
		}

	}

}
