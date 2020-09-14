package es.ubu.lsi.ubumonitor.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.jsoup.Jsoup;

import es.ubu.lsi.ubumonitor.model.DescriptionFormat;

public class Parsers {

	private static final Parser MARKDOWN_PARSER = Parser.builder()
			.build();
	private static final TextContentRenderer MARKDOWN_TEXT_CONTENT_RENDERER = TextContentRenderer.builder()
			.build();

	public static String parseHTML(String html) {
		return Jsoup.parse(html)
				.text();
	}

	public static String parseMarkdown(String markdown) {
		Node node = MARKDOWN_PARSER.parse(markdown);
		return MARKDOWN_TEXT_CONTENT_RENDERER.render(node);
	}

	public static String parse(String text, DescriptionFormat descriptionFormat) {
		switch (descriptionFormat) {
		case HTML:
			return parseHTML(text);

		case MARKDOWN:
			return parseMarkdown(text);

		default:
			return text;

		}
	}

}
