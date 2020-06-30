package es.ubu.lsi.ubumonitor.util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class JSObject extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public Object putWithQuote(String key, Object value) {
		if(value == null) {
			return put(key, null);
		}
		return put(key, "'" + UtilMethods.escapeJavaScriptText(value.toString()) + "'");
	}

	@Override
	public String toString() {
		StringJoiner stringJoiner = new StringJoiner(",", "{", "}");
		for (Map.Entry<String, Object> entry : entrySet()) {
			stringJoiner.add(entry.getKey() + ":" + entry.getValue());
		}
		return stringJoiner.toString();
	}
}
