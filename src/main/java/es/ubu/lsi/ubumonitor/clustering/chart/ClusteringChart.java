package es.ubu.lsi.ubumonitor.clustering.chart;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.util.JSArray;
import javafx.scene.web.WebView;

public abstract class ClusteringChart extends AbstractChart{

	public static final String LEGEND_FORMAT = "%s (%d/%d)";

	protected ClusteringChart(WebView webView) {
		super(webView);
	}
	
	public void rename(List<ClusterWrapper> clusters) {
		JSArray names = new JSArray();
		int total = clusters.stream().mapToInt(ClusterWrapper::size).sum();
		clusters.forEach(c -> names.addWithQuote(getLegend(c, total)));
		getWebEngine().executeScript("rename(" + names + ")");
	}
	
	protected String getLegend(ClusterWrapper cluster, int total) {
		return String.format(ClusteringChart.LEGEND_FORMAT, cluster.getName(), cluster.size(), total);
	}
	
	public abstract void updateChart(List<ClusterWrapper> clusters);

}
