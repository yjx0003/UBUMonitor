package es.ubu.lsi.ubumonitor.controllers.tabs;

import java.io.File;
import java.util.Collections;
import java.util.List;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.persistence.Serialization;
import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.bridge.JavaConnector;
import es.ubu.lsi.ubumonitor.view.chart.bridge.SigmaConnector;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class SigmaController extends WebViewAction {

	private SigmaConnector javaConnector;

	@SuppressWarnings("unchecked")
	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		Controller controller = Controller.getInstance();
		File sigmaFile = controller.getSigmaCache();

		if (!sigmaFile.exists()) {
			mainController.getWebViewTabsController()
					.getTabPane()
					.getTabs()
					.remove(tab);
		} else {
			List<Student> students;
			try {
				students = (List<Student>) Serialization.decrypt(controller.getPassword(), sigmaFile.toString());

			} catch (Exception e) {
				try {
					students = (List<Student>) Serialization.decrypt(controller.getPreviousPassword(),
							sigmaFile.toString());
					Serialization.encrypt(controller.getPassword(), sigmaFile.toString(), students);

				} catch (Exception ex) {
					students = Collections.emptyList();
					UtilMethods.errorWindow("No se puede cargar la cache de Sigma, vuelva a importarlo", ex);
				}
			}
			javaConnector = new SigmaConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController,
					actualCourse, students);
			init(tab, actualCourse, mainConfiguration, stage, javaConnector);
		}

	}

	@Override
	public void onWebViewTabChange() {
		javaConnector.updateChart();

	}

	@Override
	public void updateListViewEnrolledUser() {
		updateChart();

	}

	@Override
	public void updatePredicadeEnrolledList() {
		updateChart();

	}

	@Override
	public void applyConfiguration() {
		updateChart();

	}

	@Override
	public void onSetTabGrades() {
		javaConnector.updateChart();

	}

	@Override
	public void updateTreeViewGradeItem() {
		javaConnector.updateChart();
	}

	@Override
	public JavaConnector getJavaConnector() {
		return javaConnector;
	}

}
