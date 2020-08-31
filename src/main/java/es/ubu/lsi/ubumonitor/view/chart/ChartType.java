package es.ubu.lsi.ubumonitor.view.chart;

import java.util.HashMap;
import java.util.HashSet;
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

	RISK_BAR(Tabs.RISK, 20), BUBBLE(Tabs.RISK, 21),
	BUBBLE_LOGARITHMIC(Tabs.RISK, 22),
	RISK_BAR_TEMPORAL(Tabs.RISK, 18),
	RISK_EVOLUTION(Tabs.RISK, 19),

	FORUM_BAR(Tabs.FORUM, 24),
	FORUM_NETWORK(Tabs.FORUM, 25),
	FORUM_TABLE(Tabs.FORUM, 23),
	
	CALENDAR_EVENT_TIMELINE(Tabs.CALENDAR_EVENT, 26);

	private Tabs tab;
	private int id;

	private static Map<Integer, ChartType> map = new HashMap<>();

	private static final Set<ChartType> NON_DEFAULT_VALUES = new HashSet<>();
	private static final Set<ChartType> DEFAULT_VALUES = new HashSet<>();
	static {
		Set<Tabs> tabs = Stream.of(Tabs.values())
				.collect(Collectors.toSet());

		for (ChartType chartType : ChartType.values()) {

			for (Tabs tab : Tabs.values()) {
				if (tabs.contains(chartType.getTab())) {
					DEFAULT_VALUES.add(chartType);
					tabs.remove(tab);
				} else {
					NON_DEFAULT_VALUES.add(chartType);
				}

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