package controllers.ubulogs;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import controllers.I18n;
import model.LogLine;

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
		return Arrays.asList(true);
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

}
