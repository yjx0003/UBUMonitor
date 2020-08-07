package es.ubu.lsi.ubumonitor.controllers;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.util.I18n;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class SelectionForumController {

	@FXML
	private ListView<CourseModule> listViewForum;
	private FilteredList<CourseModule> filteredForum;

	@FXML
	private TextField textFieldForum;

	@FXML
	private CheckComboBox<Section> checkComboBoxForumSection;

	@FXML
	private CheckBox checkBoxForum;

	@FXML
	private TabPane tabPane;

	public void init(MainController mainController) {

		tabPane.visibleProperty()
				.bind(mainController.getForumTab()
						.selectedProperty());

		fillForumListView(mainController);
	}

	private void fillForumListView(MainController mainController) {
		filteredForum = new FilteredList<>(Controller.getInstance()
				.getActualCourse()
				.getModules()
				.stream()
				.filter(cm -> cm.getModuleType() == ModuleType.FORUM)
				.collect(Collectors.toCollection(FXCollections::observableArrayList)));
		listViewForum.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewForum.setItems(filteredForum);

		Section dummySection = new Section(-1);
		dummySection.setName(I18n.get("text.selectall"));
		fillCheckComboBox(dummySection, Controller.getInstance()
				.getActualCourse()
				.getSections(), checkComboBoxForumSection);

		filteredForum.predicateProperty()
				.addListener(p -> mainController.updateListViewEnrolledUser());

		textFieldForum.textProperty()
				.addListener((ob, oldValue, newValue) -> onChange());

		checkBoxForum.selectedProperty()
				.addListener(c -> onChange());
		checkComboBoxForumSection.getCheckModel()
				.getCheckedItems()
				.addListener((Change<? extends Section> c) -> onChange());
		onChange();
	}

	private <T> void fillCheckComboBox(T dummy, Collection<T> values, CheckComboBox<T> checkComboBox) {
		if (values == null || values.isEmpty()) {
			return;
		}

		checkComboBox.getItems()
				.add(dummy);
		checkComboBox.getItems()
				.addAll(values);
		checkComboBox.getCheckModel()
				.checkAll();

		checkComboBox.getItemBooleanProperty(0)
				.addListener((observable, oldValue, newValue) -> {
					if (newValue.booleanValue()) {
						checkComboBox.getCheckModel()
								.checkAll();
					} else {
						checkComboBox.getCheckModel()
								.clearChecks();
					}

				});

	}

	private void onChange() {
		filteredForum.setPredicate(getForumPredicate());
		listViewForum.setCellFactory(getListCellCourseModule());
	}

	private Predicate<CourseModule> getForumPredicate() {
		return forum -> (checkBoxForum.isSelected() || forum.isVisible()) && (textFieldForum.getText()
				.isEmpty()
				|| forum.getModuleName()
						.toLowerCase()
						.contains(textFieldForum.getText()))
				&& checkComboBoxForumSection.getCheckModel()
						.getCheckedItems()
						.contains(forum.getSection());
	}

	private Callback<ListView<CourseModule>, ListCell<CourseModule>> getListCellCourseModule() {
		return callback -> new ListCell<CourseModule>() {
			@Override
			public void updateItem(CourseModule courseModule, boolean empty) {
				super.updateItem(courseModule, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(courseModule.getModuleName());

					setTextFill(courseModule.isVisible() ? Color.BLACK : Color.GRAY);

					try {
						Image image = new Image(AppInfo.IMG_DIR + courseModule.getModuleType()
								.getModName() + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		};
	}

	public void selectAll() {

		listViewForum.getSelectionModel()
				.selectAll();
	}

}
