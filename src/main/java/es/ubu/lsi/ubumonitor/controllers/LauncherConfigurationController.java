package es.ubu.lsi.ubumonitor.controllers;

import java.io.File;
import java.util.Optional;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

public class LauncherConfigurationController {
	@FXML
	private DialogPane dialogPane;

	@FXML
	private CheckBox checkBox;

	@FXML
	private CheckBox checkBoxBetaTester;

	@FXML
	private ChoiceBox<File> choiceBox;

	@FXML
	private Label label;

	private boolean betaTester;

	private boolean checkAgain;

	public File init(boolean askAgain, boolean isBetaTester, String pathActualVersion) {
		checkAgain = askAgain;
		betaTester = isBetaTester;
		File directory = new File(".");
		File actualVersion = new File(directory, pathActualVersion);

		File[] files = directory.listFiles((dir, name) -> name.matches(AppInfo.PATTERN_FILE));
		checkBox.setSelected(askAgain);
		checkBoxBetaTester.setSelected(isBetaTester);

		if (files != null && files.length != 0) {
			choiceBox.getItems()
					.setAll(files);
			choiceBox.getSelectionModel()
					.select(actualVersion);
		}

		choiceBox.setConverter(getFileConverter());

		Alert alert = UtilMethods.createAlert(AlertType.CONFIRMATION);

		alert.setDialogPane(dialogPane);

		Optional<ButtonType> buttonType = alert.showAndWait();
		if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
			betaTester = checkBoxBetaTester.isSelected();
			checkAgain = checkBox.isSelected();
			if (choiceBox.getValue() != null) {
				return choiceBox.getValue();
			}

		}

		return actualVersion;

	}

	/**
	 * @return the askAgain
	 */
	public boolean isAskAgain() {
		return checkAgain;
	}

	/**
	 * @return if is beta tester
	 */
	public boolean isBetaTester() {
		return betaTester;
	}

	public StringConverter<File> getFileConverter() {
		return new StringConverter<File>() {

			@Override
			public String toString(File object) {
				return object.getName();
			}

			@Override
			public File fromString(String string) {
				return null;
			}
		};
	}

}
