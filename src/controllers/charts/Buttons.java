package controllers.charts;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Buttons {
	private BooleanProperty showMean;
	private BooleanProperty showLegend;
	private BooleanProperty showGroupMean;
	private IntegerProperty length;
	private IntegerProperty space;
	private DoubleProperty cutGrade;
	private static Buttons instance;
	private StringProperty dangerColor;
	private StringProperty passColor;
	private StringProperty emptyColor;


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
		cutGrade = new SimpleDoubleProperty(5.0);
		dangerColor = new SimpleStringProperty("#DC143C");
		passColor = new SimpleStringProperty("#2DC214");
		emptyColor = new SimpleStringProperty("#808080");
	}

	public boolean getShowMean() {
		return showMean.get();
	}

	public void setShowMean(boolean showMean) {
		this.showMean.setValue(showMean);
	}
	
	public boolean getShowLegend() {
		return showLegend.get();
	}
	
	public boolean swapLegend() {
		showLegend.setValue(!showLegend.get());
		return showLegend.get();
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend.setValue(showLegend);
	}

	public boolean swapMean() {
		showMean.setValue(!showMean.get());
		return showMean.get();
	}

	public boolean getShowGroupMean() {
		return showGroupMean.get();
	}
	
	public boolean swapGroupMean() {
		showGroupMean.setValue(!showGroupMean.get());
		return showGroupMean.get();
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

	/**
	 * @return the cutGrade
	 */
	public double getCutGrade() {
		return cutGrade.get();
	}

	/**
	 * @param cutGrade the cutGrade to set
	 */
	public void setCutGrade(double cutGrade) {
		this.cutGrade.set(cutGrade);;
	}

	/**
	 * @return the dangerColor
	 */
	public String getDangerColor() {
		return dangerColor.get();
	}

	/**
	 * @param dangerColor the dangerColor to set
	 */
	public void setDangerColor(String dangerColor) {
		this.dangerColor.set(dangerColor);
	}

	/**
	 * @return the passColor
	 */
	public String getPassColor() {
		return passColor.get();
	}

	/**
	 * @param passColor the passColor to set
	 */
	public void setPassColor(String passColor) {
		this.passColor.set(passColor);;
	}

	/**
	 * @return the emptyColor
	 */
	public String getEmptyColor() {
		return emptyColor.get();
	}

	/**
	 * @param emptyColor the emptyColor to set
	 */
	public void setEmptyColor(String emptyColor) {
		this.emptyColor.set(emptyColor);;
	}
}
