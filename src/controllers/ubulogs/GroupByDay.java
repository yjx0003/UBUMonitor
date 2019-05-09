package controllers.ubulogs;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import model.EnrolledUser;
import model.LogLine;

public class GroupByDay extends GroupByAbstract<ZonedDateTime> {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
	
	
	public GroupByDay(List<LogLine> logLines, Set<EnrolledUser> enrolledUsers) {
		super(logLines, enrolledUsers);
		
	}

	@Override
	public List<ZonedDateTime> getRange(ZonedDateTime start, ZonedDateTime end) {
		List<ZonedDateTime> list = new ArrayList<>();
		for (ZonedDateTime l = start; l.isBefore(end); l = l.plusDays(1)) {
			list.add(l);
		}
		return list;
	}
	

	@Override
	public Function<LogLine, ZonedDateTime> getGroupByFunction() {
		return LogLine::getTime;
	}

	@Override
	public Function<ZonedDateTime, String> getStringFormatFunction() {
		return l->l.format(DATE_TIME_FORMATTER);
	}

	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.DAY;
	}



}
