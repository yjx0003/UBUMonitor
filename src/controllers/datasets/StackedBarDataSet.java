package controllers.datasets;

import java.awt.Color;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.I18n;
import controllers.ubulogs.GroupByAbstract;
import model.EnrolledUser;
import util.UtilMethods;

public class StackedBarDataSet<T> {

	/**
	 * Nivel de opacidad para el color de fondo de las barras
	 */
	private static final float OPACITY_BAR = 0.4f;

	private List<EnrolledUser> enrolledUsers;
	private List<EnrolledUser> selectedUsers;
	private List<T> elements;
	private Map<T, Color> colors;
	private GroupByAbstract<?> groupBy;
	private LocalDate start;
	private LocalDate end;
	private StringBuilder stringBuilder;

	private DataSet<T> dataSet;


	

	/**
	 * Crea la cadena json para chart js
	 * 
	 * @param enrolledUsers
	 *            usuarios matriculados usado para la media
	 * @param selectedUsers
	 *            usuarios seleccionados para mostrar en la grafica
	 * @param selecteds
	 *            el tipo T seleccionado
	 * @param groupBy
	 *            tipo de agrupacion de tiempo
	 * @param dateStart
	 *            fecha de inicio
	 * @param dateEnd
	 *            fecha de fin
	 * @return cadena de texto en JS
	 */
	public String createData(List<EnrolledUser> enrolledUsers, List<EnrolledUser> selectedUsers,
			List<T> selecteds, GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd, DataSet<T> dataSet) {

		this.enrolledUsers = enrolledUsers;
		this.selectedUsers = selectedUsers;
		this.elements = selecteds;
		this.groupBy = groupBy;
		this.start = dateStart;
		this.end = dateEnd;
		this.dataSet = dataSet;

		setRandomColors();

		stringBuilder = new StringBuilder();
		stringBuilder.append("{"); // llave de inicio del dataset
		setLabels();
		setDatasets();
		stringBuilder.append("}");

		return stringBuilder.toString();
	}

	/**
	 * Añade las etiquetas del eje x de la grafica.
	 */
	private void setLabels() {
		List<String> rangeDates = groupBy.getRangeString(start, end);

		String stringLabels = UtilMethods.joinWithQuotes(rangeDates);

		stringBuilder.append("labels:[" + stringLabels + "],");
	}

	/**
	 * Crea los datasets de medias y de los usuarios.
	 */
	private void setDatasets() {
		stringBuilder.append("datasets: [");
		setMeans();
		setUsersDatasets();
		stringBuilder.append("]");
	}

	/**
	 * Crea el dataset de las medias
	 */
	private void setMeans() {

		Map<T, List<Double>> meanTs = dataSet.getMeans(groupBy, enrolledUsers, elements, start, end);

		for (T element : elements) {

			List<Double> data = meanTs.get(element);

			boolean anyNotZero = data.stream().anyMatch(value -> value != 0.0);

			if (anyNotZero) {
				Color c = colors.get(element);

				stringBuilder.append("{");
				stringBuilder.append(
						"label:'" + UtilMethods.escapeJavaScriptText(I18n.get("chart.mean") + " " + dataSet.translate(element)) + "',");
				stringBuilder.append("type: 'line',");
				stringBuilder.append("borderWidth: 2,");
				stringBuilder.append("fill: false,");
				stringBuilder.append("backgroundColor: 'rgba(" + c.getRed() + ", " + c.getGreen() + "," + c.getBlue() + ","
						+ OPACITY_BAR + ")',");
				stringBuilder.append("borderColor: 'rgb(" + c.getRed() + ", " + c.getGreen() + "," + c.getBlue() + ")',");
				stringBuilder.append("data: [" + UtilMethods.join(data) + "]");
				stringBuilder.append("},");
			}
		}

	}

	/**
	 * Crea el dataset de los usuarios seleccionados
	 */
	private void setUsersDatasets() {
		Map<EnrolledUser, Map<T, List<Long>>> userTDataset = dataSet.getUserCounts(groupBy, enrolledUsers, elements, start, end);

		for (EnrolledUser user : selectedUsers) {
		
			Map<T, List<Long>> elementDataset = userTDataset.get(user);
			for (T element : elements) {

				List<Long> data = elementDataset.get(element);

				boolean anyNotZero = data.stream().anyMatch(value -> value != 0);

				if (anyNotZero) {
					Color c = colors.get(element);
					stringBuilder.append("{");
					stringBuilder.append("label:'" + UtilMethods.escapeJavaScriptText(dataSet.translate(element)) + "',");
					stringBuilder.append("name:'" + UtilMethods.escapeJavaScriptText(user.toString()) + "',");
					stringBuilder.append("stack: '" + user.getId() + "',");
					stringBuilder.append(
							"backgroundColor: 'rgba(" + c.getRed() + ", " + c.getGreen() + "," + c.getBlue() + "," + OPACITY_BAR + ")',");
					stringBuilder.append("data: [" + UtilMethods.join(data) + "]");
					stringBuilder.append("},");
				}

			}
		}

	}



	

	/**
	 * Selecciona colores pseudo-aleatorios a partir del HSV
	 */
	private void setRandomColors() {
		colors = new HashMap<>();

		for (int i = 0; i < elements.size(); i++) {

			// generamos un color a partir de HSB, el hue(H) es el color, saturacion (S), y
			// brillo(B)
			Color c = new Color(Color.HSBtoRGB(i / (float) elements.size(), 0.8f, 1.0f));
			colors.put(elements.get(i), c);
		}

	}

}
