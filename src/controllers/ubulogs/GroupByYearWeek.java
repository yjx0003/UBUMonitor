package controllers.ubulogs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.threeten.extra.YearWeek;

import model.EnrolledUser;
import model.LogLine;

/**
 * Agrupación de los logs por semana y año.
 * @author Yi Peng Ji
 *
 */
public class GroupByYearWeek extends GroupByAbstract<YearWeek> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor para agrupar la lineas de log en funcion de los usuarios.
	 * @param logLines las lineas de log
	 * @param enrolledUsers los usuarios que se quiere sacar los datos
	 */
	public GroupByYearWeek(List<LogLine> logLines, Set<EnrolledUser> enrolledUsers) {
		super(logLines, enrolledUsers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<YearWeek> getRange(LocalDate start, LocalDate end) {
		List<YearWeek> list = new ArrayList<>();

		for (YearWeek yearWeekStart = YearWeek.from(start), yearWeekEnd = YearWeek.from(end);
				yearWeekStart.isBefore(yearWeekEnd) || yearWeekStart.equals(yearWeekEnd);
				yearWeekStart = yearWeekStart.plusWeeks(1)) {
			
			list.add(yearWeekStart);
			
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<LogLine, YearWeek> getGroupByFunction() {
		return LogLine::getYearWeek;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<YearWeek, String> getStringFormatFunction() {
		return YearWeek::toString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.YEAR_WEEK;
	}

}
