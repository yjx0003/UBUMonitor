package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;

public class FillSheetGradeItemGrade extends FillSheetData {

	public FillSheetGradeItemGrade() {
		super("Grades");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<GradeItem> gradeItems = Controller.getInstance()
				.getActualCourse()
				.getGradeItems();
		
		int rowIndex = 30;
		ManageDuplicate manageDuplicate = new ManageDuplicate();
		for (GradeItem gradeItem : gradeItems) {
			String gradeItemName = manageDuplicate.getValue(gradeItem.getItemname());
			for (EnrolledUser user : Controller.getInstance()
					.getActualCourse()
					.getEnrolledUsers()) {
				if(!Double.isNaN(gradeItem.getEnrolledUserPercentage(user))) {
					setCellValue(sheet, rowIndex, 0, gradeItemName);
					setCellValue(sheet, rowIndex, 1, gradeItem.getEnrolledUserPercentage(user)/10);
					++rowIndex;
				}
			
				
			}

		}
	}
}
