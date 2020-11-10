package es.ubu.lsi.ubumonitor.controllers.load;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.Duration;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Connection {

	private static final OkHttpClient CLIENT;
	private static final CookieManager COOKIE_MANAGER;

	static {
		COOKIE_MANAGER = new CookieManager();
		COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(COOKIE_MANAGER);
		CLIENT = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(COOKIE_MANAGER))
				.readTimeout(Duration.ofMinutes(5))
				.build();
	}

	private Connection() {
	}

	/**
	 * Return the client instance
	 * @return the client instance
	 */
	public static OkHttpClient getClient() {
		return CLIENT;
	}

	/**
	 * Get response of the request url
	 * @param url the url
	 * @return Response
	 * @throws IOException exception if I/O Exception 
	 */
	public static Response getResponse(String url) throws IOException {
		return getResponse(new Request.Builder().url(url)
				.build());
	}

	/**
	 * Get response response
	 * @param request request
	 * @return response
	 * @throws IOException I/O exception
	 */
	public static Response getResponse(Request request) throws IOException {
		return CLIENT.newCall(request)
				.execute();
	}

	/**
	 * Return cookemanager that stores the cookies
	 * @return
	 */
	public static CookieManager getCookieManager() {
		return COOKIE_MANAGER;
	}
	
	/**
	 * Clear cookies
	 */
	public static void clearCookies() {
		COOKIE_MANAGER.getCookieStore().removeAll();
	}

}
