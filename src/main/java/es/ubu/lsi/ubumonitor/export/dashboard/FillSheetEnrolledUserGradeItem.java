package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;

public class FillSheetEnrolledUserGradeItem extends FillSheetData {

	public FillSheetEnrolledUserGradeItem() {
		super("Enrolled User - Grade Item");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<GradeItem> gradeItems = Controller.getInstance()
				.getActualCourse()
				.getGradeItems();
		int rowIndex = 1;
		for (GradeItem gradeItem : gradeItems) {
			for (EnrolledUser user : Controller.getInstance()
					.getActualCourse()
					.getEnrolledUsers()) {
				
				
				if(!Double.isNaN(gradeItem.getEnrolledUserPercentage(user))) {
					int columnIndex = -1;
					setCellValue(sheet, rowIndex, ++columnIndex, user.getId());
					setCellValue(sheet, rowIndex, ++columnIndex, gradeItem.getId());
					setCellValue(sheet, rowIndex, ++columnIndex, gradeItem.getEnrolledUserPercentage(user)/10);
					++rowIndex;
				}
			
				
			}

		}

	}
}
