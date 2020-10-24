package es.ubu.lsi.ubumonitor.util;

import java.time.LocalDateTime;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import es.ubu.lsi.ubumonitor.controllers.Controller;

public class WordUtil {
	
	public static void fillHeaderAndFoot(XWPFDocument document, String headerText) {
		XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
		if (headerFooterPolicy == null)
			headerFooterPolicy = document.createHeaderFooterPolicy();

		// create header start
		XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

		XWPFParagraph paragraph = header.createParagraph();

		XWPFRun run = paragraph.createRun();
		run.setText(headerText);
		paragraph = header.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.RIGHT);

		run = paragraph.createRun();
		run.setText(LocalDateTime.now()
				.format(Controller.DATE_TIME_FORMATTER));

		XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

		paragraph = footer.getParagraphArray(0);
		if (paragraph == null)
			paragraph = footer.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();

		paragraph.getCTP()
				.addNewFldSimple()
				.setInstr("PAGE \\* ARABIC MERGEFORMAT");
		run = paragraph.createRun();
		run.setText("/");
		paragraph.getCTP()
				.addNewFldSimple()
				.setInstr("NUMPAGES \\* ARABIC MERGEFORMAT");
	}
	
}
