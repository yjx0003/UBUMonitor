package controllers.ubulogs;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import model.EnrolledUser;
import model.LogLine;

public class GroupByDay extends GroupByAbstract<LocalDate> {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
	
	
	public GroupByDay(List<LogLine> logLines, Set<EnrolledUser> enrolledUsers) {
		super(logLines, enrolledUsers);
		
	}
	@Override
	public List<LocalDate> getRange(ZonedDateTime start, ZonedDateTime end) {
		List<LocalDate> list = new ArrayList<>();
		
		LocalDate l = start.toLocalDate();
		list.add(l);
		for (; l.isBefore(end.toLocalDate()); l = l.plusDays(1)) {
			list.add(l);
		}
		return list;
	}
	

	@Override
	public Function<LogLine, LocalDate> getGroupByFunction() {
		return LogLine::getLocalDate;
	}

	@Override
	public Function<LocalDate, String> getStringFormatFunction() {
		return l->l.format(DATE_TIME_FORMATTER);
	}

	@Override
	public TypeTimes getTypeTime() {
		return TypeTimes.DAY;
	}



}
