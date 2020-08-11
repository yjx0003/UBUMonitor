package es.ubu.lsi.ubumonitor.view.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ChartType {

	TOTAL_BAR(Tabs.LOGS, 0),
	STACKED_BAR(Tabs.LOGS, 1), 
	HEAT_MAP(Tabs.LOGS, 2),
	BOXPLOT_LOG(Tabs.LOGS, 23),
	VIOLIN_LOG(Tabs.LOGS, 24),
	BOXPLOT_LOG_TIME(Tabs.LOGS, 3),
	VIOLIN_LOG_TIME(Tabs.LOGS, 4), 
	CUM_LINE(Tabs.LOGS, 5),
	MEAN_DIFF(Tabs.LOGS, 6), 
	SCATTER(Tabs.LOGS, 7),
	SCATTER_USER(Tabs.LOGS, 8), 
	TABLE_LOG(Tabs.LOGS, 9),
	SESSION(Tabs.LOGS, 10),

	LINE(Tabs.GRADES, 11),
	RADAR(Tabs.GRADES, 12),
	BOXPLOT(Tabs.GRADES, 13),
	VIOLIN(Tabs.GRADES, 14),
	GRADE_REPORT_TABLE(Tabs.GRADES, 15),
	CALIFICATION_BAR(Tabs.GRADES, 16),

	ACTIVITIES_TABLE(Tabs.ACTIVITY_COMPLETION, 17),

	
	RISK_BAR(Tabs.RISK, 20),
	BUBBLE(Tabs.RISK, 21),
	BUBBLE_LOGARITHMIC(Tabs.RISK, 22),
	RISK_BAR_TEMPORAL(Tabs.RISK, 18),
	RISK_EVOLUTION(Tabs.RISK, 19),
	
	FORUM_TABLE(Tabs.FORUM, 23);
	

	private Tabs tab;
	private int id;

	public static final ChartType DEFAULT_LOGS = TOTAL_BAR;
	public static final ChartType DEFAULT_GRADES = LINE;
	public static final ChartType DEFAULT_ACTIVITY_COMPLETION = ACTIVITIES_TABLE;
	public static final ChartType DEFAULT_RISK = RISK_BAR;
	public static final ChartType DEFAULT_FORUM = FORUM_TABLE;

	private static Map<Integer, ChartType> map = new HashMap<>();

	private static final List<ChartType> NON_DEFAULT_VALUES = new ArrayList<>();
	private static final Set<ChartType> DEFAULT_VALUES = Stream
			.of(DEFAULT_LOGS, DEFAULT_GRADES, DEFAULT_ACTIVITY_COMPLETION, DEFAULT_RISK)
			.collect(Collectors.toSet());
	static {
		for (ChartType chartType : ChartType.values()) {
			map.put(chartType.id, chartType);
			if (!DEFAULT_VALUES.contains(chartType)) {
				NON_DEFAULT_VALUES.add(chartType);
			}
		}

	}

	private ChartType(Tabs tab, int id) {
		this.tab = tab;
		this.id = id;
	}

	public Tabs getTab() {
		return tab;
	}

	public int getId() {
		return id;
	}

	public static ChartType getById(int id) {
		return map.get(id);
	}
	
	public static List<ChartType> getNonDefaultValues() {
		return NON_DEFAULT_VALUES;
	}
	
	public static Set<ChartType> getDefaultValues(){
		return DEFAULT_VALUES;
	}

}