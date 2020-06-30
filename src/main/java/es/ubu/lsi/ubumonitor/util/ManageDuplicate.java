package es.ubu.lsi.ubumonitor.util;

import java.util.HashMap;
import java.util.Map;

public class ManageDuplicate {
	private Map<String, Integer> counter;

	public ManageDuplicate() {
		counter = new HashMap<>();
	}

	public String getValue(String string) {
		int count = counter.computeIfAbsent(string, k -> 0);
		counter.put(string, count + 1);
		if (count == 0) {
			return string;
		}

		return string + " (" + count + ")";

	}
}
