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

	SPANISH("Español", "es",""),
	SPANISH_SPAIN("Español-España","es","ES"),
	ENGLISH("English", "en","");

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

	Languages(String lang, String codeLanguage, String codeCountry) {
		this.lang = lang;
		this.locale = new Locale(codeLanguage, codeCountry);
		this.code = locale.getLanguage();
	}

	public static Languages getLanguageByCode(String code) {
		return codeMap.getOrDefault(new Locale(code).getLanguage(),Languages.ENGLISH);
	}

	public static Languages getLanguageByLocale(Locale locale) {
		// si no existe el locale con el lenguaje y pais, se devuelve el un locale solo
		// con el lenguaje
		return localeMap.getOrDefault(locale, getLanguageByCode(locale.getLanguage()));
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
