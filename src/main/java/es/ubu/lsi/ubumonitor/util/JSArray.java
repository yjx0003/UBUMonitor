package es.ubu.lsi.ubumonitor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringJoiner;

public class JSArray extends ArrayList<Object> {

	private static final long serialVersionUID = 1L;

	public boolean addWithQuote(Object object) {
		return add("'" + UtilMethods.escapeJavaScriptText(object.toString()) + "'");
	}

	public void addAllWithQuote(Collection<?> collection) {
		for (Object object : collection) {
			addWithQuote(object);
		}
	}

	@Override
	public String toString() {
		StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
		for (Object o : this) {
			stringJoiner.add(o == null ? null : o.toString());
		}
		return stringJoiner.toString();
	}
}