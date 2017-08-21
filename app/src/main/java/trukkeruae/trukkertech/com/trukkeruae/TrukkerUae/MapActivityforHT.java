package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.BackgroundOrderService;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.GPSTracker;
import trukkeruae.trukkertech.com.trukkeruae.Helper.DataBaseHelper;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class MapActivityforHT extends FragmentActivity implements LocationListener, View.OnClickListener {

    UserFunctions UF;
    ConnectionDetector cd;
    SessionManager sm;
    GPSTracker gps;
    DataBaseHelper db;

    GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    Marker my_Marker;

    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    double fused_lat, fused_lng;
    String f_latitude, f_longitude, gcmRegID, unregJson;
    double source_lat = 0.0, source_lng = 0.0;

    AppCompatButton start_pickup_btn, loading_start_btn, unloading_complete_btn;
    LinearLayout dialog_view, mobile_num_layout, emailid_layout, shipper_name_layout, total_fright_layout,
            footer_layout;
    RelativeLayout order_detail_layout;
    ImageView icon_arrow;
    TextView customer_name_tv, customer_emailid_tv, load_inq_no, tvsource, cutomer_mobno_tv, total_fright_tv, order_detail_btn,
            lable_from, lable_fright, lable_shipper_name, lable_emailid, lable_mobile_num;
    FloatingActionButton fab;

    ArrayList<String> latLngListForDeletion;
    private IntentFilter mIntentFilter;

    int position;
    boolean visible = false;
    String json_save, statuscode, android_id, source_add, shipper_id, truck_id, owner_id, receiver_mobile, driver_id;

    android.support.v7.app.AlertDialog alert;
    android.support.v7.app.AlertDialog.Builder dialog;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_map_activityfor_ht);

        //get value of intent
        try {
            position = Constants.click_position;

            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction("NewNearOrder");
            mIntentFilter.addAction("ACTION_LOGOUT");
            mIntentFilter.addAction("OrderListForStatus");
        } catch (Exception e) {
            position = 0;
            e.printStackTrace();
        }
        //initialize values & id
        init();
        try {
            //set google map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_map);
            // Getting reference to the Google Map
            mMap = mapFragment.getMap();
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            if (cd.isConnectingToInternet()) {
                try {
                    gps.getLocation();
                    if (gps.getLongitude() == 0.0) {
                        if (gps.canGetLocation()) {
                            gps.getLocation();
                            my_Marker = mMap.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(), gps.getLongitude())).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck)));
                            //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()), 15));
                            f_latitude = String.valueOf(gps.getLatitude());
                            f_longitude = String.valueOf(gps.getLongitude());

                            float zoomLevel = 15; //This goes up to 21
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(gps.getLatitude(), gps.getLongitude())), zoomLevel));
                        }

                    } else {
                        gps.getLocation();
                        my_Marker = mMap.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(), gps.getLongitude())).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck)));
                        //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()), 15));
                        f_latitude = String.valueOf(gps.getLatitude());
                        f_longitude = String.valueOf(gps.getLongitude());

                        float zoomLevel = 15; //This goes up to 21
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(gps.getLatitude(), gps.getLongitude())), zoomLevel));
                    }

                } catch (Exception e) {
                    if (gps.canGetLocation()) {
                        gps.getLocation();
                        my_Marker = mMap.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(), gps.getLongitude())).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck)));
                        //   mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()), 15));
                        f_latitude = String.valueOf(gps.getLatitude());
                        f_longitude = String.valueOf(gps.getLongitude());

                        float zoomLevel = 15; //This goes up to 21
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(gps.getLatitude(), gps.getLongitude())), zoomLevel));

                    } else {
                        if (gps.canGetLocation()) {
                            gps.getLocation();
                        }
                    }
                }
            }
            getTruckLatLng();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String status = Constants.map2.get(position).get("status");
            sm.saveLastTruckStatus(status);
            sm.setLastCompleteOrder("");
            updateUI();

            //set all data
            shipper_id = Constants.map2.get(position).get("shipper_id");
            truck_id = Constants.map2.get(position).get("truck_id");
            sm.saveDriverId(truck_id);
            owner_id = Constants.map2.get(position).get("owner_id");
            receiver_mobile = Constants.map2.get(position).get("receiver_mobile");
            driver_id = Constants.map2.get(position).get("driver_id");

            if (sm.getDriverDuety().equalsIgnoreCase("true")) {
                start_pickup_btn.setEnabled(true);
                loading_start_btn.setEnabled(true);
                unloading_complete_btn.setEnabled(true);
            } else {

                start_pickup_btn.setEnabled(false);
                loading_start_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(false);
                UF.msg("You Are Off Duty On This Time");
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (dialog_view.getVisibility() == View.VISIBLE) {
                dialog_view.setVisibility(View.GONE);
            } else {
                super.onBackPressed();
                MapActivityforHT.this.overridePendingTransition(
                        R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            }
            visible = false;

            mGoogleApiClient.disconnect();
        } catch (Exception e) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        try {
            position = Constants.click_position;
            registerReceiver(mReceiver, mIntentFilter);
            if (checkPlayServices()) {
                startFusedLocation();
                registerRequestUpdate(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mGoogleApiClient.disconnect();
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        //set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");

        //get common classes
        UF = new UserFunctions(MapActivityforHT.this);
        cd = new ConnectionDetector(MapActivityforHT.this);
        sm = new SessionManager(MapActivityforHT.this);
        gps = new GPSTracker(MapActivityforHT.this);
        db = new DataBaseHelper(getApplicationContext());

        pDialog = new ProgressDialog(MapActivityforHT.this);

        //find id & set style & font
        order_detail_layout = (RelativeLayout) findViewById(R.id.order_detail_layout);
        order_detail_layout.setOnClickListener(this);

        start_pickup_btn = (AppCompatButton) findViewById(R.id.start_pickup_btn);
        start_pickup_btn.setTypeface(font);
        start_pickup_btn.setOnClickListener(this);
        start_pickup_btn.setVisibility(View.GONE);

        loading_start_btn = (AppCompatButton) findViewById(R.id.loading_start_btn);
        loading_start_btn.setTypeface(font);
        loading_start_btn.setOnClickListener(this);
        loading_start_btn.setVisibility(View.GONE);

        unloading_complete_btn = (AppCompatButton) findViewById(R.id.unloading_complete_btn);
        unloading_complete_btn.setTypeface(font);
        unloading_complete_btn.setOnClickListener(this);
        unloading_complete_btn.setVisibility(View.GONE);

        mobile_num_layout = (LinearLayout) findViewById(R.id.mobile_num_layout);
        emailid_layout = (LinearLayout) findViewById(R.id.emailid_layout);
        shipper_name_layout = (LinearLayout) findViewById(R.id.shipper_name_layout);
        total_fright_layout = (LinearLayout) findViewById(R.id.total_fright_layout);

        dialog_view = (LinearLayout) findViewById(R.id.dialog_view);
        dialog_view.setVisibility(View.GONE);
        footer_layout = (LinearLayout) findViewById(R.id.footer_layout);
        footer_layout.bringToFront();

        icon_arrow = (ImageView) findViewById(R.id.icon_arrow);
        fab = (FloatingActionButton) findViewById(R.id.fab_navigation);
        fab.bringToFront();
        fab.setOnClickListener(this);

        //dialog data
        customer_name_tv = (TextView) findViewById(R.id.customer_name_tv);
        customer_name_tv.setTypeface(font);
        total_fright_tv = (TextView) findViewById(R.id.total_fright_tv);
        total_fright_tv.setTypeface(font);
        load_inq_no = (TextView) findViewById(R.id.load_inq_no);
        load_inq_no.setTypeface(font);
        customer_emailid_tv = (TextView) findViewById(R.id.customer_emailid_tv);
        customer_emailid_tv.setTypeface(font);
        tvsource = (TextView) findViewById(R.id.tvsource);
        tvsource.setTypeface(font);
        cutomer_mobno_tv = (TextView) findViewById(R.id.cutomer_mobno_tv);
        cutomer_mobno_tv.setTypeface(font);
        order_detail_btn = (TextView) findViewById(R.id.order_detail_btn);
        order_detail_btn.setTypeface(font);

        lable_from = (TextView) findViewById(R.id.lable_from);
        lable_from.setTypeface(font);
        lable_fright = (TextView) findViewById(R.id.lable_fright);
        lable_fright.setTypeface(font);
        lable_shipper_name = (TextView) findViewById(R.id.lable_shipper_name);
        lable_shipper_name.setTypeface(font);
        lable_emailid = (TextView) findViewById(R.id.lable_emailid);
        lable_emailid.setTypeface(font);
        lable_mobile_num = (TextView) findViewById(R.id.lable_mobile_num);
        lable_mobile_num.setTypeface(font);

        //get devicce id
        android_id = Settings.Secure.getString(MapActivityforHT.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        latLngListForDeletion = new ArrayList<String>();

        try {
            if (cd.isConnectingToInternet()) {
            } else {
                UF.msg("Internet connection lost or slow");

                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(MapActivityforHT.this);
                dialog.setCancelable(false);
                dialog.setMessage("Please check your internet connection and try again");
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finish();
                        MapActivityforHT.this.overridePendingTransition(
                                R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                    }
                });
                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                    }
                });

                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (gps.canGetLocation()) {
                gps.getLocation();
            } else {
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(MapActivityforHT.this);
                dialog.setCancelable(false);
                dialog.setMessage("GPS is not enabled.First Enable GPS Location");
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finish();
                        MapActivityforHT.this.overridePendingTransition(
                                R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                    }
                });
                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

                dialog.show();
                if (gps.canGetLocation()) {
                    gps.getLocation();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab_navigation:
                try {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", gps.getLatitude(), gps.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.order_detail_layout:
                setAnimationDialog();
                break;

            case R.id.start_pickup_btn:
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(MapActivityforHT.this);
                dialog.setMessage("Are you sure For Start Pickup?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statuscode = "05";
                        callActionService();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;

            case R.id.loading_start_btn:
                android.support.v7.app.AlertDialog.Builder dialog2 = new android.support.v7.app.AlertDialog.Builder(MapActivityforHT.this);
                dialog2.setMessage("Are you sure for Start Order?");
                dialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {

                        statuscode = "07";
                        callActionService();
                    }
                });
                dialog2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;

            case R.id.unloading_complete_btn:
                android.support.v7.app.AlertDialog.Builder dialog5 = new android.support.v7.app.AlertDialog.Builder(MapActivityforHT.this);
                dialog5.setMessage("Are you sure for Unloading Completed?");
                dialog5.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog5, int which) {
                        statuscode = "45";
                        callActionService();
                    }
                });
                dialog5.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
    }

    private void setAnimationDialog() {
        try {
            if (dialog_view.getVisibility() == View.VISIBLE) {
                icon_arrow.setImageResource(R.drawable.up_arrow);
                Animation anm2 = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.slide_down2);
                dialog_view.startAnimation(anm2);
                dialog_view.setVisibility(View.GONE);
                dialog_view.setClickable(false);
            } else {
                icon_arrow.setImageResource(R.drawable.down_arrow);
                Animation anm = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.slide_up2);
                dialog_view.startAnimation(anm);
                dialog_view.bringToFront();
                dialog_view.setVisibility(View.VISIBLE);
                dialog_view.setClickable(true);
            }
        } catch (Exception e) {
        }
    }

    private void callActionService() {
        if (cd.isConnectingToInternet()) {
            new GetJsonAction().execute();
        } else {
            UF.msg("Internet connection loss or slow");
        }
    }

    //call service for action:
    private class GetJsonAction extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            JSONObject prms = new JSONObject();
            JSONObject prmsLogin = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                prmsLogin.put("load_inquiry_no", Constants.map2.get(position).get("load_inquiry_no"));
                prmsLogin.put("shipper_id", shipper_id);
                prmsLogin.put("truck_id", truck_id);
                prmsLogin.put("owner_id", owner_id);
                try {
                    //put current lat lang
                    if (Constants.f_lat.equalsIgnoreCase("0.0") || Constants.f_lat.equalsIgnoreCase("0") || Constants.f_lat.equalsIgnoreCase("")) {
                        prmsLogin.put("current_lat", f_latitude);
                        prmsLogin.put("current_lng", f_longitude);
                    } else {
                        prmsLogin.put("current_lat", Constants.f_lat);
                        prmsLogin.put("current_lng", Constants.f_lon);
                    }

                } catch (Exception e) {
                    prmsLogin.put("current_lat", Constants.f_lat);
                    prmsLogin.put("current_lng", Constants.f_lon);
                }
                prmsLogin.put("otp", "");
                prmsLogin.put("receiver_mobile", receiver_mobile);
                prmsLogin.put("status", statuscode);
                prmsLogin.put("driver_id", driver_id);

                if (statuscode.equalsIgnoreCase("05")) {
                    if (Constants.distance.length() > 0) {
                        if (Constants.distance.contains(" ")) {
                            String str[] = Constants.distance.split(" ");
                            prmsLogin.put("distance_kms_to_origin", str[0]);
                        } else {
                            prmsLogin.put("distance_kms_to_origin", Constants.distance);
                        }
                    } else {
                        prmsLogin.put("distance_kms_to_origin", "0.0");
                    }
                    prmsLogin.put("approx_time_to_reach", Constants.duration);
                } else {
                    prmsLogin.put("distance_kms_to_origin", "0.0");
                    prmsLogin.put("approx_time_to_reach", "");
                }

                prmsLogin.put("created_by", sm.getUserId());
                prmsLogin.put("created_host", android_id);
                prmsLogin.put("device_id", android_id);
                prmsLogin.put("device_type", "Android-Mobile");
                jsonArray.put(prmsLogin);

                prms.put("orders", jsonArray);
                Log.e("SaveDriverNoti_req", prms + "");
                json_save = UF.LoginUser("driver/SaveDriverNotification", prms);
                Log.e("SaveDriverNoti_res", json_save + "");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return json_save;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            {
                if (json_save.equalsIgnoreCase("0")) {
                } else {
                    try {
                        JSONObject jobj = new JSONObject(json_save);
                        String status = jobj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            String msg = jobj.getString("message");
                            sm.saveInqNO(Constants.map2.get(position).get("load_inquiry_no"));
                            sm.saveListPosition(String.valueOf(position));

                            if (statuscode.equalsIgnoreCase("05")) {
                                sm.setBothLatLng(Constants.map2.get(position).get("inquiry_source_lat").toString(),
                                        Constants.map2.get(position).get("inquiry_source_lng").toString(),
                                        Constants.map2.get(position).get("inquiry_destionation_lat").toString(),
                                        Constants.map2.get(position).get("inquiry_destionation_lng").toString());
                                sm.saveLastTruckStatus(statuscode);     //store currrent ongoing last staus
                                sm.saveBusyOrd("Y");
                                sm.setLastCompleteOrder("");
                                sm.setLastCompleteOrder(sm.getInqId());
                            }

                            if (statuscode.equalsIgnoreCase("07")) {

                                sm.saveLastTruckStatus(statuscode);
                                sm.setLastCompleteOrder(sm.getInqId());
                            }

                            if (statuscode.equalsIgnoreCase("45")) {

                                sm.setLastCompleteOrder(sm.getInqId());
                                Intent i = new Intent(MapActivityforHT.this, OrderListActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();

                                sm.saveLastTruckStatus(statuscode);
                                sm.saveInqNO("");
                                sm.saveBusyOrd("N");
                                sm.setBothLatLng("", "", "", ""); //set null value

                               /* AlarmManager alarmManager1;
                                Intent myIntent;
                                PendingIntent pendingIntent1;

                                alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                myIntent = new Intent(getApplicationContext(), MyWakefulReceiver.class);
                                pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 2015, myIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                                if (Build.VERSION.SDK_INT >= 23) {
                                    alarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                                            SystemClock.elapsedRealtime() + 3000, pendingIntent1);
                                    alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                            SystemClock.elapsedRealtime() + 3000, 30 * 60, pendingIntent1);
                                } else {
                                    alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                            SystemClock.elapsedRealtime() + 3000, 60 * 1000, pendingIntent1);
                                }*/
                            }
                            updateUI();
                        } else if (status.equalsIgnoreCase("3")) {
                            LogoutUser();
                        } else {
                            String msg = jobj.getString("message");
                            UF.msg(msg + "");
                        }
                    } catch (Exception e) {
                        UF.msg("Internet connection loss or slow");
                    }
                }
            }
        }
    }

    private void LogoutUser() {
        UF.msg("Account Login By Another Device");
        try {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            sm.logoutUser();

            if (!isMyServiceRunning(BackgroundOrderService.class)) {
            } else {
                stopService(new Intent(MapActivityforHT.this, BackgroundOrderService.class));
            }
            gcmRegID = FirebaseInstanceId.getInstance().getToken();
            new UnregisterUser().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                e.printStackTrace();
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

    private void getTruckLatLng() {
        try {
            if (Constants.maptmpforLatlng.size() > 0) {
                double destlatitude = 0.0;
                double destlongitude = 0.0;
                source_add = Constants.map2.get(position).get("inquiry_source_addr");
                if (Constants.maptmpforLatlng.get(position).get("inquiry_source_lat").length() > 0) {
                    source_lat = Double.parseDouble(Constants.maptmpforLatlng.get(position).get("inquiry_source_lat"));
                } else {
                    source_lat = 0.0;
                }
                if (Constants.maptmpforLatlng.get(position).get("inquiry_source_lng").length() > 0) {
                    source_lng = Double.parseDouble(Constants.maptmpforLatlng.get(position).get("inquiry_source_lng"));
                } else {
                    source_lng = 0.0;
                }

            } else {

            }

            //set dialog view data
            if (Constants.map2.get(position).get("shipper_name").equalsIgnoreCase("") || Constants.map2.get(position).get("shipper_name").equalsIgnoreCase("0") || Constants.map2.get(position).get("shipper_name").equalsIgnoreCase(null)) {
                customer_name_tv.setText("-");
            } else {
                customer_name_tv.setText(Constants.map2.get(position).get("shipper_name"));
            }

            if (Constants.map2.get(position).get("shipper_email").equalsIgnoreCase("") || Constants.map2.get(position).get("shipper_email").equalsIgnoreCase("0") || Constants.map2.get(position).get("shipper_email").equalsIgnoreCase(null)) {
                customer_emailid_tv.setText("-");
            } else {
                customer_emailid_tv.setText(Constants.map2.get(position).get("shipper_email"));
            }
            if (Constants.map2.get(position).get("shipper_mobile").equalsIgnoreCase("") || Constants.map2.get(position).get("shipper_mobile").equalsIgnoreCase(null) || Constants.map2.get(position).get("shipper_mobile").equalsIgnoreCase("0")) {
                cutomer_mobno_tv.setText("-");
                cutomer_mobno_tv.setVisibility(View.GONE);
            } else {
                cutomer_mobno_tv.setText(Constants.map2.get(position).get("shipper_mobile"));
                cutomer_mobno_tv.setVisibility(View.VISIBLE);
            }
            if (Constants.map2.get(position).get("totalamount_shipper").equalsIgnoreCase("") || Constants.map2.get(position).get("totalamount_shipper").equalsIgnoreCase(null) || Constants.map2.get(position).get("totalamount_shipper").equalsIgnoreCase("0")) {
                total_fright_tv.setText("-");
            } else {
                total_fright_tv.setText(Constants.map2.get(position).get("totalamount_shipper"));
            }
            load_inq_no.setText("Trip " + Constants.map2.get(position).get("load_inquiry_no"));

            String source_add = Constants.map2.get(position).get("source_full_add");
            if (source_add.equalsIgnoreCase("") || source_add.equalsIgnoreCase(null)) {
                tvsource.setText("-");
            } else {
                if (source_add.contains("^")) {
                    source_add = source_add.replace("^", ",");
                    tvsource.setText(source_add);
                } else {
                    tvsource.setText(Constants.map2.get(position).get("source_full_add"));
                }
            }

            // final int num= Integer.parseInt(cutomer_mobno_tv.getText().toString());
            cutomer_mobno_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + Constants.map2.get(position).get("shipper_mobile")));
                    startActivity(intent);
                }
            });
        } catch (Exception e) {

        }
    }

    private void updateUI() {
        try {
            if (sm.getLastTruckStatus().equalsIgnoreCase("02")) {
                start_pickup_btn.setEnabled(true);
                loading_start_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(false);

                start_pickup_btn.setVisibility(View.VISIBLE);

            } else if (sm.getLastTruckStatus().equalsIgnoreCase("05")) {
                start_pickup_btn.setEnabled(false);
                loading_start_btn.setEnabled(true);
                unloading_complete_btn.setEnabled(false);

                loading_start_btn.setVisibility(View.VISIBLE);
                start_pickup_btn.setVisibility(View.GONE);
            } else if (sm.getLastTruckStatus().equalsIgnoreCase("07")) {
                start_pickup_btn.setEnabled(false);
                loading_start_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(true);

                loading_start_btn.setVisibility(View.GONE);
                start_pickup_btn.setVisibility(View.GONE);
                unloading_complete_btn.setVisibility(View.VISIBLE);
            } else {
                start_pickup_btn.setEnabled(true);
                loading_start_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(false);

                start_pickup_btn.setVisibility(View.VISIBLE);
                loading_start_btn.setVisibility(View.GONE);
                unloading_complete_btn.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Log.e("location_change", location.getLatitude() + "," + location.getLongitude());
            setFusedLatitude(location.getLatitude());
            setFusedLongitude(location.getLongitude());
            fused_lat = getFusedLatitude();
            fused_lng = getFusedLongitude();
            f_latitude = String.valueOf(getFusedLatitude());
            f_longitude = String.valueOf(getFusedLongitude());

            LatLng f_latlng = new LatLng(fused_lat, fused_lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(f_latlng));

            //set marker
            if (my_Marker != null) {
                my_Marker.remove();
            }
            my_Marker = mMap.addMarker(new MarkerOptions().position(new LatLng(fused_lat, fused_lng)).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck)));

        } catch (Exception e) {

        }
    }

    //check for google palyservice
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(MapActivityforHT.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            } else {

            }
            return false;
        }
        return true;
    }

    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MapActivityforHT.this).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {

                        }

                        @Override

                        public void onConnected(Bundle connectionHint) {

                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {

                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // every 5 second

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 0);
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    public void setFusedLatitude(double lat) {
        fusedLatitude = lat;
    }

    public void setFusedLongitude(double lon) {
        fusedLongitude = lon;
    }

    public double getFusedLatitude() {
        return fusedLatitude;
    }

    public double getFusedLongitude() {
        return fusedLongitude;
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
            if (intent.getAction().equals("OrderListForStatus")) {

                int counter = intent.getIntExtra("Refresh", 0);
                if (counter == 0) {
                    if (Constants.map2.size() > 0 && position < Constants.map2.size()) {

                        try {
                            String status = Constants.map2.get(position).get("status");

                            if (status.equalsIgnoreCase("02")) {
                                if (!sm.getLastCompleteOrder().equalsIgnoreCase("")) {
                                    String txt = load_inq_no.getText().toString();
                                    String[] splited = txt.split("\\s+");
                                    txt = splited[1];

                                    for (int i = 0; i < Constants.only_order_id.size(); i++) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map = Constants.only_order_id.get(i);
                                        Log.e("map", map.toString());
                                        if (map.containsValue(txt.trim())) {
                                            sm.setLastCompleteOrder(sm.getInqId());
                                            sm.saveLastTruckStatus("02");
                                            sm.saveInqNO("");
                                            sm.saveBusyOrd("N");
                                            updateUI();
                                        }
                                        if (!map.containsValue(txt.trim())) {
                                            sm.setLastCompleteOrder(sm.getInqId());
                                            sm.saveLastTruckStatus("45");
                                            sm.saveInqNO("");
                                            sm.saveBusyOrd("N");
                                            finish();
                                            break;
                                        }
                                    }

                                } else {
                                    //updateUI();
                                }

                            } else {
                                sm.saveLastTruckStatus(status);
                                sm.saveInqNO(Constants.map2.get(position).get("load_inquiry_no"));
                                updateUI();

                                if (status.equalsIgnoreCase("05") || status.equalsIgnoreCase("06") || status.equalsIgnoreCase("07")
                                        || status.equalsIgnoreCase("08")) {
                                    sm.setBothLatLng(Constants.map2.get(position).get("inquiry_source_lat").toString(),
                                            Constants.map2.get(position).get("inquiry_source_lng").toString(),
                                            Constants.map2.get(position).get("inquiry_destionation_lat").toString(),
                                            Constants.map2.get(position).get("inquiry_destionation_lng").toString());
                                    sm.saveLastTruckStatus(statuscode);     //store currrent ongoing last staus
                                    sm.saveBusyOrd("Y");
                                    sm.setLastCompleteOrder("");
                                    sm.setLastCompleteOrder(sm.getInqId());
                                }
                                if (status.equalsIgnoreCase("45")) {
                                    sm.setLastCompleteOrder(sm.getInqId());
                                    sm.saveLastTruckStatus("45");
                                    sm.saveInqNO("");
                                    sm.saveBusyOrd("N");
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        finish();
                        sm.saveInqNO("");
                        sm.saveBusyOrd("N");
                        sm.setBothLatLng("", "", "", ""); //set null value
                    }
                } else {
                }
            }
        }
    };

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
