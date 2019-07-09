package export.builder;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import export.CSVBuilderAbstract;
import model.DataBase;
import model.EnrolledUser;


/**
 * Builds the enrolled user file.
 * 
 * @author Raúl Marticorena
 * @since 2.4.0.0
 */
public class CSVEnrolledUser extends CSVBuilderAbstract {
	
	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVEnrolledUser.class);
	
	/** Header. */
	private static final String[] HEADER = new String[] { "UserId", "FullName", "eMail", "LastAccess" };


	/**
	 * Constructor.
	 * 
	 * @param name name 
	 * @param dataBase dataBase
	 */
	public CSVEnrolledUser(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		// Gets users
		Map<Integer, EnrolledUser> enrolledUsers = getDataBase().getUsers();
		Collection<EnrolledUser> studentsCollection = enrolledUsers.values();
		// Remove nulls and sort
		Stream<EnrolledUser> usersWithoutNull = studentsCollection.stream().filter(user -> user.getFullName() != null)
				.sorted(compareByFullName);
		// Load data users
		usersWithoutNull.forEach(eu -> {
			LOGGER.debug("Data line: {}, {}, {}, {}", eu.getId(), eu.getFullName(), eu.getEmail(), eu.getLastaccess());
			getData().add(new String[] { Integer.toString(eu.getId()), eu.getFullName(), eu.getEmail(),
					eu.getLastaccess().toString() });
		});
	}

}
