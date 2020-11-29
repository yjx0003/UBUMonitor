package es.ubu.lsi.ubumonitor.model.datasets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;

public class DatasSetCourseModule implements DataSet<CourseModule> {

	
	/**
	 * Instacia única de la clase StackedBarDatasSetCourseModule.
	 */
	private static DatasSetCourseModule instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private DatasSetCourseModule() {
	}

	/**
	 * Devuelve la instancia única de StackedBarDatasSetCourseModule.
	 * @return instancia singleton
	 */
	public static DatasSetCourseModule getInstance() {
		if (instance == null) {
			instance = new DatasSetCourseModule();
		}
		return instance;
	}
	
	@Override
	public String translate(CourseModule element) {
		return element.getModuleName();
	}

	@Override
	public Map<CourseModule, List<Double>> getMeans(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<CourseModule> elements, LocalDate start, LocalDate end) {
		return groupBy.getCourseModules().getMeans(enrolledUsers, elements, start, end);
	}

	@Override
	public Map<EnrolledUser, Map<CourseModule, List<Integer>>> getUserCounts(GroupByAbstract<?> groupBy,
			List<EnrolledUser> enrolledUsers, List<CourseModule> elements, LocalDate start, LocalDate end) {
		return groupBy.getCourseModules().getUsersCounts(enrolledUsers, elements, start, end);
	}

	@Override
	public Map<EnrolledUser, Map<CourseModule, List<LogLine>>> getUserLogs(GroupByAbstract<?> groupBy,
			List<EnrolledUser> enrolledUsers, List<CourseModule> elements, LocalDate start, LocalDate end) {
		return groupBy.getCourseModules().getUserLogs(enrolledUsers, elements, start, end);
	}

	@Override
	public Map<EnrolledUser, Integer> getUserTotalLogs(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<CourseModule> elements, LocalDate start, LocalDate end) {
		return groupBy.getCourseModules().getUserTotalLogs(enrolledUsers, elements, start, end);
	}

	@Override
	public Map<EnrolledUser, Map<CourseModule, Integer>> getUserLogsGroupedByLogElement(GroupByAbstract<?> groupBy,
			List<EnrolledUser> enrolledUsers, List<CourseModule> elements, LocalDate start, LocalDate end) {
		return groupBy.getCourseModules().getUserLogsGroupedByLogElement(enrolledUsers, elements, start, end);
	}
}
