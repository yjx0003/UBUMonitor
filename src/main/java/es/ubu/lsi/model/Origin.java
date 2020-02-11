package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeración que indica el origen de un log.
 * @author Yi Peng Ji
 *
 */
public enum Origin {
	
	ORIGIN_NOT_DEFINED("Origin not defined"),
	
	CLI("cli"),
	WEB("web"),
	WS("ws");
	
	private String name;
	private static Map<String, Origin> originByString = new HashMap<>();
	static {
		for (Origin value : Origin.values()) {
			originByString.put(value.name, value);
		}
	}

	private Origin(String name) {
		this.name = name;
	}

	public static Origin get(String name) {
		return originByString.getOrDefault(name, ORIGIN_NOT_DEFINED);
	}
	
}
