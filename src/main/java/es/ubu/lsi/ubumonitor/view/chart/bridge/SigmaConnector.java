package es.ubu.lsi.ubumonitor.view.chart.bridge;

import java.util.List;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.sigma.controller.EnrolledUserStudentMapping;
import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaBar;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaBoxplot;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaParallelYearConsumed;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaParallelGenderAccess;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaPie;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaStackedBar;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaTableEnrolled;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaTableNotEnrolled;
import javafx.scene.web.WebView;

public class SigmaConnector extends JavaConnectorAbstract {

	public SigmaConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController,
			Course actualCourse, List<Student> students) {
		super(webView, mainConfiguration, mainController, actualCourse);
		
		EnrolledUserStudentMapping enrolledUserStudentMapping = EnrolledUserStudentMapping.getInstance();
		enrolledUserStudentMapping.map(actualCourse.getEnrolledUsers(), students);
		addChart(new SigmaPie(mainController, enrolledUserStudentMapping));
		addChart(new SigmaBar(mainController, enrolledUserStudentMapping));
		addChart(new SigmaStackedBar(mainController, enrolledUserStudentMapping));
		addChart(new SigmaParallelGenderAccess(mainController, enrolledUserStudentMapping));
		addChart(new SigmaParallelYearConsumed(mainController, enrolledUserStudentMapping));
		addChart(new SigmaTableEnrolled(mainController, enrolledUserStudentMapping));
		addChart(new SigmaTableNotEnrolled(mainController, actualCourse, students));
		addChart(new SigmaBoxplot(mainController, enrolledUserStudentMapping));
		currentChart = charts.get(ChartType.getDefault(Tabs.SIGMA));
	}

}
