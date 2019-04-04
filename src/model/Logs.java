package model;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logs implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final Logger logger = LoggerFactory.getLogger(Logs.class);

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

		if (isEmpty(logLines)) {
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

	@Override
	public String toString() {
		return "Logs [logLines=" + logLines + "]";
	}

}
