package mercadopago;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RestClient {

	private static final String API_BASE_URL = "https://api.mercadolibre.com";

	public enum ContentTypes {
		FORM, JSON
	}

	public enum HttpMethods {
		GET, POST, PUT
	}

	public static JSONObject get (String uri) throws JSONException, Exception {
		return get(uri, ContentTypes.JSON);
	}

	public static JSONObject get (String uri, ContentTypes contentType) throws JSONException, Exception {
		return exec (HttpMethods.GET, uri, null, contentType);
	}

	public static JSONObject post (String uri, HashMap<String, Object> data) throws JSONException, Exception {
		return post(uri, buildBody(data), ContentTypes.FORM);
	}

	public static JSONObject post (String uri, JSONObject data) throws JSONException, Exception {
		return post(uri, buildBody(data), ContentTypes.JSON);
	}

	public static JSONObject post (String uri, String data, ContentTypes contentType) throws JSONException, Exception {
		return exec (HttpMethods.POST, uri, data, contentType);
	}

	public static JSONObject put (String uri, HashMap<String, Object> data) throws JSONException, Exception {
		return put(uri, buildBody(data), ContentTypes.JSON);
	}

	public static JSONObject put (String uri, JSONObject data) throws JSONException, Exception {
		return put(uri, buildBody(data), ContentTypes.JSON);
	}

	public static JSONObject put (String uri, String data, ContentTypes contentType) throws JSONException, Exception {
		return exec (HttpMethods.PUT, uri, data, contentType);
	}
	
	private static JSONObject exec (HttpMethods method, String uri, String data, ContentTypes contentType) throws JSONException, Exception {
		
		// Execute method
		JSONObject execResult = null;
		if (method == HttpMethods.GET) {
			execResult = execGetRequest(uri);			
		}
		else if (method == HttpMethods.POST) {
			execResult = execPostRequest(uri, data, contentType);
		}
		else if (method == HttpMethods.PUT) {
			execResult = execPutRequest(uri, data, contentType);			
		}
		else {
			throw new Exception("Unsupported method");
		}
		
		return execResult;
	}
	    
    private static JSONObject execGetRequest(String path) throws Exception {
    	
		// Set HttpClient
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(API_BASE_URL + path);
		
		// Set request headers
		httpget.setHeader("Accept", "application/json");			
		httpget.setHeader("User-Agent", "MercadoPago Java SDK v" + MP.version);			
		
		// Execute api call
		HttpResponse execResponse = httpclient.execute(httpget);

		// Prepare result
		JSONObject result = new JSONObject ();
		HttpEntity entity = execResponse.getEntity();
        JSONObject response = new JSONObject(EntityUtils.toString(entity));        
		result.put("status", execResponse.getStatusLine().getStatusCode());
		result.put("response", response);
				
		return result;
    }

	private static JSONObject execPostRequest(String path, String data, ContentTypes contentType) throws Exception {
    	
		// Set HttpClient
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(API_BASE_URL + path);
		
		// Set request body
		StringEntity se = new StringEntity(data);
		httpost.setEntity(se);
		
		// Set request headers
		if (contentType == ContentTypes.JSON) {
			httpost.setHeader("Content-type", "application/json");
		}
		else {
			httpost.setHeader("Content-type", "application/x-www-form-urlencoded");
 		}
		httpost.setHeader("Accept", "application/json");			
		httpost.setHeader("User-Agent", "MercadoPago Java SDK v" + MP.version);			
		
		// Execute api call
		HttpResponse execResponse = httpclient.execute(httpost);

		// Prepare result
		JSONObject result = new JSONObject ();
		HttpEntity entity = execResponse.getEntity();
        JSONObject response = new JSONObject(EntityUtils.toString(entity));        
		result.put("status", execResponse.getStatusLine().getStatusCode());
		result.put("response", response);
				
		return result;
	}    
    
    private static JSONObject execPutRequest(String path, String data, ContentTypes contentType) throws Exception {
    	
		// Set HttpClient
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPut httput = new HttpPut(API_BASE_URL + path);
		
		// Set request body
		StringEntity se = new StringEntity(data);
		httput.setEntity(se);
		
		// Set request headers
		if (contentType == ContentTypes.JSON) {
			httput.setHeader("Content-type", "application/json");
		}
		else {
			httput.setHeader("Content-type", "application/x-www-form-urlencoded");
 		}
		httput.setHeader("Accept", "application/json");			
		httput.setHeader("User-Agent", "MercadoPago Java SDK v" + MP.version);			
		
		// Execute api call
		HttpResponse execResponse = httpclient.execute(httput);

		// Prepare result
		JSONObject result = new JSONObject ();
		HttpEntity entity = execResponse.getEntity();
        JSONObject response = new JSONObject(EntityUtils.toString(entity));        
		result.put("status", execResponse.getStatusLine().getStatusCode());
		result.put("response", response);
				
		return result;
	}    
    
    private static String buildBody(JSONObject body) {

    	String result = "";
    	
    	// hack: unsupported stringed collector id
    	if (body != null) {
    		try {
    			String collectionId = body.getString("collector_id");
    			if (collectionId != "") {
    				long auxColector = Long.parseLong(collectionId);
    				body.remove("collector_id");
    				body.put("collector_id", auxColector);
    			}
    		}
    		catch (Exception ex) {
    			// continue
    		}
    		result = body.toString();
    	}
    	
    	return result;
    }
    
    private static String buildBody(HashMap<String, Object> body) {

    	return buildQuery(body);
    }
    
	public static String buildQuery (Map<String, Object> params) {
		
		String query = "";
		
		// Encode and join
		for (String key : params.keySet()) {
			String val = String.valueOf(params.get(key) != null ? params.get(key) : "");
			try {
				val = URLEncoder.encode(val, "UTF-8");
			} 
			catch (UnsupportedEncodingException e) {
				// TODO: if encoding fails, then? ...
			}
			query += key + "=" + val + "&";
		}

		// Clean last char
		if (!query.equals("")) {
			query = query.substring(0, query.length() - 1);
		}
		
		return query;
	}
}
