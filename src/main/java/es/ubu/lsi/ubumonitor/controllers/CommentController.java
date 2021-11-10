package es.ubu.lsi.ubumonitor.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.controllers.configuration.RemoteConfiguration;
import es.ubu.lsi.ubumonitor.controllers.load.Connection;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.MicrosoftForms;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import okhttp3.Response;

public class CommentController implements Initializable{
	
	@FXML
	private Button buttonSend;
	
	@FXML
	private TextField textFieldEmail;
	
	@FXML
	private TextArea textAreaComment;
	
	@FXML
	private Label labelCharacterCount;
	
	@FXML
	private CheckBox checkBoxAcceptCondition;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		textAreaComment.setTextFormatter(new TextFormatter<String>(change -> 
        change.getControlNewText().length() <= MicrosoftForms.LIMIT_CHARACTERS ? change : null));
		
		buttonSend.disableProperty().bind(Bindings.isEmpty(textAreaComment.textProperty()).or(checkBoxAcceptCondition.selectedProperty().not()));
		labelCharacterCount.textProperty().bind(Bindings.length(textAreaComment.textProperty())
                .asString("%d/"+ MicrosoftForms.LIMIT_CHARACTERS));
		
	}
	
	public void openMicrosoftFormPolicy() {
		UtilMethods.openURL(I18n.get("url.microsoftFormPolicy"));
	}
	
	
	public void sendComment() {
		RemoteConfiguration remoteConfiguration = RemoteConfiguration.getInstance();
		JSONObject suggerenceBox = remoteConfiguration.getJSONObject("suggerenceBox");
		
		MicrosoftForms microsoftForms = new MicrosoftForms(suggerenceBox.getString("url"));
		microsoftForms.addAnswer(suggerenceBox.getString("emailQuestionId"), textFieldEmail.getText());
		microsoftForms.addAnswer(suggerenceBox.getString("commentQuestionId"), textAreaComment.getText());
		
		try(Response response= Connection.getResponse(microsoftForms.getRequest())){
			if(response.isSuccessful()) {
				UtilMethods.infoWindow(I18n.get("text.sended"));
				Stage stage = (Stage)buttonSend.getScene().getWindow();
				stage.close();
			} else {
				throw new IllegalStateException(response.body().string());
			}
		} catch (Exception e) {
			UtilMethods.errorWindow("Cannot send the suggerence", e);
			
		}
	}
	

	

}
