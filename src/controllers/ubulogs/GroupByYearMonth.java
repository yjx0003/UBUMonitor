package controllers.ubulogs;

import java.time.YearMonth;
import java.time.ZonedDateTime;
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
	public List<YearMonth> getRange(ZonedDateTime start, ZonedDateTime end) {
		List<YearMonth> list = new ArrayList<>();
		for (YearMonth yearMonth = YearMonth.from(start); 
				yearMonth.isBefore(YearMonth.from(end)); 
				yearMonth = yearMonth.plusMonths(1)) {
			
			list.add(yearMonth);
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
