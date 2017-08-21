package trukkeruae.trukkertech.com.trukkeruae.backgroundServices;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by acd on 1/23/2017.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public AlarmManager alarmManager1;
    Intent myIntent;
    public PendingIntent pendingIntent1;
    Context c;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        c = context;

        Log.e("service", "BroadcastReceiver here");

        if (!isMyServiceRunning(DriverGoodsOrder.class))
        {
            context.stopService(new Intent(context, DriverGoodsOrder.class));
        }
        alarmManager1 = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        myIntent = new Intent(context, MyWakefulReceiver.class);
        pendingIntent1 = PendingIntent.getBroadcast(context, 2015, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 23)
        {
            alarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    SystemClock.elapsedRealtime() + 3000, pendingIntent1);
            alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 3000, 30 * 60, pendingIntent1);
        } else if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22)
        {
            alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    1 * 60000, pendingIntent1);// 60000 = 1 minute,
        } else {
            alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 3000, 60 * 1000, pendingIntent1);
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
