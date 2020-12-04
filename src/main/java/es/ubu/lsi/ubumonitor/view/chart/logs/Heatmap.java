package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.paint.Color;

public class Heatmap extends PlotlyLog {

	private JSArray annotations;

	public Heatmap(MainController mainController) {
		super(mainController, ChartType.HEAT_MAP);

		useGroupBy = true;
		useLegend = true;

	}
	
	@Override
	public String getOnClickFunction() {
		return "function(n){if(n!==undefined&&n.points!==undefined){let t=n.points[counter++%n.points.length];if(t!==undefined&&t.data!==undefined&&t.data.userids){javaConnector.dataPointSelection(t.data.userids[t.pointIndex[0]]||t.data.userids||-100);}}}";
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {
		JSArray data = new JSArray();
		annotations = new JSArray();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);
		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);
		JSArray x = new JSArray();
		rangeDates.forEach(x::addWithQuote);
		JSArray y = new JSArray();
		JSArray userids = new JSArray();
		JSArray z = new JSArray();
		ManageDuplicate manageDuplicate = new ManageDuplicate();
		for (EnrolledUser selectedUser : selectedUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			JSArray zRow = new JSArray();
			z.add(zRow);
			String username = manageDuplicate.getValue(selectedUser.getFullName());
			y.addWithQuote(username);
			userids.add(selectedUser.getId());
			for (int j = 0; j < rangeDates.size(); j++) {
				long result = 0;
				for (E type : typeLogs) {
					List<Integer> times = types.get(type);
					result += times.get(j);
				}

				zRow.add(result);
				if (result > 0) {
					JSObject annotation = new JSObject();
					annotation.putWithQuote("x", rangeDates.get(j));
					annotation.putWithQuote("y", username);
					annotation.put("text", result);
					annotation.put("showarrow", false);
					annotations.add(annotation);
				}

			}
		}

		Color zeroValue = getConfigValue("zeroValue");
		Color firstInterval = getConfigValue("firstInterval");
		Color secondInterval = getConfigValue("secondInterval");
		Color thirdInterval = getConfigValue("thirdInterval");
		Color fourthInterval = getConfigValue("fourthInterval");
		Color moreMax = getConfigValue("moreMax");
		long max = typeLogs.isEmpty() ? 1 : getSuggestedMax(textFieldMax.getText());
		JSObject trace = createTrace(x, y, z, max, Arrays.asList(0.0, 0.000001, 0.25, 0.50, 0.75, 1.0), zeroValue,
				firstInterval, secondInterval, thirdInterval, fourthInterval, moreMax);
		trace.put("userids", userids);
		data.add(trace);
		return data;

	}

	private JSObject createTrace(Collection<?> x, Collection<?> y, Collection<?> z, long max,
			List<Double> colorscalePercentages, Color... colors) {
		JSObject trace = new JSObject();
		if (max != 0) {
			trace.put("zmin", 0);
			trace.put("zmax", max);
		}
		trace.put("type", "'heatmap'");
		trace.put("x", x);
		trace.put("y", y);
		trace.put("z", z);
		JSArray colorscale = new JSArray();
		for (int i = 0; i < colors.length; i++) {
			double value = colorscalePercentages.get(i);
			Color color = colors[i];
			JSArray c = new JSArray();
			c.add(value);
			c.add(colorToRGB(color));
			colorscale.add(c);
		}
		trace.put("colorscale", colorscale);

		trace.put("hovertemplate", "'<b>%{x}<br>%{y}:</b> %{z}<extra></extra>'");
		return trace;
	}

	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {
		JSObject layout = new JSObject();
		JSObject xaxis = new JSObject();
		Plotly.defaultAxisValues(xaxis, getXAxisTitle(), null);
		xaxis.put("type", "'category'");

		JSObject yaxis = new JSObject();
		Plotly.defaultAxisValues(yaxis, null, null);
		yaxis.put("autorange", "'reversed'");
		yaxis.put("type", "'category'");

		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);

		layout.put("annotations", annotations);
		annotations = null;
		return layout;

	}

	@Override
	public void fillOptions(JSObject jsObject) {
		JSObject config = Plotly.createConfig();
		config.put("scrollZoom", false);
		Plotly.fillOptions(jsObject, config, getOnClickFunction());
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<?> rangeDates = groupBy.getRange(dateStart, dateEnd);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			List<Integer> results = new ArrayList<>();
			for (int j = 0; j < rangeDates.size(); j++) {
				int result = 0;
				for (E type : typeLogs) {
					List<Integer> times = types.get(type);
					result += times.get(j);
				}
				results.add(result);

			}
			printer.print(selectedUser.getId());
			printer.print(selectedUser.getFullName());
			printer.printRecord(results);
		}
	}

	@Override
	protected String[] getCSVHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");
		return range.toArray(new String[0]);
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		boolean hasId = hasId();
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			for (E type : typeLogs) {
				List<Integer> times = types.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());
				if (hasId) {
					printer.print(type.hashCode());
				}

				printer.print(type);
				printer.printRecord(times);
			}

		}

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> list = new ArrayList<>();
		list.add("userid");
		list.add("fullname");
		String selectedTab = tabPaneSelection.getSelectionModel()
				.getSelectedItem()
				.getText();
		if (hasId()) {
			list.add(selectedTab + "_id");
		}
		list.add(selectedTab);
		list.addAll(groupBy.getRangeString(dateStart, dateEnd));
		return list.toArray(new String[0]);
	}

}
