package es.ubu.lsi.ubumonitor.controllers.charts;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

public class Session {
	private List<LogLine> logLines;

	public Session(LogLine start) {
		logLines = new ArrayList<>();
		logLines.add(start);
	}
	
	public void add(LogLine next) {
		logLines.add(next);
	}

	public long getDiffMinutes() {
		ZonedDateTime start = logLines.get(0).getTime();
		ZonedDateTime end = logLines.get(logLines.size() - 1).getTime();
		return start.until(end, ChronoUnit.MINUTES);
	}
}
