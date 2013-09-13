package mercadopago;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * MercadoPago Integration Library
 * Access MercadoPago for payments integration
 *
 * @date 2012/03/29
 * @author hcasatti
 *
 */
public class MP {
	public static final String version = "0.1.8";

	private final String client_id;
	private final String client_secret;
	private JSONObject access_data = null;
	private boolean sandbox = false;

	public MP (final String client_id, final String client_secret) {
		this.client_id = client_id;
		this.client_secret = client_secret;
	}

	public boolean sandboxMode () {
		return this.sandbox;
	}

	public boolean sandboxMode (boolean enable) {
		this.sandbox = enable;
		return this.sandbox;
	}

	/**
	 * Get Access Token for API use
	 * @throws JSONException 
	 */
	public String getAccessToken () throws JSONException, Exception {
		HashMap<String, Object> appClientValues = new HashMap<String, Object>();
		appClientValues.put("grant_type", "client_credentials");
		appClientValues.put("client_id", this.client_id);
		appClientValues.put("client_secret", this.client_secret);

        String appClientValuesQuery = RestClient.buildQuery(appClientValues);

		JSONObject access_data = RestClient.post ("/oauth/token", appClientValuesQuery, RestClient.ContentTypes.FORM);

		if(access_data.getInt("status") == 200) {
			this.access_data = access_data.getJSONObject("response");
			return this.access_data.optString("access_token");
		} else {
			throw new Exception(access_data.toString());
		}
	}

	/**
	 * Get information for specific payment
	 * @param id
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getPaymentInfo (String id) throws JSONException, Exception {
		String accessToken;
		try {
			accessToken = this.getAccessToken ();
		} catch (Exception e) {
			JSONObject result = new JSONObject(e.getMessage());
			return result;
		}

		String uriPrefix = this.sandbox ? "/sandbox" : "";

		JSONObject paymentInfo = RestClient.get (uriPrefix + "/collections/notifications/"+id+"?access_token="+accessToken);

		return paymentInfo;
	}

	/**
	 * Refund accredited payment
	 * @param id
	 * @return
	 * @throws JSONException
	 */
	public JSONObject refundPayment (String id) throws JSONException, Exception {
		String accessToken;
		try {
			accessToken = this.getAccessToken ();
		} catch (Exception e) {
			JSONObject result = new JSONObject(e.getMessage());
			return result;
		}

		JSONObject refundStatus = new JSONObject ();
		refundStatus.put("status", "refunded");

		JSONObject response = RestClient.put ("/collections/"+id+"?access_token="+accessToken, refundStatus);

		return response;
	}

	/**
	 * Cancel pending payment
	 * @param id
	 * @return
	 * @throws JSONException
	 */
	public JSONObject cancelPayment (String id) throws JSONException, Exception {
		String accessToken;
		try {
			accessToken = this.getAccessToken ();
		} catch (Exception e) {
			JSONObject result = new JSONObject(e.getMessage());
			return result;
		}

		JSONObject cancelStatus = new JSONObject ();
		cancelStatus.put("status", "cancelled");

		JSONObject response = RestClient.put ("/collections/"+id+"?access_token="+accessToken, cancelStatus);

		return response;
	}

	/**
	 * Search payments according to filters, with pagination
	 * @param filters
	 * @param offset
	 * @param limit
	 * @return
	 * @throws JSONException
	 */
	public JSONObject searchPayment (Map<String, Object> filters) throws JSONException, Exception {
		return this.searchPayment(filters, 0, 0);
	}
	public JSONObject searchPayment (Map<String, Object> filters, int offset, int limit) throws JSONException, Exception {
		return this.searchPayment(filters, Long.valueOf(offset), Long.valueOf(limit));
	}
	public JSONObject searchPayment (Map<String, Object> filters, Long offset, Long limit) throws JSONException, Exception {
		String accessToken;
		try {
			accessToken = this.getAccessToken ();
		} catch (Exception e) {
			JSONObject result = new JSONObject(e.getMessage());
			return result;
		}

		filters.put("offset", offset);
		filters.put("limit", limit);

		String filtersQuery = RestClient.buildQuery(filters);

		String uriPrefix = this.sandbox ? "/sandbox" : "";

		JSONObject collectionResult = RestClient.get(uriPrefix + "/collections/search?"+filtersQuery+"&access_token="+accessToken);
		return collectionResult;
	}

	/**
	 * Create a checkout preference
	 * @param preference
	 * @return
	 * @throws JSONException
	 */
	public JSONObject createPreference (String preference) throws JSONException, Exception {
		JSONObject preferenceJSON = new JSONObject (preference);
		return this.createPreference(preferenceJSON);
	}
	public JSONObject createPreference (Map<?, ?> preference) throws JSONException, Exception {
		JSONObject preferenceJSON = map2json (preference);
		return this.createPreference(preferenceJSON);
	}
	public JSONObject createPreference (JSONObject preference) throws JSONException, Exception {
		String accessToken;
		try {
			accessToken = this.getAccessToken ();
		} catch (Exception e) {
			JSONObject result = new JSONObject(e.getMessage());
			return result;
		}

		JSONObject preferenceResult = RestClient.post ("/checkout/preferences?access_token="+accessToken, preference);
		return preferenceResult;
	}

	/**
	 * Update a checkout preference
	 * @param string $id
	 * @param array $preference
	 * @return array(json)
	 * @throws JSONException 
	 */
	public JSONObject updatePreference (String id, String preference) throws JSONException, Exception {
		JSONObject preferenceJSON = new JSONObject (preference);
		return this.updatePreference(id, preferenceJSON);
	}
	public JSONObject updatePreference (String id, Map<?, ?> preference) throws JSONException, Exception {
		JSONObject preferenceJSON = map2json (preference);
		return this.updatePreference(id, preferenceJSON);
	}
	public JSONObject updatePreference (String id, JSONObject preference) throws JSONException, Exception {
		String accessToken;
		try {
			accessToken = this.getAccessToken ();
		} catch (Exception e) {
			JSONObject result = new JSONObject(e.getMessage());
			return result;
		}

		JSONObject preferenceResult = RestClient.put ("/checkout/preferences/"+id+"?access_token="+accessToken, preference);
		return preferenceResult;
	}

	/**
	 * Get a checkout preference
	 * @param id
	 * @return
	 * @throws JSONException 
	 */
	public JSONObject getPreference (String id) throws JSONException, Exception {
		String accessToken;
		try {
			accessToken = this.getAccessToken ();
		} catch (Exception e) {
			JSONObject result = new JSONObject(e.getMessage());
			return result;
		}

		JSONObject preferenceResult = RestClient.get ("/checkout/preferences/"+id+"?access_token="+accessToken);
		return preferenceResult;
	}

	private static JSONObject map2json (Map<?, ?> preference) throws JSONException, Exception {
		JSONObject result = new JSONObject();

        for (Entry<?, ?> entry : preference.entrySet()) {
        	if (entry.getValue () instanceof Collection) {
        		result.put((String) entry.getKey(), map2json((Collection<?>)entry.getValue()));
        	} else if (entry.getValue() instanceof Map) {
        		result.put((String) entry.getKey(), map2json((Map<?, ?>)entry.getValue()));
        	} else {
        		result.put((String) entry.getKey(), entry.getValue());
        	}
        }

        return result;
	}

	private static JSONArray map2json (Collection<?> collection) throws JSONException, Exception {
		JSONArray result = new JSONArray();

        for (Object object : collection) {
        	if (object instanceof Map) {
        		result.put(map2json((Map<?, ?>)object));
        	} else {
        		result.put(object);
        	}
        }
        
        return result;
    }
}
