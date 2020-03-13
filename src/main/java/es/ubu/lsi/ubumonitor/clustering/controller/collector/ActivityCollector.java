package es.ubu.lsi.ubumonitor.clustering.controller.collector;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.Datum;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.scene.control.ListView;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion.State;
import es.ubu.lsi.ubumonitor.model.CourseModule;

public class ActivityCollector extends DataCollector {

	private ListView<CourseModule> courseModules;

	public ActivityCollector(MainController mainController) {
		super("clustering.type.activity");
		courseModules = mainController.getListViewActivity();
	}

	@Override
	public void collect(List<UserData> users) {
		List<CourseModule> selected = courseModules.getSelectionModel().getSelectedItems();
		for (UserData userData : users) {
			for (CourseModule courseModule : selected) {
				ActivityCompletion activity = courseModule.getActivitiesCompletion().get(userData.getEnrolledUser());
				State state = activity.getState();
				int value = (state == State.COMPLETE || state == State.COMPLETE_PASS) ? 1 : 0;
				userData.addDatum(new Datum(getType(), courseModule.getModuleName(),
						courseModule.getModuleType().getModName(), value));
				userData.addNormalizedDatum(value);
			}
		}
	}
}
