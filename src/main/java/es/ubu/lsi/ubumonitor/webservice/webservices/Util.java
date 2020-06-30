package es.ubu.lsi.ubumonitor.webservice.webservices;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class Util {

	private Util() {
		throw new UnsupportedOperationException();
	}

	public static <T> void putIfNotNull(Map<String, String> parameters, String key, T value) {
		if (value != null)
			parameters.put(key, value.toString());
	}

	public static <T> void putIfNotNull(Map<String, String> parameters, String arrayName, String keyName,
			String valueName, Map<String, T> map) {

		if (map == null)
			return;

		int index = 0;
		for (Entry<String, T> entry : map.entrySet()) {
			String keyValue = entry.getKey();
			T value = entry.getValue();
			if (value != null) {
				parameters.put(arrayName + "[" + index + "][" + keyName + "]", keyValue);
				parameters.put(arrayName + "[" + index + "][" + valueName + "]", value.toString());
				index++;
			}
		}
	}

	public static <T> void putIfNotNull(Map<String, String> parameters, String arrayName, Collection<T> values) {
		int index = 0;

		if (values == null)
			return;

		for (T value : values) {
			if (value != null) {
				parameters.put(arrayName + "[" + index + "]", value.toString());
				index++;
			}
		}
	}

	public static <T> void putIfNotNull(Map<String, String> parameters, String arrayName, String keyName,
			Collection<T> values) {

		if (values == null)
			return;

		int index = 0;
		for (T value : values) {
			if (value != null) {
				parameters.put(arrayName + "[" + keyName + "][" + index + "]", value.toString());
				index++;
			}
		}
	}

	public static <T> void putIfNotNull(Map<String, String> parameters, String arrayName, String keyName, T value) {
		if (value != null) {
			parameters.put(arrayName + "[" + keyName + "]", value.toString());
		}
	}

	public static String booleanToBinary(Boolean bool) {
		if (bool == null)
			return null;
		return bool ? "1" : "0";
	}
}
