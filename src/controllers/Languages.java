package controllers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Lenguajes disponibles, añadir una entrada nueva de enumeracion cada vez que
 * se añada un fichero de idiomas en la carpeta resource
 * 
 * @author Yi Peng Ji
 *
 */
public enum Languages {

	SPANISH("Español", "es"),
	SPANISH_SPAIN("Español-España","es-ES"),
	ENGLISH("English", "en");
	
	
	

	private String lang;
	private String code;
	private Locale locale;

	private static Map<String, Languages> codeMap;
	private static Map<Locale, Languages> localeMap;

	static {
		codeMap = new HashMap<>();
		localeMap = new HashMap<>();
		for (Languages language : Languages.values()) {
			codeMap.put(language.code, language);
			localeMap.put(language.locale, language);
		}
	}

	/**
	 * Contructor de la enumeración
	 * @param lang nombre del lenguaje
	 * @param codeLanguage codigo de lenguaje según IETF BCP 47
	 */
	private Languages(String lang, String languajeTag) {
		this.lang = lang;
		this.locale = Locale.forLanguageTag(languajeTag);
	}


	/**
	 * Devuelve un lenguaje a partor del codigo de lengua, por ejemplo el Español
	 * seria "es".
	 * 
	 * @param code
	 *            codigo de la lengua
	 * @return elemento de la enumeracion asociada o el ingles si no existe.
	 */
	public static Languages getLanguageByTag(String code) {
		return codeMap.getOrDefault(Locale.forLanguageTag(code).getLanguage(), Languages.ENGLISH);
	}

	/**
	 * Devuelve el elemento de la enumeracion a partir del Locale.
	 * @param locale locale
	 * @return elmento de la enum, si no existe busca a partir del lenguage de locale
	 */
	public static Languages getLanguageByLocale(Locale locale) {
		// si no existe el locale con el lenguaje y pais, se devuelve el un locale solo
		// con el lenguaje
		return localeMap.getOrDefault(locale, getLanguageByTag(locale.getLanguage()));
	}

	/**
	 * Devuelve la lengua
	 * @return lengua
	 */
	public String getLanguage() {
		return lang;
	}

	/**
	 * Devuelve el locale
	 * @return locale
	 */
	public Locale getLocale() {
		return locale;

	}

	/**
	 * Devuelve el codigo de lengua
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return lang;
	}

}
