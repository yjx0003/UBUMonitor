package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.GroupByDay;
import controllers.ubulogs.GroupByYear;
import controllers.ubulogs.GroupByYearMonth;
import controllers.ubulogs.GroupByYearQuarter;
import controllers.ubulogs.GroupByYearWeek;

public class LogStats implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<GroupByAbstract<?>> stastistics;
	
	public LogStats(List<LogLine> logLines,Set<EnrolledUser> users ) {
		stastistics=new ArrayList<>();
		
		stastistics.add(new GroupByDay(logLines,users));
		stastistics.add(new GroupByYearWeek(logLines,users));
		stastistics.add(new GroupByYearMonth(logLines,users));
		stastistics.add(new GroupByYearQuarter(logLines,users));
		stastistics.add(new GroupByYear(logLines, users));

		
	}

	public List<GroupByAbstract<?>> getList() {
		return stastistics;
	}
	
	
	
	


}
