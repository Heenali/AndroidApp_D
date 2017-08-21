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
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
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
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import trukkeruae.trukkertech.com.trukkeruae.ModelClass.MixUtils;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.BackgroundOrderService;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.MyWakefulReceiver;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.GPSTracker;
import trukkeruae.trukkertech.com.trukkeruae.Helper.DataBaseHelper;
import trukkeruae.trukkertech.com.trukkeruae.Helper.DirectionsJSONParser;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.DemoJobService;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.JobForm;

public class DriverMapActivity extends FragmentActivity implements LocationListener, View.OnClickListener {

    UserFunctions UF;
    ConnectionDetector cd;
    SessionManager sm;
    GPSTracker gps;
    DataBaseHelper db;
    MixUtils MU;

    GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    Marker my_Marker;
    Polyline line_actual, line_followed = null;

    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    double fused_lat, fused_lng;
    String f_latitude, f_longitude, gcmRegID, unregJson, var;
    int distance;
    double source_lat = 0.0, source_lng = 0.0;
    LatLng destLatLng;

    AppCompatButton start_pickup_btn, loading_start_btn, ongoing_btn, on_way_btn, unloading_complete_btn;
    LinearLayout dialog_view, mobile_num_layout, emailid_layout, shipper_name_layout, total_fright_layout,
            footer_layout, view_addr1, view_addr2;
    RelativeLayout order_detail_layout, view_img_1;
    ImageView icon_arrow;
    TextView customer_name_tv, customer_emailid_tv, load_inq_no, tvsource, tvdesc, cutomer_mobno_tv, total_fright_tv, total_km_tv, order_detail_btn,
            lable_from, lable_to, lable_fright, lable_shipper_name, lable_emailid, lable_mobile_num;
    FloatingActionButton fab;

    ArrayList<String> latLngListForDeletion;
    private IntentFilter mIntentFilter;

    int result_dis = 0, position;
    boolean visible = false;
    String json_save, statuscode, android_id, source_add, destination_add, shipper_id, truck_id, owner_id, receiver_mobile, driver_id;

    android.support.v7.app.AlertDialog alert;
    android.support.v7.app.AlertDialog.Builder dialog;
    ProgressDialog pDialog;

    private ArrayList<LatLng> points; //added
    Polyline line; //added
    double[] lat_long_point;
    ArrayList<LatLng> lat_long_list = new ArrayList<>();
    HashMap<String, LatLng> latlong = new HashMap<String, LatLng>();

    private JobForm form = new JobForm();
    private FirebaseJobDispatcher jobDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_driver_map);

        jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));



        try {
            //get value of intent
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
                ongoing_btn.setEnabled(true);
                on_way_btn.setEnabled(true);
                unloading_complete_btn.setEnabled(true);
            } else {

                start_pickup_btn.setEnabled(false);
                loading_start_btn.setEnabled(false);
                ongoing_btn.setEnabled(false);
                on_way_btn.setEnabled(false);
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
                DriverMapActivity.this.overridePendingTransition(
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

    private void init()
    {



        //set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");

        UF = new UserFunctions(DriverMapActivity.this);
        cd = new ConnectionDetector(DriverMapActivity.this);
        sm = new SessionManager(DriverMapActivity.this);
        gps = new GPSTracker(DriverMapActivity.this);
        db = new DataBaseHelper(getApplicationContext());
        pDialog = new ProgressDialog(DriverMapActivity.this);

        //find id & set style & font
        order_detail_layout = (RelativeLayout) findViewById(R.id.order_detail_layout);
        order_detail_layout.setOnClickListener(this);
        view_img_1 = (RelativeLayout) findViewById(R.id.view_img_1);

        start_pickup_btn = (AppCompatButton) findViewById(R.id.start_pickup_btn);
        start_pickup_btn.setTypeface(font);
        start_pickup_btn.setOnClickListener(this);
        start_pickup_btn.setVisibility(View.GONE);

        loading_start_btn = (AppCompatButton) findViewById(R.id.loading_start_btn);
        loading_start_btn.setTypeface(font);
        loading_start_btn.setOnClickListener(this);
        loading_start_btn.setVisibility(View.GONE);

        ongoing_btn = (AppCompatButton) findViewById(R.id.ongoing_btn);
        ongoing_btn.setTypeface(font);
        ongoing_btn.setOnClickListener(this);
        ongoing_btn.setVisibility(View.GONE);

        on_way_btn = (AppCompatButton) findViewById(R.id.on_way_btn);
        on_way_btn.setTypeface(font);
        on_way_btn.setOnClickListener(this);
        on_way_btn.setVisibility(View.GONE);

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
        view_addr1 = (LinearLayout) findViewById(R.id.view_addr1);
        view_addr2 = (LinearLayout) findViewById(R.id.view_addr2);

        icon_arrow = (ImageView) findViewById(R.id.icon_arrow);
        fab = (FloatingActionButton) findViewById(R.id.fab_navigation);
        fab.bringToFront();
        fab.setOnClickListener(this);

        //dialog data
        customer_name_tv = (TextView) findViewById(R.id.customer_name_tv);
        customer_name_tv.setTypeface(font);
        total_fright_tv = (TextView) findViewById(R.id.total_fright_tv);
        total_fright_tv.setTypeface(font);
        customer_emailid_tv = (TextView) findViewById(R.id.customer_emailid_tv);
        customer_emailid_tv.setTypeface(font);
        load_inq_no = (TextView) findViewById(R.id.load_inq_no);
        load_inq_no.setTypeface(font);
        total_km_tv = (TextView) findViewById(R.id.total_km_tv);
        total_km_tv.setTypeface(font);
        tvsource = (TextView) findViewById(R.id.tvsource);
        tvsource.setTypeface(font);
        tvdesc = (TextView) findViewById(R.id.tvdesc);
        tvdesc.setTypeface(font);
        cutomer_mobno_tv = (TextView) findViewById(R.id.cutomer_mobno_tv);
        cutomer_mobno_tv.setTypeface(font);
        order_detail_btn = (TextView) findViewById(R.id.order_detail_btn);
        order_detail_btn.setTypeface(font);

        lable_from = (TextView) findViewById(R.id.lable_from);
        lable_from.setTypeface(font);
        lable_to = (TextView) findViewById(R.id.lable_to);
        lable_to.setTypeface(font);
        lable_fright = (TextView) findViewById(R.id.lable_fright);
        lable_fright.setTypeface(font);
        lable_shipper_name = (TextView) findViewById(R.id.lable_shipper_name);
        lable_shipper_name.setTypeface(font);
        lable_emailid = (TextView) findViewById(R.id.lable_emailid);
        lable_emailid.setTypeface(font);
        lable_mobile_num = (TextView) findViewById(R.id.lable_mobile_num);
        lable_mobile_num.setTypeface(font);

        //get devicce id
        android_id = Settings.Secure.getString(DriverMapActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        latLngListForDeletion = new ArrayList<String>();

        points = new ArrayList<LatLng>(); //added

        try {
            if (cd.isConnectingToInternet()) {
            } else {
                UF.msg("Internet connection lost or slow");

                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(DriverMapActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Please check your internet connection and try again");
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finishAffinity();
                        DriverMapActivity.this.overridePendingTransition(
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
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(DriverMapActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("GPS is not enabled.First Enable GPS Location");
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finishAffinity();
                        DriverMapActivity.this.overridePendingTransition(
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
                    if (sm.getLastTruckStatus().equalsIgnoreCase("05")) {
                        loadNavigation(source_lat, source_lng);
                    } else {
                        loadNavigation(Constants.destLat, Constants.destLong);
                    }
                } catch (Exception e) {
                    loadNavigation(Constants.destLat, Constants.destLong);
                }
                break;

            case R.id.order_detail_layout:
                setAnimationDialog();
                break;

            case R.id.start_pickup_btn:
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(DriverMapActivity.this);
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
                android.support.v7.app.AlertDialog.Builder dialog2 = new android.support.v7.app.AlertDialog.Builder(DriverMapActivity.this);
                dialog2.setMessage("Are you sure for Loading Start?");
                dialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {

                        statuscode = "06";
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

            case R.id.ongoing_btn:
                android.support.v7.app.AlertDialog.Builder dialog3 = new android.support.v7.app.AlertDialog.Builder(DriverMapActivity.this);
                dialog3.setMessage("Are you sure for Starting To Destination?");
                dialog3.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog3, int which) {

                        statuscode = "07";
                        callActionService();
                    }
                });
                dialog3.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;

            case R.id.on_way_btn:
                android.support.v7.app.AlertDialog.Builder dialog4 = new android.support.v7.app.AlertDialog.Builder(DriverMapActivity.this);
                dialog4.setMessage("Are you sure for Unloading Started?");
                dialog4.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog4, int which) {

                        statuscode = "08";
                        callActionService();
                    }
                });
                dialog4.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;

            case R.id.unloading_complete_btn:
                android.support.v7.app.AlertDialog.Builder dialog5 = new android.support.v7.app.AlertDialog.Builder(DriverMapActivity.this);
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

    private void loadNavigation(double dest_lat, double dest_lng) {
        String format = "google.navigation:q=" + dest_lat + "," + dest_lng;
        Uri uri = Uri.parse(format);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

                        if (status.equalsIgnoreCase("1"))
                        {
                            String msg = jobj.getString("message");
                            sm.saveInqNO(Constants.map2.get(position).get("load_inquiry_no"));
                            sm.saveListPosition(String.valueOf(position));

                            if (statuscode.equalsIgnoreCase("05"))
                            {
                                sm.setBothLatLng(Constants.map2.get(position).get("inquiry_source_lat").toString(),
                                        Constants.map2.get(position).get("inquiry_source_lng").toString(),
                                        Constants.map2.get(position).get("inquiry_destionation_lat").toString(),
                                        Constants.map2.get(position).get("inquiry_destionation_lng").toString());
                                sm.saveLastTruckStatus(statuscode);     //store currrent ongoing last staus
                                sm.saveBusyOrd("Y");
                                sm.setLastCompleteOrder("");
                                sm.setLastCompleteOrder(sm.getInqId());
                                calljobshedular();
                            }
                            if (statuscode.equalsIgnoreCase("06")) {
                                sm.saveLastTruckStatus(statuscode);
                                sm.setLastCompleteOrder(sm.getInqId());
                            }
                            if (statuscode.equalsIgnoreCase("07")) {
                                sm.saveLastTruckStatus(statuscode);
                                sm.setLastCompleteOrder(sm.getInqId());
                            }
                            if (statuscode.equalsIgnoreCase("08")) {
                                sm.saveLastTruckStatus(statuscode);
                                sm.setLastCompleteOrder(sm.getInqId());
                            }

                            if (statuscode.equalsIgnoreCase("45"))
                            {
                                sm.setLastCompleteOrder(sm.getInqId());
                                Intent i = new Intent(DriverMapActivity.this, OrderListActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();

                                sm.saveLastTruckStatus(statuscode);
                                sm.saveInqNO("");
                                sm.saveBusyOrd("N");
                                sm.setBothLatLng("", "", "", ""); //set null value

                                calljobshedular();
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
                stopService(new Intent(DriverMapActivity.this, BackgroundOrderService.class));
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
                destination_add = Constants.map2.get(position).get("inquiry_destination_addr");
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
                if (Constants.maptmpforLatlng.get(position).get("inquiry_destionation_lat").equalsIgnoreCase("")) {
                    destlatitude = 0.0;
                } else {
                    destlatitude = Double.valueOf(Constants.maptmpforLatlng.get(position).get("inquiry_destionation_lat"));
                    Constants.destLat = destlatitude;
                }

                if (Constants.maptmpforLatlng.get(position).get("inquiry_destionation_lng").equalsIgnoreCase("")) {
                    destlongitude = 0.0;
                } else {
                    destlongitude = Double.valueOf(Constants.maptmpforLatlng.get(position).get("inquiry_destionation_lng"));
                    Constants.destLong = destlongitude;
                }
                destLatLng = new LatLng(destlatitude, destlongitude);
            } else {
                destLatLng = new LatLng(Constants.destLat, Constants.destLong);
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
            total_km_tv.setText(Constants.map2.get(position).get("TotalDistance") + " " + Constants.map2.get(position).get("TotalDistanceUOM"));

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

            String desti_add = Constants.map2.get(position).get("destination_full_add");
            if (desti_add.equalsIgnoreCase("") || desti_add.equalsIgnoreCase(null)) {
                tvdesc.setText("-");
            } else {
                if (desti_add.contains("^")) {
                    desti_add = desti_add.replace("^", ",");
                    tvdesc.setText(desti_add);
                } else {
                    tvdesc.setText(Constants.map2.get(position).get("destination_full_add"));
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
            drawRoot(mMap, source_lat, source_lng, Constants.destLat, Constants.destLong, "");
            if (sm.getLastTruckStatus().equalsIgnoreCase("02")) {
                start_pickup_btn.setEnabled(true);
                loading_start_btn.setEnabled(false);
                ongoing_btn.setEnabled(false);
                on_way_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(false);

                start_pickup_btn.setVisibility(View.VISIBLE);
            } else if (sm.getLastTruckStatus().equalsIgnoreCase("05")) {
                start_pickup_btn.setEnabled(false);
                loading_start_btn.setEnabled(true);
                ongoing_btn.setEnabled(false);
                on_way_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(false);

                loading_start_btn.setVisibility(View.VISIBLE);
                start_pickup_btn.setVisibility(View.GONE);
            } else if (sm.getLastTruckStatus().equalsIgnoreCase("06")) {
                start_pickup_btn.setEnabled(false);
                loading_start_btn.setEnabled(false);
                ongoing_btn.setEnabled(true);
                on_way_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(false);

                ongoing_btn.setVisibility(View.VISIBLE);
                loading_start_btn.setVisibility(View.GONE);
            } else if (sm.getLastTruckStatus().equalsIgnoreCase("07")) {
                start_pickup_btn.setEnabled(false);
                loading_start_btn.setEnabled(false);
                ongoing_btn.setEnabled(false);
                on_way_btn.setEnabled(true);
                unloading_complete_btn.setEnabled(false);

                ongoing_btn.setVisibility(View.GONE);
                on_way_btn.setVisibility(View.VISIBLE);
            } else if (sm.getLastTruckStatus().equalsIgnoreCase("08")) {
                start_pickup_btn.setEnabled(false);
                loading_start_btn.setEnabled(false);
                ongoing_btn.setEnabled(false);
                on_way_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(true);

                unloading_complete_btn.setVisibility(View.VISIBLE);
                on_way_btn.setVisibility(View.GONE);
            } else {
                start_pickup_btn.setEnabled(true);
                loading_start_btn.setEnabled(false);
                ongoing_btn.setEnabled(false);
                on_way_btn.setEnabled(false);
                unloading_complete_btn.setEnabled(false);

                start_pickup_btn.setVisibility(View.VISIBLE);
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
            LatLng dest_latlng = new LatLng(Constants.destLat, Constants.destLong);
            LatLng source_latlng = new LatLng(source_lat, source_lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(f_latlng));

            String url1;
            try {
                if (sm.getLastTruckStatus() != null) {
                    if (sm.getLastTruckStatus().equalsIgnoreCase("05")) {
                        url1 = getDirectionsUrl(f_latlng, source_latlng);
                    } else {
                        url1 = getDirectionsUrl(f_latlng, dest_latlng);
                    }
                    if (cd.isConnectingToInternet()) {
                        DownloadTask1 downloadTask1 = new DownloadTask1();
                        downloadTask1.execute(url1);
                    }

                } else {

                }
            } catch (Exception e) {

            }

            //set marker
            if (my_Marker != null) {
                my_Marker.remove();
            }
            my_Marker = mMap.addMarker(new MarkerOptions().position(new LatLng(fused_lat, fused_lng)).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck)));

        } catch (Exception e) {

        }
    }

    private void drawRoot(GoogleMap mMap, double source_lat, double source_lng, double dest_lat, double dest_lng, String title) {
        try
        {
            final LatLng lat_lng1 = new LatLng(source_lat, source_lng);
            final LatLng lat_lng2 = new LatLng(dest_lat, dest_lng);
            MarkerOptions marker1 = null, marker2 = null;

            marker1 = new MarkerOptions().position(lat_lng1).title("Source Location").icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).snippet("Source Location");
            marker2 = new MarkerOptions().position(lat_lng2).title("Destination Location").icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).snippet("Destination Location");
            mMap.addMarker(marker1);
            mMap.addMarker(marker2);

            if (cd.isConnectingToInternet())
            {
                String url = getDirectionsUrl(lat_lng1, lat_lng2);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //check for google palyservice
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(DriverMapActivity.this);
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
            mGoogleApiClient = new GoogleApiClient.Builder(DriverMapActivity.this).addApi(LocationServices.API)
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

    // all common method
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                //  Log.d("Background Task", e.toString());
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
                            lineOptions.width(7);
                            lineOptions.color(Color.parseColor("#FF0000")).geodesic(true);
                        }
                        line_actual = mMap.addPolyline(lineOptions);    // Drawing polyline in the Google Map for the i-th route
                    }
                }

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
                ParserTask1 parserTask = new ParserTask1();

                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
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
                PolylineOptions lineOptions2 = null;
                MarkerOptions markerOptions = new MarkerOptions();

                // Traversing through all the routes
                if (result != null) {
                    if (result.size() > 0) {

                        for (int i = 0; i < result.size(); i++) {
                            points = new ArrayList<LatLng>();
                            lineOptions2 = new PolylineOptions();

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
                            lineOptions2.addAll(points);
                            lineOptions2.width(5);
                            lineOptions2.color(Color.parseColor("#59c1e3")).geodesic(true);
                            //59c1e3
                        }
                        if (line_followed != null) {
                            line_followed.remove();
                        }
                        line_followed = mMap.addPolyline(lineOptions2);    // Drawing polyline in the Google Map for the i-th route
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
        return url;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
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

    public  void calljobshedular()
    {


        final Job.Builder builder = jobDispatcher.newJobBuilder()
            .setTag("LocationGet")
            .setRecurring(true)
            .setLifetime(true ? Lifetime.FOREVER : Lifetime.UNTIL_NEXT_BOOT)
            .setService(DemoJobService.class)
            .setTrigger(Trigger.executionWindow(30,60))
            .setReplaceCurrent(true)
            .setRetryStrategy(jobDispatcher.newRetryStrategy(1,30,3600));


        if (false)
        {
            builder.addConstraint(Constraint.DEVICE_CHARGING);
        }
        if (true) {
            builder.addConstraint(Constraint.ON_ANY_NETWORK);
        }
        if (false) {
            builder.addConstraint(Constraint.ON_UNMETERED_NETWORK);
        }

        Log.i("FJD.JobForm", "scheduling new job");
        jobDispatcher.mustSchedule(builder.build());

        Toast.makeText(getApplicationContext(),"Your Tracking successfully activated !!",Toast.LENGTH_LONG).show();
    }
}
