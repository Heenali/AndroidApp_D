package trukkeruae.trukkertech.com.trukkeruae.backgroundServices;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 8/8/2016.
 */
public class NewOrderNotifyService extends Service {
    private UserFunctions UF;
    ConnectionDetector cd;
    SessionManager session;
    Handler hl = new Handler();
    Runnable runnable;
    String json,android_id;


    private void initForDataRefresh() {
        session = new SessionManager(NewOrderNotifyService.this);
        cd = new ConnectionDetector(NewOrderNotifyService.this);
        UF = new UserFunctions(NewOrderNotifyService.this);
        android_id = Settings.Secure.getString(NewOrderNotifyService.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initForDataRefresh();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Looper.prepare();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (cd.isConnectingToInternet()) {
            new GetNewOrder().execute();
        } else {
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hl.removeCallbacks(runnable);
        hl.removeCallbacksAndMessages(null);
    }

    //call service
    private class GetNewOrder extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                if (session.getDriverDuety().equalsIgnoreCase("true")) {
                    if (session.getBusyOrd().equalsIgnoreCase("N")) {
                        json = UF.TruckMakeMst("driver/GetdriverOrderNotificationByDriverID?DriverID=" + session.getUniqueId() + "&deviceid=" + android_id);
                        Log.e("new_Order_json", json);
                    }
                }
            } catch (Exception e) {

            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String[] or_Key = Constants.NewOrderKeys;
            ArrayList<HashMap<String, String>> inq_list = new ArrayList<HashMap<String, String>>();
            Constants.order_new.clear();
            {
                try {
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
                            Constants.order_new.add(map);
                        }
                        JSONObject jobJsonObject2 = jsonArray.optJSONObject(0);
                        String load_inq_no= jobJsonObject2.getString("load_inquiry_no");

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("NewNearOrder");
                        broadcastIntent.putExtra("NewOrder", inq_list.size());
                        broadcastIntent.putExtra("inq_no",load_inq_no);
                        sendBroadcast(broadcastIntent);

                    } else if (status.equalsIgnoreCase("3")) {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("ACTION_LOGOUT");
                        broadcastIntent.putExtra("LOGOUT", 0);
                        sendBroadcast(broadcastIntent);
                    } else {
                        session.saveNewOrder("0");
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("NewNearOrder");
                        broadcastIntent.putExtra("NewOrder", 0);
                        sendBroadcast(broadcastIntent);
                    }

                } catch (JSONException e) {
                }
            }

            runnable = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    initForDataRefresh();
                    if (cd.isConnectingToInternet()) {

                        new GetNewOrder().execute();

                        onDestroy();
                    } else {
                        UF.msg(String.valueOf(R.string.no_internet));
                    }
                }
            };
            try {
                if (session.getDriverDuety().equalsIgnoreCase("true")) {

                    if (session.getBusyOrd().equalsIgnoreCase("N")) {
                        hl.postDelayed(runnable, 30000);//30sec
                    } else {
                    }
                } else {
                }
            } catch (Exception e) {

            }
        }
    }
}
