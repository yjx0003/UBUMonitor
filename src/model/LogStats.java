package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.GroupByDay;
import controllers.ubulogs.GroupByYearMonth;
import controllers.ubulogs.GroupByYearWeek;

public class LogStats implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<GroupByAbstract<?>> stastistics;
	
	public LogStats(List<LogLine> logLines,Set<EnrolledUser> users ) {
		stastistics=new ArrayList<>();
		
		GroupByDay groupByDay=new GroupByDay(logLines,users);
		GroupByYearWeek groupByYearWeek=new GroupByYearWeek(logLines,users);
		GroupByYearMonth groupByYearMonth=new GroupByYearMonth(logLines,users);
		
		stastistics.add(groupByDay);
		stastistics.add(groupByYearWeek);
		stastistics.add(groupByYearMonth);
		
	}

	public List<GroupByAbstract<?>> getList() {
		return stastistics;
	}
	
	
	
	


}
