package es.ubu.lsi.ubumonitor.export.dashboard;

import java.time.ZoneId;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class FillSheetEnrolledUser extends FillSheetData {
	private static final String SHEET_NAME = "Enrolled User";

	public FillSheetEnrolledUser() {
		super(SHEET_NAME);
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {

		Set<EnrolledUser> enrolledUsers = Controller.getInstance()
				.getActualCourse()
				.getEnrolledUsers();

		int rowIndex = 1;
		for (EnrolledUser enrolledUser : enrolledUsers) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, enrolledUser.getId());
			setCellValue(sheet, rowIndex, ++columnIndex, enrolledUser.getLastname());
			setCellValue(sheet, rowIndex, ++columnIndex, enrolledUser.getFirstname());
			setCellValue(sheet, rowIndex, ++columnIndex, enrolledUser.getFullName());
			setCellValue(sheet, rowIndex, ++columnIndex, enrolledUser.getEmail());
			setCellValue(sheet, rowIndex, ++columnIndex, enrolledUser.getLastaccess()
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime(), dateStyle);
			setCellValue(sheet, rowIndex, ++columnIndex, enrolledUser.getLastcourseaccess()
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime(), dateStyle);
			++rowIndex;
		}

	}

}
