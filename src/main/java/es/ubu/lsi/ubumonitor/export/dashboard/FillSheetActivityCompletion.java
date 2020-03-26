package es.ubu.lsi.ubumonitor.export.dashboard;

import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class FillSheetActivityCompletion extends FillSheetData {

	public FillSheetActivityCompletion() {
		super("Activity Completion");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		Set<CourseModule> courseModules = Controller.getInstance()
				.getActualCourse()
				.getModules();
		int rowIndex = 1;
		for (CourseModule courseModule : courseModules) {
			Map<EnrolledUser, ActivityCompletion> map = courseModule.getActivitiesCompletion();
			if (!map.isEmpty()) {

				for (Map.Entry<EnrolledUser, ActivityCompletion> entry : map.entrySet()) {
					int columnIndex = -1;
					ActivityCompletion activityCompletion = entry.getValue();
					setCellValue(sheet, rowIndex, ++columnIndex, entry.getKey().getId());
					setCellValue(sheet, rowIndex, ++columnIndex, courseModule.getCmid());
					setCellValue(sheet, rowIndex, ++columnIndex, activityCompletion.getState().name());
					setCellValue(sheet, rowIndex, ++columnIndex, activityCompletion.getTracking().name());
					setCellValue(sheet, rowIndex, ++columnIndex, activityCompletion.getTimecompleted()
							.atZone(ZoneId.systemDefault())
							.toLocalDateTime(), dateStyle);
					++rowIndex;
				}

			}
		}
	}

}
