package es.ubu.lsi.ubumonitor.export.builder;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.export.CSVBuilderAbstract;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;

/**
 * Builds the group file.
 * 
 * @author Raúl Marticorena
 * @since 2.4.0.0
 */
public class CSVGroup extends CSVBuilderAbstract {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVGroup.class);

	/** Header. */
	private static final String[] HEADER = new String[] { "GroupId", "Name", "Description", "UserId", "FullName" };

	/**
	 * Constructor.
	 * 
	 * @param name name 
	 * @param dataBase dataBase
	 */
	public CSVGroup(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		Collection<Group> groups = getDataBase().getGroups().getMap().values();
		// Load data
		for (Group group : groups) {
			Stream<EnrolledUser> users = group.getEnrolledUsers().stream()
					.sorted(EnrolledUser.getNameComparator());
			for (EnrolledUser enrollmentUser : (Iterable<EnrolledUser>) users::iterator) {
				LOGGER.debug("Data line: {}, {}, {}, {}, {}", group.getGroupId(), group.getGroupName(), group.getDescription(),
						enrollmentUser.getId(), enrollmentUser.getFullName());
				getData().add(new String[] {
						Integer.toString(group.getGroupId()),
						group.getGroupName(),
						group.getDescription(),
						Integer.toString(enrollmentUser.getId()),
						enrollmentUser.getFullName() });
			}
		}
	}
}
