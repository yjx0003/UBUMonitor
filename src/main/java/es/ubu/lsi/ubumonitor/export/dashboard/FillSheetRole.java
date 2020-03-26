package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Role;

public class FillSheetRole extends FillSheetData {

	private static final String SHEET_NAME = "Role";

	public FillSheetRole() {
		super(SHEET_NAME);
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<Role> roles = Controller.getInstance()
				.getActualCourse()
				.getRoles();
		int rowIndex = 1;
		for (Role role : roles) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, role.getRoleId());
			setCellValue(sheet, rowIndex, ++columnIndex, role.getRoleName());
			setCellValue(sheet, rowIndex, ++columnIndex, role.getRoleShortName());
			++rowIndex;
		}
	}

}
