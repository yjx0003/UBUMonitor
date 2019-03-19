package model;

import java.util.HashMap;
import java.util.Map;


public enum DescriptionFormat {
	MOODLE(0), HTML(1), PLAIN(2), MARKDOWN(4);

	private int number;
	private static Map<Integer, DescriptionFormat> map = new HashMap<Integer, DescriptionFormat>();
	static {
		for (DescriptionFormat df : DescriptionFormat.values()) {
			map.put(df.number, df);
		}
	}

	DescriptionFormat(int number) {
		this.number = number;
	}

	public static DescriptionFormat get(int number) {
		return map.get(number);
	}

}