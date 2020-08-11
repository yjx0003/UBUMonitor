package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.chart.AnalysisChart;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Controlador del resultado del análisis.
 * 
 * @author Xing Long Ji
 *
 */
public class AnalysisController {

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private WebView webView;

	private AnalysisChart analysisChart;
	private AnalysisMethod analysisMethod;
	private List<EnrolledUser> users;
	private List<DataCollector> collectors;
	private int start;
	private int end;

	/**
	 * Inicializa el controlador.
	 * 
	 * @param analysisMethod método de analisis
	 * @param users          usuarios
	 * @param collectors     lista de colectores
	 * @param start          inicio del rango
	 * @param end            fin del rango
	 */
	public void setUp(AnalysisMethod analysisMethod, List<EnrolledUser> users, List<DataCollector> collectors,
			int start, int end) {
		analysisChart = new AnalysisChart(webView);
		this.analysisMethod = analysisMethod;
		this.users = users;
		this.collectors = collectors;
		this.start = start;
		this.end = end;

		createService().start();
	}

	private Service<List<Double>> createService() {
		Service<List<Double>> service = new Service<List<Double>>() {

			@Override
			protected Task<List<Double>> createTask() {
				return new Task<List<Double>>() {

					@Override
					protected List<Double> call() throws Exception {
						return analysisMethod.analyze(start, end, users, collectors);

					}
				};
			}
		};

		service.setOnSucceeded(e -> {
			analysisChart.updateChart(analysisMethod, service.getValue(), start);
			progressIndicator.setVisible(false);
			webView.toFront();
			webView.setVisible(true);
		});
		service.setOnFailed(e -> {
			Stage stage = (Stage) webView.getScene().getWindow();
			stage.close();
			UtilMethods.errorWindow(I18n.get(service.getException().getMessage()));
		});

		return service;
	}
}
