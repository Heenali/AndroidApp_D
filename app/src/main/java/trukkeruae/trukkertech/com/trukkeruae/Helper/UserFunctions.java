package trukkeruae.trukkertech.com.trukkeruae.Helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.JSONParser;
import trukkeruae.trukkertech.com.trukkeruae.TrukkerUae.DriverMapActivity;

/**
 * Created by user2 on 6/13/2016.
 */
public class UserFunctions
{
    private JSONParser jsonParser;
    Context context;

   // public static String URL_img = "http://trukker.ae/trukkerUAEApi";// final live url for image load
   // public static String URL = "http://trukker.ae/trukkerUAEApi/Api/"; // final live url

   /*  public static String direction_key = "AIzaSyAzDE3Ghsd_mbNCvehA2Yl25TrBqLD7_EU";  // live API key for distace & direction
    public static String map_gms_key = "AIzaSyCxtyZc4exPvPR1t3qCuLoyBp2f_6NerBs";*/ // live API key for gms map support

   /* public static String URL = "http://192.168.1.2/trukkerUAE/Api/";                             //live url -test to use
    public static String URL_img = "http://192.168.1.2/trukkerUAE";    */                            // live url -test url for image load

  public static String URL = "http://test.trukker.ae/trukkerUAEApitest/Api/";        //live test only
  public static String URL_img = "http://test.trukker.ae/trukkerUAEApiTest/";       //live test only for image load

    public static String direction_key = "AIzaSyAzDE3Ghsd_mbNCvehA2Yl25TrBqLD7_EU";                  // try API key for distace & direction
    public static String map_gms_key = "AIzaSyD_Hvp-mAjMMMS_OgPFEIxqX-AsffaYK0E";                    // try API key for gms map support

    // constructor
    public UserFunctions(Context context)
    {
        this.context = context;
        jsonParser = new JSONParser();
    }

    public void msg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //  Login User
    public String DriverInqList(String methodName) {
        // Building Parameters
        String urls = "";

        urls = URL + methodName;

        // Log.e("Login Urljson", urls);
        String json = jsonParser.sendGet(urls);
        //	Log.e("new res", "res"+json);
        //String jsonFormattedString = json.replaceAll("\\\\", "");
        if (json.length() > 0) {
            json = json.trim();
            json = json.substring(1, json.length() - 1);
            json = json.replace("\\", "");
            Log.e("new res", json);
        }
        return json;
    }

    //  Login User
    public String LoginUser(String methodName, JSONObject jsonData) {
        // Building Parameters
        String urls = "";
        urls = URL + methodName;

        Log.e("Login Urljson", urls);
        String json = jsonParser.sendJSONPost(urls, jsonData);
        //	Log.e("new res", "res"+json);
        //String jsonFormattedString = json.replaceAll("\\\\", "");
        if (json.length() != 0) {
            json = json.trim();
          /*  json = json.substring(1, json.length() - 1);
            json = json.replace("\\", "");*/
            try {
                json = new JSONTokener(json).nextValue().toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Log.e("new res", json);
        }
        return json;
    }

    //for state
    public String TruckMakeMst(String methodName)
    {
        // Building Parameters
        String urls = "";
        urls = URL + methodName;
        Log.e("truckmake Urljson", urls);
        String json = jsonParser.sendGet(urls);
        /// String jsonFormattedString = json.replaceAll("\\\\", "");
        json = json.trim();
        if (json.length() > 0) {
            // json = json.substring(1, json.length() - 1);
            //json = json.replace("\\", "");
            try {
                json = new JSONTokener(json).nextValue().toString();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            Log.e("new res", json);
        } else {
            json = "";
        }
        return json;
    }

}
