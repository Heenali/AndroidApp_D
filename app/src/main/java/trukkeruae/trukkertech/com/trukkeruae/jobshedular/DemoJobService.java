
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package trukkeruae.trukkertech.com.trukkeruae.jobshedular;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.JSONParser;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;

public class DemoJobService extends JobService implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener
{
    private static final String TAG = "LocationService";

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    String latlngJson, json;
    JSONParser jsonParser;
    String android_id;
     double fb_lat = 0.00;
     double fb_lng = 0.00;

    @Override
    public boolean onStartJob(JobParameters job)
    {
        Log.i("FJD.DemoJobService", "onStartJob called");

        Bundle extras = job.getExtras();
        assert extras != null;
        int result = extras.getInt("return");
        CentralContainer.getStore(getApplicationContext()).recordResult(job, result);
        jsonParser = new JSONParser();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        if (!currentlyProcessingLocation)
        {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return false; // No more work to do

    }

@Override
public boolean onStopJob(JobParameters job)
{
    return false;

}

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            Log.e(TAG, "service: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            if (location.getAccuracy() < 500.0f)
            {
                stopLocationUpdates();
                sendLocationDataToWebsite(location);
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle)
    {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.e(TAG, "onConnectionFailed");
        stopLocationUpdates();
        stopSelf();
    }

    private void startTracking()
    {
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

    protected void sendLocationDataToWebsite(Location location)
    {
        // formatted for mysql datetime format

        location.getLatitude();
        location.getLongitude();
        Log.e("sssss",location.getLatitude()+"");
        Log.e("sssss",location.getLongitude()+"");
        fb_lat = location.getLatitude();
        fb_lng =location.getLongitude();
        new GetJson().execute();
        stopSelf();
    }
    private void stopLocationUpdates()
    {
        if (googleApiClient != null && googleApiClient.isConnected())
        {
            googleApiClient.disconnect();
        }
    }
    private class GetJson extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params)
        {
            try
            {

                JSONObject prmsLogin = new JSONObject();

                prmsLogin.put("load_inquiry_no", "");
                prmsLogin.put("remaining_kms", "");
                prmsLogin.put("eta", "");
                prmsLogin.put("driver_id", "");
                prmsLogin.put("device_id", android_id+"");
                prmsLogin.put("truck_id", "");
                prmsLogin.put("truck_lat", fb_lat+"");
                prmsLogin.put("truck_lng", fb_lng+"");
                prmsLogin.put("truck_location", "");
                prmsLogin.put("created_by", "driver_app7");
                prmsLogin.put("created_host", "");
                prmsLogin.put("device_type", "");

                //latlngJson = UF.RegisterUser("truck/SaveTruckPositionNew", prmsLogin);
                // latlngJson = jsonParser.sendJSONPost("http://trukker.ae/trukkerUAEApi/Api/truck/SaveTruckPositionNew" , prmsLogin);
                latlngJson = jsonParser.sendJSONPost(UserFunctions.URL+"truck/SaveTruckPositionNew" , prmsLogin);
                latlngJson = latlngJson.trim();
                if (latlngJson.length() > 0) {
                    try {
                        latlngJson = new JSONTokener(latlngJson).nextValue().toString();
                    } catch (JSONException e)
                    {
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
