package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Chartjs;

public class ForumUserPostBar extends Chartjs{

	public ForumUserPostBar(MainController mainController) {
		super(mainController, ChartType.FORUM_USER_POST_BAR);
		
	}

	@Override
	public void exportCSV(String path) throws IOException {
		
		
	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalBar" : "bar");
		String xLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		String yLabel = useHorizontal ? getYScaleLabel() : getXScaleLabel();
		jsObject.put("scales", "{yAxes:[{" + yLabel + ",ticks:{stepSize:0}}],xAxes:[{" + xLabel
				+ (useHorizontal ? ",ticks:{maxTicksLimit:10}" : "") + "}]}");
		jsObject.put("onClick", "function(e,t){return e.datasetI}");
		JSObject callbacks = new JSObject();
		callbacks.put("title", "function(a,t){return a[0].xLabel+' ('+a[0].yLabel+')'}");
		callbacks.put("label", "function(e,t){return t.datasets[e.datasetIndex].discussions[e.index]}");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");
		return jsObject;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
