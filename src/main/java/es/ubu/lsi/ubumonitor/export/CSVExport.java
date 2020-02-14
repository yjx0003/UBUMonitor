package es.ubu.lsi.ubumonitor.export;

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
	public static void run() {
		DataBase dataBase = Controller.getInstance().getDataBase();
		generateFile(new CSVEnrolledUser("enrolled_users", dataBase));
		generateFile(new CSVCourseModule("course_modules", dataBase));
		generateFile(new CSVLog("logs", dataBase));
		generateFile(new CSVGrade("grades", dataBase));
		generateFile(new CSVGroup("groups", dataBase));
		generateFile(new CSVRole("roles", dataBase));
		generateFile(new CSVSection("sections", dataBase));
		generateFile(new CSVCourse("courses",dataBase));
	}	

	/**
	 * Generate a CSV file. Create the header, body and save the csv file.
	 * 
	 * @param builder builder
	 */
	private static void generateFile(CSVBuilder builder) {
		builder.buildHeader();
		builder.buildBody();
		builder.writeCSV();
	}
	
	
	private CSVExport() {
		throw new UnsupportedOperationException();
	}
}
