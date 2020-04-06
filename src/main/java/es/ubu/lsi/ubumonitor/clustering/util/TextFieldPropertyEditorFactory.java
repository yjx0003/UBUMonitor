package es.ubu.lsi.ubumonitor.clustering.util;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.PropertyEditor;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class TextFieldPropertyEditorFactory implements Callback<Item, PropertyEditor<?>> {

	private List<Editor> editors = new ArrayList<>();

	@Override
	public PropertyEditor<?> call(Item param) {
		TextField textField = new TextField();
		List<String> suggestions = ConfigHelper.getArray("labels");
		Editor editor = new Editor(param, textField, suggestions);
		editors.add(editor);
		return editor;
	}

	public void add(String string) {
		for (Editor editor : editors) {
			editor.addAutoCompletion(string);
		}
	}

	private static class Editor extends AbstractPropertyEditor<String, TextField> {
		
		private AutoCompletionBinding<String> suggestions;
		private List<String> list;

		public Editor(Item property, TextField control, List<String> list) {
			super(property, control);
			this.suggestions = TextFields.bindAutoCompletion(getEditor(), list);
			this.list = list;
		}
		
		private void addAutoCompletion(String string) {
			list.add(string);
			suggestions.dispose();
			suggestions = TextFields.bindAutoCompletion(getEditor(), list);
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
