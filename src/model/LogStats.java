package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.GroupByAll;
import controllers.ubulogs.GroupByDay;
import controllers.ubulogs.GroupByDayOfWeek;
import controllers.ubulogs.GroupByHour;
import controllers.ubulogs.GroupByYearMonth;
import controllers.ubulogs.GroupByYearWeek;
import controllers.ubulogs.TypeTimes;

/**
 * Clase contenedora que crea las instancias de las distintas agrupaciónes por
 * fechas.
 * 
 * @author Yi Peng Ji
 *
 */
public class LogStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final TypeTimes DEFAULT_TYPE = TypeTimes.YEAR_WEEK;

	private List<GroupByAbstract<?>> stastistics;

	public LogStats(List<LogLine> logLines) {
		stastistics = new ArrayList<>();

		stastistics.add(new GroupByHour(logLines));

		stastistics.add(new GroupByDay(logLines));
		stastistics.add(new GroupByDayOfWeek(logLines));
		stastistics.add(new GroupByYearWeek(logLines));
		stastistics.add(new GroupByYearMonth(logLines));
		stastistics.add(new GroupByAll(logLines));

	}

	public List<GroupByAbstract<?>> getList() {
		return stastistics;
	}

	public GroupByAbstract<?> getByType(TypeTimes typeTimes) {
		for (GroupByAbstract<?> groupBy : stastistics) {
			if (groupBy.getTypeTime().equals(typeTimes)) {
				return groupBy;
			}
		}
		return null;
	}

	public GroupByAbstract<?> getByType() {
		return getByType(DEFAULT_TYPE);
	}

}
