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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import trukkeruae.trukkertech.com.trukkeruae.Adapter.HistoryAdapter;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.BackgroundOrderService;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class HistoryListActivity extends AppCompatActivity {

    SessionManager sm;
    UserFunctions UF;
    ConnectionDetector cd;

    ListView order_recycler_view;
    HistoryAdapter adapter;
    String json, android_id, gcmRegID, unregJson;
    TextView noRecord, activtiy_title;
    ImageView back_click;
    View toolbar_header;
    boolean visible = false;

    ProgressDialog pDialog;
    private IntentFilter mIntentFilter;
    android.support.v7.app.AlertDialog alert;
    android.support.v7.app.AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        //initially define all id
        init();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("NewNearOrder");
        mIntentFilter.addAction("ACTION_LOGOUT");

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        if (cd.isConnectingToInternet()) {
            //call service if internet is connected
            new OrderService().execute();
        } else {
            UF.msg("Internet connection lost or slow");
        }
    }

    private void init() {
        sm = new SessionManager(HistoryListActivity.this);
        cd = new ConnectionDetector(HistoryListActivity.this);
        UF = new UserFunctions(HistoryListActivity.this);

        pDialog = new ProgressDialog(HistoryListActivity.this);

        order_recycler_view = (ListView) findViewById(R.id.order_recycler_view);
        noRecord = (TextView) findViewById(R.id.noRecord);
        toolbar_header = findViewById(R.id.toolbar_header);
        activtiy_title = (TextView) toolbar_header.findViewById(R.id.activtiy_title);
        activtiy_title.setText("My History");
        back_click = (ImageView) toolbar_header.findViewById(R.id.back_click);
        back_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");//set fonts
        activtiy_title.setTypeface(font);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HistoryListActivity.this.overridePendingTransition(
                R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        visible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog.isShowing()) {
            pDialog.dismiss();
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

            json = UF.TruckMakeMst("driver/GetCompletedOrdersBydriverID?drvid=" + sm.getUniqueId() + "&deviceid=" + android_id);
            Log.e("CompletedOrders_res", json + "");
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            ArrayList<HashMap<String, String>> inq_list2 = new ArrayList<HashMap<String, String>>();
            String[] or_Key = Constants.HistoryKeys;
            {
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
                                inq_list2.add(map);
                                Constants.history_map.add(map);
                            }
                            if (inq_list2.size() > 0) {
                                noRecord.setVisibility(View.GONE);
                                order_recycler_view.setVisibility(View.VISIBLE);
                                adapter = new HistoryAdapter(HistoryListActivity.this, R.layout.adapter_orderlist_row, inq_list2);
                              /*  RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                order_recycler_view.setLayoutManager(mLayoutManager);
                                order_recycler_view.setItemAnimator(new DefaultItemAnimator());*/
                                adapter.notifyDataSetChanged();
                                order_recycler_view.setAdapter(adapter);
                            } else {
                                noRecord.setVisibility(View.VISIBLE);
                                order_recycler_view.setVisibility(View.GONE);
                            }
                        } else {
                            noRecord.setVisibility(View.VISIBLE);
                            order_recycler_view.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        UF.msg("Internet connection loss or slow");
                    }
                }
            }
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
            stopService(new Intent(HistoryListActivity.this, BackgroundOrderService.class));
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
                //Log.e("gcm_reg_response", unregJson + "");
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
