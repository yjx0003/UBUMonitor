package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.Background;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.MaskImage;
import es.ubu.lsi.ubumonitor.util.Parsers;
import es.ubu.lsi.ubumonitor.util.StopWord;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.WordCloudChart;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class ForumWordCloud extends WordCloudChart {

	private ListView<CourseModule> listViewForum;
	private WebView webView;

	public ForumWordCloud(MainController mainController, ListView<CourseModule> listViewForum, WebView webView) {
		super(mainController, ChartType.FORUM_WORD_CLOUD);
		this.listViewForum = listViewForum;
		this.webView = webView;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<WordFrequency> wordCount = wordCount();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("word", "freq"))) {
			for (WordFrequency wordFrequency : wordCount) {

				printer.printRecord(wordFrequency.getWord(), wordFrequency.getFrequency());

			}
		}
	}

	@Override
	public JSObject getOptions(JSObject jsObject) {

		return jsObject;
	}

	@Override
	public void update() {
		
		List<WordFrequency> wordCount = wordCount();

		MaskImage maskImage =  getConfigValue("backGroundImage");

		Dimension dimension;
		Background background;
		if (maskImage != MaskImage.RECTANGLE) {
			try (InputStream in = getClass().getResourceAsStream(maskImage.getPath())){

				background = new PixelBoundryBackground(in);
				dimension = new Dimension(maskImage.getWidth(), maskImage.getHeight() - 30);
				
			} catch (IOException e) {
				dimension = new Dimension((int) webView.getWidth(), (int) webView.getHeight() - 30);
				background = new RectangleBackground(dimension);
			}
		} else {
			dimension = new Dimension((int) webView.getWidth(), (int) webView.getHeight() - 30);
			background = new RectangleBackground(dimension);
		}

		WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

		wordCloud.setColorPalette(new ColorPalette(new Color(0xed1941), new Color(0xf26522), new Color(0x845538),
				new Color(0x8a5d19), new Color(0x7f7522), new Color(0x5c7a29), new Color(0x1d953f), new Color(0x007d65),
				new Color(0x65c294)));
		wordCloud.setBackground(background);
		wordCloud.setPadding(getConfigValue("padding"));
	
		wordCloud.setFontScalar(new LinearFontScalar(getConfigValue("minFont"), getConfigValue("maxFont")));
		wordCloud.setBackgroundColor(
				UtilMethods.toAwtColor(getConfigValue("chartBackgroundColor")));

		wordCloud.build(wordCount);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		wordCloud.writeToStreamAsPNG(output);
		byte[] outputByte = output.toByteArray();
		String img = Base64.getEncoder()
				.encodeToString(outputByte);

		JSObject options = getOptions();

		webViewChartsEngine.executeScript("updateWordCloud('data:image/png;base64," + img + "'," + options + ")");
	}



	public List<WordFrequency> wordCount() {
		List<EnrolledUser> selectedEnrolledUsers = getSelectedEnrolledUser();
		List<DiscussionPost> discussionPosts = getSelectedDiscussionPosts(selectedEnrolledUsers);

		List<String> texts = new ArrayList<>();
		for (DiscussionPost discussionPost : discussionPosts) {

			String parsed = Parsers.parse(discussionPost.getMessage(), discussionPost.getMessageformat());
			texts.add(parsed);
		}

		FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		StopWord stopWords = getConfigValue("stopWords");
		frequencyAnalyzer.setStopWords(stopWords.getValues());
		frequencyAnalyzer
				.setWordFrequenciesToReturn(getConfigValue("wordFrequencesToReturn"));
		frequencyAnalyzer.setMinWordLength(getConfigValue("minWordLength"));
		frequencyAnalyzer.setMaxWordLength(getConfigValue("maxWordLength"));

		return frequencyAnalyzer.load(texts);

	}

	public List<DiscussionPost> getSelectedDiscussionPosts(Collection<EnrolledUser> selectedUsers) {
		Set<CourseModule> selectedForums = new HashSet<>(listViewForum.getSelectionModel()
				.getSelectedItems());
		return actualCourse.getDiscussionPosts()
				.stream()

				.filter(discussionPost -> selectedForums.contains(discussionPost.getForum())
						&& selectedUsers.contains(discussionPost.getUser()))
				.collect(Collectors.toList());
	}

}
