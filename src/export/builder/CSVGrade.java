package export.builder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import export.CSVBuilderAbstract;
import model.DataBase;
import model.EnrolledUser;
import model.GradeItem;

/**
 * Builds the grades file.
 * 
 * @author Raúl Marticorena
 * @since 2.4.0.0
 */
public class CSVGrade extends CSVBuilderAbstract {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVGrade.class);

	/** Header. */
	private static final String[] HEADER = new String[] { "UserId", "CourseModule", "Level", "Grade", "Percentage", "MinGrade",
			"MaxGrade", "WeightRaw", "UserFullName", "CourseModuleName" };

	/** Decimal format (change , by . ). */
	static DecimalFormat decimalFormat;

	// Format number with four decimal digits and point.
	static {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		decimalFormat = new DecimalFormat("#.0000", otherSymbols);
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            name
	 * @param dataBase
	 *            dataBase
	 */
	public CSVGrade(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {

		
		Set<GradeItem> gradeItems = getDataBase().getActualCourse().getGradeItems();

		Set<EnrolledUser> enrolledUsers = getDataBase().getActualCourse().getEnrolledUsers();

		Stream<EnrolledUser> enrolledUsersSorted = enrolledUsers.stream()
				.sorted(EnrolledUser.NAME_COMPARATOR);

		
		enrolledUsersSorted.forEach(eu->{
			String modName;
			String courseModuleId;
			String fullName;
			String weightRaw;
			
			for (GradeItem gradeItem : gradeItems) {
				double value = gradeItem.getEnrolledUserGrade(eu);
				modName = gradeItem.getItemModule() != null ? gradeItem.getItemModule().getModName()
						: "";
				fullName = eu.getFullName();
				courseModuleId = gradeItem.getModule() != null ? Integer.toString(gradeItem.getModule().getCmid())
						: "";
				weightRaw = !Double.isNaN(gradeItem.getWeightraw()) ? decimalFormat.format(gradeItem.getWeightraw())
						: "NaN";
				
				double percentage = gradeItem.getEnrolledUserPercentage(eu);
				LOGGER.debug("Data line: {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}", eu.getId(), courseModuleId,
						gradeItem.getLevel(),
						value, percentage, gradeItem.getGrademin(), gradeItem.getGrademax(),
						weightRaw, fullName, gradeItem.getItemname(), modName);

				getData().add(new String[] {
						Integer.toString(eu.getId()),
						courseModuleId,
						Integer.toString(gradeItem.getLevel()),
						Double.toString(value),
						Double.toString(percentage),
						Double.toString(gradeItem.getGrademin()),
						Double.toString(gradeItem.getGrademax()),
						weightRaw,
						fullName,
						gradeItem.getItemname(),
						modName
				});
			}

		});
	}
}
