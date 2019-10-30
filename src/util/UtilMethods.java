package util;

import java.util.List;
import java.util.stream.Collectors;

public class UtilMethods {
	/**
	 * Escapa las comillas simples de un texto añadiendo un \
	 * 
	 * @param input
	 *            texto
	 * @return texto escapado
	 */
	public static String escapeJavaScriptText(String input) {
		return input.replaceAll("'", "\\\\'");
	}
	
	
	/**
	 * Convierte una lista de elementos en string separados por comas
	 * 
	 * @param datasets
	 * @return
	 */
	public static <E> String join(List<E> datasets) {
		return datasets.stream()
				.map(E::toString)
				.collect(Collectors.joining(", "));
	}
	
	/**
	 * Convierte una lista en string con los elementos entre comillas y separado por
	 * comas.
	 * 
	 * @param list
	 * @return
	 */
	public static String joinWithQuotes(List<String> list) {
		// https://stackoverflow.com/a/18229122
		return list.stream()
				.map(s -> "'" + s + "'")
				.collect(Collectors.joining(", "));
	}

	/**
	 * Removes reserved character from Windows filename. 
	 * @param stringToRemove need to remove reserved character
	 * @return without reserved character
	 */
	public static String removeReservedChar(String stringToRemove) {
		return stringToRemove.replaceAll(":|\\|/|\\?|\\*|\"|\\|", "");
	}
}
