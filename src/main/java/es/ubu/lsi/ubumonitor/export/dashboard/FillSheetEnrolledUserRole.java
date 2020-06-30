package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Role;

public class FillSheetEnrolledUserRole extends FillSheetData{

	public FillSheetEnrolledUserRole() {
		super("Enrolled User - Role");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<Role> roles = Controller.getInstance().getActualCourse().getRoles();
		int rowIndex = 1;
		for (Role role: roles) {
			for(EnrolledUser user : role.getEnrolledUsers()) {
				int columnIndex = -1;
				setCellValue(sheet, rowIndex, ++columnIndex, user.getId());
				setCellValue(sheet, rowIndex, ++columnIndex, role.getRoleId());
				
				++rowIndex;
			}

		}
		
	}

}
