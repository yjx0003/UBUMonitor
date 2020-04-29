package es.ubu.lsi.ubumonitor.model.log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.util.I18n;

/**
 * Agrupa todos logs en solo uno.
 * @author Yi Peng Ji
 *
 */
public class GroupByAll extends GroupByAbstract<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final List<Boolean> LIST= Arrays.asList(true);

	/**
	 * Contructor que recibe las lineas de logs.
	 * @param logLines lineas de log
	 */
	public GroupByAll(List<LogLine> logLines) {
		super(logLines);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Boolean> getRange(LocalDate start, LocalDate end) {
		return LIST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LogLine, Boolean> getGroupByFunction() {
		return l -> true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<Boolean, String> getStringFormatFunction() {
		return b -> I18n.get("choiceBox.ALL");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.ALL;
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
