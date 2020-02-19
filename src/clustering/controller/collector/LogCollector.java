package clustering.controller.collector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import clustering.data.UserData;
import controllers.Controller;
import controllers.I18n;
import controllers.datasets.DataSet;
import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.TypeTimes;
import javafx.scene.control.ListView;
import model.Course;
import model.EnrolledUser;

public class LogCollector<T> extends DataCollector {
	
	private String type;
	private ListView<T> listView;
	private DataSet<T> dataSet;
	
	public LogCollector(String type, ListView<T> listView, DataSet<T> dataSet) {
		this.type = type;
		this.listView = listView;
		this.dataSet = dataSet;
	}

	@Override
	public void collect(List<UserData> users) {
		List<T> selected = listView.getSelectionModel().getSelectedItems();
		Course couse = Controller.getInstance().getActualCourse();
		GroupByAbstract<?> groupBy = couse.getLogStats().getByType(TypeTimes.ALL);
		List<EnrolledUser> enrolledUsers = users.stream().map(UserData::getEnrolledUser).collect(Collectors.toList());
		Map<EnrolledUser, Map<T, List<Long>>> result = dataSet.getUserCounts(groupBy, enrolledUsers, selected, null, null);
		for (UserData userData : users) {
			for (T component : selected) {
				double datum = result.get(userData.getEnrolledUser()).get(component).get(0);
				userData.addDatum(component.toString(), datum);
			}
		}
	}
	
	@Override
	public String toString() {
		return I18n.get("tab." + type);
	}

}
