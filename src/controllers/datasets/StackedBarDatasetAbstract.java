package controllers.datasets;

import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import controllers.I18n;
import controllers.ubulogs.GroupByAbstract;
import model.EnrolledUser;

public abstract class StackedBarDatasetAbstract<T> {

	/**
	 * Nivel de opacidad para el color de fondo de las barras
	 */
	private static final float OPACITY_BAR = 0.4f;

	protected List<EnrolledUser> enrolledUsers;
	protected List<EnrolledUser> selectedUsers;
	protected List<T> elements;
	protected Map<T, int[]> colors;
	protected GroupByAbstract<?> groupBy;
	protected LocalDate start;
	protected LocalDate end;
	protected StringBuilder stringBuilder;

	/**
	 * Escapa las comillas simples de un texto añadiendo un \
	 * 
	 * @param input
	 *            texto
	 * @return texto escapado
	 */
	private static String escapeJavaScriptText(String input) {
		return input.replaceAll("'", "\\\\'");
	}

	/**
	 * Traduce el elemento
	 * 
	 * @param element
	 *            elemento
	 * @return traducido
	 */
	protected abstract String translate(T element);

	/**
	 * Devuelve un mapa con las medias.
	 * 
	 * @return las medias
	 */
	protected abstract Map<T, List<Double>> getMeans();

	/**
	 * Devuele los conteos de los usuarios
	 * 
	 * @return los conteos de los usuarios
	 */
	protected abstract Map<EnrolledUser, Map<T, List<Long>>> getUserCounts();

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
			List<T> selecteds, GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd) {

		// lo metemos en un nuevo arraylist para evitar que se actualice en tiempo real
		// los elementos
		this.enrolledUsers = new ArrayList<>(enrolledUsers);
		this.selectedUsers = new ArrayList<>(selectedUsers);
		this.elements = new ArrayList<>(selecteds);
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

	/**
	 * Añade las etiquetas del eje x de la grafica.
	 */
	private void setLabels() {
		List<String> rangeDates = groupBy.getRangeString(start, end);

		String stringLabels = joinWithQuotes(rangeDates);

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

		Map<T, List<Double>> meanTs = getMeans();

		for (T element : elements) {

			List<Double> data = meanTs.get(element);

			boolean anyNotZero = data.stream().anyMatch(value -> value != 0.0);

			if (anyNotZero) {
				int[] color = colors.get(element);

				stringBuilder.append("{");
				stringBuilder.append(
						"label:'" + escapeJavaScriptText(I18n.get("chart.mean") + " " + element) + "',");
				stringBuilder.append("type: 'line',");
				stringBuilder.append("borderWidth: 2,");
				stringBuilder.append("fill: false,");
				stringBuilder.append("borderColor: 'rgb(" + color[0] + ", " + color[1] + "," + color[2] + ")',");
				stringBuilder.append("data: [" + join(data) + "]");
				stringBuilder.append("},");
			}
		}

	}

	/**
	 * Crea el dataset de los usuarios seleccionados
	 */
	private void setUsersDatasets() {
		Map<EnrolledUser, Map<T, List<Long>>> userTDataset = getUserCounts();

		for (EnrolledUser user : selectedUsers) {
			Map<T, List<Long>> elementDataset = userTDataset.get(user);
			for (T element : elements) {

				List<Long> data = elementDataset.get(element);

				boolean anyNotZero = data.stream().anyMatch(value -> value != 0);

				if (anyNotZero) {
					int[] c = colors.get(element);
					stringBuilder.append("{");
					stringBuilder.append("label:'" + escapeJavaScriptText(translate(element)) + "',");
					stringBuilder.append("name:'" + escapeJavaScriptText(user.toString()) + "',");
					stringBuilder.append("stack: '" + user.getId() + "',");
					stringBuilder.append(
							"backgroundColor: 'rgba(" + c[0] + ", " + c[1] + "," + c[2] + "," + OPACITY_BAR + ")',");
					stringBuilder.append("data: [" + join(data) + "]");
					stringBuilder.append("},");
				}

			}
		}

	}

	/**
	 * Convierte una lista en string con los elementos entre comillas y separado por
	 * comas.
	 * 
	 * @param list
	 * @return
	 */
	private String joinWithQuotes(List<String> list) {
		// https://stackoverflow.com/a/18229122
		return list.stream()
				.map(s -> "'" + s + "'")
				.collect(Collectors.joining(", "));
	}

	/**
	 * Convierte una lista de elementos en string separados por comas
	 * 
	 * @param datasets
	 * @return
	 */
	private <E> String join(List<E> datasets) {
		return datasets.stream()
				.map(E::toString)
				.collect(Collectors.joining(", "));
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
			int[] array = { c.getRed(), c.getGreen(), c.getBlue() };
			colors.put(elements.get(i), array);
		}

	}

}
