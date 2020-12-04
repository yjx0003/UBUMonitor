package es.ubu.lsi.ubumonitor.view.chart;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSObject;

public abstract class VisNetwork extends Chart {

	public VisNetwork(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
		
	}

	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearVisNetwork()");

	}
	
	public JSObject getNodesOptions() {
		return new JSObject();
	}
	
	public JSObject getEdgesOptions() {
		return new JSObject();
	}
	
	public JSObject getLayoutOptions() {
		return new JSObject();
	}
	
	public JSObject getPhysicsOptions() {
		return new JSObject();
	}

	public JSObject getInteractionOptions() {
		JSObject interaction =  new JSObject();
		interaction.put("keyboard", getConfigValue("interaction.keyboard"));
		interaction.put("multiselect", getConfigValue("interaction.multiselect"));
		interaction.put("navigationButtons", getConfigValue("interaction.navigationButtons"));
		interaction.put("tooltipDelay", getConfigValue("interaction.tooltipDelay"));
		return interaction;
	}
	
	public enum Solver {
		BARNES_HUT("barnesHut"), FORCE_ATLAS_2_BASED("forceAtlas2Based"), REPULSION("repulsion");

		private String name;

		private Solver(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return I18n.get(name());
		}
	}
	
}
