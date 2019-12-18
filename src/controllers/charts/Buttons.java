package controllers.charts;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Buttons {
	private BooleanProperty showMean;
	private BooleanProperty showLegend;
	private BooleanProperty showGroupMean;
	private static Buttons instance;


	/**
	 * Devuelve la instancia única de Buttons.
	 * 
	 * @return instancia singleton
	 */
	public static Buttons getInstance() {
		if (instance == null) {
			instance = new Buttons();
		}
		return instance;
	}

	private Buttons() {
		showMean = new SimpleBooleanProperty(true);
		showLegend = new SimpleBooleanProperty(true);
		showGroupMean = new SimpleBooleanProperty(true);
	}

	public boolean getShowMean() {
		return showMean.getValue();
	}

	public void setShowMean(boolean showMean) {
		this.showMean.setValue(showMean);
	}
	
	public boolean getShowLegend() {
		return showLegend.getValue();
	}
	
	public boolean swapLegend() {
		showLegend.setValue(!showLegend.getValue());
		return showLegend.getValue();
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend.setValue(showLegend);
	}

	public boolean swapMean() {
		showMean.setValue(!showMean.getValue());
		return showMean.getValue();
	}

	public boolean getShowGroupMean() {
		return showGroupMean.getValue();
	}
	
	public boolean swapGroupMean() {
		showGroupMean.setValue(!showGroupMean.getValue());
		return showGroupMean.getValue();
	}
}
