package es.ubu.lsi.ubumonitor.model;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class LastActivityFactory {

	private List<LastActivity> spanTime;

	public static final LastActivityFactory DEFAULT = new LastActivityFactory();

	static {

		DEFAULT.addActivity(0, 3, Color.GREEN, ChronoUnit.DAYS);
		DEFAULT.addActivity(3, 7, Color.BLUE, ChronoUnit.DAYS);
		DEFAULT.addActivity(7, 14, Color.ORANGE, ChronoUnit.DAYS);
		DEFAULT.addActivity(14, Integer.MAX_VALUE, Color.RED, ChronoUnit.DAYS);
	}

	public LastActivityFactory() {
		spanTime = new ArrayList<>();
	}

	public int getIndex(Temporal startInclusive, Temporal endExclusive) {
		if (startInclusive != null && endExclusive != null) {
			for (int i = 0; i < spanTime.size(); ++i) {
				if (spanTime.get(i)
						.isBetween(startInclusive, endExclusive)) {
					return i;
				}
			}
		}

		return spanTime.size() - 1;

	}

	public Color getColorActivity(Temporal startInclusive, Temporal endExclusive) {
		if (startInclusive == null || endExclusive == null) {
			return Color.BLACK;
		}

		for (LastActivity lastActivity : spanTime) {
			if (lastActivity.isBetween(startInclusive, endExclusive)) {
				return lastActivity.getColor();
			}
		}
		return Color.BLACK;
	}

	public LastActivity getActivity(Temporal startInclusive, Temporal endExclusive) {
		if (startInclusive == null || endExclusive == null) {
			return spanTime.get(spanTime.size() - 1);
		}

		for (LastActivity lastActivity : spanTime) {
			if (lastActivity.isBetween(startInclusive, endExclusive)) {
				return lastActivity;
			}
		}
		throw new IllegalArgumentException();
	}

	public List<LastActivity> getAllLastActivity() {
		return spanTime;
	}

	public LastActivity getActivity(int index) {
		for (LastActivity activity : spanTime) {
			if (activity.getIndex() == index) {
				return activity;
			}
		}
		return null;
	}

	public void addActivity(LastActivity lastActivity) {
		spanTime.add(lastActivity);
	}

	public void addActivity(int startInclusive, int endInclusive, Color color, ChronoUnit chronoUnit) {
		addActivity(new LastActivity(spanTime.size(), startInclusive, endInclusive, color, chronoUnit));
	}

}
