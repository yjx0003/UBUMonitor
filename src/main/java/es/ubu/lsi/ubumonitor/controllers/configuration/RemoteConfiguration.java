package es.ubu.lsi.ubumonitor.controllers.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.load.Connection;
import okhttp3.Response;

public class RemoteConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteConfiguration.class);

	private static final RemoteConfiguration INSTANCE = new RemoteConfiguration();
	private JSONObject configuration;

	private RemoteConfiguration() {
	}

	public static RemoteConfiguration getInstance() {
		return INSTANCE;
	}

	public void setConfiguration(JSONObject configuration) {
		this.configuration = configuration;
	}

	public void setConfiguration(String url) {
		try (Response response = Connection.getResponse(url)) {
			if (response.isSuccessful()) {
				this.configuration = new JSONObject(response.body()
						.string());
			}
		} catch (Exception e) {
			LOGGER.warn("Failed to recover the remote configuration from {}", url, e);
		}
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#get(java.lang.String)
	 */
	public Object get(String key) {
		return configuration.get(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String key) {
		return configuration.getBoolean(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getBigInteger(java.lang.String)
	 */
	public BigInteger getBigInteger(String key) {
		return configuration.getBigInteger(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String key) {
		return configuration.getBigDecimal(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getDouble(java.lang.String)
	 */
	public double getDouble(String key) {
		return configuration.getDouble(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getFloat(java.lang.String)
	 */
	public float getFloat(String key) {
		return configuration.getFloat(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getNumber(java.lang.String)
	 */
	public Number getNumber(String key) {
		return configuration.getNumber(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getInt(java.lang.String)
	 */
	public int getInt(String key) {
		return configuration.getInt(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getJSONArray(java.lang.String)
	 */
	public JSONArray getJSONArray(String key) {
		return configuration.getJSONArray(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getJSONObject(java.lang.String)
	 */
	public JSONObject getJSONObject(String key) {
		return configuration.getJSONObject(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getLong(java.lang.String)
	 */
	public long getLong(String key) {
		return configuration.getLong(key);
	}

	/**
	 * @param key
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#getString(java.lang.String)
	 */
	public String getString(String key) {
		return configuration.getString(key);
	}

	/**
	 * @param key
	 * @return
	 * @see org.json.JSONObject#has(java.lang.String)
	 */
	public boolean has(String key) {
		return configuration.has(key);
	}

	/**
	 * @param key
	 * @return
	 * @see org.json.JSONObject#isNull(java.lang.String)
	 */
	public boolean isNull(String key) {
		return configuration.isNull(key);
	}

	/**
	 * @return
	 * @see org.json.JSONObject#keys()
	 */
	public Iterator<String> keys() {
		return configuration.keys();
	}

	/**
	 * @return
	 * @see org.json.JSONObject#keySet()
	 */
	public Set<String> keySet() {
		return configuration.keySet();
	}

	/**
	 * @return
	 * @see org.json.JSONObject#length()
	 */
	public int length() {
		return configuration.length();
	}

	/**
	 * @return
	 * @see org.json.JSONObject#isEmpty()
	 */
	public boolean isEmpty() {
		return configuration.isEmpty();
	}

	/**
	 * @return
	 * @see org.json.JSONObject#names()
	 */
	public JSONArray names() {
		return configuration.names();
	}

	/**
	 * @param key
	 * @return
	 * @see org.json.JSONObject#optJSONArray(java.lang.String)
	 */
	public JSONArray optJSONArray(String key) {
		return configuration.optJSONArray(key);
	}

	/**
	 * @param key
	 * @return
	 * @see org.json.JSONObject#optJSONObject(java.lang.String)
	 */
	public JSONObject optJSONObject(String key) {
		return configuration.optJSONObject(key);
	}

	/**
	 * @param key
	 * @return
	 * @see org.json.JSONObject#optString(java.lang.String)
	 */
	public String optString(String key) {
		return configuration.optString(key);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 * @see org.json.JSONObject#optString(java.lang.String, java.lang.String)
	 */
	public String optString(String key, String defaultValue) {
		return configuration.optString(key, defaultValue);
	}

	/**
	 * @return
	 * @see org.json.JSONObject#toString()
	 */
	public String toString() {
		return configuration.toString();
	}

	/**
	 * @param indentFactor
	 * @return
	 * @throws JSONException
	 * @see org.json.JSONObject#toString(int)
	 */
	public String toString(int indentFactor) {
		return configuration.toString(indentFactor);
	}

}