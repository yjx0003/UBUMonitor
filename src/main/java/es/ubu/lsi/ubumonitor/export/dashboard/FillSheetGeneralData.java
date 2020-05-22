package es.ubu.lsi.ubumonitor.export.dashboard;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;

public class FillSheetGeneralData extends FillSheetData {

	public FillSheetGeneralData() {
		super("General Tables");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		fillCourseStructureTable(sheet);
		ZonedDateTime lastLogDate = Controller.getInstance().getUpdatedLog();
		Set<EnrolledUser> enrolledUsers = Controller.getInstance()
				.getActualCourse()
				.getEnrolledUsers();
		Map<LastActivity, Long> lastActivities = enrolledUsers.stream()
				.map(e -> LastActivityFactory.DEFAULT.getActivity(e.getLastcourseaccess(), lastLogDate))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		int rowIndex = 0;
		for (LastActivity lastActivity : LastActivityFactory.DEFAULT.getAllLastActivity()) {
			if (lastActivities.get(lastActivity) != null) {
				setCellValue(sheet, ++rowIndex, 3, lastActivities.get(lastActivity));

			}
		}
	}

	public void fillCourseStructureTable(XSSFSheet sheet) {
		int rowIndex = 0;
		Course course = Controller.getInstance()
				.getActualCourse();
		setCellValue(sheet, ++rowIndex, 1, course.getUniqueComponents()
				.size());
		setCellValue(sheet, ++rowIndex, 1, course.getUniqueComponentsEvents()
				.size());
		setCellValue(sheet, ++rowIndex, 1, course.getSections()
				.size());
		setCellValue(sheet, ++rowIndex, 1, course.getModules()
				.size());
		setCellValue(sheet, ++rowIndex, 1, course.getGradeItems()
				.size());
		setCellValue(sheet, ++rowIndex, 1, course.getModules()
				.stream()
				.filter(m -> !m.getActivitiesCompletion()
						.isEmpty())
				.count());
	}

}
