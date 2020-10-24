package es.ubu.lsi.ubumonitor.export.photos;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.WordUtil;

public class UserPhoto {
	
	private static final int N_COLUMNS = 5;
	
	private static final int PHOTO_SIZE = 80;

	public void exportEnrolledUsersPhoto(Course course, List<EnrolledUser> enrolledUsers, File file,
			boolean defaultPhoto) throws IOException {

		byte[] defaultUser = null;

		if (defaultPhoto) {
			BufferedImage bImage = ImageIO.read(getClass().getResource("/img/default_user.png"));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bImage, "png", bos);
			defaultUser = bos.toByteArray();
		}

		try (FileOutputStream out = new FileOutputStream(file); XWPFDocument document = new XWPFDocument()) {

			WordUtil.fillHeaderAndFoot(document, course.getFullName());

			XWPFTable tab = document.createTable((int) Math.ceil(enrolledUsers.size() / (double) N_COLUMNS),
					enrolledUsers.size() < N_COLUMNS ? enrolledUsers.size() : N_COLUMNS);
			tab.setCellMargins(50, 0, 0, 0);

			for (int i = 0; i < enrolledUsers.size(); i++) {

				XWPFTableRow row = tab.getRow(i / N_COLUMNS);
				row.setCantSplitRow(false);
				XWPFTableCell cell = row.getCell(i % N_COLUMNS);
				XWPFParagraph paragraph = cell.getParagraphArray(0);
				paragraph.setAlignment(ParagraphAlignment.CENTER);
				XWPFRun run = paragraph.createRun();
				byte[] bytePhoto = defaultPhoto ? defaultUser
						: enrolledUsers.get(i)
								.getImageBytes();

				run.addPicture(new ByteArrayInputStream(bytePhoto), Document.PICTURE_TYPE_PNG, enrolledUsers.get(i)
						.getId() + ".png", Units.pixelToEMU(PHOTO_SIZE), Units.pixelToEMU(PHOTO_SIZE));
				
				run.addBreak();
				run.setText(enrolledUsers.get(i)
						.getFullName());

			}
			document.write(out);

		} catch (InvalidFormatException e) {
			throw new IllegalStateException("Invalid format exception");
		}

	}
}
