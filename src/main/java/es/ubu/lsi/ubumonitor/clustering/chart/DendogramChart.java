package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.util.Tree;
import es.ubu.lsi.ubumonitor.clustering.util.Tree.Node;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import javafx.scene.web.WebView;

public class DendogramChart extends AbstractChart {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DendogramChart.class);

	public DendogramChart(WebView webView) {
		super(webView);
		getWebEngine().load(getClass().getResource("/graphics/DendogramChart.html").toExternalForm());
	}

	public void updateChart(Tree<String> tree) {
		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		JSArray datasets = new JSArray();

		JSArray points = new JSArray();
		Node<String> node = tree.getRoot();
		nodeToJS(points, labels, node);
		JSObject dataset = new JSObject();
		dataset.put("data", points);
		datasets.add(dataset);
		data.put("labels", labels);
		data.put("datasets", datasets);

		LOGGER.debug("Herarchical: {}", data);
		getWebEngine().executeScript("updateChart(" + data + ")");
	}

	private void nodeToJS(JSArray points, JSArray labels, Node<String> node) {
		labels.addWithQuote(node.getValue());

		JSObject object = new JSObject();
		object.putWithQuote("name", node.getValue());
		if (node.getParentNode() != null)
			object.putWithQuote("parent", node.getParentNode());

		JSArray users = new JSArray();
		addChildrens(users, node);
		object.put("users", users);

		points.add(object);

		for (Node<String> children : node.getChildrens()) {
			nodeToJS(points, labels, children);
		}
	}

	private void addChildrens(JSArray array, Node<String> node) {
		if (node.getChildrens().isEmpty()) {
			array.addWithQuote(node.getValue());
		}
		for (Node<String> children : node.getChildrens()) {
			if (children.getChildrens().isEmpty()) {
				array.addWithQuote(children.getValue());
			} else {
				addChildrens(array, children);
			}
		}
	}

	@Override
	protected void exportData(File file) throws IOException {
		// TODO Auto-generated method stub

	}

}
