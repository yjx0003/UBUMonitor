package es.ubu.lsi.ubumonitor.export.builder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.export.CSVBuilderAbstract;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class CSVCourse extends CSVBuilderAbstract {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVCourse.class);

	/** Header. */
	private static final String[] HEADER = new String[] { "CourseId", "ShortName", "FullName", "UserId",
			"UserFullName" };

	public CSVCourse(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		Collection<Course> courses = getDataBase().getCourses().getMap().values();

		for (Course course : courses) {
			List<EnrolledUser> enrolledUsers = course.getEnrolledUsers().stream()
					.sorted(EnrolledUser.getNameComparator())
					.collect(Collectors.toList());
			for (EnrolledUser enrolledUser : enrolledUsers) {
				LOGGER.debug("Data line: {}, {}, {}, {}, {}", course.getId(), course.getShortName(), course.getFullName(),
						 enrolledUser.getId(), enrolledUser.getFullName());
				getData().add(new String[] {
						Integer.toString(course.getId()),
						course.getShortName(),
						course.getFullName(),
						Integer.toString(enrolledUser.getId()),
						enrolledUser.getFullName()
				});
			}
		}

	}

}
