package es.ubu.lsi.ubumonitor.model;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class LastActivityFactory {

	private static final List<LastActivity> SPAN_TIME = new ArrayList<>();

	static {
		SPAN_TIME.add(new LastActivity(SPAN_TIME.size(), 0, 3, Color.GREEN));
		SPAN_TIME.add(new LastActivity(SPAN_TIME.size(), 3, 7, Color.BLUE));
		SPAN_TIME.add(new LastActivity(SPAN_TIME.size(), 7, 14, Color.ORANGE));
		SPAN_TIME.add(new LastActivity(SPAN_TIME.size(), 14, Integer.MAX_VALUE, Color.RED));
	}

	public static Color getColorActivity(Temporal startInclusive, Temporal endInclusive) {
		long days = Duration.between(startInclusive, endInclusive)
				.toDays();

		for (LastActivity lastActivity : SPAN_TIME) {
			if (days < lastActivity.getLimitDaysConnection()) {
				return lastActivity.getColor();
			}
		}
		return Color.BLACK;
	}

	public static LastActivity getActivity(Temporal startInclusive, Temporal endInclusive) {
		if (startInclusive == null || endInclusive == null) {
			return SPAN_TIME.get(SPAN_TIME.size() - 1);
		}
		long days = Duration.between(startInclusive, endInclusive)
				.toDays();
		for (LastActivity lastActivity : SPAN_TIME) {
			if (days < lastActivity.getLimitDaysConnection()) {
				return lastActivity;
			}
		}
		throw new IllegalArgumentException();
	}

	public static List<LastActivity> getAllLastActivity() {
		return SPAN_TIME;
	}

	private LastActivityFactory() {
		throw new UnsupportedOperationException();
	}

	public static LastActivity getActivity(int index) {
		for (LastActivity activity : SPAN_TIME) {
			if (activity.getIndex() == index) {
				return activity;
			}
		}
		return null;
	}

}
