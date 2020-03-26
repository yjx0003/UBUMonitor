package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;

public class FillSheetEnrolledUserGroup extends FillSheetData {

	public FillSheetEnrolledUserGroup() {
		super("Enrolled User - Group");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<Group> groups = Controller.getInstance()
				.getActualCourse()
				.getGroups();
		int rowIndex = 1;
		for (Group group : groups) {
			for (EnrolledUser user : group.getEnrolledUsers()) {
				int columnIndex = -1;
				setCellValue(sheet, rowIndex, ++columnIndex, user.getId());
				setCellValue(sheet, rowIndex, ++columnIndex, group.getGroupId());
				++rowIndex;
			}

		}

	}

}
