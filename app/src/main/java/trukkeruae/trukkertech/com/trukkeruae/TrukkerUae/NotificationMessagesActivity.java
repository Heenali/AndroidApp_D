package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import trukkeruae.trukkertech.com.trukkeruae.Adapter.DriverOrderAdapter;
import trukkeruae.trukkertech.com.trukkeruae.Adapter.NotificationListAdapter;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class NotificationMessagesActivity extends AppCompatActivity {

    SessionManager sm;
    UserFunctions UF;
    ConnectionDetector cd;

    RecyclerView notification_recycl;
    NotificationListAdapter adapter;
    String json, android_id, json_unread;
    TextView noRecord, activtiy_title;
    ImageView back_click;
    View toolbar_header;

    private IntentFilter mIntentFilter;
    android.support.v7.app.AlertDialog alert;
    android.support.v7.app.AlertDialog.Builder dialog;
    boolean visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_messages);

        init();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("NewNearOrder");
        mIntentFilter.addAction("ACTION_LOGOUT");

        if (cd.isConnectingToInternet()) {
            new AllNotificationService().execute();
            new MarkReadService().execute();
        } else {
            UF.msg("Internet connection lost or slow");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        visible = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void init() {
        //set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");

        sm = new SessionManager(NotificationMessagesActivity.this);
        cd = new ConnectionDetector(NotificationMessagesActivity.this);
        UF = new UserFunctions(NotificationMessagesActivity.this);

        toolbar_header = findViewById(R.id.toolbar_header);
        activtiy_title = (TextView) toolbar_header.findViewById(R.id.activtiy_title);
        activtiy_title.setText("My Notifications");
        back_click = (ImageView) toolbar_header.findViewById(R.id.back_click);
        back_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activtiy_title.setTypeface(font);

        notification_recycl = (RecyclerView) findViewById(R.id.notification_recycl);
        noRecord = (TextView) findViewById(R.id.noRecord);
        noRecord.setTypeface(font);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // call notification service
    private class AllNotificationService extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(NotificationMessagesActivity.this);

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
                prms.put("AppName", "TrukkerUAE");
                prms.put("UniqueId", sm.getUniqueId());
                prms.put("LastMessageDateTime", "");
                //Log.e("Notifi-json", prms + "");
                json = UF.LoginUser("Login/GetMessages", prms);

                Log.e("Notifi-response", json + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            ArrayList<HashMap<String, String>> inq_list = new ArrayList<HashMap<String, String>>();
            String[] or_Key = Constants.NotificationKeys;
            try {
                if (json.equalsIgnoreCase("0")) {
                    noRecord.setVisibility(View.VISIBLE);
                    notification_recycl.setVisibility(View.GONE);
                } else {
                    JSONArray jsonArray;
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
                            Constants.notification_list.add(map);
                            if (inq_list.size() > 0) {
                                noRecord.setVisibility(View.GONE);
                                notification_recycl.setVisibility(View.VISIBLE);
                                adapter = new NotificationListAdapter(NotificationMessagesActivity.this, R.layout.adapter_orderlist_row, inq_list);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                notification_recycl.setLayoutManager(mLayoutManager);
                                notification_recycl.setItemAnimator(new DefaultItemAnimator());
                                adapter.notifyDataSetChanged();
                                notification_recycl.setAdapter(adapter);
                            } else {
                                noRecord.setVisibility(View.VISIBLE);
                                notification_recycl.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        noRecord.setVisibility(View.VISIBLE);
                        notification_recycl.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                UF.msg("Internet connection lost or slow");
            }
        }
    }

    //unread mark
    private class MarkReadService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {

                JSONObject prms = new JSONObject();
                prms.put("AppName", "TrukkerUAE");
                prms.put("UniqueId", sm.getUniqueId());
                Log.e("MarkRead-json", prms + "");
                json_unread = UF.LoginUser("login/MarkReadALLMessages", prms);

                Log.e("MarkRead-response", json_unread + "");
            } catch (JSONException e) {

            }
            return json_unread;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (json_unread.equalsIgnoreCase("0")) {
                } else {
                    JSONObject jobj = new JSONObject(json_unread);
                    String status = jobj.getString("status");
                    String message = jobj.getString("message");
                    if (status.equalsIgnoreCase("1")) {

                    } else {
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
