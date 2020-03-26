package es.ubu.lsi.ubumonitor.export.dashboard;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;

public class FillSheetLog extends FillSheetData {

	public FillSheetLog() {
		super("Log");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		List<LogLine> logs = Controller.getInstance()
				.getActualCourse()
				.getLogs()
				.getList();
		int rowIndex = 1;
		for (LogLine log : logs) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, log.getTime()
					.toLocalDateTime(), dateStyle);
			setCellValue(sheet, rowIndex, ++columnIndex, log.getComponent()
					.getName());
			setCellValue(sheet, rowIndex, ++columnIndex, log.getEventName()
					.getName());
			setCellValue(sheet, rowIndex, ++columnIndex, log.getOrigin()
					.getName());
			setCellValue(sheet, rowIndex, ++columnIndex, log.getIPAdress());
			setCellValue(sheet, rowIndex, ++columnIndex, log.getUser()
					.getId());
			EnrolledUser affectedUser = log.getAffectedUser();
			if (affectedUser != null) {
				setCellValue(sheet, rowIndex, ++columnIndex, affectedUser.getId());

			}
			CourseModule cm = log.getCourseModule();
			if (cm != null) {
				setCellValue(sheet, rowIndex, ++columnIndex, cm.getCmid());
			}

			++rowIndex;
		}
	}

}
