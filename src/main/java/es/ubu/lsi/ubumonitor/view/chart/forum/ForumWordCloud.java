package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.Parsers;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.WordCloud;
import javafx.scene.control.ListView;

public class ForumWordCloud extends WordCloud {
	
	private ListView<CourseModule> listViewForum;

	public ForumWordCloud(MainController mainController, ListView<CourseModule> listViewForum) {
		super(mainController, ChartType.FORUM_WORD_CLOUD);
		this.listViewForum = listViewForum;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		Map<String, Long> wordCount = wordCount();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("word", "freq"))) {
			for(Map.Entry<String, Long> entry:wordCount.entrySet()) {
				if(entry.getKey().length() > 3) {
					
					printer.printRecords(entry.getKey(), entry.getValue());
				}
				
			}
		}

	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		jsObject.put("gridSize", "Math.round(16 * document.getElementById('wordCloud').width / 1024)");
		jsObject.put("weightFactor",
				"function (size) {return Math.pow(size, 2.3) * document.getElementById('wordCloud').width / 1024; }");
		jsObject.put("rotateRatio", 0.5);
		jsObject.put("rotationSteps", 2);
		jsObject.put("backgroundColor", "'#ffe0e0'");
		jsObject.put("minSize", 12);
		return jsObject;
	}

	@Override
	public void update() {
		Map<String, Long> wordCount = wordCount();
		JSArray jsArray = toJS(wordCount);
		JSObject options = getOptions();
		options.put("list", jsArray);
		
		webViewChartsEngine.executeScript("updateWordCloud(" + options+ ")");
	}

	public Map<String, Long> wordCount() {
		List<EnrolledUser> selectedEnrolledUsers = getSelectedEnrolledUser();
		List<DiscussionPost> discussionPosts = getSelectedDiscussionPosts(selectedEnrolledUsers);
		
		List<String> result = new ArrayList<>();
		try(Analyzer analyzer = new StandardAnalyzer()){
			for(DiscussionPost discussionPost: discussionPosts) {
				
				String parsed = Parsers.parse(discussionPost.getMessage(), discussionPost.getMessageformat());
				analyze(result, parsed, analyzer);
			}
		}
		
		return result.stream().collect(Collectors.groupingBy(String::toString, Collectors.counting()));
		
	}
	
	
	public JSArray toJS(Map<String, Long> wordCount) {
		JSArray jsArray = new JSArray();
		for(Map.Entry<String, Long> entry:wordCount.entrySet()) {
			if(entry.getKey().length() > 3) {
				JSArray array = new JSArray();
				array.addWithQuote(entry.getKey());
				array.add(entry.getValue());
				jsArray.add(array);
			}
			
		}
		return jsArray;
	}

	public List<DiscussionPost> getSelectedDiscussionPosts(Collection<EnrolledUser> selectedUsers) {
		Set<CourseModule> selectedForums = new HashSet<>(listViewForum.getSelectionModel()
				.getSelectedItems());
		return actualCourse.getDiscussionPosts()
				.stream()

				.filter(discussionPost -> selectedForums.contains(discussionPost.getForum())
						&& selectedUsers.contains(discussionPost.getUser()) && discussionPost.getParent()
								.getId() != 0)
				.collect(Collectors.toList());
	}

	public void analyze(Collection<String> result, String text, Analyzer analyzer) {

		try (TokenStream tokenStream = analyzer.tokenStream(null, text)) {
			CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				result.add(attr.toString());
			}
		} catch (IOException e) {
			//do nothing, never has a IOException cuz is a StringReader
		}

	}
}
