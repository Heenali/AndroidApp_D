package trukkeruae.trukkertech.com.trukkeruae.firebaseService;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import trukkeruae.trukkertech.com.trukkeruae.R;
import trukkeruae.trukkertech.com.trukkeruae.TrukkerUae.DriverDashboard;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.DriverGoodsOrder;
import trukkeruae.trukkertech.com.trukkeruae.backgroundServices.MyWakefulReceiver;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public AlarmManager alarmManager1;
    Intent myIntent;
    public PendingIntent pendingIntent1;
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        // Check if message contains a notification payload.
        try {
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                Log.d(TAG, "From: " + remoteMessage.getFrom());
                Log.d("status_is", remoteMessage.getData().get("alert"));
                try {
                    if (remoteMessage.getData().get("alert").contains("DRIVERAVAILIBLITY"))
                    {
                        sendNotification(remoteMessage.getData().get("alert"));
                        Log.d("status_is", "DRIVERAVAILIBLITY");
                        //callService();
                    } else {
                        sendNotification(remoteMessage.getData().get("alert"));
                        Log.d("status_is", "MESSEGE");
                    }
                } catch (Exception e) {
                    sendNotification(remoteMessage.getData().get("alert"));
                }

            }
        } catch (Exception e) {
            Log.e("error_is", e.toString());
        }
    }
    private void callService()
    {
        if (!isMyServiceRunning(DriverGoodsOrder.class)) {
            stopService(new Intent(getApplicationContext(), DriverGoodsOrder.class));
        }
        alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        myIntent = new Intent(this, MyWakefulReceiver.class);
        pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 2015, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    SystemClock.elapsedRealtime() + 3000, pendingIntent1);
            alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 3000, 30 * 60, pendingIntent1);
        }else if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    1 * 60000, // 60000 = 1 minute,
                    pendingIntent1);
        } else {
            alarmManager1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 3000, 60 * 1000, pendingIntent1);
        }

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, DriverDashboard.class);
        intent.putExtra("from", "splash");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.logo);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(bitmap)
                .setContentTitle("TrukkerUAE Driver")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
