package clustering.controller.collector;

import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DatasSetCourseModule;
import model.CourseModule;

public class LogCourseModuleCollector extends LogCollector<CourseModule> {

	public LogCourseModuleCollector(MainController mainController) {
		super(mainController.getListViewCourseModule(), DatasSetCourseModule.getInstance());
	}

	@Override
	public String toString() {
		return I18n.get("tab.coursemodule");
	}

}
