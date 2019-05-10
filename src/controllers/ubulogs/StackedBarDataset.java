package controllers.ubulogs;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.ubulogs.logcreator.Component;
import model.EnrolledUser;

public class StackedBarDataset {

	static final Logger logger = LoggerFactory.getLogger(StackedBarDataset.class);
	private Controller controller = Controller.getInstance();

	private List<EnrolledUser> selectedUsers = new ArrayList<>();
	private List<Component> selectedComponents = new ArrayList<>();
	private Map<Component, int[]> componentColors;
	private GroupByAbstract<?> groupBy;
	private ZonedDateTime start;
	private ZonedDateTime end;
	private StringBuilder stringBuilder;
	/**
	 * static Singleton instance.
	 */
	private static StackedBarDataset instance;

	/**
	 * Private constructor for singleton.
	 */
	private StackedBarDataset() {
	}

	/**
	 * Return a singleton instance of CreateStackedBarDataset.
	 */
	public static StackedBarDataset getInstance() {
		if (instance == null) {
			instance = new StackedBarDataset();
		}
		return instance;
	}

	private static String escapeJavaScriptText(String input) {
		return input.replaceAll("'", "\\\\'");
	}

	public String createDataset(List<EnrolledUser> selectedUsers,
			List<Component> selectedComponents, GroupByAbstract<?> groupBy, ZonedDateTime start, ZonedDateTime end) {

		this.selectedUsers = selectedUsers;
		this.selectedComponents = selectedComponents;
		this.groupBy = groupBy;
		this.start = start;
		this.end = end;

		setRandomColors();

		stringBuilder = new StringBuilder();
		stringBuilder.append("{"); // llave de inicio del dataset
		setLabels();
		setDatasets();
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

	public String getDataset() {
		return stringBuilder.toString();
	}

	private void setRandomColors() {
		componentColors = new HashMap<>();
		Random rand = new Random();
		for (Component component : selectedComponents) {
			int r = rand.nextInt(256);
			int g = rand.nextInt(256);
			int b = rand.nextInt(256);
			int[] array = { r, g, b };
			componentColors.put(component, array);
		}

	}

	private String translateComponent(Component component) {
		return controller.getResourceBundle().getString("component." + component);
	}

	private void setDatasets() {
		stringBuilder.append("datasets: [");
		setMeanComponents();
		setUsersDatasets();
		stringBuilder.append("]");
	}

	/**
	 * 
	 * 
	 */
	private void setMeanComponents() {

		Map<Component, List<Long>> meanComponents = groupBy.getComponentsMeans(selectedComponents, start, end);

		for (Entry<Component, List<Long>> entry : meanComponents.entrySet()) {
			Component component = entry.getKey();
			List<Long> data = entry.getValue();
			int[] color = componentColors.get(component);

			stringBuilder.append("{");
			stringBuilder.append(
					"label:['" + escapeJavaScriptText(controller.getResourceBundle().getString("chart.mean") + " "
							+ translateComponent(component)) + "'],");
			stringBuilder.append("type:'line',");
			stringBuilder.append("borderWidth: 2,");
			stringBuilder.append("fill:false,");
			stringBuilder.append("borderColor: 'rgb(" + color[0] + ", " + color[1] + "," + color[2] + ")',");
			stringBuilder.append("data: [" + join(data) + "]");
			stringBuilder.append("},");
		}

	}

	private void setUsersDatasets() {
		Map<EnrolledUser, Map<Component, List<Long>>> userComponentDataset = groupBy.getUsersCounts(selectedUsers,
				selectedComponents, start, end);
		List<String> datasets = new ArrayList<>();

		for (EnrolledUser user : selectedUsers) {
			Map<Component, List<Long>> componentDataset = userComponentDataset.get(user);
			for (Component component : selectedComponents) {
				int[] color = componentColors.get(component);
				StringBuilder bar = new StringBuilder();
				List<Long> data = componentDataset.get(component);

				bar.append("{");
				bar.append("label:['" + escapeJavaScriptText(translateComponent(component)) + "', '"
						+ escapeJavaScriptText(user.toString()) + "'],");
				bar.append("stack: '" + escapeJavaScriptText(user.toString()) + "',");
				bar.append("backgroundColor:'rgba(" + color[0] + ", " + color[1] + "," + color[2] + ",0.6)',");
				bar.append("data: [" + join(data) + "]");
				bar.append("}");

				datasets.add(bar.toString());
			}
		}
		stringBuilder.append(join(datasets));
	}

	private String joinWithQuotes(List<String > list) {
		// https://stackoverflow.com/a/18229122
		return list.stream()
				.map(s -> "'" + s + "'")
				.collect(Collectors.joining(", "));
	}

	private <T> String join(List<T> list) {
		return list.stream()
				.map(T::toString)
				.collect(Collectors.joining(", "));
	}

	private void setLabels() {
		List<String> rangeDates = groupBy.getRangeString(start, end);

		String stringLabels = joinWithQuotes(rangeDates);

		stringBuilder.append("labels:[" + stringLabels + "],");
	}

	public boolean isSameUsersAndComponents(List<EnrolledUser> selectedUsers, List<Component> selectedComponents) {
		return this.selectedUsers.equals(selectedUsers) && this.selectedComponents.equals(selectedComponents);
	}
}
