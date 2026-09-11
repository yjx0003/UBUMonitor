package es.ubu.lsi.ubumonitor.controllers.load;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.control.NotificationPane;
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
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login {

	private static final String HTTP = "http://";

	private static final String HTTPS = "https://";

	private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

	private static final String HOST_LOGIN_DEFAULT_PATH = "/login/index.php";

	/*
	 * Moodle termina el login mediante navegador redirigiendo al esquema
	 * moodlemobile://token=<base64>. No usamos \w+ porque Base64 puede contener
	 * caracteres como '+', '/' y '='.
	 */
	private static final String MOODLE_MOBILE_TOKEN_PREFIX = "moodlemobile://token=";
	private static final Pattern PATTERN_MOODLE_MOBILE = Pattern.compile("^moodlemobile://token=([^&#\\s]+)",
			Pattern.CASE_INSENSITIVE);

	private static final int DEFAULT_TYPE_OF_LOGIN = 1;

	private WebService webService;
	private int typeoflogin;
	private String launchurl;
	private WebView webView;
	private String username;
	private String password;
	private String host;

	/*
	 * Estado explícito del flujo SSO. El objeto webService siempre existe, por lo
	 * que comprobar webService == null no permite saber si el usuario ha obtenido
	 * realmente un token.
	 */
	private volatile boolean ssoAuthenticated;
	private volatile String lastSsoLocation;

	public Login(String host, String username, String password) {

		webService = new WebService();
		this.host = host;
		this.username = username;
		this.password = password;
	}

	/**
	 * Intenta buscar el token de acceso a la REST API de Moodle e iniciar sesión en
	 * la página de Moodle.
	 *
	 * @throws IOException si no ha podido conectarse o la contraseña es errónea
	 */
	public void tryLogin() throws IOException {
		boolean hasError = true;
		boolean coreSupported = false;

		// Cada intento debe comenzar sin considerar válido un SSO anterior.
		ssoAuthenticated = false;
		lastSsoLocation = null;

		try (Response response = Connection.getResponse(host + "/local/mobile/check.php?service=local_mobile")) {
			JSONObject jsonObject = new JSONObject(response.body().string());
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

		login(typeoflogin, launchurl);
	}

	public void toolMobileGetPublicConfig(String host) throws IllegalAccessError {
		try (Response response = WebService.getAjaxResponse(host, new ToolMobileGetPublicConfig())) {
			JSONObject data = new JSONArray(new JSONTokener(response.body().byteStream())).getJSONObject(0)
					.optJSONObject("data");

			if (data != null) {
				launchurl = data.getString("launchurl")
						+ "?service=moodle_mobile_app&urlscheme=moodlemobile&passport=1";
				typeoflogin = data.optInt("typeoflogin", DEFAULT_TYPE_OF_LOGIN);
				if (data.optInt("enablemobilewebservice", 1) == 0) {
					throw new IllegalAccessError("Mobile web service not enabled");
				}
			}
		} catch (Exception e) {
			LOGGER.info("not toolMobileGetPublicConfig");
			typeoflogin = DEFAULT_TYPE_OF_LOGIN;
		}
	}

	public void reLogin() throws IOException {
		tryLogin();
	}

	/**
	 * Se loguea en el servidor de Moodle mediante web scraping o mediante el
	 * launcher SSO de Moodle.
	 */
	private void login(int typeoflogin, String launchurl) throws IOException {

		if (launchurl != null && typeoflogin != DEFAULT_TYPE_OF_LOGIN) {
			LOGGER.info("Login SSO with Launch url {}", safeLocationForLog(launchurl));
			loginWebViewWithLaunchUrl(launchurl, Controller.getInstance().getStage());
		} else {
			LOGGER.info("Login normal");
			normalLogin();
		}
	}

	public void normalLogin() throws IOException {
		webService = new WebService(host, username, password);
		String hostLogin = host + HOST_LOGIN_DEFAULT_PATH;
		LOGGER.info("Logeandose para web scraping");

		try (Response response = Connection.getResponse(hostLogin)) {
			String redirectedUrl = response.request().url().toString();

			Document loginDoc = Jsoup.parse(response.body().byteStream(), null, hostLogin);
			Element e = loginDoc.selectFirst("input[name=logintoken]");
			String logintoken = (e == null) ? "" : e.attr("value");

			RequestBody formBody = new FormBody.Builder().add("username", username).add("password", password)
					.add("logintoken", logintoken).build();

			try (Response loginResponse = Connection
					.getResponse(new Request.Builder().url(redirectedUrl).post(formBody).build())) {

				String html = loginResponse.body().string();

				String sesskey = findSesskey(html);
				if (sesskey == null) {
					LOGGER.info("cannot login in the login/index.php page, trying with webview");
					loginWithWebView(host + HOST_LOGIN_DEFAULT_PATH, Controller.getInstance().getStage());
				} else {
					webService.setSesskey(sesskey);
				}
			}
		}
	}

	/**
	 * Fallback de login web convencional. Se extrae el HTML de la propia WebView
	 * para no perder las cookies/sesión del navegador JavaFX.
	 */
	private void loginWithWebView(String host, Window owner) {
		CompletableFuture.runAsync(() -> {
			Stage popup = UtilMethods.createStage(owner, Modality.WINDOW_MODAL);

			setWebview(host, popup, (ov, oldState, newState) -> {
				if (newState != Worker.State.SUCCEEDED) {
					return;
				}

				String sesskey = findSesskeyInCurrentWebViewDocument();
				if (sesskey != null) {
					webService.setSesskey(sesskey);
					popup.close();
				}
			});

		}, Platform::runLater).join();
	}

	/**
	 * Login SSO mediante el launch URL de Moodle.
	 *
	 * El token se obtiene desde el callback moodlemobile://. Una vez completado el
	 * SSO se recupera la sesskey mediante Connection, de modo que las llamadas AJAX
	 * posteriores utilizan la misma sesión HTTP que OkHttp.
	 */
	public void loginWebViewWithLaunchUrl(String launchurl, Window owner) {

		ssoAuthenticated = false;
		lastSsoLocation = null;

		/*
		 * Start every SSO attempt with a clean shared cookie jar. This avoids stale
		 * MoodleSession cookies from previous attempts conflicting with the session
		 * created by the SAML login.
		 */
		LOGGER.info("Clearing cookies before starting Moodle SSO");
		Connection.clearCookies();

		CompletableFuture.runAsync(() -> {
			Stage popup = UtilMethods.createStage(owner, Modality.WINDOW_MODAL);

			popup.setOnCloseRequest(event -> {
				if (!ssoAuthenticated) {
					LOGGER.warn("SSO window closed before receiving Moodle token. Last WebView location={}",
							safeLocationForLog(lastSsoLocation));
				}
			});

			setWebview(launchurl, popup, launcherLogin(popup));

		}, Platform::runLater).join();

		/*
		 * webService nunca es null: se crea en el constructor. La comprobación válida
		 * es que setData(...) haya recibido realmente un token.
		 */
		if (!ssoAuthenticated || webService.getToken() == null || webService.getToken().trim().isEmpty()) {

			LOGGER.error("SSO finished without a Moodle WebService token. Last WebView location={}",
					safeLocationForLog(lastSsoLocation));

			throw new IllegalStateException(I18n.get("error.windowclosed"));
		}

		/*
		 * El token REST ya se ha obtenido correctamente.
		 *
		 * Recuperamos ahora una sesskey utilizando la misma pila HTTP
		 * (Connection/OkHttp) que posteriormente realizará las llamadas AJAX.
		 */
		String connectionSesskey = recoverSesskeyFromConnection();

		if (connectionSesskey != null) {
			webService.setSesskey(connectionSesskey);
			LOGGER.info("Using sesskey recovered from Connection session");
		} else {
			LOGGER.warn("No sesskey available for Moodle AJAX services");
		}
	}

	/**
	 * Configura la WebView.
	 *
	 * Se registran todos los listeners ANTES de llamar a load(), para no perder una
	 * navegación rápida hacia el callback moodlemobile://.
	 */
	public void setWebview(String launchurl, Stage popup, ChangeListener<Worker.State> listener) {

		webView = new WebView();
		WebEngine engine = webView.getEngine();

		NotificationPane notificationPane = new NotificationPane();
		notificationPane.setContent(webView);
		notificationPane.setText(I18n.get("notificationLoginSSO"));

		/*
		 * Listener principal para detectar el callback SSO.
		 *
		 * JavaFX puede marcar FAILED cuando intenta cargar un esquema no soportado como
		 * moodlemobile://. Por eso lo importante es capturar el cambio de location
		 * antes de depender del resultado de la carga.
		 */
		engine.locationProperty().addListener((observable, oldLocation, newLocation) -> {
			lastSsoLocation = newLocation;

			LOGGER.debug("WebView location changed: {} -> {}", safeLocationForLog(oldLocation),
					safeLocationForLog(newLocation));

			processMoodleMobileRedirect(newLocation, popup);
		});

		engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
			LOGGER.debug("WebView load state: {} -> {}; location={}", oldState, newState,
					safeLocationForLog(engine.getLocation()));

			/*
			 * Fallback: si JavaFX no notificase locationProperty como esperamos,
			 * inspeccionamos también engine.getLocation() en cada cambio de estado.
			 */
			processMoodleMobileRedirect(engine.getLocation(), popup);
		});

		engine.getLoadWorker().exceptionProperty().addListener((observable, oldException, newException) -> {
			if (newException != null) {
				LOGGER.warn("WebView load exception at location={}: {}", safeLocationForLog(engine.getLocation()),
						newException.toString());
			}
		});

		if (listener != null) {
			engine.getLoadWorker().stateProperty().addListener(listener);
		}

		popup.setMaximized(true);
		popup.setScene(new Scene(notificationPane, 960, 600));

		Platform.runLater(() -> {
			notificationPane.show();
			PauseTransition pauseTransition = new PauseTransition(Duration.seconds(10));
			pauseTransition.setOnFinished(event -> notificationPane.hide());
			pauseTransition.play();
		});

		LOGGER.info("Loading SSO WebView URL {}", safeLocationForLog(launchurl));
		engine.load(launchurl);

		popup.showAndWait();
	}

	/**
	 * Listener de páginas HTML cargadas durante el SSO.
	 *
	 * Ya no intenta descubrir la redirección final haciendo
	 * Connection.getResponse(engine.getLocation()). El callback final lo procesa
	 * processMoodleMobileRedirect().
	 */
	public ChangeListener<Worker.State> launcherLogin(Stage popup) {
		return (ov, oldState, newState) -> {

			if (newState == Worker.State.FAILED) {
				String currentLocation = webView.getEngine().getLocation();

				LOGGER.warn("SSO WebView load FAILED. Current location={}",
						safeLocationForLog(currentLocation));

				// Some WebKit versions publish the custom-scheme URL before failing.
				if (processMoodleMobileRedirect(currentLocation, popup)) {
					return;
				}

				// JavaFX 8 may keep the final launch page DOM available after Malformed URL.
				String mobileUrl = getMoodleMobileUrlFromWebView();
				if (mobileUrl != null && processMoodleMobileRedirect(mobileUrl, popup)) {
					LOGGER.info("Moodle mobile callback recovered from WebView DOM");
					return;
				}

				// Final fallback: read launch.php with the authenticated shared cookie jar.
				if (recoverMoodleMobileRedirectFromLaunchPage(currentLocation, popup)) {
					return;
				}

				LOGGER.error("Unable to recover Moodle mobile callback after WebView failure");
				return;
			}

			if (newState != Worker.State.SUCCEEDED) {
				return;
			}

			LOGGER.debug("SSO page loaded successfully. location={}",
					safeLocationForLog(webView.getEngine().getLocation()));

			try {
				JSObject js = (JSObject) webView.getEngine().executeScript("window");
				js.setMember("loginJavaConnector", this);
			} catch (Exception e) {
				LOGGER.debug("Unable to install JavaScript login connector at {}",
						safeLocationForLog(webView.getEngine().getLocation()), e);
			}

			tryInjectCredentials();

			String sesskey = findSesskeyInCurrentWebViewDocument();
			if (sesskey != null) {
				webService.setSesskey(sesskey);
			}

			processMoodleMobileRedirect(webView.getEngine().getLocation(), popup);
		};
	}

	/**
	 * Recovers the Moodle mobile callback when JavaFX 8 cannot navigate to the
	 * moodlemobile:// custom scheme.
	 */
	private boolean recoverMoodleMobileRedirectFromLaunchPage(
			String location,
			Stage popup) {

		if (location == null
				|| ssoAuthenticated
				|| !location.contains("/admin/tool/mobile/launch.php")) {

			return false;
		}

		LOGGER.info(
				"Trying to recover Moodle mobile callback "
						+ "from launch.php");

		try (Response response = Connection.getResponse(location)) {

			LOGGER.info(
					"launch.php recovery HTTP status={}",
					Integer.valueOf(response.code()));

			/*
			 * IMPORTANTE:
			 *
			 * OkHttp sigue automáticamente redirects HTTP/HTTPS, pero no puede
			 * seguir un redirect hacia un esquema personalizado como:
			 *
			 * moodlemobile://token=...
			 *
			 * En ese caso devuelve al llamador la respuesta 3xx original.
			 *
			 * Por tanto, ANTES de comprobar response.isSuccessful(), debemos mirar
			 * la cabecera Location. El 302 puede ser precisamente el resultado
			 * correcto del flujo SSO.
			 */
			String redirectLocation = response.header("Location");

			if (redirectLocation != null
					&& !redirectLocation.trim().isEmpty()) {

				redirectLocation = redirectLocation.trim();

				LOGGER.info(
						"launch.php recovery redirect: HTTP {}, Location={}",
						Integer.valueOf(response.code()),
						safeLocationForLog(redirectLocation));

				/*
				 * Si Location es moodlemobile://token=..., hemos encontrado
				 * directamente el callback sin necesidad de procesar HTML.
				 */
				if (processMoodleMobileRedirect(
						redirectLocation,
						popup)) {

					LOGGER.info(
							"Moodle mobile callback recovered "
									+ "from HTTP Location header");

					return true;
				}

				LOGGER.debug(
						"launch.php Location header is not a Moodle mobile callback: {}",
						safeLocationForLog(redirectLocation));
			}

			/*
			 * Si no hemos recuperado el callback desde Location y la respuesta
			 * no es 2xx, no hay HTML útil que procesar.
			 */
			if (!response.isSuccessful()) {

				LOGGER.warn(
						"Unable to recover Moodle callback. "
								+ "HTTP status={} and Location did not contain "
								+ "a Moodle mobile callback",
						Integer.valueOf(response.code()));

				return false;
			}

			/*
			 * Algunas versiones/configuraciones de Moodle muestran una página
			 * HTML con:
			 *
			 * <a id="launchapp" href="moodlemobile://token=...">
			 *
			 * En ese caso extraemos el href mediante Jsoup.
			 */
			if (response.body() == null) {

				LOGGER.warn(
						"Unable to recover Moodle callback: "
								+ "empty response body");

				return false;
			}

			String html = response.body().string();

			LOGGER.debug(
					"Recovered launch.php HTML. length={}",
					Integer.valueOf(html.length()));

			Document document = Jsoup.parse(html, location);

			Element launchLink =
					document.selectFirst("#launchapp[href]");

			if (launchLink == null) {

				LOGGER.warn(
						"launch.php HTML does not contain "
								+ "#launchapp[href]");

				return false;
			}

			String mobileUrl =
					launchLink.attr("href");

			if (mobileUrl == null
					|| mobileUrl.trim().isEmpty()) {

				LOGGER.warn(
						"Moodle #launchapp link has "
								+ "an empty href");

				return false;
			}

			LOGGER.info(
					"Moodle mobile URL recovered "
							+ "from launch.php HTML: {}",
					safeLocationForLog(mobileUrl));

			return processMoodleMobileRedirect(
					mobileUrl,
					popup);

		} catch (Exception e) {

			LOGGER.error(
					"Error recovering Moodle mobile callback "
							+ "from launch.php",
					e);

			return false;
		}
	}

	/**
	 * Procesa el callback final de Moodle:
	 *
	 * moodlemobile://token=<base64>
	 *
	 * El contenido Base64 esperado mantiene la estructura histórica que ya usaba
	 * UBUMonitor:
	 *
	 * <passport/hash>:::<wstoken>[:::<privatetoken>]
	 */
	private boolean processMoodleMobileRedirect(String location, Stage popup) {

		if (location == null || ssoAuthenticated) {
			return false;
		}

		Matcher matcher = PATTERN_MOODLE_MOBILE.matcher(location);
		if (!matcher.find()) {
			return false;
		}

		String encodedToken = matcher.group(1);

		LOGGER.debug("Moodle mobile callback detected. URI={}, encoded token length={}",
				MOODLE_MOBILE_TOKEN_PREFIX + "<redacted>", Integer.valueOf(encodedToken.length()));

		try {
			/*
			 * Si Moodle/IdP ha escapado caracteres '%' en la URL, los decodificamos.
			 * URLDecoder interpreta '+' como espacio, por lo que antes protegemos los '+'
			 * literales propios del Base64.
			 */
			encodedToken = percentDecodePreservingPlus(encodedToken);

			byte[] decodedBytes;
			try {
				decodedBytes = Base64.getDecoder().decode(encodedToken);
			} catch (IllegalArgumentException standardBase64Exception) {
				/*
				 * Fallback defensivo por si alguna instalación usa Base64 URL-safe.
				 */
				LOGGER.debug("Standard Base64 decoding failed; trying URL-safe Base64");
				decodedBytes = Base64.getUrlDecoder().decode(encodedToken);
			}

			String decodedToken = new String(decodedBytes, StandardCharsets.UTF_8);
			String[] tokenParts = decodedToken.split(":::", -1);

			LOGGER.debug(
					"Decoded Moodle callback structure: parts={}, passportPresent={}, tokenPresent={}, privateTokenPresent={}",
					Integer.valueOf(tokenParts.length),
					Boolean.valueOf(tokenParts.length > 0 && !tokenParts[0].isEmpty()),
					Boolean.valueOf(tokenParts.length > 1 && !tokenParts[1].isEmpty()),
					Boolean.valueOf(tokenParts.length > 2 && !tokenParts[2].isEmpty()));

			if (tokenParts.length < 2 || tokenParts[1] == null || tokenParts[1].isEmpty()) {
				throw new IllegalStateException("Invalid Moodle SSO callback: WebService token missing");
			}

			String token = tokenParts[1];
			String privateToken = null;

			if (tokenParts.length > 2 && tokenParts[2] != null && !tokenParts[2].isEmpty()) {
				privateToken = tokenParts[2];
			}

			/*
			 * Antes de abandonar la WebView intentamos recoger sesskey de la página
			 * anterior, si sigue disponible. No es requisito para el REST token.
			 */
			String sesskey = findSesskeyInCurrentWebViewDocument();
			if (sesskey != null) {
				webService.setSesskey(sesskey);
			}
			webService.setData(host, token, privateToken);
			ssoAuthenticated = true;

			LOGGER.info("Moodle SSO token received successfully. wsTokenLength={}, privateTokenPresent={}",
					Integer.valueOf(token.length()), Boolean.valueOf(privateToken != null && !privateToken.isEmpty()));

			/*
			 * El cierre se realiza en el JavaFX Application Thread, porque este método se
			 * ejecuta desde listeners de WebEngine.
			 */
			if (popup != null && popup.isShowing()) {
				popup.close();
			}

			return true;

		} catch (Exception e) {
			LOGGER.error("Error decoding Moodle mobile callback. URI={}, encodedTokenLength={}",
					MOODLE_MOBILE_TOKEN_PREFIX + "<redacted>", Integer.valueOf(encodedToken.length()), e);

			throw new IllegalStateException("No se ha podido obtener el token de Moodle", e);
		}
	}

	/**
	 * Intenta rellenar solamente campos que parecen realmente usuario/password.
	 * Evita campos MFA/OTP/código.
	 */
	private void tryInjectCredentials() {

		String escapedUsername = UtilMethods.escapeJavaScriptText(username);
		String escapedPassword = UtilMethods.escapeJavaScriptText(password);

		String script = "(function() {" + "  try {" + "    var inputs = document.getElementsByTagName('input');"
				+ "    for (var i = 0; i < inputs.length; i++) {" + "      var o = inputs[i];"
				+ "      if (!o || o.hidden || o.disabled) continue;" + "      var type = (o.type || '').toLowerCase();"
				+ "      var meta = ((o.name || '') + ' ' + (o.id || '') + ' ' + "
				+ "                  (o.autocomplete || '') + ' ' + (o.placeholder || '')).toLowerCase();"
				+ "      var isCode = /(otp|mfa|code|token|verification|verify|passcode|one.?time)/.test(meta);"
				+ "      if (isCode) continue;"
				+ "      var isUser = /(username|user|email|login|loginfmt|identifier)/.test(meta);"
				+ "      var isPassword = /(password|passwd|pass)/.test(meta);"
				+ "      if ((type === 'text' || type === 'email') && isUser && !o.value) {" + "        o.value = '"
				+ escapedUsername + "';" + "      } else if (type === 'password' && isPassword && !o.value) {"
				+ "        o.value = '" + escapedPassword + "';" + "      }" + "    }" + "  } catch (e) {"
				+ "    /* Ignorar errores del helper; el SSO debe continuar manualmente. */" + "  }" + "})();";

		try {
			webView.getEngine().executeScript(script);
		} catch (Exception e) {
			LOGGER.debug("Unable to inject credentials at {}", safeLocationForLog(webView.getEngine().getLocation()),
					e);
		}
	}

	/**
	 * Busca sesskey en el DOM actualmente cargado en la WebView.
	 */
	private String findSesskeyInCurrentWebViewDocument() {

		if (webView == null || webView.getEngine() == null) {
			return null;
		}

		try {
			Object sesskeyObject = webView.getEngine().executeScript(
					"typeof M !== 'undefined' && M.cfg && M.cfg.sesskey "
					+ "? M.cfg.sesskey : null");

			if (sesskeyObject instanceof String) {
				String sesskey = ((String) sesskeyObject).trim();

				if (!sesskey.isEmpty()) {
					LOGGER.debug("Sesskey found in WebView M.cfg");
					return sesskey;
				}
			}

		} catch (Exception e) {
			LOGGER.debug("Could not read M.cfg.sesskey at {}",
					safeLocationForLog(webView.getEngine().getLocation()), e);
		}

		/*
		 * Fallback histórico: buscar la sesskey en el HTML.
		 */
		try {
			Object htmlObject = webView.getEngine().executeScript(
					"document && document.documentElement "
					+ "? document.documentElement.outerHTML : ''");

			if (!(htmlObject instanceof String)) {
				return null;
			}

			String html = (String) htmlObject;

			if (html.isEmpty()) {
				return null;
			}

			return findSesskey(html);

		} catch (Exception e) {
			LOGGER.debug("Could not inspect WebView DOM for sesskey at {}",
					safeLocationForLog(webView.getEngine().getLocation()), e);

			return null;
		}
	}

	private String percentDecodePreservingPlus(String value) throws IOException {

		if (value == null || value.indexOf('%') < 0) {
			return value;
		}

		/*
		 * URLDecoder traduce '+' a espacio. Escapamos primero los '+' literales para
		 * preservar Base64 estándar.
		 */
		return URLDecoder.decode(value.replace("+", "%2B"), "UTF-8");
	}

	/**
	 * Conserva los nombres de parámetros para diagnosticar redirects, pero oculta
	 * todos sus valores.
	 */
	private String redactQueryValues(String query) {
		if (query == null || query.isEmpty()) {
			return "";
		}

		StringBuilder result = new StringBuilder();
		String[] params = query.split("&");

		for (String param : params) {
			if (result.length() > 0) {
				result.append("&");
			}

			int equals = param.indexOf('=');
			if (equals < 0) {
				result.append(param);
			} else {
				result.append(param.substring(0, equals)).append("=<redacted>");
			}
		}

		return result.toString();
	}

	/**
	 * Devuelve una URL adecuada para logging sin escribir tokens/códigos SSO en el
	 * log.
	 */
	private String safeLocationForLog(String location) {

		if (location == null) {
			return "<null>";
		}

		Matcher matcher = PATTERN_MOODLE_MOBILE.matcher(location);
		if (matcher.find()) {
			return MOODLE_MOBILE_TOKEN_PREFIX + "<redacted,length=" + matcher.group(1).length() + ">";
		}

		/*
		 * En URLs HTTP(S) de proveedores SSO puede haber códigos OAuth/SAML en query o
		 * fragment. Conservamos scheme/host/path y ocultamos query/fragment.
		 */
		try {
			URL url = new URL(location);
			StringBuilder safe = new StringBuilder();
			safe.append(url.getProtocol()).append("://").append(url.getHost());

			if (url.getPort() >= 0) {
				safe.append(":").append(url.getPort());
			}

			if (url.getPath() != null) {
				safe.append(url.getPath());
			}

			if (url.getQuery() != null && !url.getQuery().isEmpty()) {
				safe.append("?").append(redactQueryValues(url.getQuery()));
			}

			if (url.getRef() != null && !url.getRef().isEmpty()) {
				safe.append("#<fragment-redacted>");
			}

			return safe.toString();

		} catch (Exception e) {
			/*
			 * Para esquemas que java.net.URL no conoce, no exponemos el valor completo.
			 */
			int colon = location.indexOf(':');
			if (colon > 0) {
				return location.substring(0, colon + 1) + "<redacted>";
			}

			return "<unparseable-location>";
		}
	}

	public String findSesskey(String html) {

		if (html == null) {
			LOGGER.debug("Cannot find sesskey in null HTML");
			return null;
		}

		Pattern[] patterns = {
				Pattern.compile("sesskey=([A-Za-z0-9]+)"),
				Pattern.compile("\"sesskey\"\\s*:\\s*\"([^\"]+)\""),
				Pattern.compile("'sesskey'\\s*:\\s*'([^']+)'")
		};

		for (Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(html);

			if (matcher.find()) {
				LOGGER.debug("Sesskey found");
				return matcher.group(1);
			}
		}

		LOGGER.debug("Didn't find a sesskey in current page (htmlLength={})",
				Integer.valueOf(html.length()));

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

		throw new IllegalArgumentException(I18n.get("error.host"));
	}

	private boolean checkWebsService(URL url) {

		try (Response response = Connection.getResponse(url + "/login/token.php")) {
			JSONObject jsonObject = new JSONObject(response.body().string());
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

	public WebService getWebService() {
		return webService;
	}

	public void setWebService(WebService webService) {
		this.webService = webService;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	private String getMoodleMobileUrlFromWebView() {

		if (webView == null
				|| webView.getEngine() == null) {

			return null;
		}

		try {

			/*
			 * Usamos getAttribute('href') y no element.href para obtener
			 * exactamente el valor escrito por Moodle.
			 */
			Object result =
					webView.getEngine().executeScript(
							"(function() {"
							+ "var e = document.getElementById('launchapp');"
							+ "if (!e) return null;"
							+ "return e.getAttribute('href');"
							+ "})();");

			if (!(result instanceof String)) {
				LOGGER.debug(
						"WebView DOM does not expose #launchapp href "
								+ "after failed custom-scheme navigation");
				return null;
			}

			String url = ((String) result).trim();

			if (url.startsWith(
					MOODLE_MOBILE_TOKEN_PREFIX)) {

				return url;
			}

			LOGGER.debug(
					"#launchapp found but href isn't moodlemobile: {}",
					safeLocationForLog(url));

		} catch (Exception e) {

			LOGGER.debug(
					"Unable to recover Moodle callback "
							+ "from WebView DOM",
					e);
		}

		return null;
	}

	private String recoverSesskeyFromConnection() {

		try (Response response = Connection.getResponse(host + "/my/")) {

			LOGGER.debug("Trying to recover sesskey using Connection. HTTP status={}, finalUrl={}",
					Integer.valueOf(response.code()),
					safeLocationForLog(response.request().url().toString()));

			if (response.body() == null) {
				LOGGER.warn("Cannot recover sesskey using Connection: empty response body");
				return null;
			}

			String html = response.body().string();
			String sesskey = findSesskey(html);

			if (sesskey != null) {
				LOGGER.debug("Sesskey recovered from Connection session");
			} else {
				LOGGER.warn("Could not find sesskey in Connection session page");
			}

			return sesskey;

		} catch (Exception e) {
			LOGGER.warn("Error recovering sesskey using Connection", e);
			return null;
		}
	}
}
