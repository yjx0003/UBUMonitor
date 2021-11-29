package es.ubu.lsi.ubumonitor.view.chart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public enum ChartType {

	TOTAL_BAR(Tabs.LOGS, 0), 
	HEAT_MAP(Tabs.LOGS, 2), 
	BOXPLOT_LOG(Tabs.LOGS, 23),
	VIOLIN_LOG(Tabs.LOGS, 24),
	BOXPLOT_LOG_TIME(Tabs.LOGS, 3),
	VIOLIN_LOG_TIME(Tabs.LOGS, 4),
	CUM_LINE(Tabs.LOGS, 5),
	MEAN_DIFF(Tabs.LOGS, 6),
	SCATTER(Tabs.LOGS, 7), 
	SCATTER_USER(Tabs.LOGS, 8), 
	STACKED_BAR(Tabs.LOGS, 1), 
	SESSION(Tabs.LOGS, 10),
	TABLE_LOG(Tabs.LOGS, 9),

	LINE(Tabs.GRADES, 11),
	RADAR(Tabs.GRADES, 12), 
	BOXPLOT(Tabs.GRADES, 13),
	VIOLIN(Tabs.GRADES, 14),
	GRADE_REPORT_TABLE(Tabs.GRADES, 15),
	CALIFICATION_BAR(Tabs.GRADES, 16),
	PARALLEL_CATEGORY(Tabs.GRADES, 40),

	ACTIVITIES_TABLE(Tabs.ACTIVITY_COMPLETION, 17),

	RISK_BAR(Tabs.RISK, 20),
	BUBBLE(Tabs.RISK, 21), 
	BUBBLE_LOGARITHMIC(Tabs.RISK, 22), 
	RISK_BAR_TEMPORAL(Tabs.RISK, 18),
	RISK_EVOLUTION(Tabs.RISK, 19),

	FORUM_BAR(Tabs.FORUM, 26), 
	FORUM_USER_POST_BAR(Tabs.FORUM, 30),
	FORUM_NETWORK(Tabs.FORUM, 27),
	FORUM_POSTS(Tabs.FORUM, 33),
	FORUM_TREE_MAP(Tabs.FORUM, 31),
	FORUM_TREE_MAP_USER(Tabs.FORUM, 32),
	FORUM_WORD_CLOUD(Tabs.FORUM, 29),
	FORUM_TABLE(Tabs.FORUM, 25),

	CALENDAR_EVENT_TIMELINE(Tabs.CALENDAR_EVENT, 28),

	RANKING_TABLE(Tabs.MULTI, 34), 
	POINTS_TABLE(Tabs.MULTI, 36),
	BUBBLE_COMPARISON(Tabs.MULTI, 35),
	
	ENROLLMENT_BAR(Tabs.ENROLLMENT, 37),
	ENROLLMENT_COURSE_NETWORK(Tabs.ENROLLMENT, 39),
	ENROLLMENT_SANKEY(Tabs.ENROLLMENT, 38);
	
	// next id 41

	private Tabs tab;
	private int id;

	private static Map<Integer, ChartType> map = new HashMap<>();

	private static final Set<ChartType> NON_DEFAULT_VALUES = new LinkedHashSet<>();
	private static final Set<ChartType> DEFAULT_VALUES = new HashSet<>();
	static {

		Set<Tabs> tabs = new HashSet<>();

		for (ChartType chartType : ChartType.values()) {
	
			if (tabs.contains(chartType.getTab())) {
				NON_DEFAULT_VALUES.add(chartType);
			} else {
				DEFAULT_VALUES.add(chartType);
				tabs.add(chartType.tab);
			}
		}

		for (ChartType chartType : ChartType.values()) {
			map.put(chartType.id, chartType);
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

	public static Set<ChartType> getNonDefaultValues() {
		return NON_DEFAULT_VALUES;
	}

	public static Set<ChartType> getDefaultValues() {
		return DEFAULT_VALUES;
	}

	public static ChartType getDefault(Tabs tab) {
		
		for (ChartType chartType : DEFAULT_VALUES) {
			if (chartType.getTab()
					.equals(tab)) {
				return chartType;
			}
		}
		
		return null;
	}

}