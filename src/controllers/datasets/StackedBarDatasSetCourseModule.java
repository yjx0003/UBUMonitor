package controllers.datasets;

import java.util.List;
import java.util.Map;

import model.CourseModule;
import model.EnrolledUser;

public class StackedBarDatasSetCourseModule extends StackedBarDatasetAbstract<CourseModule> {

	
	/**
	 * Instacia única de la clase StackedBarDatasSetCourseModule.
	 */
	private static StackedBarDatasSetCourseModule instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private StackedBarDatasSetCourseModule() {
	}

	/**
	 * Devuelve la instancia única de StackedBarDatasSetCourseModule.
	 * @return instancia singleton
	 */
	public static StackedBarDatasSetCourseModule getInstance() {
		if (instance == null) {
			instance = new StackedBarDatasSetCourseModule();
		}
		return instance;
	}
	
	@Override
	protected String translate(CourseModule element) {
		return element.getModuleName();
	}

	@Override
	protected Map<CourseModule, List<Double>> getMeans() {
		return groupBy.getCourseModules().getMeans(enrolledUsers, elements, start, end);
	}

	@Override
	protected Map<EnrolledUser, Map<CourseModule, List<Long>>> getUserCounts() {
		return groupBy.getCourseModules().getUsersCounts(enrolledUsers, elements, start, end);
	}

}
