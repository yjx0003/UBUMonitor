package es.ubu.lsi.ubumonitor.controllers.ubulogs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.threeten.extra.YearQuarter;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * Agrupa los logs por trimestre y año.
 * 
 * @author Yi Peng Ji
 *
 */
public class GroupByYearQuarter extends GroupByAbstract<YearQuarter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor para agrupar la lineas de log en funcion de los usuarios.
	 * 
	 * @param logLines
	 *            las lineas de log
	 */
	public GroupByYearQuarter(List<LogLine> logLines) {
		super(logLines);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<YearQuarter> getRange(LocalDate start, LocalDate end) {
		List<YearQuarter> list = new ArrayList<>();

		for (YearQuarter YearQuarterStart = YearQuarter.from(start), YearQuarterEnd = YearQuarter.from(end);
				!YearQuarterStart.isAfter(YearQuarterEnd);
				YearQuarterStart = YearQuarterStart.plusQuarters(1)) {

			list.add(YearQuarterStart);

		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LogLine, YearQuarter> getGroupByFunction() {
		return LogLine::getYearQuarter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<YearQuarter, String> getStringFormatFunction() {
		//return yearQuarter -> yearQuarter.getQuarter().getDisplayName(TextStyle.FULL, Locale.getDefault());
		return YearQuarter::toString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.YEAR_QUARTER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean useDatePicker() {
		return true;
	}
}
