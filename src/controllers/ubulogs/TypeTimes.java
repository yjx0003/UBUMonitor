package controllers.ubulogs;

import java.util.function.Function;

import model.LogLine;

public enum TypeTimes {

	DAY(LogLine::getLocalDate),
	YEAR_MONTH(LogLine::getYearMonth),
	WEEK_OF_THE_YEAR(LogLine::getWeekOfYear);

	private Function<LogLine, ?> function;

	private TypeTimes(Function<LogLine, ?> function) {
		this.function = function;
	}

	public Function<LogLine, ?> getFunction() {
		return function;
	}

}
