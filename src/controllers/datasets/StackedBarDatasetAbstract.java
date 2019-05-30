package controllers.ubulogs;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import model.EnrolledUser;

public abstract class StackedBarDatasetAbstract<T> {

	static final Logger logger = LoggerFactory.getLogger(StackedBarDatasetAbstract.class);
	protected Controller controller = Controller.getInstance();
	/**
	 * Nivel de opacidad para el color de fondo de las barras
	 */
	private static final float OPACITY_BAR = 0.4f;

	protected List<EnrolledUser> selectedUsers = new ArrayList<>();
	protected List<T> selecteds = new ArrayList<>();
	protected Map<T, int[]> colors;
	protected GroupByAbstract<?> groupBy;
	protected LocalDate start;
	protected LocalDate end;
	protected StringBuilder stringBuilder;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
	{
		DecimalFormatSymbols decimalFormatSymbols = DECIMAL_FORMAT.getDecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		DECIMAL_FORMAT.setDecimalFormatSymbols(decimalFormatSymbols);
		DECIMAL_FORMAT.setMaximumFractionDigits(2);
		DECIMAL_FORMAT.setMinimumFractionDigits(2);
	}

	private static String escapeJavaScriptText(String input) {
		return input.replaceAll("'", "\\\\'");
	}
	
	protected abstract String translate(T element);
	
	protected abstract Map<T, List<Double>> getMeans();
	

	protected abstract Map<EnrolledUser, Map<T, List<Long>>> getUserCounts();

	public String createData(List<EnrolledUser> selectedUsers,
			List<T> selecteds, GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd) {
		
		//lo metemos en un nuevo arraylist para evitar que se actualice en tiempo real los elementos
		this.selectedUsers = new ArrayList<>(selectedUsers);
		this.selecteds = new ArrayList<>(selecteds);
		this.groupBy = groupBy;
		this.start = dateStart;
		this.end = dateEnd;

		setRandomColors();

		stringBuilder = new StringBuilder();
		stringBuilder.append("{"); // llave de inicio del dataset
		setLabels();
		setDatasets();
		stringBuilder.append("}");

		return stringBuilder.toString();
	}

	public String getData() {
		return stringBuilder.toString();
	}

	
	private void setLabels() {
		List<String> rangeDates = groupBy.getRangeString(start, end);

		String stringLabels = joinWithQuotes(rangeDates);

		stringBuilder.append("labels:[" + stringLabels + "],");
	}

	private void setDatasets() {
		stringBuilder.append("datasets: [");
		setMeans();
		setUsersDatasets();
		stringBuilder.append("]");
	}
	
	
	/**
	 * 
	 * 
	 */
	private void setMeans() {

		Map<T, List<Double>> meanTs = getMeans();

		for (Entry<T, List<Double>> entry : meanTs.entrySet()) {
			T element = entry.getKey();
			List<Double> data = entry.getValue();
			
			// convertimos el valor double en string limitado a dos decimales
			List<String> dataString = data.stream()
					.map(d -> DECIMAL_FORMAT.format(d))
					.collect(Collectors.toList());

			int[] color = colors.get(element);

			stringBuilder.append("{");
			stringBuilder.append(
					"label:'" + escapeJavaScriptText(controller.getResourceBundle().getString("chart.mean") + " "
							+ translate(element)) + "',");
			stringBuilder.append("type: 'line',");
			stringBuilder.append("borderWidth: 2,");
			stringBuilder.append("fill: false,");
			stringBuilder.append("borderColor: 'rgb(" + color[0] + ", " + color[1] + "," + color[2] + ")',");
			stringBuilder.append("data: [" + join(dataString) + "]");
			stringBuilder.append("},");
		}

	}



	
	private void setUsersDatasets() {
		Map<EnrolledUser, Map<T, List<Long>>> userTDataset = getUserCounts();
		List<String> datasets = new ArrayList<>();

		for (EnrolledUser user : selectedUsers) {
			Map<T, List<Long>> elementDataset = userTDataset.get(user);
			for (T element : selecteds) {
				int[] c = colors.get(element);
				StringBuilder bar = new StringBuilder();
				List<Long> data = elementDataset.get(element);

				bar.append("{");
				bar.append("label:'" + escapeJavaScriptText(translate(element)) + "',");
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

	private <E> String join(List<E> datasets) {
		return datasets.stream()
				.map(E::toString)
				.collect(Collectors.joining(", "));
	}

	

	

	private void setRandomColors() {
		colors = new HashMap<>();

		for (int i = 0, n = selecteds.size(); i < n; i++) {
			
			//generamos un color a partir de HSB, el hue(H) es el color, saturacion (S), y brillo(B)
			Color c = new Color(Color.HSBtoRGB(i / (float) n, 0.8f, 1.0f));
			int[] array = { c.getRed(), c.getGreen(), c.getBlue() };
			colors.put(selecteds.get(i), array);
		}

	}
	
}
