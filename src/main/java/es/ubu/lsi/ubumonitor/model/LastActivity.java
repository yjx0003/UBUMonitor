package es.ubu.lsi.ubumonitor.model;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import es.ubu.lsi.ubumonitor.util.I18n;
import javafx.scene.paint.Color;

public class LastActivity implements Comparable<LastActivity> {

	private int index;

	private int startInclusive;

	private int endInclusive;

	private Color color;
	private ChronoUnit chronoUnit;

	public LastActivity(int index, int startInclusive, int endInclusive, Color color, ChronoUnit chronoUnit) {
		this.index = index;
		this.startInclusive = startInclusive;
		this.endInclusive = endInclusive;
		this.color = color;
		this.setChronoUnit(chronoUnit);
	}

	public boolean isBetween(Temporal startInclusive, Temporal endExclusive) {
		long span = chronoUnit.between(startInclusive, endExclusive);
		return span >= this.startInclusive && span < this.endInclusive;
	}

	public int getStartInclusive() {
		return startInclusive;
	}

	public void setStartInclusive(int startInclusive) {
		this.startInclusive = startInclusive;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the chronoUnit
	 */
	public ChronoUnit getChronoUnit() {
		return chronoUnit;
	}

	/**
	 * @param chronoUnit the chronoUnit to set
	 */
	public void setChronoUnit(ChronoUnit chronoUnit) {
		this.chronoUnit = chronoUnit;
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
		return index == other.index;

	}

	public String toStringEnumerated() {
		String chronoString = I18n.get(chronoUnit.toString());
		if (Integer.MAX_VALUE == endInclusive) {
			return (char) (index + 65) + ". +" + startInclusive + " " + chronoString;
		}
		return (char) (index + 65) + ". [" + startInclusive + " " + chronoString + " - " + endInclusive + " "
				+ chronoString + ")";
	}

	@Override
	public String toString() {
		String chronoString = I18n.get(chronoUnit.toString());
		if (Integer.MAX_VALUE == endInclusive) {
			return "+" + startInclusive + " " + chronoString;
		}
		return "[" + startInclusive + " " + chronoString + " - " + endInclusive + " " + chronoString + ")";
	}

	@Override
	public int compareTo(LastActivity o) {
		if (o == null) {
			return -1;
		}
		return Integer.compare(this.getIndex(), o.getIndex());
	}
}
