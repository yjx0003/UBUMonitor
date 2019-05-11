package controllers.ubulogs;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.threeten.extra.YearWeek;

import model.EnrolledUser;
import model.LogLine;

public class GroupByYearWeek extends GroupByAbstract<YearWeek> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupByYearWeek(List<LogLine> logLines, Set<EnrolledUser> enrolledUsers) {
		super(logLines, enrolledUsers);
	}

	@Override
	public List<YearWeek> getRange(ZonedDateTime start, ZonedDateTime end) {
		List<YearWeek> list = new ArrayList<>();

		for (YearWeek yearWeekStart = YearWeek.from(start), yearWeekEnd = YearWeek.from(end);
				yearWeekStart.isBefore(yearWeekEnd) || yearWeekStart.equals(yearWeekEnd);
				yearWeekStart = yearWeekStart.plusWeeks(1)) {
			
			list.add(yearWeekStart);
			
		}
		return list;
	}

	@Override
	public Function<LogLine, YearWeek> getGroupByFunction() {
		return LogLine::getYearWeek;
	}

	@Override
	public Function<YearWeek, String> getStringFormatFunction() {
		return YearWeek::toString;
	}

	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.YEAR_WEEK;
	}

}
