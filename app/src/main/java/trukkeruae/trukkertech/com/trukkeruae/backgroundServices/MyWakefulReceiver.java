package trukkeruae.trukkertech.com.trukkeruae.backgroundServices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;

/**
 * Created by acd on 12/20/2016.
 */
public class MyWakefulReceiver extends WakefulBroadcastReceiver {

    SessionManager sm;

    @Override
    public void onReceive(Context context, Intent intent) {
        sm = new SessionManager(context);
        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        Log.e("service", "WakefulBroadcastReceiver");
        try {
            /*
            if (sm.getInqId().equalsIgnoreCase("")) {
            Intent service = new Intent(context, DriverGoodsOrder.class);
            startWakefulService(context, service);
            }*/

            WakeLocker.acquire(context);
            // do something
            WakeLocker.release();

            Intent service2 = new Intent(context, LocationService.class);
            startWakefulService(context, service2);

        } catch (Exception e) {

        }
    }
}
