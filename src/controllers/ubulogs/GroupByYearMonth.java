package controllers.ubulogs;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import model.LogLine;

/**
 * Agrupa los logs por mes y año
 * @author Yi Peng Ji
 *
 */
public class GroupByYearMonth extends GroupByAbstract<YearMonth> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor para agrupar la lineas de log en funcion de los usuarios.
	 * @param logLines las lineas de log
	 * @param enrolledUsers los usuarios que se quiere sacar los datos
	 */
	public GroupByYearMonth(List<LogLine> logLines) {
		super(logLines);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<YearMonth> getRange(LocalDate start, LocalDate end) {
		List<YearMonth> list = new ArrayList<>();
		
		for (YearMonth yearMonthStart = YearMonth.from(start),yearMonthEnd=YearMonth.from(end);
				!yearMonthStart.isAfter(yearMonthEnd);
				yearMonthStart = yearMonthStart.plusMonths(1)) {
			
			list.add(yearMonthStart);
			
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LogLine, YearMonth> getGroupByFunction() {
		return LogLine::getYearMonth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<YearMonth, String> getStringFormatFunction() {
		return YearMonth::toString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.YEAR_MONTH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean useDatePicker() {
		return true;
	}
}
