package es.ubu.lsi.ubumonitor.model.log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * Agrupa los logs por dia.
 * @author Yi Peng Ji
 *
 */
public class GroupByDay extends GroupByAbstract<LocalDate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

	/**
	 * Constructor para agrupar la lineas de log en funcion de los usuarios.
	 * 
	 * @param logLines
	 *            las lineas de log
	 */
	public GroupByDay(List<LogLine> logLines) {
		super(logLines);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LocalDate> getRange(LocalDate start, LocalDate end) {
		List<LocalDate> list = new ArrayList<>();

		for (LocalDate lStart = start; !lStart.isAfter(end); lStart = lStart.plusDays(1)) {
			list.add(lStart);
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LogLine, LocalDate> getGroupByFunction() {
		return LogLine::getLocalDate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LocalDate, String> getStringFormatFunction() {
		return l -> l.format(DATE_TIME_FORMATTER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.DAY;
	}

	@Override
	public boolean useDatePicker() {
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<LocalDate, List<LocalDateTime>> getRangeLocalDateTime(LocalDate start, LocalDate end) {
		Map<LocalDate, List<LocalDateTime>> map = new HashMap<>();

		for (LocalDate lStart = start; !lStart.isAfter(end); lStart = lStart.plusDays(1)) {
			map.computeIfAbsent(lStart, k-> new ArrayList<>()).add(lStart.atStartOfDay());
		}
		return map;
	}

}
