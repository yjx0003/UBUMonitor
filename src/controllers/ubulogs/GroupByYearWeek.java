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
		YearWeek yearWeek = YearWeek.from(start);
		list.add(yearWeek);
		for (; yearWeek.isBefore(YearWeek.from(end)); yearWeek = yearWeek.plusWeeks(1)) {
			list.add(yearWeek);
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
		return TypeTimes.WEEK_OF_THE_YEAR;
	}

}
