package es.ubu.lsi.ubumonitor.controllers;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class UserInfoController {
	private MainController mainController;
	private EnrolledUser actualEnrolledUser;

	@FXML
	private ImageView imageView;
	@FXML
	private Label labelUser;
	@FXML
	private Hyperlink hyperlinkEmail;
	@FXML
	private Label labelFirstAccess;
	@FXML
	private Label labelLastAccess;
	@FXML
	private Label labelLastCourseAccess;
	@FXML
	private Label labelRoles;
	@FXML
	private Label labelGroups;
	@FXML
	private Label labelNcourses;

	@FXML
	private TableColumn<Course, String> coursesColumn;

	@FXML
	private TableView<Course> tableView;

	public void init(MainController mainController, EnrolledUser enrolledUser) {
		this.mainController = mainController;
		setUser(enrolledUser);
	}

	public void previousUser() {
		ObservableList<EnrolledUser> list = mainController.getSelectionUserController().getListParticipants().getItems();
		int index = list.indexOf(actualEnrolledUser);
		EnrolledUser previousUser = index == 0 ? list.get(list.size() - 1) : list.get(--index);
		setUser(previousUser);
	}

	public void nextUser() {
		ObservableList<EnrolledUser> list =  mainController.getSelectionUserController().getListParticipants().getItems();

		int index = list.indexOf(actualEnrolledUser);

		EnrolledUser previousUser = list.get(++index % list.size());

		setUser(previousUser);
	}

	public void setUser(EnrolledUser enrolledUser) {
		actualEnrolledUser = enrolledUser;

		imageView.setImage(new Image(new ByteArrayInputStream(enrolledUser.getImageBytes())));
		labelUser.setText(enrolledUser.toString());
		hyperlinkEmail.setText(enrolledUser.getEmail());
		hyperlinkEmail.setOnAction(e -> UtilMethods.mailTo(enrolledUser.getEmail()));
		Instant reference = Controller.getInstance().getUpdatedCourseData().toInstant();
		labelFirstAccess.setText(getDifferenceTime(enrolledUser.getFirstaccess(), reference));
		labelLastAccess.setText(getDifferenceTime(enrolledUser.getLastaccess(), reference));
		Circle circleLastAccees = new Circle(10);
		circleLastAccees.setFill(LastActivityFactory.DEFAULT.getColorActivity(enrolledUser.getLastaccess(),
				reference));
		labelLastAccess.setGraphic(circleLastAccees);
		
		
		labelLastCourseAccess.setText(getDifferenceTime(enrolledUser.getLastcourseaccess(), reference));
		Circle circle = new Circle(10);
		circle.setFill(LastActivityFactory.DEFAULT.getColorActivity(enrolledUser.getLastcourseaccess(),
				reference));
		labelLastCourseAccess.setGraphic(circle);
		labelRoles.setText(Controller.getInstance().getActualCourse().getRoles().stream()
				.filter(r -> r.contains(enrolledUser)).map(Role::getRoleName).collect(Collectors.joining(", ")));
		labelGroups.setText(Controller.getInstance().getActualCourse().getGroups().stream()
				.filter(r -> r.contains(enrolledUser)).map(Group::getGroupName).collect(Collectors.joining(", ")));
		
		ObservableList<Course> courses = Controller.getInstance().getDataBase().getCourses().getMap().values().stream()
				.filter(c -> c.contains(enrolledUser))
				.collect(Collectors.toCollection(FXCollections::observableArrayList));
		courses.sort(Comparator.comparing(Course::getId, Comparator.reverseOrder()));
		
		coursesColumn.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getFullName()));
		coursesColumn.setComparator(String::compareToIgnoreCase);
		tableView.setItems(courses);
		labelNcourses.setText(String.valueOf(courses.size()));
		

	}

	private String getDifferenceTime(Instant start, Instant end) {
		if (start != null && end != null && start.getEpochSecond() != 0) {
			return Controller.DATE_TIME_FORMATTER.format(start.atZone(ZoneId.systemDefault())) + " ("
					+ UtilMethods.formatDates(start, end) + ")";
		}

		return I18n.get("text.never");
	}
}
