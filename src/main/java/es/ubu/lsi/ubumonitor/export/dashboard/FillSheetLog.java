package es.ubu.lsi.ubumonitor.export.dashboard;

import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.LogLine;

public class FillSheetLog extends FillSheetData {

	public FillSheetLog() {
		super("Log");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		
		TemporalField firstDayWeek  = WeekFields.of(Locale.getDefault()).dayOfWeek();
		List<LogLine> logs = Controller.getInstance()
				.getActualCourse()
				.getLogs()
				.getList();
		int rowIndex = 1;
		for (LogLine log : logs) {
			int columnIndex = -1;
			setCellValue(sheet, rowIndex, ++columnIndex, log.getTime()
					.toLocalDateTime(), dateStyle);
			setCellValue(sheet, rowIndex, ++columnIndex, log.getTime()
					.toLocalDate().with(firstDayWeek,1), dateStyle);
			setCellValue(sheet, rowIndex, ++columnIndex, log.getComponent()
					.getName());
			setCellValue(sheet, rowIndex, ++columnIndex, log.getEventName()
					.getName());
			setCellValue(sheet, rowIndex, ++columnIndex, log.getOrigin()
					.getName());
			setCellValue(sheet, rowIndex, ++columnIndex, log.getIPAdress());
			setCellValue(sheet, rowIndex, ++columnIndex, log.getUser()
					.getId());
			CourseModule cm = log.getCourseModule();
			if (cm != null) {
				setCellValue(sheet, rowIndex, ++columnIndex, cm.getCmid());
			}

			++rowIndex;
		}
	}

}
