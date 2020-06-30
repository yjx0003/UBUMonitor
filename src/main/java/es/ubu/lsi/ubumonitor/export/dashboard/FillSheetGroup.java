package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;

public class FillSheetGroup extends FillSheetData {

	public FillSheetGroup() {
		super("Group");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<Group> groups = Controller.getInstance()
				.getActualCourse()
				.getGroups();
		int rowIndex = 1;
		ManageDuplicate manageDuplicate = new ManageDuplicate();
		for (Group group : groups) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, group.getGroupId());
			setCellValue(sheet, rowIndex, ++columnIndex, manageDuplicate.getValue(group.getGroupName()));
			setCellValue(sheet, rowIndex, ++columnIndex, group.getDescription());
			++rowIndex;
		}
		
	}

}
