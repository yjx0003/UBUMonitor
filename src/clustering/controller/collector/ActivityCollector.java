package clustering.controller.collector;

import java.util.List;

import clustering.data.Datum;
import clustering.data.UserData;
import controllers.MainController;
import javafx.scene.control.ListView;
import model.ActivityCompletion;
import model.ActivityCompletion.State;
import model.CourseModule;

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
				double value = (state == State.COMPLETE || state == State.COMPLETE_PASS) ? 1.0 : 0.0;
				userData.addDatum(new Datum(getType(), courseModule.getModuleName(),
						courseModule.getModuleType().getModName(), value));
				userData.addNormalizedDatum(value);
			}
		}
	}
}
