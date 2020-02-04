package export.builder;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import export.CSVBuilderAbstract;
import model.DataBase;
import model.EnrolledUser;
import model.Role;

/**
 * Builds the role file.
 * 
 * @author Raúl Marticorena
 * @since 2.4.0.0
 */
public class CSVRole extends CSVBuilderAbstract {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVRole.class);

	/** Header. */
	private static final String[] HEADER = new String[] { "RoleId", "Name", "ShortName", "UserId", "FullName" };

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            name
	 * @param dataBase
	 *            dataBase
	 */
	public CSVRole(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		Collection<Role> roles = getDataBase().getRoles().getMap().valueCollection();
		// Load data rows for roles
		for (Role role : roles) {
			// Filter nulls and sort
			Stream<EnrolledUser> users = role.getEnrolledUsers().stream()
					.sorted(EnrolledUser.NAME_COMPARATOR);
			// Load data rows for enrolledUsers
			users.forEach(enrollmentUser -> {
				LOGGER.debug(role.getRoleId() + "," + role.getRoleName() + "," + role.getRoleShortName() + ","
						+ enrollmentUser.getId() + "," + enrollmentUser.getFullName());
				getData().add(new String[] {
						Integer.toString(role.getRoleId()),
						role.getRoleName(),
						role.getRoleShortName(),
						Integer.toString(enrollmentUser.getId()),
						enrollmentUser.getFullName() });
			});
		}
	}

}
