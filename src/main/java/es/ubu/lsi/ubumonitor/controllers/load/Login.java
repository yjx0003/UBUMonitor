package es.ubu.lsi.ubumonitor.controllers.load;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.tool.mobile.ToolMobileGetPublicConfig;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login {
	private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

	private String sesskey;
	private WebService webService;
	private int typeoflogin;
	private String launchurl;
	private static final int DEFAULT_TYPE_OF_LOGIN =1;
	public Login() {
		typeoflogin = DEFAULT_TYPE_OF_LOGIN;
	}

	/**
	 * Intenta buscar el token de acceso a la REST API de moodle e iniciar sesion en
	 * la pagina de moodle.
	 * 
	 * @param host     servidor moodle
	 * @param username nombre de usuario
	 * @param password contraseña
	 * @throws IOException si no ha podido conectarse o la contraseña es erronea
	 */
	public void tryLogin(String host, String username, String password) throws IOException {

		try (Response response = WebService.getAjaxResponse(host, new ToolMobileGetPublicConfig())) {
			JSONObject data = new JSONArray(new JSONTokener(response.body()
					.byteStream())).getJSONObject(0)
							.optJSONObject("data");
			if (data != null) {
				// The type of login. 1 for app, 2 for browser, 3 for embedded.
				typeoflogin = data.optInt("typeoflogin", DEFAULT_TYPE_OF_LOGIN); 
				launchurl = data.optString("launchurl"); 
			}
		}

		login(typeoflogin, host, launchurl, username, password);

	}

	public void reLogin(String host, String username, String password) throws IOException {

		login(typeoflogin, host, launchurl, username, password);

	}

	/**
	 * Se loguea en el servidor de moodle mediante web scraping.
	 * 
	 * @param username nombre de usuario de la cuenta
	 * @param password password contraseña
	 * @return las cookies que se usan para navegar dentro del servidor despues de
	 *         loguearse
	 * @throws IOException
	 * @throws IllegalStateException si no ha podido loguearse
	 */
	private void login(int typeoflogin, String host, String launchurl, String username, String password)
			throws IOException {
		LOGGER.info("Logeandose para web scraping");

		if (typeoflogin == 1) {
			normalLogin(host, username, password);
		} else {
			loginWebView(host, launchurl + "?service=local_mobile&urlscheme=moodlemobile&passport="
					+ UtilMethods.ranmdomAlphanumeric(), Controller.getInstance().getStage());
		}

	}

	public void normalLogin(String host, String username, String password) throws IOException {
		webService = new WebService(host, username, password);
		String hostLogin = host + "/login/index.php";
		try (Response response = Connection.getResponse(hostLogin)) {
			String redirectedUrl = response.request()
					.url()
					.toString();
			Document loginDoc = Jsoup.parse(response.body()
					.byteStream(), null, hostLogin);
			Element e = loginDoc.selectFirst("input[name=logintoken]");
			String logintoken = (e == null) ? "" : e.attr("value");

			RequestBody formBody = new FormBody.Builder().add("username", username)
					.add("password", password)
					.add("logintoken", logintoken)
					.build();
			String html = Connection.getResponse(new Request.Builder().url(redirectedUrl)
					.post(formBody)
					.build())
					.body()
					.string();

			sesskey = findSesskey(html);
		}
	}

	public void loginWebView(String host, String launchurl, Window owner) {

		CompletableFuture.runAsync(() -> {

			Pattern pattern = Pattern.compile("^moodlemobile://token=(\\w+)");
			Stage popup = UtilMethods.createStage(owner, Modality.WINDOW_MODAL);
			WebView webView = new WebView();
			webView.getEngine()
					.locationProperty()
					.addListener((ov, old, newValue) -> {
						try (Response response = Connection.getResponse(newValue)) {
							String location = response.header("location");
							if (location == null)
								return;
							Matcher matcher = pattern.matcher(location);
							if (matcher.find()) {
								byte[] decode = Base64.getDecoder()
										.decode(matcher.group(1));
								Pattern patternToken = Pattern.compile("^\\w+:::(\\w+)");
								matcher = patternToken.matcher(new String(decode));
								if (matcher.find()) {
									webService = new WebService(host, matcher.group(1));
								} else {
									throw new IllegalAccessError(
											"Cannot get the token in the decoded: " + matcher);
								}

								try (Response sesskeyResponse = Connection.getResponse(host)) {
									sesskey = findSesskey(sesskeyResponse.body()
											.string());
									popup.close();
								}

							}
						} catch (Exception e) {
							LOGGER.error("Error al intentar loguearse", e);
							throw new IllegalStateException("No se ha podido loguear al servidor");
						}
					});
			webView.getEngine()
					.load(launchurl);

			popup.setScene(new Scene(webView, 960, 600));
			popup.showAndWait();
		}, Platform::runLater)
				.join();
		if (webService == null) {
			throw new IllegalStateException(I18n.get("error.windowclosed"));
		}
	}

	public String findSesskey(String html) {
		Pattern pattern = Pattern.compile("sesskey=(\\w+)");
		Matcher m = pattern.matcher(html);
		if (m.find()) {
			LOGGER.info("Sesskey found");
			return m.group(1);
		}
		LOGGER.warn("Didn't found a sesskey in principal page after login: {}", html);
		return null;
	}

	public String getSesskey() {
		return sesskey;
	}

	public void setSesskey(String sesskey) {
		this.sesskey = sesskey;
	}

	public WebService getWebService() {
		return webService;
	}

	public void setWebService(WebService webService) {
		this.webService = webService;
	}

}
