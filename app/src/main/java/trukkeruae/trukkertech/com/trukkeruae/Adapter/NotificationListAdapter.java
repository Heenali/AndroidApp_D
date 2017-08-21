package trukkeruae.trukkertech.com.trukkeruae.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

/**
 * Created by acd on 9/1/2016.
 */
public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    String android_id;
    Context mContext;
    ArrayList<HashMap<String, String>> inq_list;
    SessionManager sm;
    UserFunctions UF;
    ConnectionDetector CD;
    String localTime;

    public NotificationListAdapter(Context mContext, int order_row_item, ArrayList<HashMap<String, String>> inq_list) {
        this.inq_list = inq_list;
        this.mContext = mContext;

        sm = new SessionManager(mContext);
        UF = new UserFunctions(mContext);
        CD = new ConnectionDetector(mContext);
    }

    @Override
    public NotificationListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification_list, parent, false);

        android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return new MyViewHolder(itemView);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView notifi_title, notifi_message, notifi_time;
        public ImageView notifi_msg_delete;

        public MyViewHolder(View v) {
            super(v);
            //set fonts
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "Lato-Medium.ttf");

            notifi_title = (TextView) v.findViewById(R.id.notifi_title);
            notifi_title.setTypeface(font);
            notifi_message = (TextView) v.findViewById(R.id.notifi_message);
            notifi_message.setTypeface(font);
            notifi_time = (TextView) v.findViewById(R.id.notifi_time);
            notifi_time.setTypeface(font);

            // notifi_msg_delete = (ImageView) v.findViewById(R.id.notifi_msg_delete);
        }
    }

    @Override
    public int getItemCount() {
        return inq_list.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.notifi_title.setText(inq_list.get(position).get("Subject"));

        // holder.notifi_time.setText(inq_list.get(position).get("Date"));

        String msg_date = inq_list.get(position).get("Date");
        if (msg_date.equalsIgnoreCase(" ")) {

        } else {
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                SimpleDateFormat parseFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                Date date = parseFormat.parse(msg_date);
                msg_date = displayFormat.format(date);

                Date date11 = null;
                date11 = displayFormat.parse(msg_date);

                DateFormat gmtFormate = new SimpleDateFormat();
                TimeZone gmtTime = TimeZone.getTimeZone("GMT+04:00");
                gmtFormate.setTimeZone(gmtTime);
                Log.e("time_zone", gmtFormate.format(date11));
                msg_date = gmtFormate.format(date11);

            } catch (final ParseException e) {
                e.printStackTrace();
            }

        }
        holder.notifi_time.setText(msg_date);

        String message = inq_list.get(position).get("Message");
        try {
            if (message.contains("u0027")) {
                message = message.replace("u0027", " ' ");
                holder.notifi_message.setText(message);
            } else {
                holder.notifi_message.setText(inq_list.get(position).get("Message"));
            }
        } catch (Exception e) {
            holder.notifi_message.setText(inq_list.get(position).get("Message"));
        }
    }

    public String gettimezone(String datetime) throws ParseException {
        String newFormat = datetime;
        System.out.println(".....Date..." + newFormat);

        newFormat = newFormat.substring(0, 5);
        System.out.println("....time..." + newFormat);
        String hr = newFormat.substring(0, Math.min(newFormat.length(), 2));
        String min = newFormat.substring(newFormat.length() - 2);
        Log.e("HR", hr);
        Log.e("min", min);

        int h = Integer.parseInt(hr.trim());
        int m = Integer.parseInt(min.trim());
        String localTime = "";

        Calendar gmt = Calendar.getInstance();

        gmt.set(Calendar.HOUR, h);
        gmt.set(Calendar.MINUTE, m);
        gmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        Calendar local = Calendar.getInstance();
        local.setTimeZone(TimeZone.getDefault());
        local.setTime(gmt.getTime());


        int hour = local.get(Calendar.HOUR);
        int minutes = local.get(Calendar.MINUTE);
        boolean am = local.get(Calendar.AM_PM) == Calendar.AM;
        String str_hr = "";
        String str_min = "";
        String am_pm = "";

        if (hour < 10)
            str_hr = "0";
        if (minutes < 10)
            str_min = "0";
        if (am)
            am_pm = "PM";
        else
            am_pm = "AM";

        localTime = str_hr + hour + ":" + str_min + minutes + " " + am_pm;

        Log.e("Local time...", localTime);


        String g = localTime.toString();
        return g;
    }
}
