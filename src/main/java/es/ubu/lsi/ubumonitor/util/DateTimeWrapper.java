package es.ubu.lsi.ubumonitor.util;

import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public class DateTimeWrapper {

	private String datePattern;
	private String timePattern;
	private DateTimeFormatter dateFormatter;
	private DateTimeFormatter timeFormatter;

	public DateTimeWrapper() {
		datePattern = DateTimeFormatterBuilder
				.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, Locale.getDefault())
				.toUpperCase();
		timePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT,
				IsoChronology.INSTANCE, Locale.getDefault());
		dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
				.withZone(ZoneId.systemDefault());
		timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
				.withZone(ZoneId.systemDefault());
		
	}

	public String getPattern() {
		return datePattern + " " + timePattern;
	}
	


	public String format(TemporalAccessor temporalAccessor) {
		return formatDate(temporalAccessor) + " " + formatTime(temporalAccessor);
	}

	public String formatDate(TemporalAccessor temporalAccessor) {
		return dateFormatter.format(temporalAccessor);
	}

	public String formatTime(TemporalAccessor temporalAccessor) {
		return timeFormatter.format(temporalAccessor);
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public String getTimePattern() {
		return timePattern;
	}

	public void setTimePattern(String timePattern) {
		this.timePattern = timePattern;
	}

	public DateTimeFormatter getDateFormatter() {
		return dateFormatter;
	}

	public void setDateFormatter(DateTimeFormatter dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	public DateTimeFormatter getTimeFormatter() {
		return timeFormatter;
	}

	public void setTimeFormatter(DateTimeFormatter timeFormatter) {
		this.timeFormatter = timeFormatter;
	}
}
