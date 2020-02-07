package clustering.controller;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clustering.algorithm.Algorithm;
import clustering.algorithm.Algorithms;
import controllers.I18n;
import controllers.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ClusteringController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringController.class);
	
	private MainController mainController;

	@FXML
	private PropertySheet propertySheet;
	
	@FXML
	private ListView<Algorithm> algorithmList;

	@FXML
	private Button executeBtn;

	@FXML
	private WebView webView;
	
	private static final Callback<Item, PropertyEditor<?>> DEFAULT_PROPERTY_EDITOR_FACTORY = new DefaultPropertyEditorFactory();

	@SuppressWarnings("unchecked")
	public void init(MainController controller) {
		mainController = controller;
		algorithmList.setItems(FXCollections.observableArrayList(Algorithms.getAlgorithms()));
		algorithmList.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> changeAlgorithm(newValue));
		algorithmList.getSelectionModel().selectFirst();
		StringConverter<DistanceMeasure> stringConverter = new StringConverter<DistanceMeasure>() {

			@Override
			public String toString(DistanceMeasure object) {
				return I18n.get("clustering." + object.getClass().getSimpleName());
			}

			@Override
			public DistanceMeasure fromString(String string) {
				return null;
			}
		};

		propertySheet.setPropertyEditorFactory(item -> {
			if (item.getValue() instanceof DistanceMeasure) {
				AbstractPropertyEditor<DistanceMeasure, ComboBox<DistanceMeasure>> editor = (AbstractPropertyEditor<DistanceMeasure, ComboBox<DistanceMeasure>>) Editors
						.createChoiceEditor(item, Algorithms.DISTANCES_LIST);
				editor.getEditor().setConverter(stringConverter);
				return editor;
			}
			return DEFAULT_PROPERTY_EDITOR_FACTORY.call(item);
		});
	}

	private void changeAlgorithm(Algorithm algorithm) {
		propertySheet.getItems().setAll(algorithm.getParameters().getPropertyItems());
	}
}
