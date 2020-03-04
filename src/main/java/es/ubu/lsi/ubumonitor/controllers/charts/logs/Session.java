package es.ubu.lsi.ubumonitor.controllers.charts.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.TypeTimes;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class Session extends ChartjsLog{

	public Session(MainController mainController) {
		super(mainController, ChartType.SESSION);
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		return null;
	
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String[] getCSVHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
