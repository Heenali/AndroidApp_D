package trukkeruae.trukkertech.com.trukkeruae.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;
import trukkeruae.trukkertech.com.trukkeruae.TrukkerUae.TrackOrderMap;

/**
 * Created by user2 on 6/14/2016.
 */
public class HistoryAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> inq_list;
    SessionManager sm;
    UserFunctions UF;
    ConnectionDetector CD;
    String android_id;
    String setStatus;

    public HistoryAdapter(Context mContext, int resource, ArrayList<HashMap<String, String>> minq_list) {
        super(mContext, resource);
        this.inq_list = minq_list;
        this.context = mContext;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sm = new SessionManager(mContext);
        UF = new UserFunctions(mContext);
        CD = new ConnectionDetector(mContext);
        android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return inq_list.size();
    }

    public static class ViewHolder {
        public TextView order_id_tv, status_tv, address1_tv, address2_tv, total_earning_tv, total_amount_tv, date_tv, time_tv, pament_type_tv,
                moving_from_lable, moving_to_lable, total_amount_lable, total_earning_lable, status_lable;
        public CardView track_order;
        public AppCompatButton km_btn;
        public RelativeLayout middel_layout;
        public LinearLayout dest_add_view, layout_footer_1, source_add_view, layout_2, layout_3, layout_1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = null;
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_history_row, null);
            ViewHolder holder = new ViewHolder();
            Typeface font = Typeface.createFromAsset(context.getAssets(), "Lato-Medium.ttf");

            holder.order_id_tv = (TextView) view.findViewById(R.id.order_id_tv);
            holder.status_tv = (TextView) view.findViewById(R.id.status_tv);
            holder.address1_tv = (TextView) view.findViewById(R.id.address1_tv);
            holder.address2_tv = (TextView) view.findViewById(R.id.address2_tv);
            holder.total_earning_tv = (TextView) view.findViewById(R.id.total_earning_tv);
            holder.total_amount_tv = (TextView) view.findViewById(R.id.total_amount_tv);
            holder.date_tv = (TextView) view.findViewById(R.id.date_tv);
            holder.time_tv = (TextView) view.findViewById(R.id.time_tv);
            holder.pament_type_tv = (TextView) view.findViewById(R.id.pament_type_tv2);

            holder.moving_from_lable = (TextView) view.findViewById(R.id.moving_from_lable);
            holder.moving_to_lable = (TextView) view.findViewById(R.id.moving_to_lable);
            holder.total_amount_lable = (TextView) view.findViewById(R.id.total_amount_lable);
            holder.total_earning_lable = (TextView) view.findViewById(R.id.total_earning_lable);
            holder.status_lable = (TextView) view.findViewById(R.id.status_lable);

            holder.middel_layout = (RelativeLayout) view.findViewById(R.id.middel_layout);
            holder.layout_footer_1 = (LinearLayout) view.findViewById(R.id.layout_footer_1);
            holder.dest_add_view = (LinearLayout) view.findViewById(R.id.dest_add_view);
            holder.source_add_view = (LinearLayout) view.findViewById(R.id.source_add_view);
            holder.layout_2 = (LinearLayout) view.findViewById(R.id.layout_2);
            holder.layout_3 = (LinearLayout) view.findViewById(R.id.layout_3);
            holder.layout_1 = (LinearLayout) view.findViewById(R.id.layout_1);

            holder.order_id_tv.setTypeface(font);
            holder.status_tv.setTypeface(font);
            holder.address1_tv.setTypeface(font);
            holder.address2_tv.setTypeface(font);
            holder.total_earning_tv.setTypeface(font);
            holder.total_amount_tv.setTypeface(font);
            holder.date_tv.setTypeface(font);
            holder.time_tv.setTypeface(font);
            holder.pament_type_tv.setTypeface(font);
            holder.moving_from_lable.setTypeface(font);
            holder.moving_to_lable.setTypeface(font);
            holder.total_amount_lable.setTypeface(font);
            holder.total_earning_lable.setTypeface(font);
            holder.status_lable.setTypeface(font);

            holder.track_order = (CardView) view.findViewById(R.id.track_order);
            holder.km_btn = (AppCompatButton) view.findViewById(R.id.km_btn);

            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        final HashMap<String, String> hashmap = this.inq_list.get(position);
        try {
            holder.order_id_tv.setText(hashmap.get("load_inquiry_no"));
            holder.address1_tv.setText(hashmap.get("inquiry_source_addr"));
            holder.address2_tv.setText(hashmap.get("inquiry_destination_addr"));
            holder.km_btn.setText(hashmap.get("TotalDistance") + " " + hashmap.get("TotalDistanceUOM"));
            holder.km_btn.setAllCaps(true);
            if (hashmap.get("payment_status").equalsIgnoreCase("O")) {
                holder.pament_type_tv.setText("online payment");
            } else if (hashmap.get("payment_status").equalsIgnoreCase("C")) {
                holder.pament_type_tv.setText("cash payment");
            } else {
                holder.pament_type_tv.setText("cash payment");
            }
            try {
                if (hashmap.get("totaldriver_quot").equalsIgnoreCase("0") || hashmap.get("totaldriver_quot").equalsIgnoreCase("") || hashmap.get("totaldriver_quot").equalsIgnoreCase(null)) {
                    holder.total_earning_tv.setText("-");
                } else {
                    String totalEarn = hashmap.get("totaldriver_quot");
                    if (totalEarn.contains(".00")) {
                        totalEarn = totalEarn.replace(".00", "");
                        holder.total_earning_tv.setText("AED " + totalEarn);
                    } else {
                        holder.total_earning_tv.setText("AED " + totalEarn);
                    }
                }
                if (hashmap.get("totalamount_shipper").equalsIgnoreCase("0") || hashmap.get("totalamount_shipper").equalsIgnoreCase("") || hashmap.get("totalamount_shipper").equalsIgnoreCase(null)) {
                    holder.total_amount_tv.setText("-");
                } else {
                    String totalAmount = hashmap.get("totalamount_shipper");
                    if (totalAmount.contains(".00")) {
                        totalAmount = totalAmount.replace(".00", "");
                        holder.total_amount_tv.setText("AED " + totalAmount);
                    } else {
                        holder.total_amount_tv.setText("AED " + totalAmount);
                    }
                }
                //set date & time formate
                String jsonDate = hashmap.get("load_inquiry_shipping_date");
                String date[] = jsonDate.split(" ");
                String finalStr = date[0];
                DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
                DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                try {
                    Date date2 = inputFormat.parse(finalStr);
                    holder.date_tv.setText(outputFormat.format(date2) + " - ");

                } catch (Exception e) {
                }

                String startTime = hashmap.get("load_inquiry_shipping_time");
                String time[] = startTime.split(":");//18:20:00
                String time1 = time[0];
                String time2 = time[1];
                holder.time_tv.setText(time1 + ":" + time2);

            } catch (Exception e) {

            }
            if (hashmap.get("order_type_flag").equalsIgnoreCase("HT")) {
                String status = hashmap.get("status");
                if (status.equalsIgnoreCase("02")) {
                    setStatus = "Upcoming";
                } else if (status.equalsIgnoreCase("05")) {
                    setStatus = "Start For Pickup";
                } else if (status.equalsIgnoreCase("07")) {
                    setStatus = "Ongoing";
                } else if (status.equalsIgnoreCase("45")) {
                    setStatus = "Completed";
                }
                holder.status_tv.setText(setStatus);
                holder.dest_add_view.setVisibility(View.GONE);
                holder.middel_layout.setVisibility(View.GONE);
                holder.pament_type_tv.setVisibility(View.GONE);
                holder.km_btn.setVisibility(View.GONE);
                holder.layout_2.setVisibility(View.GONE);
                holder.source_add_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5f));
                holder.total_amount_lable.setText("Number Of Days");
                holder.total_amount_tv.setText(hashmap.get("Hiretruck_NoofDay"));
                //set weigh sum of layout_footer_1 layout
                holder.layout_3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));
                holder.layout_1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));

            } else {
                String status = hashmap.get("status");

                if (status.equalsIgnoreCase("02")) {
                    setStatus = "Upcoming";
                } else if (status.equalsIgnoreCase("05")) {
                    setStatus = "Start For Pickup";
                } else if (status.equalsIgnoreCase("06")) {
                    setStatus = "Loading Started";
                } else if (status.equalsIgnoreCase("07")) {
                    setStatus = "On Going";
                } else if (status.equalsIgnoreCase("08")) {
                    setStatus = "On The Way";
                } else if (status.equalsIgnoreCase("45")) {
                    setStatus = "Moving completed";
                }
                holder.status_tv.setText(setStatus);
                holder.track_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TrackOrderMap.class);
                        intent.putExtra("truck_id", hashmap.get("truck_id"));
                        intent.putExtra("load_inquiry_no", hashmap.get("load_inquiry_no"));
                        intent.putExtra("source_lat", hashmap.get("inquiry_source_lat"));
                        intent.putExtra("source_lng", hashmap.get("inquiry_source_lng"));
                        intent.putExtra("dest_lat", hashmap.get("inquiry_destionation_lat"));
                        intent.putExtra("dest_lng", hashmap.get("inquiry_destionation_lng"));
                        context.startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
