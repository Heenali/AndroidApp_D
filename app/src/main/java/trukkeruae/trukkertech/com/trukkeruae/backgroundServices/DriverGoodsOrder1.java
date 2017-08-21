package trukkeruae.trukkertech.com.trukkeruae.backgroundServices;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.AsyncTaskExecutor;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;
import trukkeruae.trukkertech.com.trukkeruae.TrukkerUae.NewOrderBringActivity;

/**
 * Created by acd on 1/21/2017.
 */
public class DriverGoodsOrder1 extends Service {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    UserFunctions UF;
    ConnectionDetector cd;
    SessionManager session;
    Handler hl = new Handler();
    Runnable runnable;
    String json, android_id;
    Context c;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        session = new SessionManager(DriverGoodsOrder1.this);
        cd = new ConnectionDetector(DriverGoodsOrder1.this);
        UF = new UserFunctions(DriverGoodsOrder1.this);
        android_id = Settings.Secure.getString(DriverGoodsOrder1.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        c = getApplicationContext();
        try {
            if (cd.isConnectingToInternet()) {
                if (session.getBusyOrd() != null) {
                    if (session.getBusyOrd().equalsIgnoreCase("N")) {

                        GetNewOrder ce = new GetNewOrder();
                        AsyncTaskExecutor.executeConcurrently(ce);
                        Log.e("service", "call service");

                    }
                } else {
                    Log.e("error is ", "session null");
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error is", e + "");
        }
        return START_STICKY;

    }

    //call service
    private class GetNewOrder extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                json = UF.TruckMakeMst("driver/GetdriverOrderNotificationByDriverID?DriverID=" + session.getUniqueId() + "&deviceid=" + android_id);
                Log.e("new_Order_json", json);
                Log.e("service", json);
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
                    Log.e("service", status);

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
//                        JSONObject jobJsonObject2 = jsonArray.optJSONObject(0);
//                        String load_inq_no = jobJsonObject2.getString("load_inquiry_no");

//                        Intent broadcastIntent = new Intent();
//                        broadcastIntent.setAction("NewNearOrder");
//                        broadcastIntent.putExtra("NewOrder", inq_list.size());
//                        broadcastIntent.putExtra("inq_no", load_inq_no);
//                        sendBroadcast(broadcastIntent);

                        //call activity
                        Intent i = new Intent(c, NewOrderBringActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    } else if (status.equalsIgnoreCase("3")) {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("ACTION_LOGOUT");
                        broadcastIntent.putExtra("LOGOUT", 0);
                        sendBroadcast(broadcastIntent);
                    } else {
                        session.saveNewOrder("0");
                       /* Intent i = new Intent(c, NewOrderBringActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);*/
                    }

                } catch (JSONException e) {
                }
            }

            runnable = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (cd.isConnectingToInternet()) {

                        new GetNewOrder().execute();
                    } else {
                        UF.msg(String.valueOf(R.string.no_internet));
                    }
                }
            };
            try {
                if (session.getDriverDuety().equalsIgnoreCase("true")) {

                    if (session.getBusyOrd().equalsIgnoreCase("N")) {
                        hl.postDelayed(runnable, 60000);//60sec
                    } else {
                    }
                } else {
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hl.removeCallbacks(runnable);
        hl.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
