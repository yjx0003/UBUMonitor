package es.ubu.lsi.ubumonitor.controllers;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

public class LauncherConfigurationController {
	@FXML
	private DialogPane dialogPane;

	@FXML
	private CheckBox checkBox;

	@FXML
	private CheckBox checkBoxBetaTester;

	@FXML
	private ChoiceBox<String> choiceBox;

	@FXML
	private Label label;

	public File init(boolean askAgain, boolean isBetaTester, File pathActualVersion) {

		File directory = pathActualVersion.getParentFile();
		File[] files = directory.listFiles((dir, value) -> value.endsWith(".jar"));

		List<String> versions = files == null || files.length == 0 ? Collections.emptyList()
				: Arrays.stream(files)
						.map(File::getName)
						.collect(Collectors.toList());

		checkBox.setSelected(askAgain);
		checkBoxBetaTester.setSelected(isBetaTester);

		choiceBox.getItems()
				.setAll(versions);

		choiceBox.getSelectionModel().select(pathActualVersion.getName());
		Alert alert = UtilMethods.createAlert(AlertType.CONFIRMATION);

		alert.setDialogPane(dialogPane);

		Optional<ButtonType> buttonType = alert.showAndWait();
		if (buttonType.isPresent() && buttonType.get() == ButtonType.OK && choiceBox.getValue() != null) {

			return new File(directory, choiceBox.getValue());

		}

		return pathActualVersion;

	}

	/**
	 * @return the askAgain
	 */
	public boolean isAskAgain() {
		return checkBox.isSelected();
	}

	/**
	 * @return if is beta tester
	 */
	public boolean isBetaTester() {
		return checkBoxBetaTester.isSelected();
	}

}
