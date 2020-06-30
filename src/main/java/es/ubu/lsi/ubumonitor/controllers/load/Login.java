package es.ubu.lsi.ubumonitor.controllers.load;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
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
	private static final String HTTP = "http://";

	private static final String HTTPS = "https://";

	private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

	private static final String HOST_LOGIN_DEFAULT_PATH = "/login/index.php";
	private static final Pattern PATTERN_MOODLE_MOBILE = Pattern.compile("^moodlemobile://token=(\\w+)");
	private static final Pattern PATTERN_TOKEN = Pattern.compile("^\\w+:::(\\w+)(:::(\\w+))?");
	private static final int DEFAULT_TYPE_OF_LOGIN = 1;

	private String sesskey;
	private WebService webService;
	private int typeoflogin;
	private String launchurl;
	private WebView webView;

	public Login() {

		webService = new WebService();
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
		boolean hasError = true;
		boolean coreSupported = false;
		try (Response response = Connection.getResponse(host + "/local/mobile/check.php?service=local_mobile")) {
			JSONObject jsonObject = new JSONObject(response.body()
					.string());
			hasError = jsonObject.optInt("error", 1) == 1;
			if (!hasError) {
				// The type of login. 1 for app, 2 for browser, 3 for embedded.
				typeoflogin = Integer.valueOf(jsonObject.optString("code"));
				coreSupported = jsonObject.optInt("coresupported") == 1;
			}
			if (typeoflogin != DEFAULT_TYPE_OF_LOGIN) {
				launchurl = host + "/local/mobile/launch.php?service=local_mobile&urlscheme=moodlemobile&passport=1";
			}

		} catch (Exception e) {
			typeoflogin = DEFAULT_TYPE_OF_LOGIN;

			LOGGER.info("has not launch url");
		}

		if (hasError || (coreSupported && typeoflogin != DEFAULT_TYPE_OF_LOGIN)) {
			toolMobileGetPublicConfig(host);

		}

		login(typeoflogin, host, launchurl, username, password);

	}

	public void toolMobileGetPublicConfig(String host) throws IllegalAccessError {
		try (Response response = WebService.getAjaxResponse(host, new ToolMobileGetPublicConfig())) {
			JSONObject data = new JSONArray(new JSONTokener(response.body()
					.byteStream())).getJSONObject(0)
							.optJSONObject("data");
			if (data != null) {

				launchurl = data.getString("launchurl") + "?service=local_mobile&urlscheme=moodlemobile&passport=1";
				typeoflogin = data.optInt("typeoflogin", DEFAULT_TYPE_OF_LOGIN);
				if (data.optInt("enablemobilewebservice", 1) == 0) {
					throw new IllegalAccessError("Mobile web service not enabled");
				}
				try (Response responseLaunch = Connection.getResponse(launchurl)) {
					if (responseLaunch.code() != 200) {
						launchurl = null;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.info("not toolMobileGetPublicConfig");
		}
	}

	public void reLogin(String host, String username, String password) throws IOException {
		sesskey = null;
		launchurl = null;
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

		if (launchurl != null && typeoflogin != DEFAULT_TYPE_OF_LOGIN) {

			LOGGER.info("Login SSO with Launch url");
			loginWebViewWithLaunchUrl(host, launchurl, Controller.getInstance()
					.getStage());

		} else {
			LOGGER.info("Login normal");
			normalLogin(host, username, password);
		}

	}

	public void normalLogin(String host, String username, String password) throws IOException {
		webService = new WebService(host, username, password);
		String hostLogin = host + HOST_LOGIN_DEFAULT_PATH;
		LOGGER.info("Logeandose para web scraping");
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
			if (sesskey == null) {
				LOGGER.info("cannot login in the login/index.php page, trying with webview");
				loginWithWebView(host + HOST_LOGIN_DEFAULT_PATH, Controller.getInstance()
						.getStage());
			}
		}
	}

	private void loginWithWebView(String host, Window owner) {
		CompletableFuture.runAsync(() -> {
			Stage popup = UtilMethods.createStage(owner, Modality.WINDOW_MODAL);
			setWebview(host, popup, (ov, old, newValue) -> {
				if (newValue != Worker.State.SUCCEEDED)
					return;
				try (Response response = Connection.getResponse(webView.getEngine()
						.getLocation())) {

					sesskey = findSesskey(response.body()
							.string());
					if (sesskey != null) {
						popup.close();
					}
				} catch (Exception e) {
					LOGGER.error("Error al intentar loguearse", e);
					throw new IllegalStateException("No se ha podido loguear al servidor");
				}
			});
		}, Platform::runLater)
				.join();
	}

	public void loginWebViewWithLaunchUrl(String host, String launchurl, Window owner) {
		CompletableFuture.runAsync(() -> {

			Stage popup = UtilMethods.createStage(owner, Modality.WINDOW_MODAL);
			setWebview(launchurl, popup, launcherLogin(host, popup));

		}, Platform::runLater)
				.join();
		if (webService == null) {
			throw new IllegalStateException(I18n.get("error.windowclosed"));
		}
	}

	public void setWebview(String launchurl, Stage popup, ChangeListener<Worker.State> listenner) {

		webView = new WebView();
		webView.getEngine()
				.getLoadWorker()
				.stateProperty()
				.addListener(listenner);
		webView.getEngine()
				.load(launchurl);
		popup.setMaximized(true);
		popup.setScene(new Scene(webView, 960, 600));
		popup.showAndWait();

	}

	public ChangeListener<Worker.State> launcherLogin(String host, Stage popup) {
		return (ov, old, newValue) -> {

			if (newValue != Worker.State.FAILED)
				return; // failed if try to go url scheme MOODLE_LOCAL_SERVICE

			try (Response response = Connection.getResponse(webView.getEngine()
					.getLocation())) {
				String location = response.header("location");

				if (location == null)
					return;
				Matcher matcher = PATTERN_MOODLE_MOBILE.matcher(location);
				if (matcher.find()) {
					byte[] decode = Base64.getDecoder()
							.decode(matcher.group(1));

					matcher = PATTERN_TOKEN.matcher(new String(decode));
					if (matcher.find()) {

						webService.setData(host, matcher.group(1), matcher.group(3));
					} else {
						throw new IllegalAccessError("Cannot get the token in the decoded: " + matcher);
					}
					popup.close();
					try (Response sesskeyResponse = Connection.getResponse(host)) {
						sesskey = findSesskey(sesskeyResponse.body()
								.string());

					}

				}
			} catch (Exception e) {
				LOGGER.error("Error al intentar loguearse", e);
				throw new IllegalStateException("No se ha podido loguear al servidor");
			}
		};
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

	public String checkUrlServer(String host) throws MalformedURLException {
		String url = convertToHttps(host);
		URL httpsUrl = new URL(url);
		if (checkWebsService(httpsUrl)) {
			return httpsUrl.toString();
		}

		url = url.replaceFirst(HTTPS, HTTP);
		URL httpUrl = new URL(url);
		if (checkWebsService(httpUrl)) {
			return httpUrl.toString();
		}
		throw new IllegalArgumentException(I18n.get("error.malformedurl"));

	}

	private boolean checkWebsService(URL url) {

		try (Response response = Connection.getResponse(url + "/login/token.php")) {
			JSONObject jsonObject = new JSONObject(response.body()
					.string());
			return jsonObject.has("error");

		} catch (IOException e) {
			LOGGER.info("Has not protocol", e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(I18n.get("error.malformedurl"), e);
		}

		return false;
	}

	private String convertToHttps(String host) {
		String url;

		if (!host.matches("^(?i)https?://.*$")) {

			url = HTTPS + host;
		} else if (host.matches("^(?i)http://.*$")) {
			url = host.replaceFirst("(?i)http://", HTTPS);
		} else {
			url = host;
		}

		return url;
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
