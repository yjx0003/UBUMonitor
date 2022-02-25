package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.time.LocalDate;
import java.util.List;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public abstract class PlotlyLog extends ChartLogs {

	public PlotlyLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	public String getYAxisTitle() {
		return "<b>" + super.getYAxisTitle() + "</b>";
	}

	@Override
	public String getXAxisTitle() {
		return "<b>" + super.getXAxisTitle() + "</b>";
	}

	@Override
	protected String getJSFunction(String dataset, String options) {
		return "updatePlotly(" + dataset + "," + options + ")";
	}

	@Override
	public void fillOptions(JSObject jsObject) {
		Plotly.fillOptions(jsObject, Plotly.createConfig(), getOnClickFunction());

	}

	public String getOnClickFunction() {
		return Plotly.DEFAULT_ON_CLICK_FUNCTION;
	}

	@Override
	public void clear() {
		Plotly.clear(webViewChartsEngine);

	}

	@Override
	public int onClick(int userid) {
		EnrolledUser user = Controller.getInstance()
				.getDataBase()
				.getUsers()
				.getById(userid);
		return getFilteredUsers().indexOf(user);
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = getGroupBy();
		JSObject plot = new JSObject();
		plot.put("data", createData(typeLogs, dataSet, selectedUsers, dateStart, dateEnd, groupBy));
		plot.put("layout", createLayout(typeLogs, dataSet, dateStart, dateEnd, groupBy));
		return plot.toString();
	}

	public GroupByAbstract<?> getGroupBy() {
		if (choiceBoxDate == null) {
			return null;
		}
		return choiceBoxDate.getValue();

	}

	public abstract <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy);

	public abstract <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart,
			LocalDate dateEnd, GroupByAbstract<?> groupBy);

}
