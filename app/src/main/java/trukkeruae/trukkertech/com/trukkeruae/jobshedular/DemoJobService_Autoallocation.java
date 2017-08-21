// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package trukkeruae.trukkertech.com.trukkeruae.jobshedular;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

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
import trukkeruae.trukkertech.com.trukkeruae.TrukkerUae.NewOrderBringActivity;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.DriverGoodsOrder;

public class DemoJobService_Autoallocation extends JobService
{
    UserFunctions UF;
    ConnectionDetector cd;
    SessionManager session;
    String json, android_id;
    Context c;

    @Override
    public boolean onStartJob(JobParameters job)
    {
        Log.i("FJD.DemoJobService", "onStartJob called Authoallocation");

        Bundle extras = job.getExtras();
        assert extras != null;
        int result = extras.getInt("return");
        Log.e("service", "DriverGoodsOrder here");
        session = new SessionManager(DemoJobService_Autoallocation.this);
        cd = new ConnectionDetector(DemoJobService_Autoallocation.this);
        UF = new UserFunctions(DemoJobService_Autoallocation.this);
        android_id = Settings.Secure.getString(DemoJobService_Autoallocation.this.getContentResolver(),Settings.Secure.ANDROID_ID);
        c = getApplicationContext();
        try
        {
            if (cd.isConnectingToInternet())
            {
                if (session.getBusyOrd() != null)
                {
                    if (session.getBusyOrd().equalsIgnoreCase("N"))
                    {
                        if (Constants.scree == false)
                        {
                            GetNewOrder ce = new GetNewOrder();
                            AsyncTaskExecutor.executeConcurrently(ce);
                            Log.e("service", "call service");
                        }
                        else
                        {
                            Log.e("service", "service already running");
                        }
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


        CentralContainer.getStore(getApplicationContext()).recordResult(job, result);

        return false; // No more work to do
    }
    private class GetNewOrder extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                json = UF.TruckMakeMst("driver/GetDriver_order_RequestDetails?DriverID=" + session.getUniqueId() + "&deviceid=" + android_id);
                //json = UF.TruckMakeMst("driver/GetdriverOrderNotificationByDriverID?DriverID=" + session.getUniqueId() + "&deviceid=" + android_id);
                Log.e("new_Order_json", json);

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
                try
                {
                    JSONArray jsonArray;
                    JSONObject jobj = new JSONObject(json);
                    String status = jobj.getString("status");
                    Log.e("Autoalocation .........", status);
                    if (status.equalsIgnoreCase("1"))
                    {

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

                    } else if (status.equalsIgnoreCase("3"))
                    {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("ACTION_LOGOUT");
                        broadcastIntent.putExtra("LOGOUT", 0);
                        sendBroadcast(broadcastIntent);
                    }
                    else
                    {
                        session.saveNewOrder("0");
                    }

                } catch (JSONException e) {
                }
            }
        }
    }
    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // No more work to do
    }
}
