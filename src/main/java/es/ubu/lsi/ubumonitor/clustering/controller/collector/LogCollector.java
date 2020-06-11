package es.ubu.lsi.ubumonitor.clustering.controller.collector;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.clustering.data.Datum;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.TypeTimes;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import javafx.scene.control.ListView;

/**
 * Clase que recoge los logs de tipo <T>.
 * 
 * @author Xing Long Ji
 *
 * @param <T> tipo de log
 */
public class LogCollector<T> extends DataCollector {

	private String type;
	private ListView<T> listView;
	private DataSet<T> dataSet;
	private Function<T, String> iconFunction;

	/**
	 * Constructor.
	 * @param type tipo de log
	 * @param listView ListView asociado al tipo de log
	 * @param dataSet conjunto de datos que contiene este tipo de log
	 * @param iconFunction funcion para obtener el icono del log
	 */
	public LogCollector(String type, ListView<T> listView, DataSet<T> dataSet, Function<T, String> iconFunction) {
		super("clustering.type.logs." + type);
		this.type = type;
		this.listView = listView;
		this.dataSet = dataSet;
		this.iconFunction = iconFunction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void collect(List<UserData> users) {
		List<T> selected = listView.getSelectionModel().getSelectedItems();
		Course couse = Controller.getInstance().getActualCourse();
		GroupByAbstract<?> groupBy = couse.getLogStats().getByType(TypeTimes.ALL);
		List<EnrolledUser> enrolledUsers = users.stream().map(UserData::getEnrolledUser).collect(Collectors.toList());
		Map<EnrolledUser, Map<T, List<Integer>>> result = dataSet.getUserCounts(groupBy, enrolledUsers, selected, null,
				null);
		for (T logType : selected) {
			List<Integer> values = result.values().stream().map(m -> m.get(logType).get(0))
					.collect(Collectors.toList());
			double min = Collections.min(values);
			double max = Collections.max(values);
			for (UserData userData : users) {
				int value = result.get(userData.getEnrolledUser()).get(logType).get(0);
				userData.addDatum(new Datum(getType(), dataSet.translate(logType), iconFunction.apply(logType), value));

				double normalized = (value - min) / (max - min);
				userData.addNormalizedDatum(normalized);
			}
		}
	}

	@Override
	public String toString() {
		return I18n.get("tab." + type);
	}

}
