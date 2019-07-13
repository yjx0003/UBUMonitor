package export;

import controllers.Controller;
import export.builder.CSVCourseModule;
import export.builder.CSVEnrolledUser;
import export.builder.CSVGrade;
import export.builder.CSVGroup;
import export.builder.CSVLog;
import export.builder.CSVRole;
import model.DataBase;

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
}
