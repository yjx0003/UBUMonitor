package es.ubu.lsi.ubumonitor.controllers;

import java.io.ByteArrayInputStream;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

public class SelectionUserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectionUserController.class);
	public static final Image DEFAULT_IMAGE = new Image("/img/default_user.png", 50, 50, false, false);

	@FXML
	private Label labelNotEnrolled;

	@FXML
	private TextField textFieldNotEnrolled;

	private AutoCompletionBinding<EnrolledUser> autoCompletionBindingNotEnrolled;
	@FXML
	private TabPane tabPane;

	@FXML
	private Tab tabEnrolled;
	@FXML
	private Tab tabNotEnrolled;

	@FXML
	private Label lblCountParticipants;
	@FXML
	private ListView<EnrolledUser> listParticipants;
	private FilteredList<EnrolledUser> filteredEnrolledList;

	@FXML
	private ListView<EnrolledUser> listParticipantsOut;
	@FXML
	private CheckComboBox<Role> checkComboBoxRole;

	@FXML
	private CheckComboBox<Group> checkComboBoxGroup;

	@FXML
	private CheckComboBox<LastActivity> checkComboBoxActivity;

	@FXML
	private TextField tfdParticipants;
	private AutoCompletionBinding<EnrolledUser> autoCompletionBinding;

	private MainController mainController;
	private Map<Tab, ListView<EnrolledUser>> tabListView;

	public void init(MainController mainController, Course course, MainConfiguration mainConfiguration) {
		this.mainController = mainController;
		// ocultamos los usuarios cuando se selecciona el la pestaña del calendario
		tabPane.visibleProperty()
				.bind(mainController.getWebViewTabsController()
						.getCalendarEventTab()
						.selectedProperty()
						.not());
		// deshabilitamos los usuaraios no matriculados en la pestaña de clustering
		tabNotEnrolled.disableProperty()
				.bind(mainController.getWebViewTabsController()
						.getClusteringTab()
						.selectedProperty());
		tabPane.getSelectionModel()
				.selectedItemProperty()
				.addListener((ov, old, newValue) -> {
					SelectionController selectionController = mainController.getSelectionController();
					if (newValue.equals(tabNotEnrolled)) {
						selectionController.getTabActivity()
								.setDisable(true);
						selectionController.getTabUbuGrades()
								.setDisable(true);
						selectionController.getTabPane()
								.getSelectionModel()
								.select(selectionController.getTabUbuLogs());
					} else if (old.equals(tabNotEnrolled)) {
						selectionController.getTabActivity()
								.setDisable(false);
						selectionController.getTabUbuGrades()
								.setDisable(false);
					}
					this.mainController.getActions()
							.updateListViewEnrolledUser();
				});

		initEnrolledUsers(course, mainConfiguration);
		initNotEnrolledUsers(course);
		tabListView = new HashMap<>();
		tabListView.put(tabEnrolled, listParticipants);
		tabListView.put(tabNotEnrolled, listParticipantsOut);
	}

	private void initEnrolledUsers(Course course, MainConfiguration mainConfiguration) {
		// Mostramos nº participantes

		tfdParticipants.setOnAction(event -> filterParticipants());

		initEnrolledUsersListView(course);
		autoCompletionBinding = UtilMethods.createAutoCompletionBinding(tfdParticipants, filteredEnrolledList);
		Group dummyGroup = new Group(-1);
		dummyGroup.setGroupName(I18n.get("text.selectall"));
		UtilMethods.fillCheckComboBox(dummyGroup, course.getGroups()
				.stream()
				.sorted(Comparator.comparing(Group::getGroupName, Comparator.nullsLast(Collator.getInstance())))
				.collect(Collectors.toList()), checkComboBoxGroup);
		checkComboBoxGroup.getCheckModel().clearChecks();
		ObservableList<Group> groups = mainConfiguration.getValue(MainConfiguration.GENERAL, "initialGroups");
		
		if (groups != null) {
			groups.forEach(checkComboBoxGroup.getCheckModel()::check);
		}

		checkComboBoxGroup.getCheckModel()
				.getCheckedItems()
				.addListener((Change<? extends Group> g) -> filterParticipants());

		checkComboBoxRole.getItems()
				.addAll(course.getRoles());
		ObservableList<Role> roles = Controller.getInstance()
				.getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "initialRoles");
		if (roles != null) {
			roles.forEach(checkComboBoxRole.getCheckModel()::check);
		}
		checkComboBoxRole.getCheckModel()
				.getCheckedItems()
				.addListener((Change<? extends Role> r) -> filterParticipants());

		checkComboBoxActivity.getItems()
				.addAll(LastActivityFactory.DEFAULT.getAllLastActivity());
		ObservableList<LastActivity> lastActivities = mainConfiguration.getValue(MainConfiguration.GENERAL,
				"initialLastActivity");
		if (lastActivities != null) {
			lastActivities.forEach(checkComboBoxActivity.getCheckModel()::check);
		}

		checkComboBoxActivity.getCheckModel()
				.getCheckedItems()
				.addListener((Change<? extends LastActivity> l) -> filterParticipants());

		lblCountParticipants.textProperty()
				.bind(Bindings.size(filteredEnrolledList)
						.asString());
		filterParticipants();
	}

	/**
	 * Inicializa la lista de usuarios.
	 */
	private void initEnrolledUsersListView(Course course) {

		Set<EnrolledUser> users = course.getEnrolledUsers();

		ObservableList<EnrolledUser> observableUsers = FXCollections.observableArrayList(users);
		observableUsers.sort(EnrolledUser.getNameComparator());
		filteredEnrolledList = new FilteredList<>(observableUsers);
		filteredEnrolledList.predicateProperty()
				.addListener(p -> mainController.getActions()
						.updatePredicadeEnrolledList());
		// Activamos la selección múltiple en la lista de participantes
		listParticipants.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);

		listParticipants.getSelectionModel()
				.getSelectedItems()
				.addListener((Change<? extends EnrolledUser> usersSelected) -> mainController.getActions()
						.updateListViewEnrolledUser());

		/// Mostramos la lista de participantes
		listParticipants.setItems(filteredEnrolledList);

		listParticipants.setCellFactory(callback -> new ListCell<EnrolledUser>() {
			@Override
			public void updateItem(EnrolledUser user, boolean empty) {
				super.updateItem(user, empty);
				if (empty || user == null) {
					setText(null);
					setGraphic(null);
					setOnMouseClicked(null);
					setContextMenu(null);
				} else {
					Instant lastCourseAccess = user.getLastcourseaccess();
					Instant lastAccess = user.getLastaccess();
					Instant lastLogInstant = Controller.getInstance()
							.getUpdatedCourseData()
							.toInstant();

					setText(user + "\n" + I18n.get("label.course")
							+ UtilMethods.formatDates(lastCourseAccess, lastLogInstant) + " | "
							+ I18n.get("text.moodle") + UtilMethods.formatDates(lastAccess, lastLogInstant));

					setTextFill(LastActivityFactory.DEFAULT.getColorActivity(lastCourseAccess, lastLogInstant));

					try {
						Image image = new Image(new ByteArrayInputStream(user.getImageBytes()), 50, 50, false, false);
						setGraphic(new ImageView(image));

					} catch (Exception e) {
						LOGGER.error("No se ha podido cargar la imagen de: {}", user);
						setGraphic(new ImageView(DEFAULT_IMAGE));
					}
					ContextMenu menu = new ContextMenu();
					MenuItem seeUser = new MenuItem(I18n.get("text.see") + user);
					seeUser.setOnAction(e -> userInfo(user));
					menu.getItems()
							.addAll(seeUser);
					setContextMenu(menu);
					setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
							userInfo(user);
						}
					});
				}
			}

		});
	}

	private void initNotEnrolledUsers(Course course) {

		
		
		ObservableList<EnrolledUser> user = FXCollections.observableArrayList(course.getNotEnrolledUser());
		
		String rules = ((RuleBasedCollator) Collator.getInstance()).getRules();
		RuleBasedCollator correctedCollator;
		try {
			correctedCollator = new RuleBasedCollator(rules.replaceAll("<'\\u005f'", "<' '<'\\u005f'")); // añadimos el espacio antes del underscore
		} catch (ParseException e1) {
			correctedCollator = (RuleBasedCollator) Collator.getInstance();
		} 
	
		
		user.sort(Comparator.comparing(EnrolledUser::getFullName, Comparator.nullsLast(correctedCollator)));
		FilteredList<EnrolledUser> filteredListNotEnrolled = new FilteredList<>(user);
		filteredListNotEnrolled.predicateProperty()
				.addListener(value -> mainController.getActions()
						.updatePredicadeEnrolledList());
		listParticipantsOut.setItems(filteredListNotEnrolled);
		listParticipantsOut.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);

		listParticipantsOut.setCellFactory(callback -> new ListCell<EnrolledUser>() {
			@Override
			public void updateItem(EnrolledUser user, boolean empty) {
				super.updateItem(user, empty);
				if (empty || user == null) {
					setText(null);
					setGraphic(null);
				} else {
					Instant lastAccess = user.getLastaccess();
					Instant lastLogInstant = Controller.getInstance()
							.getUpdatedCourseData()
							.toInstant();

					setText(user + "\n" + I18n.get("text.moodle")
							+ UtilMethods.formatDates(lastAccess, lastLogInstant));

					try {
						if (user.getImageBytes() != null) {
							Image image = new Image(new ByteArrayInputStream(user.getImageBytes()), 50, 50, false,
									false);
							setGraphic(new ImageView(image));
						} else {
							setGraphic(new ImageView(DEFAULT_IMAGE));
						}

					} catch (Exception e) {
						LOGGER.warn("No se ha podido cargar la imagen de: {}", user);
						setGraphic(new ImageView(DEFAULT_IMAGE));
					}
					ContextMenu menu = new ContextMenu();
					MenuItem seeUser = new MenuItem(I18n.get("text.see") + user);
					seeUser.setOnAction(e -> userInfo(user));
					menu.getItems()
							.addAll(seeUser);
					setContextMenu(menu);
					setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
							userInfo(user);
						}
					});
				}
			}

		});
		listParticipantsOut.getSelectionModel()
				.getSelectedItems()
				.addListener((Change<? extends EnrolledUser> usersSelected) -> {
					mainController.getActions()
							.updateListViewEnrolledUser();
					autoCompletionBindingNotEnrolled.dispose();
					autoCompletionBinding = UtilMethods.createAutoCompletionBinding(textFieldNotEnrolled,
							filteredListNotEnrolled);
				});
		labelNotEnrolled.textProperty()
				.bind(Bindings.size(filteredListNotEnrolled)
						.asString());
		textFieldNotEnrolled.setOnAction(event -> {
			String text = textFieldNotEnrolled.getText()
					.toLowerCase();
			filteredListNotEnrolled.setPredicate(e -> (text.isEmpty() || e.getFullName()
					.toLowerCase()
					.contains(text)));
		});

		autoCompletionBindingNotEnrolled = UtilMethods.createAutoCompletionBinding(textFieldNotEnrolled,
				filteredListNotEnrolled);
	}

	public void userInfo(EnrolledUser enrolledUser) {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserInfo.fxml"), I18n.getResourceBundle());

		UtilMethods.createDialog(loader, Controller.getInstance()
				.getStage());

		UserInfoController userInfoController = loader.getController();
		userInfoController.init(mainController, enrolledUser);

	}

	/**
	 * Filtra los participantes según el rol, el grupo y el patrón indicados
	 */
	public void filterParticipants() {
		List<Role> rol = checkComboBoxRole.getCheckModel()
				.getCheckedItems();
		List<Group> group = checkComboBoxGroup.getCheckModel()
				.getCheckedItems();
		List<LastActivity> lastActivity = checkComboBoxActivity.getCheckModel()
				.getCheckedItems();
		String textField = tfdParticipants.getText()
				.toLowerCase();
		Instant lastLogInstant = Controller.getInstance()
				.getUpdatedCourseData()
				.toInstant();
		filteredEnrolledList.setPredicate(e -> (checkUserHasRole(rol, e)) && (checkUserHasGroup(group, e))
				&& (textField.isEmpty() || e.getFullName()
						.toLowerCase()
						.contains(textField))
				&& (lastActivity
						.contains(LastActivityFactory.DEFAULT.getActivity(e.getLastcourseaccess(), lastLogInstant))));
		autoCompletionBinding.dispose();
		autoCompletionBinding = UtilMethods.createAutoCompletionBinding(tfdParticipants, filteredEnrolledList);
	}

	private boolean checkUserHasGroup(List<Group> groups, EnrolledUser user) {
		if (groups.isEmpty())
			return true;
		for (Group group : groups) {
			if (group.contains(user)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkUserHasRole(List<Role> roles, EnrolledUser user) {
		for (Role rol : roles) {
			if (rol.contains(user)) {
				return true;
			}
		}
		return false;
	}

	public Label getLblCountParticipants() {
		return lblCountParticipants;
	}

	public ListView<EnrolledUser> getListParticipants() {
		return tabListView.get(tabPane.getSelectionModel()
				.getSelectedItem());

	}

	public List<EnrolledUser> getFilteredUsers() {
		List<EnrolledUser> user = new ArrayList<>(tabListView.get(tabPane.getSelectionModel()
				.getSelectedItem())
				.getItems());
		user.removeAll(Collections.singletonList(null));
		return user;
	}

	public List<EnrolledUser> getSelectedUsers() {
		List<EnrolledUser> user = new ArrayList<>(tabListView.get(tabPane.getSelectionModel()
				.getSelectedItem())
				.getSelectionModel()
				.getSelectedItems());
		user.removeAll(Collections.singletonList(null));
		return user;
	}

	public CheckComboBox<Role> getCheckComboBoxRole() {
		return checkComboBoxRole;
	}

	public CheckComboBox<Group> getCheckComboBoxGroup() {
		return checkComboBoxGroup;
	}

	public CheckComboBox<LastActivity> getCheckComboBoxActivity() {
		return checkComboBoxActivity;
	}

	public TextField getTfdParticipants() {
		return tfdParticipants;
	}

	public AutoCompletionBinding<EnrolledUser> getAutoCompletionBinding() {
		return autoCompletionBinding;
	}

	public void selectAllUsers() {
		listParticipants.getSelectionModel()
				.selectAll();
	}

	public void selectAllNonUsers() {
		listParticipantsOut.getSelectionModel()
				.selectAll();
	}

}
