package es.ubu.lsi.ubumonitor.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import es.ubu.lsi.ubumonitor.model.DescriptionFormat;

public class Parsers {

	private static final Parser MARKDOWN_PARSER = Parser.builder()
			.build();
	private static final TextContentRenderer MARKDOWN_TEXT_CONTENT_RENDERER = TextContentRenderer.builder()
			.build();
	private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder()
			.build();

	public static String parseHtmlToString(String html) {
		return Jsoup.parse(html)
				.text();
	}

	public static String parseMarkdownToString(String markdown) {
		Node node = MARKDOWN_PARSER.parse(markdown);
		return MARKDOWN_TEXT_CONTENT_RENDERER.render(node);
	}

	public static String parseToString(String text, DescriptionFormat descriptionFormat) {
		switch (descriptionFormat) {
		case HTML:
			return parseHtmlToString(text);

		case MARKDOWN:
			return parseMarkdownToString(text);

		default:
			return text;

		}
	}
	
	public static String parseToHtml(String text, DescriptionFormat descriptionFormat) {
		if(descriptionFormat==DescriptionFormat.MARKDOWN) {
			Node node = MARKDOWN_PARSER.parse(text);
			return HTML_RENDERER.render(node);
		}
		return text;
			 
	}
	
	public static String changeImages(String text, String alternativeImg) {
		Document doc = Jsoup.parse(text);
		Elements elements = doc.select("img[src]");
		elements.attr("src", alternativeImg);
		elements.attr("height", "64");
		elements.attr("width", "64");
		return doc.toString();
	}

}
