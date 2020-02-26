package es.ubu.lsi.ubumonitor.util;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class JSArray {
	private StringJoiner stringJoiner;

	public JSArray() {
		stringJoiner = new StringJoiner(",", "[", "]");
	}

	public void add(Object element) {
		stringJoiner.add(element.toString());
	}

	public void addWithQuote(Object element) {
		stringJoiner.add("'" + element + "'");
	}

	public void addAll(Collection<?> collection) {
		String string = collection.stream().map(Object::toString).collect(Collectors.joining(","));
		stringJoiner.add(string);
	}

	public void addAllWithQuote(Collection<?> collection) {
		String string = collection.stream().map(e -> "'" + UtilMethods.escapeJavaScriptText(e.toString()) + "'")
				.collect(Collectors.joining(","));
		stringJoiner.add(string);
	}

	@Override
	public String toString() {
		return stringJoiner.toString();
	}
}
