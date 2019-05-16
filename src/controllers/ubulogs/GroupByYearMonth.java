package controllers.ubulogs;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import model.EnrolledUser;
import model.LogLine;

public class GroupByYearMonth extends GroupByAbstract<YearMonth> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupByYearMonth(List<LogLine> logLines, Set<EnrolledUser> enrolledUsers) {
		super(logLines, enrolledUsers);
	}

	@Override
	public List<YearMonth> getRange(LocalDate start, LocalDate end) {
		List<YearMonth> list = new ArrayList<>();
		
		for (YearMonth yearMonthStart = YearMonth.from(start),yearMonthEnd=YearMonth.from(end);
				yearMonthStart.isBefore(yearMonthEnd) || yearMonthStart.equals(yearMonthEnd);
				yearMonthStart = yearMonthStart.plusMonths(1)) {
			
			list.add(yearMonthStart);
			
		}
		return list;
	}

	@Override
	public Function<LogLine, YearMonth> getGroupByFunction() {
		return LogLine::getYearMonth;
	}

	@Override
	public Function<YearMonth, String> getStringFormatFunction() {
		return YearMonth::toString;
	}

	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.YEAR_MONTH;
	}

}
