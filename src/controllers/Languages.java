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
	ENGLISH("English", "en");

	private String lang;
	private String code;
	private Locale locale;

	private static Map<String, Languages> codeMap;
	private static Map<Locale,Languages> localeMap;

	static {
		codeMap = new HashMap<>();
		localeMap=new HashMap<>();
		for (Languages language : Languages.values()) {
			codeMap.put(language.code, language);
			localeMap.put(language.locale, language);
		}
	}

	Languages(String lang, String code) {
		this.lang = lang;
		this.code = code;
		this.locale = new Locale(code);
	}

	public static Languages getLanguageByCode(String code) {
		return codeMap.get(code);
	}
	
	public static Languages getLanguageByLocale(Locale locale) {
		return localeMap.get(locale);
	}

	public String getLanguage() {
		return lang;
	}

	public Locale getLocale() {
		return locale;

	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return lang;
	}

}
