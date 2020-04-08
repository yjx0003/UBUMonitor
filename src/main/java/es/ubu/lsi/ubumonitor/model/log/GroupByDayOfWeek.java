package es.ubu.lsi.ubumonitor.model.log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * Agrupa los logs por dia de la semana.
 * @author Yi Peng Ji
 *
 */
public class GroupByDayOfWeek extends GroupByAbstract<DayOfWeek> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final List<DayOfWeek> DAYS_OF_WEEK = Arrays.asList(DayOfWeek.values());

	public GroupByDayOfWeek(List<LogLine> logLines) {
		super(logLines);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DayOfWeek> getRange(LocalDate start, LocalDate end) {
		return DAYS_OF_WEEK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LogLine, DayOfWeek> getGroupByFunction() {
		return LogLine::getDayOfWeek;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<DayOfWeek, String> getStringFormatFunction() {
		return d -> d.getDisplayName(TextStyle.FULL, Locale.getDefault());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.DAY_OF_WEEK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean useDatePicker() {
		return false;
	}

}
