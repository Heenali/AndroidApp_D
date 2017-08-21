package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.BackgroundOrderService;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.CustomClasses.CircleCountDownView;
import trukkeruae.trukkertech.com.trukkeruae.CustomClasses.RippleBackground;
import trukkeruae.trukkertech.com.trukkeruae.CustomClasses.SlideToUnlock;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class NewOrderBringActivity extends AppCompatActivity implements SlideToUnlock.OnUnlockListener, SlideToUnlock.OnLockListener {

    int progress;
    int endTime = 60;
    CountDownTimer countDownTimer;
    MediaPlayer mp = new MediaPlayer();
    protected CircleCountDownView countDownView;
    private SlideToUnlock slideToUnlock;
    String YesNoFlag = "N", loadInqNo, json_save, json, android_id, gcmRegID, unregJson;
    TextView tv_source_add, tv_destination_add, moving_from_lable, moving_to_lable, activtiy_title,
            new_order_time, new_order_helper, new_order_installer, new_order_feight, load_inq_no;
    UserFunctions UF;
    SessionManager sm;
    ConnectionDetector cd;
    RippleBackground rippleBackground;
    Handler handler;
    Vibrator v;
    ImageView back_click;
    View toolbar_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_new_order_bring);

        Constants.scree = true;
        init();

        try {
            if (mp.isPlaying()) {
                mp.stop();
            } else {
                try {
                    mp.reset();
                    AssetFileDescriptor afd;
                    try {
                        afd = getAssets().openFd("krishna_flute_2014.mp3");
                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            rippleBackground.startRippleAnimation();
            /*handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //  foundDevice();
                }
            }, 3000);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            countDownTimer = new CountDownTimer(60000 /*finishTime**/, 1000 /*interval**/) {
                @Override
                public void onTick(long millisUntilFinished) {
                    try {
                        // timer.setText("seconds remaining: " + millisUntilFinished / 1000);
                        countDownView.setProgress(progress, endTime);
                        progress = progress + 1;
                        try {
                            // Vibrate for 500 millisecond
                            v.vibrate(500);
                        } catch (Exception e) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        Constants.scree = false;
                        countDownView.setProgress(progress, endTime);
                        /*YesNoFlag = "N";
                        if (cd.isConnectingToInternet()) {
                            new GetJson_save().execute();
                        } else {
                            UF.msg("Internet connection lost or slow");
                        }*/

                        finish();
                        if (mp.isPlaying()) {
                            mp.stop();
                        }
                        if (v.hasVibrator()) {
                            v.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            countDownTimer.start(); // start timer
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if ((Constants.order_new.get(0).get("inquiry_source_addr") != null)) {
                tv_source_add.setText(Constants.order_new.get(0).get("inquiry_source_addr"));
            }
            if (Constants.order_new.get(0).get("inquiry_destination_addr") != null) {
                tv_destination_add.setText(Constants.order_new.get(0).get("inquiry_destination_addr"));
            }
            if (Constants.order_new.get(0).get("NoOfLabour") != null) {
                new_order_helper.setText(Constants.order_new.get(0).get("NoOfLabour"));
            }
            if (Constants.order_new.get(0).get("Total_cost") != null) {
                new_order_feight.setText(Constants.order_new.get(0).get("Total_cost"));
            }
            if (Constants.order_new.get(0).get("NoOfHandiman") != null) {
                new_order_installer.setText(Constants.order_new.get(0).get("NoOfHandiman"));
            }
            if (Constants.order_new.get(0).get("load_inquiry_no") != null) {
                load_inq_no.setText(Constants.order_new.get(0).get("load_inquiry_no"));
            }
            loadInqNo = Constants.order_new.get(0).get("load_inquiry_no");

            try {
                if (Constants.order_new.get(0).get("shippingdatetime") != null) {
                    String date_time = Constants.order_new.get(0).get("shippingdatetime");
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                        Date date = parseFormat.parse(date_time);
                        date_time = displayFormat.format(date);

                        Date date11 = null;
                        date11 = displayFormat.parse(date_time);

                        DateFormat gmtFormate = new SimpleDateFormat();
                        TimeZone gmtTime = TimeZone.getTimeZone("GMT+04:00");
                        gmtFormate.setTimeZone(gmtTime);
                        Log.e("time_zone", gmtFormate.format(date11));
                        date_time = gmtFormate.format(date11);

                        Date date2;
                        String date33[] = date_time.split(" ");// 27/01/2017 3:30 pm
                        String finalStr = date33[0];
                        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");

                        try {
                            date2 = inputFormat.parse(finalStr);
                            date_time = outputFormat.format(date2) + " " + date33[1];
                        } catch (Exception e) {
                        }

                    } catch (final ParseException e) {
                        e.printStackTrace();
                    }
                    new_order_time.setText(date_time);
                } else {
                    new_order_time.setText("-");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        handler = new Handler();

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        slideToUnlock = (SlideToUnlock) findViewById(R.id.slidetounlock);
        slideToUnlock.setOnUnlockListener(this);
        slideToUnlock.setOnLockListener(this);

        countDownView = (CircleCountDownView) findViewById(R.id.circle_count_down_view);
        progress = 1;

        sm = new SessionManager(getApplicationContext());
        UF = new UserFunctions(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        toolbar_header = findViewById(R.id.toolbar_header);
        activtiy_title = (TextView) toolbar_header.findViewById(R.id.activtiy_title);
        activtiy_title.setText("New Order");
        back_click = (ImageView) toolbar_header.findViewById(R.id.back_click);
        back_click.setVisibility(View.GONE);

        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");

        tv_source_add = (TextView) findViewById(R.id.tv_source_add);
        tv_source_add.setTypeface(font);
        tv_destination_add = (TextView) findViewById(R.id.tv_destination_add);
        tv_destination_add.setTypeface(font);
        moving_from_lable = (TextView) findViewById(R.id.moving_from_lable);
        moving_from_lable.setTypeface(font);
        moving_to_lable = (TextView) findViewById(R.id.moving_to_lable);
        moving_to_lable.setTypeface(font);
        new_order_time = (TextView) findViewById(R.id.new_order_time);
        new_order_time.setTypeface(font);
        new_order_installer = (TextView) findViewById(R.id.new_order_installer);
        new_order_installer.setTypeface(font);
        new_order_helper = (TextView) findViewById(R.id.new_order_helper);
        new_order_helper.setTypeface(font);
        new_order_feight = (TextView) findViewById(R.id.new_order_feight);
        new_order_feight.setTypeface(font);
        load_inq_no = (TextView) findViewById(R.id.load_inq_no);
        load_inq_no.setTypeface(font);

        android_id = Settings.Secure.getString(NewOrderBringActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Constants.scree = false;
            if (mp.isPlaying()) {
                mp.stop();
            }
            if (v.hasVibrator()) {
                v.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnlock() {
        try {
            Constants.scree = false;
            YesNoFlag = "Y";
            if (cd.isConnectingToInternet()) {
                new GetJson_save().execute();
            } else {
                UF.msg("Internet connection lost or slow");
            }
            if (mp.isPlaying()) {
                mp.stop();
            }
            countDownView.setProgress(endTime, endTime);
            countDownTimer.cancel();
            if (v.hasVibrator()) {
                v.cancel();
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLock() {
        try {
            Constants.scree = false;
            YesNoFlag = "N";
            if (cd.isConnectingToInternet()) {
                new GetJson_save().execute();
            } else {
                UF.msg("Internet connection lost or slow");
            }
            if (mp.isPlaying()) {
                mp.stop();
            }
            countDownView.setProgress(endTime, endTime);
            countDownTimer.cancel();
            if (v.hasVibrator()) {
                v.cancel();
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetJson_save extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            JSONObject prms = new JSONObject();
            JSONObject prmsLogin = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                prmsLogin.put("driver_id", sm.getUniqueId());
                prmsLogin.put("load_inquiry_no", loadInqNo);
                prmsLogin.put("Isaccepted", YesNoFlag);
                prmsLogin.put("modified_by", sm.getFirstName());
                prmsLogin.put("modified_host", sm.getUserId());
                prmsLogin.put("modified_device_id", android_id);
                prmsLogin.put("modified_device_type", "Android-Mobile");
                jsonArray.put(prmsLogin);
                prms.put("quote", jsonArray);
                Log.e("jsonArray_req_json", jsonArray + "");
                json_save = UF.LoginUser("postOrder/save_quote_accepted_by_Driver", prms);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return json_save;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (json_save.equalsIgnoreCase("0")) {
                    UF.msg("No Records Found.");
                } else {
                    try {
                        JSONObject jobj = new JSONObject(json_save);
                        String status = jobj.getString("status");
                        String message = jobj.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            if (YesNoFlag.equalsIgnoreCase("Y")) {
                                // mContext.stopService(new Intent(mContext, NewOrderNotifyService.class));
                                //  sm.saveBusyOrd("Y");
                                //  sm.saveInqNO(loadInqNo);

                                try {
                                    if (Constants.scree_open == true) {
                                        finish();
                                    } else {
                                        Intent ii = new Intent(getApplicationContext(), OrderListActivity.class);
                                        startActivity(ii);
                                        NewOrderBringActivity.this.overridePendingTransition(
                                                R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                                        NewOrderBringActivity.this.finish();
                                    }
                                } catch (Exception e) {

                                }
                            } else {
                                //  sm.saveBusyOrd("N");
                            }
                            finish();
                            Constants.scree = false;
                        } else if (status.equalsIgnoreCase("3")) {
                            LogoutUser();
                            finish();
                            Constants.scree = false;
                        } else {
                            finish();
                            Constants.scree = false;
                        }

                        UF.msg(message + "");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        UF.msg("something went wrong");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                UF.msg("something went wrong");
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
            stopService(new Intent(NewOrderBringActivity.this, BackgroundOrderService.class));
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
