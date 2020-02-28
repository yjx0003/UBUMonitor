package es.ubu.lsi.ubumonitor.export;

import java.nio.charset.Charset;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.export.builder.CSVCourse;
import es.ubu.lsi.ubumonitor.export.builder.CSVCourseModule;
import es.ubu.lsi.ubumonitor.export.builder.CSVEnrolledUser;
import es.ubu.lsi.ubumonitor.export.builder.CSVGrade;
import es.ubu.lsi.ubumonitor.export.builder.CSVGroup;
import es.ubu.lsi.ubumonitor.export.builder.CSVLog;
import es.ubu.lsi.ubumonitor.export.builder.CSVRole;
import es.ubu.lsi.ubumonitor.export.builder.CSVSection;
import es.ubu.lsi.ubumonitor.model.DataBase;

/**
 * Exports the data model in a set of CSV files.
 * 
 * Includes the data of:
 * <ul>
 * <li>Users</li>
 * <li>Logs</li>
 * <li>Grades</li>
 * <li>Roles</li>
 * <li>Groups</li>
 * <li>Sections</li>
 * </ul>
 * 
 * @author Raúl Marticorena
 * @since 2.4.0.0
 */
public class CSVExport {

	/**
	 * Run the CSV generation.
	 */
	public static void run(Charset charset) {
		DataBase dataBase = Controller.getInstance().getDataBase();
		generateFile(new CSVEnrolledUser("enrolled_users", dataBase), charset);
		generateFile(new CSVCourseModule("course_modules", dataBase), charset);
		generateFile(new CSVLog("logs", dataBase), charset);
		generateFile(new CSVGrade("grades", dataBase), charset);
		generateFile(new CSVGroup("groups", dataBase), charset);
		generateFile(new CSVRole("roles", dataBase), charset);
		generateFile(new CSVSection("sections", dataBase), charset);
		generateFile(new CSVCourse("courses", dataBase), charset);
	}

	/**
	 * Generate a CSV file. Create the header, body and save the csv file.
	 * 
	 * @param builder builder
	 */
	private static void generateFile(CSVBuilder builder, Charset charset) {
		builder.buildHeader();
		builder.buildBody();
		builder.writeCSV(charset);
	}

	private CSVExport() {
		throw new UnsupportedOperationException();
	}
}
