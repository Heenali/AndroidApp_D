package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;


import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.AlarmReceiver;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.DriverGoodsOrder;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.MyWakefulReceiver;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.DemoJobService;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.DemoJobService_Autoallocation;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.JobForm;
import trukkeruae.trukkertech.com.trukkeruae.jobshedular.JobFormActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout btn_login;
    AppCompatButton login_btn;
    EditText input_mobile_no, input_password;
    TextView txt_forgot_password;
    String mobilenumber, password, imei_num, deviceName, gcmRegID, device_detail;
    String jsonLogin, jsonGcm, android_id, unique_id, app_os, device_version, app_version, v;
    SessionManager sm;
    ConnectionDetector cd;
    UserFunctions UF;
    ProgressDialog pDialog;
    public AlarmManager alarmManager1;
    Intent myIntent;
    public PendingIntent pendingIntent1;

    private JobForm form = new JobForm();
    private FirebaseJobDispatcher jobDispatcher;

     SharedPreferences prefs;
    String prefName = "Login_newapp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //for job sheduling activity

        jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));
        final ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setVariable(trukkeruae.trukkertech.com.trukkeruae.BR.form, form);

        //identify all ids
        init();
        getCountry();
    }

    private void getCountry() {
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            String networkIso = tm.getNetworkCountryIso();   //
            String countryIso = tm.getSimCountryIso();

            Log.e("networkIso", networkIso);//
            Log.e("countryIso", countryIso);//

        } catch (Exception e) {
            e.printStackTrace();
        }
        //get device all detail
        try {
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei_num = mngr.getDeviceId();
            deviceName = android.os.Build.MODEL;

            StringBuilder builder = new StringBuilder();
            builder.append(Build.VERSION.RELEASE);
            app_os = builder.toString();

            Field[] fields = Build.VERSION_CODES.class.getFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                int fieldValue = -1;

                try {
                    fieldValue = field.getInt(new Object());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            try {
                device_version = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            try {
                PackageManager manager = getPackageManager();
                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                app_version = info.versionName;
                int version = info.versionCode;

                v = String.valueOf(version);
            } catch (Exception e) {

            }

            Log.d("value", "OS: " + app_os + " Device: " + deviceName + " GMS_VERSION: " + device_version + " App_Version: " + v);
            device_detail = "OS: " + app_os + " Device: " + deviceName + " GMS_VERSION: " + device_version + " App_Version: " + v;

        } catch (Exception e) {
            device_detail = "";
        }
    }

    private void init()
    {
        alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        myIntent = new Intent(getApplicationContext(), MyWakefulReceiver.class);
        pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 2015, myIntent, 0);

        //set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
        pDialog = new ProgressDialog(LoginActivity.this);

        UF = new UserFunctions(LoginActivity.this);
        sm = new SessionManager(LoginActivity.this);
        cd = new ConnectionDetector(LoginActivity.this);

        btn_login = (RelativeLayout) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        login_btn = (AppCompatButton) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        login_btn.setTypeface(font);

        input_mobile_no = (EditText) findViewById(R.id.input_mobile_no);
        input_mobile_no.setTypeface(font);
        input_password = (EditText) findViewById(R.id.input_password);
        input_password.setTypeface(font);
        txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);
        txt_forgot_password.setOnClickListener(this);
        txt_forgot_password.setTypeface(font);
        try {
            if (!isMyServiceRunning(DriverGoodsOrder.class)) {
                alarmManager1.cancel(pendingIntent1);
                Log.e("service", "service stop1");
            }
        } catch (Exception e) {

        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        try
        {
            input_mobile_no.setText("");
            input_password.setText("");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (!validate()) {
                    onLoginFailed();
                    return;
                }
                btn_login.setEnabled(false);
                // TODO: Implement your own authentication logic here.
                onLoginSuccess();
                break;

            case R.id.login_btn:
                if (!validate()) {
                    onLoginFailed();
                    return;
                }
                btn_login.setEnabled(false);
                // TODO: Implement your own authentication logic here.
                onLoginSuccess();
                break;

            case R.id.txt_forgot_password:
                Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(i);
                break;
        }
    }

    public void onLoginSuccess() {
        try {
            btn_login.setEnabled(true);
            mobilenumber = input_mobile_no.getText().toString();
            password = input_password.getText().toString();

            if (cd.isConnectingToInternet()) {
                new LoginService().execute();
            } else {
                UF.msg("Check your internet connection.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoginFailed() {
        UF.msg("Login Fail");
        btn_login.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String mobile_number = input_mobile_no.getText().toString();
        String password = input_password.getText().toString();

        if (mobile_number.isEmpty() || mobile_number.length() < 7) {
            input_mobile_no.setError("Please enter valid mobile number");
            valid = false;
        } else {
            input_mobile_no.setError(null);
        }

        if (password.isEmpty()) {
            input_password.setError("Please Enter Password");
            valid = false;
        } else {
            input_password.setError(null);
        }

        return valid;
    }

    //call service for login
    private class LoginService extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject prms = new JSONObject();
                JSONObject prmsLogin = new JSONObject();
                prmsLogin.put("user_id", mobilenumber);
                prmsLogin.put("password", password);
                prmsLogin.put("load_inquiry_no", "");
                prmsLogin.put("device_id", android_id);
                prmsLogin.put("device_type", "Android-Mobile");
                prms.put("Login", prmsLogin);

                Log.e("Login-json", prms + "");
                jsonLogin = UF.LoginUser("login/CheckIn", prms);
                Log.e("Login-response", jsonLogin + "");
            } catch (Exception e) {
            }
            return jsonLogin;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (jsonLogin != null) {
                    if (jsonLogin.equalsIgnoreCase("0")) {
                        UF.msg("Invalid Username or password");
                    } else {

                        JSONObject jobj = new JSONObject(jsonLogin);
                        String status = jobj.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            JSONArray jArray = jobj.getJSONArray("message");
                            JSONObject jobj1 = jArray.getJSONObject(0);
                            String user_id = jobj1.getString("user_id");
                            String role_id = jobj1.getString("role_id");
                            String fullName = jobj1.getString("FullName");
                            String client_type = jobj1.getString("client_type");
                            String truck_id = jobj1.getString("truck_id");
                            String load_inquiry_no = jobj1.getString("load_inquiry_no");
                            String driver_photo = jobj1.getString("driver_photo");
                            unique_id = jobj1.getString("unique_id");

                            sm.createLoginSession(user_id);
                            sm.setUserId(user_id, fullName, client_type, unique_id, role_id);
                            sm.saveTruckId(truck_id);
                            sm.saveInqNO(load_inquiry_no);
                            sm.setLastCompleteOrder(load_inquiry_no);
                            sm.setBothLatLng("0.0", "0.0", "0.0", "0.0");
                            sm.saveDriverPhoto(driver_photo);
                            if (load_inquiry_no.equalsIgnoreCase("")) {
                                sm.saveBusyOrd("N");
                            } else {
                                sm.saveBusyOrd("Y");
                            }

                            Intent i = new Intent(LoginActivity.this, DriverDashboard.class);
                            i.putExtra("from", "login");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                            LoginActivity.this.overridePendingTransition(
                                    R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                            GetGCM();

                        } else {
                            String servermsg = jobj.getString("message");
                            UF.msg(servermsg);
                        }
                    }
                } else {
                    UF.msg("Internet connection lost or slow");
                }
            } catch (Exception e) {
                UF.msg("Internet connection lost or slow");
            }
            pDialog.dismiss();
        }
    }

    private class RegGCMService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {

                JSONObject prmsLogin = new JSONObject();
                prmsLogin.put("AppName", "TrukkerUAE");
                prmsLogin.put("UniqueId", unique_id);
                prmsLogin.put("DeviceId", gcmRegID);
                prmsLogin.put("TokenId", "AIzaSyCqNE20B4vSjbfbg-qlNsxnr0k5gbrCbrY");
                prmsLogin.put("DeviceInfo", device_detail);
                prmsLogin.put("OS", "Android");
                prmsLogin.put("IMEINo", imei_num);
                Log.e("gcm_reg_json", prmsLogin + "");
                jsonGcm = UF.LoginUser("login/RegisterDevice", prmsLogin);

                //  Log.e("gcm_reg_response", jsonGcm + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonGcm;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (jsonGcm != null) {
                    if (jsonGcm.equalsIgnoreCase("0")) {

                    } else {
                        JSONObject jobj = new JSONObject(jsonGcm);
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

    private void GetGCM()
    {
        gcmRegID = FirebaseInstanceId.getInstance().getToken();
        new RegGCMService().execute();
       // callGoodsService();
         calljobshedular();
    }

    private void callGoodsService()
    {
        try {
            Constants.currentlogin =1;

            if (!isMyServiceRunning(DriverGoodsOrder.class))
            {
                alarmManager1.cancel(pendingIntent1);
                Log.e("service", "service stop2");
            }
            if (Build.VERSION.SDK_INT >= 23)
            {
                Log.e("service", "23");
                alarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 3000, pendingIntent1);
                alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, 30 * 60, pendingIntent1);
            }
            else if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22)
            {
                alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(), 1 * 60000, pendingIntent1);// 60000 = 1 minute,

            }
            else
            {
                Log.e("service", "<21");
                alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(), 1 * 60000, pendingIntent1);// 60000 = 1 minute,
                //alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, 60 * 1000, pendingIntent1);
            }

            startAt_everyhour();
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

            ComponentName receiver = new ComponentName(LoginActivity.this, MyWakefulReceiver.class);
            PackageManager pm = getApplicationContext().getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
        catch (Exception e)
        {
            e.printStackTrace();
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
    public void startAt_everyhour()
    {
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        int interval = 3600000;

        Calendar c = Calendar.getInstance();
        int minutes = c.get(Calendar.MINUTE);
        int hours = (c.get(Calendar.HOUR_OF_DAY));
        Log.e("Hor of Day",hours+"   "+hours);
        Log.e("min of Day",hours+"   "+minutes);
        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hours);
        calendar.set(Calendar.MINUTE, minutes);
        /* Repeating on every 20 minutes interval */
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),interval,pendingIntent);
    }
    public  void calljobshedular()
    {
        Log.e("form.tag.get()",form.tag.get()+"");
        Log.e("form.recurring.get()",form.recurring.get()+"");
        Log.e("form.persistent.get()",form.persistent.get()+"");

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Login_newapp", "new");
        editor.commit();

        final Job.Builder builder = jobDispatcher.newJobBuilder()
            .setTag("LocationGet")

            .setRecurring(true)
            .setLifetime(true ? Lifetime.FOREVER : Lifetime.UNTIL_NEXT_BOOT)
            .setService(DemoJobService.class)
            .setTrigger(Trigger.executionWindow(form.getWinStartSeconds(), form.getWinEndSeconds()))
            .setReplaceCurrent(form.replaceCurrent.get())
            .setRetryStrategy(jobDispatcher.newRetryStrategy(
                form.retryStrategy.get(),
                form.getInitialBackoffSeconds(),
                form.getMaximumBackoffSeconds()));


        if (form.constrainDeviceCharging.get())
        {
            builder.addConstraint(Constraint.DEVICE_CHARGING);
        }
        if (form.constrainOnAnyNetwork.get()) {
            builder.addConstraint(Constraint.ON_ANY_NETWORK);
        }
        if (form.constrainOnUnmeteredNetwork.get()) {
            builder.addConstraint(Constraint.ON_UNMETERED_NETWORK);
        }

        Log.i("FJD.JobForm", "scheduling new job");
        jobDispatcher.mustSchedule(builder.build());

        Toast.makeText(getApplicationContext(),"Your Tracking successfully activated !!",Toast.LENGTH_LONG).show();
        //calljobshedular_Authoallocation();
    }
    public  void calljobshedular_Authoallocation()
    {
        Log.e("form.tag.get()",form.tag.get()+"");
        Log.e("form.recurring.get()",form.recurring.get()+"");
        Log.e("form.persistent.get()",form.persistent.get()+"");

        final Job.Builder builder = jobDispatcher.newJobBuilder()
            .setTag("Autoallocation")

            .setRecurring(true)
            .setLifetime(true ? Lifetime.FOREVER : Lifetime.UNTIL_NEXT_BOOT)
            .setService(DemoJobService_Autoallocation.class)
            .setTrigger(Trigger.executionWindow(form.getWinStartSeconds(), form.getWinEndSeconds()))
            .setReplaceCurrent(form.replaceCurrent.get())
            .setRetryStrategy(jobDispatcher.newRetryStrategy(
                form.retryStrategy.get(),
                form.getInitialBackoffSeconds(),
                form.getMaximumBackoffSeconds()));


        if (form.constrainDeviceCharging.get())
        {
            builder.addConstraint(Constraint.DEVICE_CHARGING);
        }
        if (form.constrainOnAnyNetwork.get()) {
            builder.addConstraint(Constraint.ON_ANY_NETWORK);
        }
        if (form.constrainOnUnmeteredNetwork.get())
        {
            builder.addConstraint(Constraint.ON_UNMETERED_NETWORK);
        }

        Log.i("FJD.JobForm", "scheduling new job Authoallocation");
        jobDispatcher.mustSchedule(builder.build());

        Toast.makeText(getApplicationContext(),"Your Tracking successfully activated !!",Toast.LENGTH_LONG).show();

    }
}
