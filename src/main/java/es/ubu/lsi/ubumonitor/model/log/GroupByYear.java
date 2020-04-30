package es.ubu.lsi.ubumonitor.model.log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * Agrupa los logs por año.
 * @author Yi Peng Ji
 *
 */
public class GroupByYear extends GroupByAbstract<Year> {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor para agrupar la lineas de log en funcion de los usuarios.
	 * @param logLines las lineas de log
	 */
	public GroupByYear(List<LogLine> logLines) {
		super(logLines);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Year> getRange(LocalDate start, LocalDate end) {
		List<Year> list = new ArrayList<>();

		for (Year yearStart = Year.from(start),
				yearEnd = Year.from(end);
				!yearStart.isAfter(yearEnd);
				yearStart = yearStart.plusYears(1)) {
			
			list.add(yearStart);
			
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LogLine, Year> getGroupByFunction() {
		return LogLine::getYear;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<Year, String> getStringFormatFunction() {
		return Year::toString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.YEAR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean useDatePicker() {
		return true;
	}

	@Override
	public Map<Year, List<LocalDateTime>> getRangeLocalDateTime(LocalDate start, LocalDate end) {
		return Collections.emptyMap();
	}
}
