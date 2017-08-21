package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.BackgroundOrderService;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.Helper.DirectionsJSONParser;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class TrackOrderMap extends Activity {

    // Google Map
    private GoogleMap googleMap;
    Intent i;
    private LatLng MY_LOC;
    public Polyline line, line1;
    LatLng destLatLng;
    boolean islineFlag = false;
    double startlat = 0.0;
    double starttlng = 0.0;
    double endlat = 0.0;
    double endlng = 0.0;

    MarkerOptions marker1 = null, marker2 = null;
    String jsonHistory, loadingstart_time, start_time, unloadingstart_time, complete_time;
    String truck_id, load_inquiry_id, source_lat, source_lng, dest_lat, dest_lng, gcmRegID, unregJson, position;
    private IntentFilter mIntentFilter;
    android.support.v7.app.AlertDialog alert;
    android.support.v7.app.AlertDialog.Builder dialog;
    boolean visible = false;

    UserFunctions UF;
    ConnectionDetector cd;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order_map);
        try {
            init();
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction("NewNearOrder");
            mIntentFilter.addAction("ACTION_LOGOUT");

            Intent intent = getIntent();
            truck_id = intent.getStringExtra("truck_id");
            load_inquiry_id = intent.getStringExtra("load_inquiry_no");
            source_lat = intent.getStringExtra("source_lat");
            source_lng = intent.getStringExtra("source_lng");
            dest_lat = intent.getStringExtra("dest_lat");
            dest_lng = intent.getStringExtra("dest_lng");

            destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));

            try {
                // Loading map
                initilizeMap();

                if (cd.isConnectingToInternet()) {
                    new GetJson().execute();
                } else {
                    UF.msg("Internet connection lost or slow");
                }

                // Changing map type
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

                googleMap.setMyLocationEnabled(false);// Showing / hiding your current location
                googleMap.getUiSettings().setZoomControlsEnabled(false); // Enable / Disable zooming controls
                //googleMap.getUiSettings().setMyLocationButtonEnabled(true);// Enable / Disable my location button
                googleMap.getUiSettings().setCompassEnabled(true);// Enable / Disable Compass icon
                googleMap.getUiSettings().setRotateGesturesEnabled(true); // Enable / Disable Rotate gesture
                googleMap.getUiSettings().setZoomGesturesEnabled(true);// Enable / Disable zooming functionality
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    // Use default InfoWindow frame
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    // Defines the contents of the InfoWindow
                    @Override
                    public View getInfoContents(Marker marker) {

                        // Getting view from the layout file info_window_layout
                        View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                        TextView title1, value1, title2, value2, title;
                        title = (TextView) v.findViewById(R.id.title);
                        title1 = (TextView) v.findViewById(R.id.title1);
                        value1 = (TextView) v.findViewById(R.id.value1);
                        title2 = (TextView) v.findViewById(R.id.title2);
                        value2 = (TextView) v.findViewById(R.id.value2);
                        try {
                            if (marker.getTitle().equalsIgnoreCase("Source Location")) {
                                title.setText("Source Location");
                                title1.setText("Loading Start:");
                                value1.setText(loadingstart_time);
                                title2.setText("Start For Destination:");
                                value2.setText(start_time);
                            } else if (marker.getTitle().equalsIgnoreCase("Destination Location")) {
                                title.setText("Destination Location");
                                title1.setText("Unloading Start:");
                                value1.setText(unloadingstart_time);
                                title2.setText("Complete Time:");
                                value2.setText(complete_time);
                            }
                        } catch (Exception e) {
                        }
                        return v;
                    }

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {

        }
    }

    private void init() {
        sm = new SessionManager(TrackOrderMap.this);
        UF = new UserFunctions(TrackOrderMap.this);
        cd = new ConnectionDetector(TrackOrderMap.this);
        MY_LOC = new LatLng(0.0, 0.0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            registerReceiver(mReceiver, mIntentFilter);
            initilizeMap();
        }catch (Exception e){

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            unregisterReceiver(mReceiver);
        }catch (Exception e){

        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("ACTION_LOGOUT")) {
                int counter = intent.getIntExtra("LOGOUT", 0);
                if (counter == 0) {
                    LogoutUser();
                } else {

                }
            }
        }
    };

    private void LogoutUser() {
        UF.msg("Account Login By Another Device");
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        sm.logoutUser();
        if (!isMyServiceRunning(BackgroundOrderService.class)) {
        } else {
            stopService(new Intent(TrackOrderMap.this, BackgroundOrderService.class));
        }
        gcmRegID = FirebaseInstanceId.getInstance().getToken();
        new UnregisterUser().execute();
    }

    //unreg device
    private class UnregisterUser extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject prmsLogin = new JSONObject();
                prmsLogin.put("AppName", "TrukkerUAE");
                prmsLogin.put("DeviceId", gcmRegID);
                unregJson = UF.LoginUser("login/UnRegisterDevice", prmsLogin);
                // Log.e("gcm_reg_response", unregJson + "");
            } catch (Exception e) {
            }
            return unregJson;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (unregJson != null) {
                    if (unregJson.equalsIgnoreCase("0")) {

                    } else {
                        JSONObject jobj = new JSONObject(unregJson);
                        String status = jobj.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                        } else {
                        }
                    }
                } else {
                }
            } catch (Exception e) {
            }

        }
    }

    /**
     * function to load map If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void drawRoot(GoogleMap mMap, String source_lat, String source_lng, String dest_lat, String dest_lng, String title) {
        final LatLng lat_lng1 = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
        final LatLng lat_lng2 = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));

        marker1 = new MarkerOptions().position(lat_lng1).title("Source Location").icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        marker2 = new MarkerOptions().position(lat_lng2).title("Destination Location").icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.addMarker(marker1);
        mMap.addMarker(marker2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng1, 15));

        String url = getDirectionsUrl(lat_lng1, lat_lng2);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                //Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                ParserTask parserTask = new ParserTask();

                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);

            } catch (Exception e) {

            }
        }
    }

    private class DownloadTask1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                // Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {

                ParserTask1 parserTask1 = new ParserTask1();

                // Invokes the thread for parsing the JSON data
                parserTask1.execute(result);
            } catch (Exception e) {

            }

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();

                // Traversing through all the routes
                if (result != null) {
                    if (result.size() > 0) {

                        for (int i = 0; i < result.size(); i++) {
                            points = new ArrayList<LatLng>();
                            lineOptions = new PolylineOptions();

                            // Fetching i-th route
                            List<HashMap<String, String>> path = result.get(i);

                            // Fetching all the points in i-th route
                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);

                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);

                                points.add(position);
                            }

                            // Adding all the points in the route to LineOptions
                            lineOptions.addAll(points);
                            if (!islineFlag) {
                                lineOptions.width(10);
                                lineOptions.color(Color.parseColor("#FF0000")).geodesic(true);
                                islineFlag = true;
                            } else {
                                lineOptions.width(7);
                                lineOptions.color(Color.parseColor("#FF0000")).geodesic(true);
                            }
                            //lineOptions.color(Color.parseColor("#59c1e3")).geodesic(true);
                        }
                        line = googleMap.addPolyline(lineOptions);    // Drawing polyline in the Google Map for the i-th route
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    private class ParserTask1 extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();

                // Traversing through all the routes
                if (result != null) {
                    if (result.size() > 0) {

                        for (int i = 0; i < result.size(); i++) {
                            points = new ArrayList<LatLng>();
                            lineOptions = new PolylineOptions();

                            // Fetching i-th route
                            List<HashMap<String, String>> path = result.get(i);

                            // Fetching all the points in i-th route
                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);

                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);

                                points.add(position);
                            }

                            // Adding all the points in the route to LineOptions
                            lineOptions.addAll(points);
                            if (!islineFlag) {
                                lineOptions.width(10);
                                lineOptions.color(Color.parseColor("#59c1e3")).geodesic(true);
                                islineFlag = true;
                            } else {
                                lineOptions.width(7);
                                lineOptions.color(Color.parseColor("#59c1e3")).geodesic(true);
                            }
                            //lineOptions.color(Color.parseColor("#59c1e3")).geodesic(true);
                        }
                        line = googleMap.addPolyline(lineOptions);    // Drawing polyline in the Google Map for the i-th route
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.d("Exception url", "" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String key = "key=" + UF.direction_key;
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + key;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        Log.e("direction url", url);
        return url;
    }

    private String getDirectionsUrlWayPoints(LatLng origin, LatLng dest, ArrayList<HashMap<String, String>> waypointlist) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String waypoints = "";
        waypoints = "waypoints=";
        // waypoints = "waypoints=23.0192808,72.5189742|23.0211166,72.51610359999995";
        if (waypointlist.size() > 0) {
            for (int i = 0; i < waypointlist.size(); i++) {
                waypoints += waypointlist.get(i).get("truck_lat") + "," + waypointlist.get(i).get("truck_lng") + "|";
            }
        }
        // waypoints += point.latitude + "," + point.longitude + "|";
        String key = "key=" + UF.direction_key;
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints + "&" + key;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        Log.e("direction url", url);
        return url;
    }

    private class GetJson extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(TrackOrderMap.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            jsonHistory = UF.TruckMakeMst("truck/GetTruckHistorylocation?truckid=" + truck_id + "&loadinqno=" + load_inquiry_id);
            Log.e("Historylocation", jsonHistory + "");
            return jsonHistory;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog != null) {
                pDialog.dismiss();
            }
            ArrayList<HashMap<String, String>> LatLongWaypointList = new ArrayList<>();
            if (jsonHistory == null) {
                UF.msg("Connection Problem");
            } else {
                if (jsonHistory.equalsIgnoreCase("0")) {
                    UF.msg("No Data Available.");
                } else {
                    try {
                        JSONObject jobj = new JSONObject(jsonHistory);

                        String status = jobj.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            JSONArray jArray = jobj.getJSONArray("message");
                            if (jArray.length() > 0) {
                                JSONObject jobj1 = jArray.getJSONObject(0);
                                String truck_start_lat = jobj1.getString("truck_lat");
                                String truck_start_lng = jobj1.getString("truck_lng");
                                startlat = Double.parseDouble(truck_start_lat);
                                starttlng = Double.parseDouble(truck_start_lng);

                                loadingstart_time = jobj1.getString("loadingstart_time");//11/11/2016 6:21:13 AM
                                start_time = jobj1.getString("start_time");
                                unloadingstart_time = jobj1.getString("unloadingstart_time");
                                complete_time = jobj1.getString("complete_time");

                                JSONObject jobj2 = jArray.getJSONObject(jArray.length() - 1);
                                String truck_end_lat = jobj2.getString("truck_lat");
                                String truck_end_lng = jobj2.getString("truck_lng");
                                endlat = Double.parseDouble(truck_end_lat);
                                endlng = Double.parseDouble(truck_end_lng);

                                for (int i = 1; i < jArray.length() - 1; i++) {
                                    HashMap<String, String> map = new HashMap<>();
                                    JSONObject jobj3 = jArray.getJSONObject(i);
                                    String truck_lat = jobj3.getString("truck_lat");
                                    String truck_lng = jobj3.getString("truck_lng");
                                    map.put("truck_lat", truck_lat);
                                    map.put("truck_lng", truck_lng);
                                    LatLongWaypointList.add(map);
                                }
                                String urlfollowed = getDirectionsUrlWayPoints(new LatLng(startlat, starttlng), new LatLng(endlat, endlng), LatLongWaypointList);
                                DownloadTask1 downloadTask1 = new DownloadTask1();
                                downloadTask1.execute(urlfollowed);

                                changeDateFormate();
                            }
                            drawRoot(googleMap, source_lat, source_lng, dest_lat, dest_lng, "");
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void changeDateFormate() {
        // 11/10/2016 12:17:47 PM
        try {
            if (loadingstart_time.equalsIgnoreCase("")) {

            } else {
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                    Date date = parseFormat.parse(loadingstart_time);
                    loadingstart_time = displayFormat.format(date);

                    Date date11 = null;
                    date11 = displayFormat.parse(loadingstart_time);

                    DateFormat gmtFormate = new SimpleDateFormat();
                    TimeZone gmtTime = TimeZone.getTimeZone("GMT+04:00");
                    gmtFormate.setTimeZone(gmtTime);
                    Log.e("time_zone", gmtFormate.format(date11));
                    loadingstart_time = gmtFormate.format(date11);

                } catch (final ParseException e) {
                    e.printStackTrace();
                }
            }

            if (start_time.equalsIgnoreCase("")) {
            } else {
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                    Date date = parseFormat.parse(start_time);
                    start_time = displayFormat.format(date);

                    Date date11 = null;
                    date11 = displayFormat.parse(start_time);

                    DateFormat gmtFormate = new SimpleDateFormat();
                    TimeZone gmtTime = TimeZone.getTimeZone("GMT+04:00");
                    gmtFormate.setTimeZone(gmtTime);
                    Log.e("time_zone", gmtFormate.format(date11));
                    start_time = gmtFormate.format(date11);

                } catch (final ParseException e) {
                    e.printStackTrace();
                }
            }

            if (unloadingstart_time.equalsIgnoreCase("")) {

            } else {
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                    Date date = parseFormat.parse(unloadingstart_time);
                    unloadingstart_time = displayFormat.format(date);

                    Date date11 = null;
                    date11 = displayFormat.parse(unloadingstart_time);

                    DateFormat gmtFormate = new SimpleDateFormat();
                    TimeZone gmtTime = TimeZone.getTimeZone("GMT+04:00");
                    gmtFormate.setTimeZone(gmtTime);
                    Log.e("time_zone", gmtFormate.format(date11));
                    unloadingstart_time = gmtFormate.format(date11);

                } catch (final ParseException e) {
                    e.printStackTrace();
                }
            }

            if (complete_time.equalsIgnoreCase("")) {

            } else {
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                    Date date = parseFormat.parse(complete_time);
                    complete_time = displayFormat.format(date);

                    Date date11 = null;
                    date11 = displayFormat.parse(complete_time);

                    DateFormat gmtFormate = new SimpleDateFormat();
                    TimeZone gmtTime = TimeZone.getTimeZone("GMT+04:00");
                    gmtFormate.setTimeZone(gmtTime);
                    Log.e("time_zone", gmtFormate.format(date11));
                    complete_time = gmtFormate.format(date11);

                } catch (final ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
