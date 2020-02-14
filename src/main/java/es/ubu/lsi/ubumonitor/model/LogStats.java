package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAll;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAmPm;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByDay;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByDayOfWeek;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByHour;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByYear;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByYearMonth;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByYearQuarter;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByYearWeek;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.TypeTimes;

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
		stastistics.add(new GroupByAmPm(logLines));
		stastistics.add(new GroupByDay(logLines));
		stastistics.add(new GroupByDayOfWeek(logLines));
		stastistics.add(new GroupByYearWeek(logLines));
		stastistics.add(new GroupByYearMonth(logLines));
		stastistics.add(new GroupByYearQuarter(logLines));
		stastistics.add(new GroupByYear(logLines));
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
