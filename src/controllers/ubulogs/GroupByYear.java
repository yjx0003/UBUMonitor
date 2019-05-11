package controllers.ubulogs;

import java.time.Year;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import model.EnrolledUser;
import model.LogLine;

public class GroupByYear extends GroupByAbstract<Year> {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupByYear(List<LogLine> logLines, Set<EnrolledUser> enrolledUsers) {
		super(logLines, enrolledUsers);
	}

	@Override
	public List<Year> getRange(ZonedDateTime start, ZonedDateTime end) {
		List<Year> list = new ArrayList<>();

		for (Year yearStart = Year.from(start), yearEnd = Year.from(end);
				yearStart.isBefore(yearEnd) || yearStart.equals(yearEnd);
				yearStart = yearStart.plusYears(1)) {
			
			list.add(yearStart);
			
		}
		return list;
	}

	@Override
	public Function<LogLine, Year> getGroupByFunction() {
		return LogLine::getYear;
	}

	@Override
	public Function<Year, String> getStringFormatFunction() {
		return Year::toString;
	}

	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.YEAR;
	}

}
