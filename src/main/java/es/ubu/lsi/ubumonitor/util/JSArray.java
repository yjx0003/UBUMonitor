package es.ubu.lsi.ubumonitor.util;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class JSArray {

	private StringJoiner stringJoiner;
	private int size;

	public JSArray() {
		stringJoiner = new StringJoiner(",", "[", "]");
	}

	public void add(Object element) {
		stringJoiner.add(element.toString());
		size++;
	}

	public void addWithQuote(Object element) {
		stringJoiner.add("'" + UtilMethods.escapeJavaScriptText(element.toString()) + "'");
		size++;
	}

	public void addAll(Collection<?> collection) {
		if (!collection.isEmpty()) {
			String string = collection.stream().map(Object::toString).collect(Collectors.joining(","));
			stringJoiner.add(string);
			size += collection.size();
		}
	}

	public void addAllWithQuote(Collection<?> collection) {
		if (!collection.isEmpty()) {
			String string = collection.stream().map(e -> "'" + UtilMethods.escapeJavaScriptText(e.toString()) + "'")
					.collect(Collectors.joining(","));
			stringJoiner.add(string);
			size += collection.size();
		}
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public String toString() {
		return stringJoiner.toString();
	}
}
