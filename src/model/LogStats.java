package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.GroupByAll;
import controllers.ubulogs.GroupByAmPm;
import controllers.ubulogs.GroupByDay;
import controllers.ubulogs.GroupByDayOfWeek;
import controllers.ubulogs.GroupByHour;
import controllers.ubulogs.GroupByYear;
import controllers.ubulogs.GroupByYearMonth;
import controllers.ubulogs.GroupByYearQuarter;
import controllers.ubulogs.GroupByYearWeek;

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

	private List<GroupByAbstract<?>> stastistics;

	public LogStats(List<LogLine> logLines) {
		stastistics = new ArrayList<>();

		stastistics.add(new GroupByDay(logLines));
		stastistics.add(new GroupByHour(logLines));
		stastistics.add(new GroupByAmPm(logLines));
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

}
