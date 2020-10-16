package es.ubu.lsi.ubumonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import javafx.util.Pair;

public class UnitTest {

	@Test
	public void LuceneTest() throws IOException {

		String string = "<p>Hola y feliz año,</p>\r\n<p>Los Reyes Magos os han dejado un regalito ;)</p>\r\n<p>Tenéis disponible <strong>nueva versión de UBUMonitor</strong> para descargar. Hay bastantes cambios, así que hemos pasado a una versión 2.5.0, disponible en:</p>\r\n<p><a href=\"https://github.com/yjx0003/UBUMonitor/releases/tag/v2.5.0-stable\">https://github.com/yjx0003/UBUMonitor/releases/tag/v2.5.0-stable</a></p>\r\n<p>La nueva versión incluye algún <strong>cambio importante</strong>. En particular, si usáis el <strong>calificador de Moodle,</strong> se incluyen las vistas de<strong> boxplot y violín</strong> que mejoran mucho lo que ofrecía la versión previa. La visualización de la <strong>tabla de calificaciones</strong> ahora es completamente <strong>diferente.</strong> Por otro lado el <strong>heatmap</strong> ahora es <strong>ajustable</strong> en cuanto al máximo, permitiendo ajustar mejor los \"colores\".</p>\r\n<p>La lista de cambios más importantes en esta versión 2.5.0 son:</p>\r\n<ul><li>Refactorizado las gráficas del Calificador.</li>\r\n<li>Añadido nuevas gráficas del Calificador: BoxPlot, Violin y Tabla de calificaciones.</li>\r\n<li>Añadido pestañas nuevas en el listado de cursos: Recientes, Destacados, Todos, En progreso, Pasados y Futuros.</li>\r\n<li>Se guarda la último curso seleccionado en siguientes ejecuciones.</li>\r\n<li>Añadido una opción de Actualizar curso en la ventana de gráficas.</li>\r\n<li>Ahora está disponible la descarga de imágenes de gráficas en la opción Guardar imagen como...(nota: la tabla del calficador puede tardar).</li>\r\n<li>Al guardar la imagen de una gráfica se usa el útimo directorio donde se ha almacenado la anterior gráfica.</li>\r\n<li>Cambiado el nombre inicial de los ficheros de gráfica.</li>\r\n<li>Ahora al hacer click en un punto o elemento de la gráfica, automáticamente realiza un scroll en el listado de usuarios al usuario seleccionado.</li>\r\n</ul><p>La <strong>documentación en línea</strong> también ha sido modificada completamente, estando ahora disponible en:</p>\r\n<p><a href=\"https://ubumonitordocs.readthedocs.io/es/latest/\">https://ubumonitordocs.readthedocs.io/es/latest/</a></p>\r\n<p>Ahora incluye una <strong>navegación en árbol</strong> más cómoda y un <strong>buscador integrado</strong> para facilitar las consultas.</p>\r\n<p>Esperando que todas estas modificaciones os sean de ayuda. Para cualquier duda o cuestión, ya sabéis como contactar con nosotros.</p>\r\n<p>Saludos,</p>\r\n<p>Yi Peng Ji &amp; Raúl Marticorena</p>";
		String parse = Jsoup.parse(string)
				.text();
		System.out.println(parse);

		Parser parser = Parser.builder()
				.build();
		Node document = parser.parse(string);
		TextContentRenderer renderer = TextContentRenderer.builder()
				.build();
		System.out.println(renderer.render(document)); // "<p>This is <em>Sparta</em></p>\n"

		Analyzer analyzer = new SpanishAnalyzer();

		System.out.println(analyze(parse, analyzer));


	}

	@Test
	public void rankingTest() {
		Map<String, Integer> myMap = new HashMap<String, Integer>() {
			{
				put("a", 3);
				put("c", 4);
				put("d", 4);
				put("e", 1);
				put("f", 1);
			}
		};
		System.out.println(ranking(myMap));

	}

	public static <T, V extends Comparable<V>> Map<T, Integer> ranking(Map<T, V> map) {
		List<Pair<T, V>> scores = new ArrayList<>(map.size());
		map.forEach((k, v) -> scores.add(new Pair<>(k, v)));
		scores.sort(Comparator.comparing(Pair::getValue));

		Map<T, Integer> ranking = new LinkedHashMap<>();
		ranking.put(scores.get(0).getKey(), 1);
		int actualRank = 1;
		for (int i = 1; i < scores.size(); i++) {
			actualRank = scores.get(i).getValue().equals(scores.get(i - 1).getValue()) ? actualRank : i + 1;
			ranking.put(scores.get(i).getKey(), actualRank);
		}
		return ranking;
	}

	@Test
	public void LuceneEnglishTest() throws IOException {
		String string = "Here you can find activities to practise your reading skills. Reading will help you to improve your understanding of the language and build your vocabulary.\r\n"
				+ "\r\n"
				+ "The self-study lessons in this section are written and organised according to the levels of the Common European Framework of Reference for languages (CEFR). There are different types of texts and interactive exercises that practise the reading skills you need to do well in your studies, to get ahead at work and to communicate in English in your free time.";
		System.out.println(analyze(string, new EnglishAnalyzer()));
	}

	public List<String> analyze(String text, Analyzer analyzer) throws IOException {
		List<String> result = new ArrayList<String>();
		try (TokenStream tokenStream = analyzer.tokenStream(null, text)) {
			CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				result.add(attr.toString());
			}
		}

		return result;
	}
}