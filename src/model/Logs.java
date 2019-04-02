package model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Logs {

	private List<LogLine> logLines;

	private String timeZone;

	private ZoneId zoneId;

	public Logs(String timeZone, ZoneId zoneId, List<LogLine> logLines) {
		this.timeZone = timeZone;
		this.zoneId = zoneId;
		this.logLines = logLines;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}

	public ZonedDateTime getLastDatetime() {
		return isEmpty(logLines) ? null : logLines.get(logLines.size() - 1).getTime();
	}

	public void addAll(List<LogLine> logLines) {
		if (!isEmpty(logLines)) {
			return;
		}
		if (!logLines.get(0).getTime().getZone().equals(zoneId)) {
			for (LogLine logLine : logLines) {
				ZonedDateTime changeZone = logLine.getTime().withZoneSameInstant(zoneId);
				logLine.setTime(changeZone);
			}
		}
		this.logLines.addAll(logLines);
	}

	public boolean isEmpty(List<LogLine> logLines) {
		return logLines == null || logLines.isEmpty();
	}

}
