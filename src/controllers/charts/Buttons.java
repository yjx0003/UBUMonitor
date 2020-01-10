package controllers.charts;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Buttons {
	private BooleanProperty showMean;
	private BooleanProperty showLegend;
	private BooleanProperty showGroupMean;
	private IntegerProperty length;
	private IntegerProperty space;
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
		length = new SimpleIntegerProperty(5);
		space = new SimpleIntegerProperty(10);
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

	public int getLength() {
		return length.get();
	}

	public void setLength(int length) {
		this.length.set(length);;
	}

	/**
	 * @return the space
	 */
	public int getSpace() {
		return space.get();
	}

	/**
	 * @param space the space to set
	 */
	public void setSpace(int space) {
		this.space.set(space);
	}
}
