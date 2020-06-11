package es.ubu.lsi.ubumonitor.clustering.chart;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.util.JSArray;
import javafx.scene.web.WebView;

/**
 * Clase base para repesentar el resultado del clustering.
 * 
 * @author Xing Long Ji
 *
 */
public abstract class ClusteringChart extends AbstractChart {

	private static final String LEGEND_FORMAT = "%s (%d/%d)";

	/**
	 * Constrinctor mediante un WebView.
	 * 
	 * @param webView WebView que contiene la gráfica
	 */
	protected ClusteringChart(WebView webView) {
		super(webView);
	}

	/**
	 * Renombra los nombres de las agrupaciones.
	 * 
	 * @param clusters lista de agrupaciones
	 */
	public void rename(List<ClusterWrapper> clusters) {
		JSArray names = new JSArray();
		int total = clusters.stream().mapToInt(ClusterWrapper::size).sum();
		clusters.forEach(c -> names.addWithQuote(getLegend(c, total)));
		getWebEngine().executeScript("rename(" + names + ")");
	}

	/**
	 * Devuelve el texto de la leyeda para una agrupación.
	 * 
	 * @param cluster agrupación
	 * @param total   nuúmero total de puntos
	 * @return texto de la leyenda
	 */
	protected String getLegend(ClusterWrapper cluster, int total) {
		return String.format(ClusteringChart.LEGEND_FORMAT, cluster.getName(), cluster.size(), total);
	}

	/**
	 * Actualiza la gráfica con los datos del resultado del clustering.
	 * 
	 * @param clusters lista de agrupaciones
	 */
	public abstract void updateChart(List<ClusterWrapper> clusters);

}
