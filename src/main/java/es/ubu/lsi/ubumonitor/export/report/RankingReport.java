package es.ubu.lsi.ubumonitor.export.report;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.util.WordUtil;

public class RankingReport {

	private static final int PHOTO_SIZE = 50;

	private static final List<String> COLOR_RANKS = Arrays.asList("b5ff33", "fff033", "f4e3ae", "f78880");

	public <E> void createReport(File file, DataBase dataBase, List<EnrolledUser> users,
			Map<EnrolledUser, Integer> rankingLogs, Map<EnrolledUser, Integer> rankingGradeItems,
			Map<EnrolledUser, Integer> rankingActivities, Collection<E> logs, Collection<GradeItem> gradeItems,
			Collection<CourseModule> activities, LocalDate start, LocalDate end, String tabLog) throws IOException {

		Course course = dataBase.getActualCourse();

		try (FileOutputStream out = new FileOutputStream(file); XWPFDocument document = new XWPFDocument()) {
			int maxRankLog = users.isEmpty() ? 0 : Collections.max(rankingLogs.values());
			int maxRankGrade = users.isEmpty() ? 0 : Collections.max(rankingGradeItems.values());
			int maxRankActivities = users.isEmpty() ? 0 : Collections.max(rankingActivities.values());
			XWPFParagraph paragraph;
			XWPFRun run;
			Map<EnrolledUser, Collection<Role>> userRoles = userRoles(users, course.getRoles());
			Map<EnrolledUser, Collection<Group>> userGroups = userGroups(users, course.getGroups());
			Map<EnrolledUser, Collection<Course>> userCourses = userCourses(users, dataBase.getCourses()
					.getMap()
					.values());
			WordUtil.fillHeaderAndFoot(document, course.getFullName());
			for (int i = 0; i < users.size(); ++i) {
				EnrolledUser user = users.get(i);

				XWPFTable table = document.createTable(3, 3);
				table.setCellMargins(50, 50, 50, 50);
				table.setInsideHBorder(XWPFBorderType.NONE, 0, 0, "000000");
				table.setInsideVBorder(XWPFBorderType.NONE, 0, 0, "000000");
				table.getCTTbl()
						.addNewTblPr()
						.addNewTblW()
						.setW(BigInteger.valueOf(10000));
				table.setTableAlignment(TableRowAlign.CENTER);
				run = getParagraphCell(table, 0, 0).createRun();
				run.addPicture(new ByteArrayInputStream(user.getImageBytes()), Document.PICTURE_TYPE_PNG, users.get(i)
						.getId() + ".png", Units.pixelToEMU(PHOTO_SIZE), Units.pixelToEMU(PHOTO_SIZE));

				run = getParagraphCell(table, 0, 1).createRun();
				run.setBold(true);
				run.setFontSize(16);
				run.setText(user.getFullName());

				createHyperlinkRun(getParagraphCell(table, 0, 2), user.getEmail(), "mailto:" + user.getEmail());

				setRankTitle(getParagraphCell(table, 1, 0), I18n.get("tab.logs") + " - " + tabLog);
				setRankTitle(getParagraphCell(table, 1, 1), I18n.get("tab.grades"));
				setRankTitle(getParagraphCell(table, 1, 2), I18n.get("tab.activityCompletion"));

				setRank(getParagraphCell(table, 2, 0), rankingLogs.get(user), maxRankLog);
				setRank(getParagraphCell(table, 2, 1), rankingGradeItems.get(user), maxRankGrade);
				setRank(getParagraphCell(table, 2, 2), rankingActivities.get(user), maxRankActivities);

				Instant reference = Controller.getInstance()
						.getUpdatedCourseData()
						.toInstant();
				setInfo(table, "label.lastcourseaccess",
						UtilMethods.getDifferenceTime(user.getLastaccess(), reference));
				setInfo(table, "label.lastaccess", UtilMethods.getDifferenceTime(user.getLastaccess(), reference));
				setInfo(table, "label.firstaccess", UtilMethods.getDifferenceTime(user.getFirstaccess(), reference));
				setInfo(table, "label.roles", UtilMethods.join(userRoles.get(user), ", "));
				setInfo(table, "label.groups", userGroups.get(user)
						.size());
				setInfo(table, "label.ncourses", userCourses.size());

				paragraph = document.createParagraph();
				
				if (i % 2 == 1) { //odd
					paragraph.createRun()
							.addBreak(BreakType.PAGE);
				} else { //even
					paragraph.setBorderBottom(Borders.DOT_DASH);
				}

			}
			if (users.size() % 2 == 1) {
				document.getLastParagraph()
						.createRun()
						.addBreak(BreakType.PAGE);
			}
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			run.setBold(true);
			run.setFontSize(14);
			run.setText(I18n.get("label.startdate"));
			paragraph.createRun().setText(start.format(Controller.DATE_FORMATTER));
			
			paragraph= document.createParagraph();
			run = paragraph.createRun();
			run.setBold(true);
			run.setFontSize(14);
			run.setText(I18n.get("label.enddate"));
			paragraph.createRun().setText(end.format(Controller.DATE_FORMATTER));
			
			createCollectionInfo(document, 0, I18n.get("tab.logs") + " - " + tabLog, logs);
			createCollectionInfo(document, 1, I18n.get("tab.grades"), gradeItems);
			createCollectionInfo(document, 2, I18n.get("tab.activityCompletion"), activities);
			document.write(out);

		} catch (InvalidFormatException e) {
			throw new IllegalStateException("Problem with the user picture format", e);
		}
	}

	private void createCollectionInfo(XWPFDocument document, int id, String title, Collection<?> collection) {
		XWPFParagraph paragraph = document.createParagraph();
		XWPFRun run = paragraph.createRun();
		run.setText(title);
		run.setBold(true);
		run.setFontSize(14);

		CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
		// Next we set the AbstractNumId. This requires care.
		// Since we are in a new document we can start numbering from 0.
		// But if we have an existing document, we must determine the next free number
		// first.
		cTAbstractNum.setAbstractNumId(BigInteger.valueOf(id));
		CTLvl cTLvl = cTAbstractNum.addNewLvl();
		cTLvl.addNewNumFmt()
				.setVal(STNumberFormat.BULLET);
		cTLvl.addNewLvlText()
				.setVal("•");
		XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);

		XWPFNumbering numbering = document.createNumbering();

		BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);

		BigInteger numID = numbering.addNum(abstractNumID);
		for (Object element : collection) {
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setIndentFromLeft(1440 / 4); // indent from left 360 Twips = 1/4 inch
			paragraph.setIndentationHanging(1440 / 4); // indentation hanging 360 Twips = 1/4 inch
														// so bullet point hangs 1/4 inch before the text at indentation

			run = paragraph.createRun();
			run.setText(element.toString());
		}
	}

	private void setInfo(XWPFTable table, String key, Object value) {
		XWPFTableRow row = table.createRow();
		XWPFTableCell cell = row.getCell(0);

		XWPFParagraph paragraph = cell.getParagraphArray(0);
		setToCenter(cell, paragraph);
		XWPFRun run = paragraph.createRun();
		run.setBold(true);
		run.setText(I18n.get(key));

		cell = row.getCell(1);
		paragraph = cell.getParagraphArray(0);
		setToCenter(cell, paragraph);
		run = paragraph.createRun();
		run.setText(value.toString());

	}

	public Map<EnrolledUser, Collection<Role>> userRoles(Collection<EnrolledUser> users, Collection<Role> roles) {
		Map<EnrolledUser, Collection<Role>> userRoles = new HashMap<>();
		for (EnrolledUser user : users) {
			TreeSet<Role> r = new TreeSet<>(Comparator.comparing(Role::getRoleName));
			for (Role role : roles) {
				if (role.contains(user)) {
					r.add(role);
				}
			}
			userRoles.put(user, r);
		}
		return userRoles;
	}

	public Map<EnrolledUser, Collection<Group>> userGroups(Collection<EnrolledUser> users, Collection<Group> groups) {
		Map<EnrolledUser, Collection<Group>> userGroups = new HashMap<>();
		for (EnrolledUser user : users) {
			TreeSet<Group> g = new TreeSet<>(Comparator.comparing(Group::getGroupName));
			for (Group group : groups) {
				if (group.contains(user)) {
					g.add(group);
				}
			}
			userGroups.put(user, g);
		}
		return userGroups;
	}

	private Map<EnrolledUser, Collection<Course>> userCourses(List<EnrolledUser> users, Collection<Course> courses) {
		Map<EnrolledUser, Collection<Course>> userCourses = new HashMap<>();
		for (EnrolledUser user : users) {
			TreeSet<Course> c = new TreeSet<>(Comparator.comparing(Course::getFullName));
			for (Course course : courses) {
				if (course.contains(user)) {
					c.add(course);
				}
			}
			userCourses.put(user, c);
		}
		return userCourses;
	}

	private static void createHyperlinkRun(XWPFParagraph paragraph, String text, String url) {
		// Add the link as External relationship
		String id = paragraph.getDocument()
				.getPackagePart()
				.addExternalRelationship(url, XWPFRelation.HYPERLINK.getRelation())
				.getId();

		// Append the link and bind it to the relationship
		CTHyperlink cLink = paragraph.getCTP()
				.addNewHyperlink();
		cLink.setId(id);

		// Create the linked text
		CTText ctText = CTText.Factory.newInstance();
		ctText.setStringValue(text);
		CTR ctr = CTR.Factory.newInstance();
		ctr.setTArray(new CTText[] { ctText });

		// Create the formatting
		CTRPr rpr = ctr.addNewRPr();
		CTColor colour = CTColor.Factory.newInstance();
		colour.setVal("0000FF");
		rpr.setColor(colour);
		CTRPr rpr1 = ctr.addNewRPr();
		rpr1.addNewU()
				.setVal(STUnderline.SINGLE);

		// Insert the linked text into the link
		cLink.setRArray(new CTR[] { ctr });
	}

	private XWPFParagraph getParagraphCell(XWPFTable table, int row, int column) {
		XWPFTableCell cell = table.getRow(row)
				.getCell(column);

		XWPFParagraph paragraph = cell.getParagraphArray(0);

		setToCenter(cell, paragraph);
		return paragraph;
	}

	private void setToCenter(XWPFTableCell cell, XWPFParagraph paragraph) {
		cell.setVerticalAlignment(XWPFVertAlign.CENTER);
		paragraph.setAlignment(ParagraphAlignment.CENTER);
	}

	private void setRank(XWPFParagraph paragraph, int rank, int maxRank) {

		XWPFRun run = paragraph.createRun();
		run.setText(rank + "/" + maxRank);
		run.setBold(true);

		if (maxRank == 0) {

			run.setColor(COLOR_RANKS.get(COLOR_RANKS.size() - 1));
		} else {
			run.setColor(COLOR_RANKS.get((int) Math.ceil(rank * COLOR_RANKS.size() / (double) maxRank) - 1));
		}

	}

	private void setRankTitle(XWPFParagraph paragraph, String text) {
		XWPFRun run = paragraph.createRun();
		run.setBold(true);
		run.setText(text);
	}
}
