package es.ubu.lsi.ubumonitor.util;

import java.util.StringJoiner;

public class JSObject {
	private StringJoiner stringJoiner;

	public JSObject() {
		stringJoiner = new StringJoiner(",", "{", "}");
	}

	public void putWithQuote(Object key, Object value) {
		stringJoiner.add(key + ":'" + UtilMethods.escapeJavaScriptText(value.toString()) + "'");
	}
	
	public void put(Object key, Object value) {
		stringJoiner.add(key + ":" + value);
	}

	@Override
	public String toString() {
		return stringJoiner.toString();
	}
}
