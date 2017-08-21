package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.AlarmReceiver;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.BackgroundOrderService;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.DriverGoodsOrder;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.MyWakefulReceiver;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.Helper.GPSTracker;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.AlarmReceiver;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.BackgroundOrderService;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.DriverGoodsOrder;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.MyWakefulReceiver;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.DemoJobService;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.DemoJobService_Autoallocation;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.JobForm;

public class DriverDashboard extends AppCompatActivity implements View.OnClickListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener
{

    ImageView order_layout, history_layout, image_upload;
    CircularImageView driver_image_iv;
    RelativeLayout notification_icon, counter_layout, new_order_iv;
    SwitchCompat on_duety_toggle;
    TextView driver_name_tv, msg_counter_tv, terms_condition, version_code;
    String android_id, json, json_resp, jsonPP, jsonVersion, from, photo_path, json_count, gcmRegID, unregJson,
            responsestr, privacy_path, currentVersion, vers_value, driver_ver;
    boolean on_duety = false, isfree = false, visible = false, visible2 = false, visible3 = false;
    static String responseString, image_path;

    private IntentFilter mIntentFilter;
    android.support.v7.app.AlertDialog alert;
    android.support.v7.app.AlertDialog.Builder dialog;
    File finalFile;
    public static File mypath;

    SessionManager sm;
    UserFunctions UF;
    ConnectionDetector cd;
    GPSTracker gps;

    private JobForm form = new JobForm();
    private FirebaseJobDispatcher jobDispatcher;

    private int intervalInMinutes = 1;
    public Intent myIntent1;
    Intent myIntent;
    public PendingIntent pendingIntent1;
    public AlarmManager alarmManager1;
     SharedPreferences prefs;
     String prefName = "Login_newapp";
    String Login_newapp_value="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_dashboard2);

        //checking new application or not
        jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        Login_newapp_value= prefs.getString ("Login_newapp", Login_newapp_value);



        if(Login_newapp_value.equalsIgnoreCase(""))
        {
            SharedPreferences prefs1;
            String prefName1 = "Login_newapp";
            prefs1 = getSharedPreferences(prefName1, MODE_PRIVATE);
            SharedPreferences.Editor editor1 = prefs1.edit();
            editor1.putString("Login_newapp", "new");
            editor1.commit();
            calljobshedular();
        }

/*
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            String packageName = getPackageName();
            if (Build.VERSION.SDK_INT >= 23 && !pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        // initiallize all value & ids

        GoogleApiClient mGoogleApiClient = new GoogleApiClient
            .Builder(this)
            .enableAutoManage(this, 34992, this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

        locationChecker(mGoogleApiClient, DriverDashboard.this);
        init();

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            String pkg=getPackageName();
            PowerManager pm=getSystemService(PowerManager.class);
            if (!pm.isIgnoringBatteryOptimizations(pkg))
            {
                Intent i= new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:"+pkg));
                startActivity(i);

            }

        }

        try
        {
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction("NewNearOrder");
            mIntentFilter.addAction("ACTION_LOGOUT");

            Intent i = getIntent();
            from = i.getStringExtra("from");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (from.equalsIgnoreCase("login")) {
                //initially driver is on Duty
                on_duety_toggle.setChecked(true);
                on_duety = true;
                isfree = true;
                if (cd.isConnectingToInternet()) {
                    new OnDuetyService().execute();
                } else {
                    UF.msg("Internet connection lost or slow");
                }
            } else if (from.equalsIgnoreCase("splash")) {
                isfree = false;
                try {
                    if (sm.getDriverDuety().equalsIgnoreCase("true")) {
                        //true result
                        on_duety_toggle.setChecked(true);
                    } else {
                        //false result
                        on_duety_toggle.setChecked(false);

                    }
                } catch (Exception e) {
                    on_duety_toggle.setChecked(true);
                    isfree = false;
                    if (cd.isConnectingToInternet()) {
                        new OnDuetyService().execute();
                    } else {
                        UF.msg("Internet connection lost or slow");
                    }
                }

            }
        } catch (Exception e) {
            on_duety_toggle.setChecked(true);
            on_duety = true;
            isfree = true;
            if (cd.isConnectingToInternet()) {
                new OnDuetyService().execute();
            } else {
                UF.msg("Internet connection lost or slow");
            }
        }
        try
        {
            //set driver picture
            photo_path = UF.URL_img + sm.getDriverPhoto();
            Log.e("photo_path--->", photo_path);
            try {
                if (photo_path.equalsIgnoreCase("") || sm.getDriverPhoto().equalsIgnoreCase("")) {
                } else
                    {
                    new Thread() {

                        public void run() {
                            try {
                                HttpURLConnection.setFollowRedirects(false);
                                HttpURLConnection con = (HttpURLConnection) new URL(photo_path).openConnection();
                                con.setRequestMethod("HEAD");
                                if ((con.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Picasso.with(getApplicationContext()).load(photo_path).into(driver_image_iv);
                                        }
                                    });

                                } else {
                                    Log.e("FILE_EXISTS", "false");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("FILE_EXISTS", "false");
                            }
                        }
                    }.start();
                }

            } catch (Exception e) {
                Log.e("error", "" + e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (sm.getDriverDuety().equalsIgnoreCase("true")) {
                //true result
                on_duety_toggle.setChecked(true);
            } else {
                //false result
                on_duety_toggle.setChecked(false);

            }
        } catch (Exception e) {
            on_duety_toggle.setChecked(true);
            isfree = false;
            if (cd.isConnectingToInternet()) {
                new OnDuetyService().execute();
            } else {
                UF.msg("Internet connection lost or slow");
            }
        }
        if (cd.isConnectingToInternet())
        {
            new OrderService().execute();
            new UnreadCounterService().execute();
            new GetPdfFile().execute();

            try
            {
                new GetVersion().execute();
            }
            catch (Exception e)
            {

            }
        } else {

        }

        //on duety or not
        on_duety_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    on_duety = true;
                    on_duety_toggle.setText("ON DUTY");
                    if (cd.isConnectingToInternet()) {
                        new OnDuetyService().execute();
                    } else {
                        UF.msg("Internet connection lost or slow");
                    }

                } else {
                    // The toggle is disabled
                    on_duety = false;
                    on_duety_toggle.setText("OFF DUTY");
                    if (cd.isConnectingToInternet()) {
                        new OnDuetyService().execute();
                    } else {
                        UF.msg("Internet connection lost or slow");
                    }
                    sm.saveInqNO("");
                }
            }
        });
        if (on_duety_toggle.isChecked()) {

        } else {
            sm.saveInqNO("");
        }
        try {
            String v;
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            v = info.versionName;
            version_code.setText("Version " + v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void versionMatcher() {

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            String[] value1 = vers_value.split(",");
            driver_ver = value1[0];

            Log.e("version", ": driver_ver" + driver_ver);

            Float onlineVersion1 = Float.valueOf(driver_ver);
            Log.e("version2", ": driver_ver" + onlineVersion1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (Float.valueOf(currentVersion) < Float.valueOf(driver_ver)) {
                //show dialog
                dialogVersion();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogVersion() {
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(DriverDashboard.this);
        dialog.setCancelable(false);
        dialog.setMessage("There is newer version of this application available, Please click OK to upgrade now");
        dialog.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                visible2 = false;
                finish();
                DriverDashboard.this.overridePendingTransition(
                        R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            }
        });
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                visible2 = false;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + DriverDashboard.this.getPackageName())));
                finish();
            }
        });

        dialog.show();
        visible2 = true;
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
        try {
            if (visible2 == true) {
                finish();
            } else {

            }
            if (visible3 == true) {
                finish();
            } else {

            }
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(DriverDashboard.this);
            dialog.setMessage("Do you want to Exit from this Application?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    DriverDashboard.this.overridePendingTransition(
                            R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            visible = false;
        } catch (Exception e) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        registerReceiver(mReceiver, mIntentFilter);
        if (cd.isConnectingToInternet()) {
            new UnreadCounterService().execute();
        } else {
            UF.msg("Internet connection loss or slow");
        }
    }

    private void init() {
        sm = new SessionManager(DriverDashboard.this);
        cd = new ConnectionDetector(DriverDashboard.this);
        UF = new UserFunctions(DriverDashboard.this);
        gps = new GPSTracker(DriverDashboard.this);

        order_layout = (ImageView) findViewById(R.id.order_layout);
        order_layout.setOnClickListener(this);

        history_layout = (ImageView) findViewById(R.id.history_layout);
        history_layout.setOnClickListener(this);

        image_upload = (ImageView) findViewById(R.id.image_upload);
        image_upload.setOnClickListener(this);

        driver_name_tv = (TextView) findViewById(R.id.driver_name_tv);
        driver_name_tv.setText(sm.getFirstName());

        version_code = (TextView) findViewById(R.id.version_code);

        counter_layout = (RelativeLayout) findViewById(R.id.counter_layout);
        counter_layout.setVisibility(View.GONE);

        msg_counter_tv = (TextView) findViewById(R.id.msg_counter_tv);
        terms_condition = (TextView) findViewById(R.id.terms_condition);
        terms_condition.setOnClickListener(this);

        driver_image_iv = (CircularImageView) findViewById(R.id.driver_image_iv);

        on_duety_toggle = (SwitchCompat) findViewById(R.id.on_duety_toggle);

        notification_icon = (RelativeLayout) findViewById(R.id.notification_icon);
        notification_icon.setOnClickListener(this);

        new_order_iv = (RelativeLayout) findViewById(R.id.new_order_iv);
        new_order_iv.setOnClickListener(this);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Constants.device_id = android_id;

        if (cd.isConnectingToInternet()) {
        } else {
            UF.msg("Internet connection lost or slow");

            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(DriverDashboard.this);
            dialog.setCancelable(false);
            dialog.setMessage("Please check your internet connection and try again");
            dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    finish();
                    DriverDashboard.this.overridePendingTransition(
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

      /*  try
      {
            if (gps.canGetLocation())
            {
                gps.getLocation();
            }
            else
             {
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(DriverDashboard.this);
                dialog.setCancelable(false);
                dialog.setMessage("GPS is not enabled.First Enable GPS Location");
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        dialog.dismiss();
                        finish();
                        DriverDashboard.this.overridePendingTransition(
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

        }*/

        //set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
        driver_name_tv.setTypeface(font);
        on_duety_toggle.setTypeface(font);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_layout:
                Intent i = new Intent(getApplicationContext(), OrderListActivity.class);
                startActivity(i);
                DriverDashboard.this.overridePendingTransition(
                        R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                break;

            case R.id.history_layout:
                Intent ii = new Intent(getApplicationContext(), HistoryListActivity.class);
                startActivity(ii);
                DriverDashboard.this.overridePendingTransition(
                        R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                break;

            case R.id.image_upload:
                imageUpload();
                break;

            case R.id.notification_icon:
                Intent iv = new Intent(getApplicationContext(), NotificationMessagesActivity.class);
                startActivity(iv);
                DriverDashboard.this.overridePendingTransition(
                        R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                break;

            case R.id.terms_condition:
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacy_path));
                    startActivity(browserIntent);
                } catch (Exception e) {

                }
                break;
            default:
                break;
        }
    }

    private void imageUpload() {
        final CharSequence[] items = {"Take Photo", "Choose from Sdcard", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(
                DriverDashboard.this);
        builder.setTitle("Update Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);

                } else if (items[item].equals("Choose from Sdcard"))
                {
                    Intent in = new Intent(Intent.ACTION_PICK);
                    in.setType("image/*");
                    startActivityForResult(Intent.createChooser(in, "Complete action using"), 1);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getApplicationContext(), photo);  // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                finalFile = new File(getRealPathFromURI(tempUri));    // CALL THIS METHOD TO GET THE ACTUAL PATH
                image_path = finalFile.getAbsolutePath();
                mypath = new File(image_path);
                new UploadData().execute();
            }
        }

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            image_path = getPath(selectedImageUri);
            mypath = new File(image_path);

            finalFile = new File(image_path);
            new UploadData().execute();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // talent_photo
    private class UploadData extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(DriverDashboard.this);
            pd.setTitle("");
            pd.setMessage("Please wait a moment");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    upload();
                }
            });
            th.start();
            try {
                th.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("deprecation")
        private String upload() {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(UF.URL + "driver/PostFileDriver");

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                entity.addPart("driver_photo", new FileBody(finalFile));
                entity.addPart("driver_id", new StringBody(sm.getUniqueId()));
                httppost.setEntity(entity);

                // server call
                HttpResponse hResponse = httpClient.execute(httppost);
                HttpEntity hEntity = hResponse.getEntity();
                responsestr = EntityUtils.toString(hEntity);
                Log.e("Update_Propic_res->", responsestr);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return responsestr;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                responsestr = responsestr.trim();
                if (responsestr.length() > 0) {
                    responsestr = responsestr.substring(1, responsestr.length() - 1);
                    responsestr = responsestr.replace("\\", "");
                } else {
                    responsestr = "";
                }
                JSONObject jobj = new JSONObject(responsestr);
                String status = jobj.getString("status");
                if (status.equalsIgnoreCase("1")) {

                    String driver_photo = jobj.getString("driver_img");
                    sm.saveDriverPhoto(driver_photo);

                    photo_path = UF.URL_img + sm.getDriverPhoto();
                    //http://192.168.1.2/trukkerUAE//Images/DrvPhoto/D1516000005_6361532538646618110.jpg
                    Log.e("photo_path--->", photo_path);
                    try {
                        if (photo_path.equalsIgnoreCase("") || sm.getDriverPhoto().equalsIgnoreCase("")) {
                            Log.e("photo_path--->", "null");
                        } else {
                            new Thread() {

                                public void run() {
                                    try {
                                        HttpURLConnection.setFollowRedirects(false);

                                        HttpURLConnection con = (HttpURLConnection) new URL(photo_path).openConnection();
                                        con.setRequestMethod("HEAD");
                                        if ((con.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    Picasso.with(getApplicationContext()).load(photo_path).into(driver_image_iv);
                                                }
                                            });

                                        } else
                                            Log.e("FILE_EXISTS", "false");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("FILE_EXISTS", "false");
                                    }
                                }
                            }.start();
                        }
                    } catch (Exception e) {
                    }
                } else {
                    UF.msg("Uploading Failed");
                }
            } catch (Exception e) {
            }
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    //get driver order list service
    private class OrderService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                json_resp = UF.TruckMakeMst("driver/GetAllDriverOrders?drvid=" + sm.getUniqueId() + "&deviceid=" + android_id);
                Log.e("GetAllDriverOrders", json_resp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return json_resp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Constants.map2.clear();
            ArrayList<HashMap<String, String>> inq_list = new ArrayList<HashMap<String, String>>();
            String[] or_Key = Constants.OrderKeys;
            Constants.maptmpforLatlng = new ArrayList<HashMap<String, String>>();
            {
                try {
                    JSONObject jobj2 = new JSONObject(json_resp);
                    String status = jobj2.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONArray jsonArray2 = jobj2.getJSONArray("message");

                        for (int i = 0; i < jsonArray2.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            JSONObject jobJsonObject = jsonArray2.optJSONObject(i);
                            for (int j = 0; j < or_Key.length; j++) {
                                if (jobJsonObject.has(or_Key[j])) {
                                    map.put(or_Key[j], jobJsonObject.getString(or_Key[j]));
                                }
                            }
                            inq_list.add(map);
                            Constants.map2.add(map);
                            Constants.maptmpforLatlng.add(map);
                        }

                    } else if (status.equalsIgnoreCase("3")) {
                        LogoutUser();
                    } else {
                    }
                } catch (JSONException e) {
                    UF.msg("Internet connection loss or slow");
                }
            }
        }
    }

    //call service for driver is on duety or not..
    private class OnDuetyService extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject prms = new JSONObject();
                JSONObject prmsLogin = new JSONObject();
                JSONArray jArray = new JSONArray();
                prmsLogin.put("driver_id", sm.getUniqueId());

                if (on_duety == true) {
                    prmsLogin.put("IsOnDuty", "Y");
                } else {
                    prmsLogin.put("IsOnDuty", "N");
                }
                if (isfree == true) {
                    prmsLogin.put("isfree", "Y");
                } else {
                    prmsLogin.put("isfree", "N");
                }
                //this gonna be no matter for keyword "isfree" value
                prmsLogin.put("modified_by", sm.getFirstName());
                prmsLogin.put("modified_host", sm.getUserId());
                prmsLogin.put("modified_device_id", android_id);
                prmsLogin.put("modified_device_type", "Android-Mobile");
                jArray.put(prmsLogin);
                prms.put("UpdateDriver", jArray);

                Log.e("UpdateDriver-json", jArray + "");
                json = UF.LoginUser("driver/UpdateOnduty", prms);
                Log.e("UpdateDriver-response", json + "");
            } catch (JSONException e) {
            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (json != null) {
                    if (json.equalsIgnoreCase("0")) {
                    } else {
                        JSONObject jobj = new JSONObject(json);
                        String status = jobj.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                        } else if (status.equalsIgnoreCase("0")) {
                            // on_duety_toggle.setEnabled(false);
                            String servermsg = jobj.getString("message");
                            UF.msg(servermsg);
                            on_duety_toggle.setChecked(true);
                        } else if (status.equalsIgnoreCase("3")) {
                            LogoutUser();
                        } else {
                        }
                        if (on_duety_toggle.isChecked()) {
                            sm.saveDriverDuety("true");
                        } else {
                            sm.saveDriverDuety("false");
                        }
                    }

                } else {
                    UF.msg("Internet connection loss or slow");
                }
            } catch (Exception e) {
                UF.msg("Internet connection loss or slow");
            }
        }
    }

    private void LogoutUser() {
        UF.msg("Account Login By Another Device");
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        sm.logoutUser();
        if (!isMyServiceRunning(BackgroundOrderService.class)) {
        } else {
            stopService(new Intent(DriverDashboard.this, BackgroundOrderService.class));
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
                //  Log.e("gcm_reg_response", unregJson + "");
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

    // call notification unread msg counter service
    private class UnreadCounterService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {

                JSONObject prms = new JSONObject();
                JSONArray jArray = new JSONArray();
                prms.put("AppName", "TrukkerUAE");
                prms.put("UniqueId", sm.getUniqueId());
                //Log.e("UnreadCounter-json", prms + "");
                json_count = UF.LoginUser("Login/GetUnreadCount", prms);

                //  Log.e("UnreadCounter-response", json_count + "");
            } catch (JSONException e) {

            }
            return json_count;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (json_count.equalsIgnoreCase("0")) {
                    counter_layout.setVisibility(View.GONE);
                } else {
                    JSONObject jobj = new JSONObject(json_count);
                    String status = jobj.getString("status");
                    String message = jobj.getString("message");
                    if (status.equalsIgnoreCase("1")) {
                        if (message.equalsIgnoreCase("0")) {
                            counter_layout.setVisibility(View.GONE);
                        } else {
                            counter_layout.setVisibility(View.VISIBLE);
                            msg_counter_tv.setText(message);
                        }
                    } else {
                        counter_layout.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    //get pdf file for terms & condition OR privacy policy

    private class GetPdfFile extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                jsonPP = UF.TruckMakeMst("postorder/GetParameterDetails?paramvalue=PP");
                Log.e("GetPrivacyPolicy", jsonPP);
            } catch (Exception e) {

            }
            return jsonPP;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jobj2 = new JSONObject(jsonPP);
                String status = jobj2.getString("status");
                if (status.equalsIgnoreCase("1")) {
                    JSONArray jArray = jobj2.getJSONArray("message");
                    JSONObject value = jArray.getJSONObject(0);
                    privacy_path = value.getString("Value");
                    terms_condition.setVisibility(View.VISIBLE);
                    Log.e("privacy", privacy_path);
                } else {
                    privacy_path = "";
                }
            } catch (JSONException e) {
                privacy_path = "";
            }
        }
    }

    private class GetVersion extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {

                Log.e("id", sm.getUniqueId().toString());
                jsonVersion = UF.TruckMakeMst("postorder/GetParameterDetails?paramvalue=VERSION&driver_id=" + sm.getUniqueId());
                Log.e("GetVERSION", jsonVersion);
            } catch (Exception e) {

            }
            return jsonVersion;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jobj2 = new JSONObject(jsonVersion);
                String status = jobj2.getString("status");
                if (status.equalsIgnoreCase("1")) {
                    JSONArray jArray = jobj2.getJSONArray("message");
                    JSONObject value = jArray.getJSONObject(0);
                    vers_value = value.getString("Value");
                    String cretedate = value.getString("created_date");
                    String onlineUtctime = value.getString("Utcdatetime");


                    Log.e("server Time", cretedate + "");
                    Log.e("online UTC Time", onlineUtctime);

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date1 = formatter.parse(cretedate);
                        Date date2 = formatter.parse(onlineUtctime);

                        printDifference(date1, date2);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    versionMatcher();
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("ACTION_LOGOUT")) {
                int counter = intent.getIntExtra("LOGOUT", 0);
                if (counter == 0) {
                    //show alertmReceiver
                    LogoutUser();
                } else {
                }
            }
        }
    };

    public String getPath(Uri uri) {
        String[] projection = {"_data"};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 0, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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

    public void printDifference(Date startDate, Date endDate) {

        //milliseconds

        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
        long final_v = 0;
        if (elapsedDays > 0) {
            final_v = elapsedDays * 24;
            final_v = final_v * 60;
            Log.e("hors", final_v + "");
        }
        if (elapsedHours > 0) {
            if (final_v == 0) {
                final_v = elapsedDays * 60;
            } else {
                final_v = final_v + elapsedHours * 60;
            }
            Log.e("hors", final_v + "");

        }
        Log.e("Total minutes--------- ", elapsedMinutes + final_v + "");

        if (elapsedMinutes + final_v > 5)
        {


            if (Constants.currentlogin == 0)
            {

            }
                //callGoodsService();
        } else {
            //Toast.makeText(getApplicationContext(), "Tracking alredy Started", Toast.LENGTH_SHORT).show();
        }

    }

    private void callGoodsService()
    {
        try {
           // Toast.makeText(getApplicationContext(), "Tracking Start", Toast.LENGTH_SHORT).show();
            alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            myIntent = new Intent(getApplicationContext(), MyWakefulReceiver.class);
            pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 2015, myIntent, 0);


            if (!isMyServiceRunning(DriverGoodsOrder.class)) {
                alarmManager1.cancel(pendingIntent1);
                Log.e("service", "service stop2");
            }
            if (Build.VERSION.SDK_INT >= 23) {
                Log.e("service", "23");
                alarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 3000, pendingIntent1);
                alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, 30 * 60, pendingIntent1);
            } else if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
                alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime(),
                        1 * 60000, pendingIntent1);// 60000 = 1 minute,
            } else {
                Log.e("service", "<21");
                alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, 60 * 1000, pendingIntent1);
            }
            AlarmManager alarmMgr;
            PendingIntent alarmIntent;
            alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

            // Set the alarm to start at approximately 8:30 a.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 30);
            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);

            AlarmManager alarmMgr22;
            PendingIntent alarmIntent22;
            alarmMgr22 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent22 = new Intent(getApplicationContext(), AlarmReceiver.class);
            alarmIntent22 = PendingIntent.getBroadcast(getApplicationContext(), 1, intent22, PendingIntent.FLAG_ONE_SHOT);

            // Set the alarm to start at approximately 8:30 p.m.
            Calendar calendar22 = Calendar.getInstance();
            calendar22.setTimeInMillis(System.currentTimeMillis());
            calendar22.set(Calendar.HOUR_OF_DAY, 20);
            calendar22.set(Calendar.MINUTE, 30);
            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr22.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar22.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent22);

            ComponentName receiver = new ComponentName(DriverDashboard.this, MyWakefulReceiver.class);
            PackageManager pm = getApplicationContext().getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try
                        {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, 1000);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
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
        //calljobshedular_Authoallocation();
    }


}

