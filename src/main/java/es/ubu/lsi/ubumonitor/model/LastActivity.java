package es.ubu.lsi.ubumonitor.model;

import javafx.scene.paint.Color;

public class LastActivity {

	private int index;

	private int previusDays;

	private int limitDaysConnection;

	private Color color;

	public LastActivity(int index, int previusDays, int limitDaysConnection, Color color) {
		this.index = index;
		this.previusDays = previusDays;
		this.limitDaysConnection = limitDaysConnection;
		this.color = color;
	}

	public int getLimitDaysConnection() {
		return limitDaysConnection;
	}

	public void setLimitDaysConnection(int limitDaysConnection) {
		this.limitDaysConnection = limitDaysConnection;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getPreviusDays() {
		return previusDays;
	}

	public void setPreviusDays(int previusDays) {
		this.previusDays = previusDays;
	}

	/**
	 * @return the id
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param id the id to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public int hashCode() {
		return index;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LastActivity other = (LastActivity) obj;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (Integer.MAX_VALUE == limitDaysConnection) {
			return (char) (index + 65) + ". " + previusDays + "+";
		}
		return (char) (index + 65) + ". [" + previusDays + "-" + limitDaysConnection+ ")";
	}
}
