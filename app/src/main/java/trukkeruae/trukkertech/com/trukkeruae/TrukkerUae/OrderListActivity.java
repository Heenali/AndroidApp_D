package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import trukkeruae.trukkertech.com.trukkeruae.Adapter.DriverOrderAdapter;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.GPSTracker;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.JSONParser;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.BackgroundOrderService;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class OrderListActivity extends AppCompatActivity {

    ListView order_recycler_view;
    String json, android_id, gcmRegID, unregJson;
    TextView noRecord, activtiy_title;
    ImageView back_click;
    View toolbar_header;
    boolean visible = false;

    android.support.v7.app.AlertDialog alert;
    android.support.v7.app.AlertDialog.Builder dialog;
    private IntentFilter mIntentFilter;
    DriverOrderAdapter adapter;
    ProgressDialog pDialog;

    SessionManager sm;
    UserFunctions UF;
    ConnectionDetector cd;
    GPSTracker gps;
    JSONParser jparser = new JSONParser();
    ArrayList<HashMap<String, String>> inq_list = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("OrderListForStatus");
        mIntentFilter.addAction("NewNearOrder");
        mIntentFilter.addAction("ACTION_LOGOUT");

        //initially define all id
        init();

    }

    private void init() {
        Constants.scree_open = true;
        //set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");

        sm = new SessionManager(OrderListActivity.this);
        cd = new ConnectionDetector(OrderListActivity.this);
        UF = new UserFunctions(OrderListActivity.this);
        gps = new GPSTracker(OrderListActivity.this);

        pDialog = new ProgressDialog(OrderListActivity.this);

        toolbar_header = findViewById(R.id.toolbar_header);
        activtiy_title = (TextView) toolbar_header.findViewById(R.id.activtiy_title);
        activtiy_title.setText("My Orders");
        back_click = (ImageView) toolbar_header.findViewById(R.id.back_click);
        back_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activtiy_title.setTypeface(font);

        order_recycler_view = (ListView) findViewById(R.id.order_recycler_view);
        noRecord = (TextView) findViewById(R.id.noRecord);
        noRecord.setTypeface(font);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            if (cd.isConnectingToInternet()) {
            } else {
                UF.msg("Internet connection lost or slow");

                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(OrderListActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Please check your internet connection and try again");
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finishAffinity();
                        OrderListActivity.this.overridePendingTransition(
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
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(OrderListActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("GPS is not enabled.First Enable GPS Location");
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finishAffinity();
                        OrderListActivity.this.overridePendingTransition(
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
    public void onBackPressed() {
        super.onBackPressed();
        try {
            OrderListActivity.this.overridePendingTransition(
                    R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            visible = false;
            Constants.scree_open = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(mReceiver, mIntentFilter);
            if (cd.isConnectingToInternet()) {
                new OrderService().execute();
            } else {
                UF.msg("Internet connection lost or slow");
            }
            Constants.scree_open = true;
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
            Constants.scree_open = false;
        } catch (Exception e) {
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
            Constants.scree_open = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get driver order list service
    private class OrderService extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            //driver/GetAllDriverOrders?drvid=&deviceid=
            try {
                json = UF.TruckMakeMst("driver/GetAllDriverOrders?drvid=" + sm.getUniqueId() + "&deviceid=" + android_id);
                Log.e("AllDriverOrders_Res1", json + "");

            } catch (Exception e) {

            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Constants.map2.clear();
            ArrayList<HashMap<String, String>> inq_list = new ArrayList<HashMap<String, String>>();
            String[] or_Key = Constants.OrderKeys;
            Constants.maptmpforLatlng = new ArrayList<HashMap<String, String>>();
            try {
                if (json.equalsIgnoreCase("0")) {
                    noRecord.setVisibility(View.VISIBLE);
                    order_recycler_view.setVisibility(View.GONE);

                } else {

                    JSONArray jsonArray;
                    try {
                        JSONObject jobj = new JSONObject(json);
                        String status = jobj.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            jsonArray = jobj.getJSONArray("message");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject jobJsonObject = jsonArray.optJSONObject(i);
                                for (int j = 0; j < or_Key.length; j++) {
                                    if (jobJsonObject.has(or_Key[j])) {
                                        map.put(or_Key[j], jobJsonObject.getString(or_Key[j]));
                                    }
                                }
                                inq_list.add(map);
                                Constants.map2.add(map);
                                Constants.maptmpforLatlng.add(map);
                            }
                            JSONObject jobJsonObject2 = jsonArray.optJSONObject(0);
                            Constants.last_status_at0 = jobJsonObject2.getString("status");

                            if (inq_list.size() > 0) {
                                noRecord.setVisibility(View.GONE);
                                order_recycler_view.setVisibility(View.VISIBLE);
                                adapter = new DriverOrderAdapter(OrderListActivity.this, R.layout.adapter_orderlist_row, inq_list);
                                /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                order_recycler_view.setLayoutManager(mLayoutManager);
                                order_recycler_view.setItemAnimator(new DefaultItemAnimator());*/
                                adapter.notifyDataSetChanged();
                                order_recycler_view.setAdapter(adapter);
                            } else {
                                noRecord.setVisibility(View.VISIBLE);
                                order_recycler_view.setVisibility(View.GONE);
                            }

                        } else if (status.equalsIgnoreCase("3")) {
                            LogoutUser();

                        } else {
                            noRecord.setVisibility(View.VISIBLE);
                            order_recycler_view.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        UF.msg("Internet connection lost or slow");
                    }

                    if (!isMyServiceRunning(BackgroundOrderService.class)) {
                        startService(new Intent(OrderListActivity.this, BackgroundOrderService.class));
                    } else {
                        stopService(new Intent(OrderListActivity.this, BackgroundOrderService.class));
                        startService(new Intent(OrderListActivity.this, BackgroundOrderService.class));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    private class GetJsonUpdateUI extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                json = UF.TruckMakeMst("driver/GetAllDriverOrders?drvid=" + sm.getUniqueId() + "&deviceid=" + android_id);
                Log.e("AllDriverOrders_Res2", json + "");
            } catch (Exception e) {

            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ArrayList<HashMap<String, String>> inq_list = new ArrayList<HashMap<String, String>>();
            String[] or_Key = Constants.OrderKeys;
            Constants.maptmpforLatlng = new ArrayList<HashMap<String, String>>();

            try {
                JSONArray jsonArray;
                try {
                    JSONObject jobj = new JSONObject(json);
                    String status = jobj.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        jsonArray = jobj.getJSONArray("message");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            JSONObject jobJsonObject = jsonArray.optJSONObject(i);
                            for (int j = 0; j < or_Key.length; j++) {
                                if (jobJsonObject.has(or_Key[j])) {
                                    map.put(or_Key[j], jobJsonObject.getString(or_Key[j]));
                                }
                            }

                            inq_list.add(map);
                            Constants.map2.add(map);
                            Constants.maptmpforLatlng.add(map);
                        }
                        JSONObject jobJsonObject2 = jsonArray.optJSONObject(0);
                        Constants.last_status_at0 = jobJsonObject2.getString("status");

                        if (inq_list.size() > 0) {
                            noRecord.setVisibility(View.GONE);
                            order_recycler_view.setVisibility(View.VISIBLE);
                            adapter = new DriverOrderAdapter(OrderListActivity.this, R.layout.adapter_orderlist_row, inq_list);
                            adapter.notifyDataSetChanged();
                            order_recycler_view.setAdapter(adapter);
                        } else {
                            noRecord.setVisibility(View.VISIBLE);
                            order_recycler_view.setVisibility(View.GONE);
                        }

                    } else if (status.equalsIgnoreCase("3")) {
                        LogoutUser();
                    } else {
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    UF.msg("Internet connection loss or slow");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("OrderListForStatus")) {

                int counter = intent.getIntExtra("OrderStatus", 0);
                if (counter != 0) {
                    if (cd.isConnectingToInternet()) {
                        new GetJsonUpdateUI().execute();
                    } else {
                    }
                } else {
                }
            }
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
            stopService(new Intent(OrderListActivity.this, BackgroundOrderService.class));
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
