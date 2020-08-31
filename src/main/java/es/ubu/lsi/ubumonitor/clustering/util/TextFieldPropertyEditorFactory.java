package es.ubu.lsi.ubumonitor.clustering.util;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.PropertyEditor;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Generador de TextFields con autocompletado.
 * 
 * @author Xing Long Ji
 *
 */
public class TextFieldPropertyEditorFactory implements Callback<Item, PropertyEditor<?>> {

	private List<Editor> editors = new ArrayList<>();
	private List<String> suggestions;

	/**
	 * Constructor con una lista de sugerencias.
	 * 
	 * @param suggestions lista de sugerencias
	 */
	public TextFieldPropertyEditorFactory(List<String> suggestions) {
		this.suggestions = suggestions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyEditor<?> call(Item param) {
		TextField textField = new TextField();
		Editor editor = new Editor(param, textField, suggestions);
		editors.add(editor);
		return editor;
	}

	/**
	 * Añade una sugerencia si no estaba.
	 * @param sugerencia texto de sugerencia
	 */
	public void add(String sugerencia) {
		if (!suggestions.contains(sugerencia)) {
			suggestions.add(sugerencia);
			refresh();
		}
	}

	/**
	 * Actualiza los editores.
	 */
	public void refresh() {
		for (Editor editor : editors) {
			editor.refresh();
		}
	}

	private class Editor extends AbstractPropertyEditor<String, TextField> {

		private AutoCompletionBinding<String> autoCompletion;

		private Editor(Item property, TextField control, List<String> list) {
			super(property, control);
			setup(list);
		}

		private void setup(List<String> list) {
			autoCompletion = TextFields.bindAutoCompletion(getEditor(), list);
			autoCompletion.setDelay(0);
		}

		private void refresh() {
			autoCompletion.dispose();
			setup(suggestions);
		}

		@Override
		public void setValue(String value) {
			getEditor().setText(value);
		}

		@Override
		protected ObservableValue<String> getObservableValue() {
			return getEditor().textProperty();
		}

	}

}
