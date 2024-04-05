package hkma.gov.hk.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	public static boolean isValidJSON(String body) {
		try {
	        new JSONObject(body);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(body);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
}
