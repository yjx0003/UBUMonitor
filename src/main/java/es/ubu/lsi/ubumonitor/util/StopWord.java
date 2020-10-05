package es.ubu.lsi.ubumonitor.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public enum StopWord {

	ENGLISH("en", "/stopWords/english.txt"),
	SPANISH("es", "/stopWords/spanish.txt"),
	CATALAN("ca", "/stopWords/catalan.txt");
	

	private static final Map<String, StopWord> STOP_WORDS = new HashMap<>();

	private Locale locale;

	private Set<String> stopWords;

	static {
		for (StopWord stopWord : StopWord.values()) {
			STOP_WORDS.put(stopWord.locale.getLanguage(), stopWord);
		}
	}

	private StopWord(String languageTag, String path) {
		locale = Locale.forLanguageTag(languageTag);

		String word;
		stopWords = new HashSet<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream(path), StandardCharsets.UTF_8))) {
			while ((word = br.readLine()) != null) {
				stopWords.add(word);
			}
		} catch (IOException e) {
			throw new IllegalStateException("Shoudnt happen because are inside jar", e);
		}

	}

	public static StopWord getStopWordValues(Locale locale){
		return STOP_WORDS.getOrDefault(locale.getLanguage(), ENGLISH);
	}
	
	@Override
	public String toString() {
		return locale.getDisplayLanguage(locale) + " (" + locale.getLanguage() + ")";
	}

	public Collection<String> getValues() {
		return stopWords;
	}
}
