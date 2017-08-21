package trukkeruae.trukkertech.com.trukkeruae.Helper;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;

public class DirectionsJSONParser1 {

	/** Receives a JSONObject and returns a list of lists containing latitude and longitude */
	public List<List<HashMap<String,String>>> parse(JSONObject jObject){

		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;	

		try {			
			jRoutes = jObject.getJSONArray("routes");
			Log.e("length....routh", ""+jRoutes.length());
			
			// JSONObject jsonObject = new JSONObject(response);
			//  JSONArray array = jsonObject.getJSONArray("routes");
			JSONObject routes1 = jRoutes.getJSONObject(0);
			JSONArray legs = routes1.getJSONArray("legs");
			JSONObject steps = legs.getJSONObject(0);
			JSONObject distance = steps.getJSONObject("distance");
			JSONObject duration = steps.getJSONObject("duration");
			Constants.distance=distance.getString("text");
			String newdist=distance.getString("text");
			String dist[]=newdist.split(" ");
			Constants.duration=duration.getString("text");
			Log.e("distance....", ""+Constants.distance);


		} catch (JSONException e) {			
			e.printStackTrace();
		}catch (Exception e){			
		}


		return routes;
	}	

}