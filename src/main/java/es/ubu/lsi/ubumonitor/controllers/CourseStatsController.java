package es.ubu.lsi.ubumonitor.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.model.Course;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CourseStatsController implements Initializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseStatsController.class);

	@FXML
	private Label labelCourse;
	@FXML
	private TextField textFieldEnrolledUsers;
	@FXML
	private TextField textFieldLogs;
	@FXML
	private TextField textFieldComponents;
	@FXML
	private TextField textFieldComponentsEvents;
	@FXML
	private TextField textFieldSections;
	@FXML
	private TextField textFieldCourseModules;
	@FXML
	private TextField textFieldGradebook;
	@FXML
	private TextField textFieldGradebookCM;
	@FXML
	private TextField textFieldActivityCompletion;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Course actualCourse = Controller.getInstance()
				.getActualCourse();
		labelCourse.setText(actualCourse.getFullName());
		setText(textFieldEnrolledUsers, actualCourse.getEnrolledUsersCount());
		setText(textFieldLogs, actualCourse.getLogs()
				.getList()
				.size());
		setText(textFieldComponents, actualCourse.getUniqueComponents()
				.size());
		setText(textFieldComponentsEvents, actualCourse.getUniqueComponentsEvents()
				.size());
		setText(textFieldSections, actualCourse.getSections()
				.size());
		setText(textFieldCourseModules, actualCourse.getModules()
				.size());
		setText(textFieldGradebook, actualCourse.getGradeItems()
				.size());
		setText(textFieldGradebookCM, actualCourse.getGradeItems()
				.stream()
				.filter(cm -> cm.getModule() != null)
				.count());
		setText(textFieldActivityCompletion, actualCourse.getModules()
				.stream()
				.filter(cm -> !(cm.getActivitiesCompletion()
						.isEmpty()))
				.count());
		try {
			createExcel();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setText(TextField textfield, Object value) {
		textfield.setText(value.toString());
	}

	/**
	 * Log course general statistics.
	 * 
	 * @since v2.6.3-stable
	 */
	public static void logStatistics(Course actualCourse) {
		LOGGER.info("COURSE STATISTICS: {} {}", actualCourse.getId(), actualCourse.getFullName());
		LOGGER.info("# enrolled users: {}", actualCourse.getEnrolledUsersCount());
		LOGGER.info("# logs entries: {}", actualCourse.getLogs()
				.getList()
				.size());
		LOGGER.info("# component types: {}", actualCourse.getUniqueComponents()
				.size());
		LOGGER.info("# type events: {}", actualCourse.getUniqueComponentsEvents()
				.size());
		LOGGER.info("# sections: {}", actualCourse.getSections()
				.size());
		LOGGER.info("# course modules: {}", actualCourse.getModules()
				.size());
		LOGGER.info("# grade book all items: {}", actualCourse.getGradeItems()
				.size());
		LOGGER.info("# grade book course module items: {}", actualCourse.getGradeItems()
				.stream()
				.filter(cm -> cm.getModule() != null)
				.count());
		LOGGER.info("# activity completion items: {}", actualCourse.getModules()
				.stream()
				.filter(cm -> !(cm.getActivitiesCompletion()
						.isEmpty()))
				.count());
	}

	private void createExcel() throws IOException {
		Course actualCourse = Controller.getInstance()
				.getActualCourse();
		Workbook workbook = new XSSFWorkbook(getClass().getResourceAsStream("/templates/Plantilla.xlsx"));

		Sheet sheet = workbook.getSheet("Data");
		int rowCount = -1;

		setCellValue(sheet, ++rowCount, 0, actualCourse.getEnrolledUsersCount());
		setCellValue(sheet, ++rowCount, 0, actualCourse.getLogs()
				.getList()
				.size());
		setCellValue(sheet, ++rowCount, 0, actualCourse.getUniqueComponents()
				.size());
		workbook.getSheet("Dashboard").setForceFormulaRecalculation(true);
		FileOutputStream outputStream = new FileOutputStream("./Plantilla2.xlsx");
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	private void setCellValue(Sheet sheet, int rowIndex, int columnIndex, boolean value) {
		sheet.createRow(rowIndex)
				.createCell(columnIndex)
				.setCellValue(value);

	}

	private void setCellValue(Sheet sheet, int rowIndex, int columnIndex, String value) {
		sheet.createRow(rowIndex)
				.createCell(columnIndex)
				.setCellValue(value);

	}

	private void setCellValue(Sheet sheet, int rowIndex, int columnIndex, double value) {
		sheet.createRow(rowIndex)
				.createCell(columnIndex)
				.setCellValue(value);

	}

	private void setCellValue(Sheet sheet, int rowIndex, int columnIndex, LocalDateTime value) {
		sheet.createRow(rowIndex)
				.createCell(columnIndex)
				.setCellValue(value);

	}

}
