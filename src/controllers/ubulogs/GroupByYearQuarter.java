package controllers.ubulogs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.threeten.extra.YearQuarter;

import model.EnrolledUser;
import model.LogLine;

public class GroupByYearQuarter extends GroupByAbstract<YearQuarter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupByYearQuarter(List<LogLine> logLines, Set<EnrolledUser> enrolledUsers) {
		super(logLines, enrolledUsers);
	
	}

	@Override
	public List<YearQuarter> getRange(LocalDate start, LocalDate end) {
		List<YearQuarter> list = new ArrayList<>();

		for (YearQuarter YearQuarterStart = YearQuarter.from(start), YearQuarterEnd = YearQuarter.from(end);
				YearQuarterStart.isBefore(YearQuarterEnd) || YearQuarterStart.equals(YearQuarterEnd);
				YearQuarterStart = YearQuarterStart.plusQuarters(1)) {
			
			list.add(YearQuarterStart);

		}
		return list;
	}

	@Override
	public Function<LogLine, YearQuarter> getGroupByFunction() {
		return LogLine::getYearQuarter;
	}

	@Override
	public Function<YearQuarter, String> getStringFormatFunction() {
		return YearQuarter::toString;
	}

	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.YEAR_QUARTER;
	}
	
}
