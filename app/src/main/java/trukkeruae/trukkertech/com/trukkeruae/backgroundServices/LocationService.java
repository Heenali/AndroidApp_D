package trukkeruae.trukkertech.com.trukkeruae.backgroundServices;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.JSONParser;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;

//import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";
    JSONParser jsonParser;
    String android_id;
    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    String latlngJson, json;
    UserFunctions UF;

    public LocationService() {
        super("AppService");
        Log.e("service", "LocationService1");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.e("service", "LocationService");
        UF = new UserFunctions(LocationService.this);
        jsonParser = new JSONParser();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("dddddd",android_id);
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    protected void sendLocationDataToWebsite(Location location) {
        // formatted for mysql datetime format
        Constants.fb_lat = location.getLatitude();
        Constants.fb_lng = location.getLongitude();

        new GetJson().execute();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
        {
            Log.e(TAG, "service: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            if (location.getAccuracy() < 500.0f) {
                stopLocationUpdates();
                sendLocationDataToWebsite(location);
            }
        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }

    private class GetJson extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject prmsLogin = new JSONObject();

                prmsLogin.put("load_inquiry_no", "");
                prmsLogin.put("remaining_kms", "");
                prmsLogin.put("eta", "");
                prmsLogin.put("driver_id", "");
                prmsLogin.put("device_id", android_id);
                prmsLogin.put("truck_id", "");
                prmsLogin.put("truck_lat", String.valueOf(Constants.fb_lat));
                prmsLogin.put("truck_lng", String.valueOf(Constants.fb_lng));
                prmsLogin.put("truck_location", "");
                prmsLogin.put("created_by", "driver_app");
                prmsLogin.put("created_host", "");
                prmsLogin.put("device_type", "");

                //latlngJson = UF.RegisterUser("truck/SaveTruckPositionNew", prmsLogin);
               // latlngJson = jsonParser.sendJSONPost("http://trukker.ae/trukkerUAEApi/Api/truck/SaveTruckPositionNew" , prmsLogin);
                latlngJson = jsonParser.sendJSONPost("http://test.trukker.ae/trukkerUAEApitest/Api/truck/SaveTruckPositionNew" , prmsLogin);
                latlngJson = latlngJson.trim();
                if (latlngJson.length() > 0) {
                    try {
                        latlngJson = new JSONTokener(latlngJson).nextValue().toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("service", latlngJson);
                } else {
                    latlngJson = "";
                }

                //ss=latlngJson.toString();
                Log.e("service.", prmsLogin.toString());
                Log.e("service_latlng", latlngJson.toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return latlngJson;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            // Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
        }
    }
}
