package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;

/**
 * Representación gráfica del análisis. Se representa en un gráfico de lineas.
 * 
 * @author Xing Long Ji
 *
 */
public class AnalysisChart extends AbstractChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisChart.class);

	private List<Double> points;
	private int start;

	/**
	 * Constructor mediante el WebView.
	 * 
	 * @param webView WebView que contiene la gráfica
	 */
	public AnalysisChart(WebView webView) {
		super(webView);
		getWebEngine().load(getClass().getResource("/graphics/LinearChart.html").toExternalForm());
	}

	/**
	 * Actualiza la gráfica con la lista de puntos.
	 * 
	 * @param analysisMethod tipo de método de analisis
	 * @param points         lista de puntos
	 * @param start          inicio en el eje X
	 */
	public void updateChart(AnalysisMethod analysisMethod, List<Double> points, int start) {
		this.points = points;
		this.start = start;
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		JSObject data = new JSObject();
		JSArray array = new JSArray();
		array.addAll(points);
		data.put("data", array);
		data.putWithQuote("borderColor", "#3e95cd");
		data.putWithQuote("backgroundColor", "#3e95cd");
		datasets.add(data);
		root.put("datasets", datasets);

		JSArray labels = new JSArray();
		labels.addAll(IntStream.rangeClosed(start, start + points.size() - 1).boxed().collect(Collectors.toList()));
		root.put("labels", labels);

		LOGGER.debug("Optimal: {}", root);

		if (getWebEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED) {
			updateChart(analysisMethod, root);
		} else {
			getWebEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
				if (newState == Worker.State.SUCCEEDED) {
					updateChart(analysisMethod, root);
				}
			});
		}
	}

	private void updateChart(AnalysisMethod analysisMethod, JSObject root) {
		getWebEngine().executeScript(String.format("updateChart(%s,'%s','%s')", root,
				I18n.get("clustering.numberOfClusters"), I18n.get(analysisMethod.getYLabel())));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void exportData(File file) throws IOException {
		String[] head = new String[] { "Number of clusters", "Value" };
		List<List<Object>> data = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			List<Object> row = new ArrayList<>();
			row.add(start + i);
			row.add(points.get(i));
			data.add(row);
		}
		ExportUtil.exportCSV(file, head, data);
	}

}