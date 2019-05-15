package controllers.ubulogs;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.ubulogs.logcreator.Component;
import model.EnrolledUser;

public class StackedBarDataset {

	static final Logger logger = LoggerFactory.getLogger(StackedBarDataset.class);
	private Controller controller = Controller.getInstance();
	/**
	 * Nivel de opacidad para el color de fondo de las barras
	 */
	private static final float OPACITY_BAR = 0.4f;

	private List<EnrolledUser> selectedUsers = new ArrayList<>();
	private List<Component> selectedComponents = new ArrayList<>();
	private Map<Component, int[]> componentColors;
	private GroupByAbstract<?> groupBy;
	private ZonedDateTime start;
	private ZonedDateTime end;
	private StringBuilder stringBuilder;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
	{
		DecimalFormatSymbols decimalFormatSymbols = DECIMAL_FORMAT.getDecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		DECIMAL_FORMAT.setDecimalFormatSymbols(decimalFormatSymbols);
		DECIMAL_FORMAT.setMaximumFractionDigits(2);
		DECIMAL_FORMAT.setMinimumFractionDigits(2);
	}
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
		//si son los mismos parametros devolvemos el creado anteriormente
		if (isSamesPreviousValue(selectedUsers, selectedComponents, groupBy, start, end)) {
			return stringBuilder.toString();
		}

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

		for (int i = 0, n = selectedComponents.size(); i < n; i++) {
			
			//generamos un color a partir de HSB, el hue(H) es el color, saturacion (S), y brillo(B)
			Color c = new Color(Color.HSBtoRGB(i / (float) n, 0.8f, 1.0f));
			int[] array = { c.getRed(), c.getGreen(), c.getBlue() };
			componentColors.put(selectedComponents.get(i), array);
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

		Map<Component, List<Double>> meanComponents = groupBy.getComponentsMeans(selectedComponents, start, end);

		for (Entry<Component, List<Double>> entry : meanComponents.entrySet()) {
			Component component = entry.getKey();
			List<Double> data = entry.getValue();
			
			// convertimos el valor double en string limitado a dos decimales
			List<String> dataString = data.stream()
					.map(d -> DECIMAL_FORMAT.format(d))
					.collect(Collectors.toList());

			int[] color = componentColors.get(component);

			stringBuilder.append("{");
			stringBuilder.append(
					"label:'" + escapeJavaScriptText(controller.getResourceBundle().getString("chart.mean") + " "
							+ translateComponent(component)) + "',");
			stringBuilder.append("type: 'line',");
			stringBuilder.append("borderWidth: 2,");
			stringBuilder.append("fill: false,");
			stringBuilder.append("borderColor: 'rgb(" + color[0] + ", " + color[1] + "," + color[2] + ")',");
			stringBuilder.append("data: [" + join(dataString) + "]");
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
				int[] c = componentColors.get(component);
				StringBuilder bar = new StringBuilder();
				List<Long> data = componentDataset.get(component);

				bar.append("{");
				bar.append("label:'" + escapeJavaScriptText(translateComponent(component)) + "',");
				bar.append("stack: '" + escapeJavaScriptText(user.toString()) + "',");
				bar.append("backgroundColor: 'rgba(" + c[0] + ", " + c[1] + "," + c[2] + "," + OPACITY_BAR + ")',");
				bar.append("data: [" + join(data) + "]");
				bar.append("}");

				datasets.add(bar.toString());
			}
		}
		stringBuilder.append(join(datasets));
	}

	private String joinWithQuotes(List<String> list) {
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

	public boolean isSamesPreviousValue(List<EnrolledUser> selectedUsers, List<Component> selectedComponents,
			GroupByAbstract<?> groupBy, ZonedDateTime start, ZonedDateTime end) {
		return this.selectedUsers.equals(selectedUsers)
				&& this.selectedComponents.equals(selectedComponents)
				&& (this.groupBy != null && this.groupBy.equals(groupBy)
						&& (this.start != null && this.start.equals(start)
								&& (this.end != null && this.end.equals(end))));
	}
}
