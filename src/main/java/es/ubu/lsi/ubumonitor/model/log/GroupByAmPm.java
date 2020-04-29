package es.ubu.lsi.ubumonitor.model.log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.threeten.extra.AmPm;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * Agrupa los logs en AM o PM
 * @author Yi Peng Ji
 *
 */
public class GroupByAmPm extends GroupByAbstract<AmPm> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final List<AmPm> AM_PM = Arrays.asList(AmPm.AM, AmPm.PM);

	/**
	 * Constructor que recibe las lineas de log
	 * @param logLines lineas de log
	 */
	public GroupByAmPm(List<LogLine> logLines) {
		super(logLines);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AmPm> getRange(LocalDate start, LocalDate end) {
		return AM_PM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LogLine, AmPm> getGroupByFunction() {
		return LogLine::getAmPm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<AmPm, String> getStringFormatFunction() {
		return AmPm::toString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.AM_PM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean useDatePicker() {
		return false;
	}

	@Override
	public List<LocalDateTime> getRangeLocalDateTime(LocalDate start, LocalDate end) {
		return Collections.emptyList();
	}

}
