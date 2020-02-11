package es.ubu.lsi.export.builder;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.export.CSVBuilderAbstract;
import es.ubu.lsi.model.DataBase;
import es.ubu.lsi.model.EnrolledUser;

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
	private static final String[] HEADER = new String[] { "UserId", "LastName", "FirstName", "FullName", "eMail",
			"LastAccess" };

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            name
	 * @param dataBase
	 *            dataBase
	 */
	public CSVEnrolledUser(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		// Gets users
		Map<Integer, EnrolledUser> enrolledUsers = getDataBase().getUsers().getMap();
		Collection<EnrolledUser> studentsCollection = enrolledUsers.values();
		// Remove nulls and sort
		Stream<EnrolledUser> sortedUsers = studentsCollection.stream()
				.sorted(EnrolledUser.NAME_COMPARATOR.thenComparing(EnrolledUser::getId));
		// Load data users
		sortedUsers.forEach(eu -> {
			LOGGER.debug("Data line: {}, {}, {}, {}, {}, {}", eu.getId(), eu.getLastname(), eu.getFirstname(),
					eu.getFullName(), eu.getEmail(), eu.getLastaccess());
			getData().add(new String[] {
					Integer.toString(eu.getId()),
					eu.getLastname(),
					eu.getFirstname(),
					eu.getFullName(),
					eu.getEmail(),
					eu.getLastaccess() == null ? null : eu.getLastaccess().toString() });
		});
	}

}
