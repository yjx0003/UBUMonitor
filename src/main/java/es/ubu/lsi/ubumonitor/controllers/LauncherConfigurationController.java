package es.ubu.lsi.ubumonitor.controllers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.stage.StageStyle;

public class LauncherConfigurationController {
	@FXML
	private DialogPane dialogPane;

	@FXML
	private CheckBox checkBox;

	@FXML
	private ChoiceBox<String> choiceBox;

	private boolean askAgain;

	private File actualVersion;

	public void init(boolean askAgain, File pathActualVersion) {
		this.askAgain = askAgain;
		actualVersion = pathActualVersion;
		File directory = pathActualVersion.getParentFile();
		File[] files = directory.listFiles((dir, value) -> value.endsWith(".jar"));
		if (files == null || files.length == 0) {
			Alert alert = UtilMethods.createAlert(AlertType.WARNING);
			Hyperlink hyperLink = new Hyperlink(directory.getAbsolutePath());
			hyperLink.setOnAction(e -> UtilMethods.openFileFolder(directory));
			TextFlow flow = new TextFlow(new Text(I18n.get("warning.cannotfind") + "\n"), hyperLink);
			alert.getDialogPane()
					.setContent(flow);
			alert.show();
			
			return;
		}
		List<String> versions = Arrays.stream(files)
				.map(File::getName)
				.collect(Collectors.toList());
		
		checkBox.setSelected(askAgain);

		choiceBox.getItems()
				.setAll(versions);

		choiceBox.getSelectionModel()
				.select(pathActualVersion.getName());
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setDialogPane(dialogPane);
		Optional<ButtonType> buttonType = alert.showAndWait();
		if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
			this.askAgain = checkBox.isSelected();
			this.actualVersion = new File(directory, choiceBox.getValue());
		}

	}

	/**
	 * @return the askAgain
	 */
	public boolean isAskAgain() {
		return askAgain;
	}

	/**
	 * @return the actualVersion
	 */
	public File getActualVersion() {
		return actualVersion;
	}
}
