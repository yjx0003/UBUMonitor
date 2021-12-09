package es.ubu.lsi.ubumonitor.util;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
/**
 * The class is responsible for sending the responses to Microsoft Forms
 * @author Yi Peng Ji
 *
 */
public class MicrosoftForms {

	// limite de caracters impuesto por Microsoft Forms
	public static final int LIMIT_CHARACTERS = 4000;
	private static final MediaType MEDIA_TYPE = MediaType.get("application/json");

	private JSONArray answersArray = new JSONArray();
	private JSONObject answers = new JSONObject();
	private String postURL;

	public MicrosoftForms(String postURL) {
		this.postURL = postURL;
		
	}
	
	/**
	 * Add answer using the questionId in the form
	 * @param questionId question id
	 * @param answer the answer limited to last 4000 characters
	 */
	public void addAnswer(String questionId, String answer) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("questionId", questionId);
		// solo se mandan los primeros 4000 caracteres
		jsonObject.put("answer1", answer.substring(0, Math.min(LIMIT_CHARACTERS, answer.length())));
		answersArray.put(jsonObject);
	}

	/**
	 * The request after POST the answers using the POST URL 
	 * @return return the request after POST the answers 
	 */
	public Request getRequest() {
		answers.put("answers", answersArray.toString());
		
		RequestBody requestBody = RequestBody.create(answers.toString(), MEDIA_TYPE);
		
		return new Request.Builder()
				.url(postURL)
				.addHeader("content-type", "application/json")
				.post(requestBody)
				.build();
		
	}
}
