package clustering.controller.collector;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import clustering.data.Datum;
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
	private Function<T, String> iconFunction;

	public LogCollector(String type, ListView<T> listView, DataSet<T> dataSet, Function<T, String> iconFunction) {
		super("clustering.type.logs." + type);
		this.type = type;
		this.listView = listView;
		this.dataSet = dataSet;
		this.iconFunction = iconFunction;
	}

	@Override
	public void collect(List<UserData> users) {
		List<T> selected = listView.getSelectionModel().getSelectedItems();
		Course couse = Controller.getInstance().getActualCourse();
		GroupByAbstract<?> groupBy = couse.getLogStats().getByType(TypeTimes.ALL);
		List<EnrolledUser> enrolledUsers = users.stream().map(UserData::getEnrolledUser).collect(Collectors.toList());
		Map<EnrolledUser, Map<T, List<Long>>> result = dataSet.getUserCounts(groupBy, enrolledUsers, selected, null,
				null);
		for (T logType : selected) {
			List<Long> values = result.values().stream().map(m -> m.get(logType).get(0)).collect(Collectors.toList());
			long min = Collections.min(values);
			long max = Collections.max(values);
			for (UserData userData : users) {
				double value = result.get(userData.getEnrolledUser()).get(logType).get(0);
				userData.addDatum(new Datum(getType(), dataSet.translate(logType), iconFunction.apply(logType), value));
				value = (value - min) / (max - min);
				userData.addNormalizedDatum(value);

			}
		}
	}

	@Override
	public String toString() {
		return I18n.get("tab." + type);
	}

}
