package trukkeruae.trukkertech.com.trukkeruae.backgroundServices;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class BackgroundOrderService extends Service {

    private SessionManager sm;
    private UserFunctions UF;
    ConnectionDetector cd;

    Handler hl = new Handler();
    Runnable runnable;
    String json, android_id;
    ArrayList<HashMap<String, String>> inq_list;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
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
        // Let it continue running until it is stopped.

        if (cd.isConnectingToInternet()) {
            new OrderListService().execute();
        } else {
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hl.removeCallbacks(runnable);
        hl.removeCallbacksAndMessages(null);
    }

    private void initForDataRefresh() {
        sm = new SessionManager(BackgroundOrderService.this);
        cd = new ConnectionDetector(BackgroundOrderService.this);
        UF = new UserFunctions(BackgroundOrderService.this);
        //dbIN = new DBInsertion(SyncUpdatedDataService.this);
        inq_list = new ArrayList<HashMap<String, String>>();
        android_id = Settings.Secure.getString(BackgroundOrderService.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    //get driver order list service
    private class OrderListService extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            json = UF.TruckMakeMst("driver/GetAllDriverOrders?drvid=" + sm.getUniqueId() + "&deviceid=" + android_id);
            Log.e("GetAllDriverOrders",json);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String[] or_Key = Constants.OrderKeys;
            String[] order_id = Constants.OrderID;
            Constants.maptmpforLatlng = new ArrayList<HashMap<String, String>>();
            Constants.map2.clear();
            Constants.only_order_id.clear();
            if (json.equalsIgnoreCase("0")) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("OrderListForStatus");
                broadcastIntent.putExtra("OrderStatus", 0);
                sendBroadcast(broadcastIntent);
            } else {
                JSONArray jsonArray;
                try {
                    JSONObject jobj = new JSONObject(json);
                    String status = jobj.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        jsonArray = jobj.getJSONArray("message");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            HashMap<String, String> map5 = new HashMap<String, String>();
                            JSONObject jobJsonObject = jsonArray.optJSONObject(i);
                            for (int j = 0; j < or_Key.length; j++) {
                                if (jobJsonObject.has(or_Key[j])) {
                                    map.put(or_Key[j], jobJsonObject.getString(or_Key[j]));
                                    map5.put("load_inquiry_no", jobJsonObject.getString("load_inquiry_no"));
                                }
                            }

                            inq_list.add(map);
                            Constants.map2.add(map);
                            Constants.only_order_id.add(map5);
                            Constants.maptmpforLatlng.add(map);
                        }

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("OrderListForStatus");
                        broadcastIntent.putExtra("OrderStatus", inq_list.size());
                        broadcastIntent.putExtra("Refresh", 0);
                        sendBroadcast(broadcastIntent);

                    } else if (status.equalsIgnoreCase("3")) {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("ACTION_LOGOUT");
                        broadcastIntent.putExtra("LOGOUT", 0);
                        sendBroadcast(broadcastIntent);
                    } else {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("OrderListForStatus");
                        broadcastIntent.putExtra("OrderStatus", 0);
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

                        new OrderListService().execute();
                    } else {
                        UF.msg(String.valueOf(R.string.no_internet));
                    }
                }
            };
            hl.postDelayed(runnable, 60000);// 1min
        }
    }
}
