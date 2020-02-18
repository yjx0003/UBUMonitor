package es.ubu.lsi.ubumonitor.controllers;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Connection {
	private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);
	private static final OkHttpClient CLIENT;
	
	static {
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CLIENT = new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager))
				.readTimeout(Duration.ZERO)
				.addNetworkInterceptor(new Interceptor() {
					
					@Override
					public Response intercept(Chain chain) throws IOException {
						Request request = chain.request();

					    long t1 = System.nanoTime();
					    Response response = chain.proceed(request);
					   
					    long t2 = System.nanoTime();
					    LOGGER.info(String.format("Received response in %.1fms for %s/%s",
					    		(t2 - t1) / 1e6d,  request.url().host(), String.join("/", request.url().pathSegments())));

					    return response;
					  }
				})
				.build();
	}

	private Connection() {
	}

	/**
	 * @return the client instance
	 */
	public static OkHttpClient getClient() {
		return CLIENT;
	}

	public static Response getResponse(String url) throws IOException {
		return getResponse(new Request.Builder().url(url).build());
	}

	public static Response getResponse(Request request) throws IOException {
		return CLIENT.newCall(request).execute();
	}

}
