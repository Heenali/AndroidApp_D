package trukkeruae.trukkertech.com.trukkeruae.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;
import trukkeruae.trukkertech.com.trukkeruae.TrukkerUae.DriverMapActivity;
import trukkeruae.trukkertech.com.trukkeruae.TrukkerUae.MapActivityforHT;


public class DriverOrderAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> inq_list;
    SessionManager sm;
    UserFunctions UF;
    ConnectionDetector CD;
    String android_id, setStatus, dialog_type = "";

    public DriverOrderAdapter(Context mContext, int resource, ArrayList<HashMap<String, String>> minq_list) {
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
        public TextView tv_orderid, tv_status, tv_start_address, tv_stop_address, tv_shipping_date, tv_shipping_time,
                numberof_labour, numberof_handyman, tvtotal_earning, tvtotal_amount, pament_type_tv, moving_from_lable,
                moving_to_lable, helper_lable, instler_lable, amount_lable, pending_lable, date, time_view;
        public CardView item_order_row;
        public RelativeLayout middel_view;
        public LinearLayout header_inq_view, collect_amount_view, earning_layout, destination_add_view, source_add_view, helper_other_view;
        public AppCompatButton goto_map, order_km_btn;
        public View view_layout;
        public ImageView moving_type_iv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = null;
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_orderlist_row, null);
            ViewHolder holder = new ViewHolder();
            Typeface font = Typeface.createFromAsset(context.getAssets(), "Lato-Medium.ttf");

            holder.item_order_row = (CardView) view.findViewById(R.id.item_order_row);
            holder.tv_orderid = (TextView) view.findViewById(R.id.tv_orderid);
            holder.tv_orderid.setTypeface(font);
            holder.tv_status = (TextView) view.findViewById(R.id.tv_status);
            holder.tv_status.setTypeface(font);
            holder.tv_start_address = (TextView) view.findViewById(R.id.tv_start_address);
            holder.tv_start_address.setTypeface(font);
            holder.tv_stop_address = (TextView) view.findViewById(R.id.tv_stop_address);
            holder.tv_stop_address.setTypeface(font);
            holder.tvtotal_earning = (TextView) view.findViewById(R.id.tvtotal_earning);
            holder.tvtotal_earning.setTypeface(font);
          /*  holder.tvtotal_amount = (TextView) view.findViewById(R.id.tvtotal_amount);
            holder.tvtotal_amount.setTypeface(font);*/
            holder.tv_shipping_date = (TextView) view.findViewById(R.id.tv_shipping_date);
            holder.tv_shipping_date.setTypeface(font);
            holder.tv_shipping_time = (TextView) view.findViewById(R.id.tv_shipping_time);
            holder.tv_shipping_time.setTypeface(font);
            holder.numberof_labour = (TextView) view.findViewById(R.id.numberof_labour);
            holder.numberof_labour.setTypeface(font);
            holder.numberof_handyman = (TextView) view.findViewById(R.id.numberof_handyman);
            holder.numberof_handyman.setTypeface(font);
            holder.pament_type_tv = (TextView) view.findViewById(R.id.pament_type_tv);
            holder.pament_type_tv.setTypeface(font);

            holder.moving_from_lable = (TextView) view.findViewById(R.id.moving_from_lable);
            holder.moving_to_lable = (TextView) view.findViewById(R.id.moving_to_lable);
            holder.helper_lable = (TextView) view.findViewById(R.id.helper_lable);
            holder.instler_lable = (TextView) view.findViewById(R.id.instler_lable);
            holder.pending_lable = (TextView) view.findViewById(R.id.pending_lable);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.time_view = (TextView) view.findViewById(R.id.time_view);

            holder.moving_from_lable.setTypeface(font);
            holder.moving_to_lable.setTypeface(font);
            holder.helper_lable.setTypeface(font);
            holder.instler_lable.setTypeface(font);
            //amount_lable.setTypeface(font);
            holder.pending_lable.setTypeface(font);
            holder.date.setTypeface(font);
            holder.time_view.setTypeface(font);

            holder.header_inq_view = (LinearLayout) view.findViewById(R.id.header_inq_view);
            //  amount_layout = (LinearLayout) view.findViewById(R.id.amount_layout);
            holder.earning_layout = (LinearLayout) view.findViewById(R.id.earning_layout);
            holder.middel_view = (RelativeLayout) view.findViewById(R.id.middel_view);
            holder.destination_add_view = (LinearLayout) view.findViewById(R.id.destination_add_view);
            holder.source_add_view = (LinearLayout) view.findViewById(R.id.source_add_view);
            holder.helper_other_view = (LinearLayout) view.findViewById(R.id.helper_other_view);
            holder.collect_amount_view = (LinearLayout) view.findViewById(R.id.collect_amount_view);
            holder.view_layout = view.findViewById(R.id.view_layout);
            holder.moving_type_iv = (ImageView) view.findViewById(R.id.moving_type_iv);
            holder.goto_map = (AppCompatButton) view.findViewById(R.id.goto_map);
            holder.goto_map.setTypeface(font);
            holder.order_km_btn = (AppCompatButton) view.findViewById(R.id.order_km_btn);
            holder.order_km_btn.setTypeface(font);
            view.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        final HashMap<String, String> hashmap = this.inq_list.get(position);
        try {
            //GET ALL VALUE & SET
            holder.tv_orderid.setText(inq_list.get(position).get("load_inquiry_no"));
            //holder.header_inq_view.bringToFront();
            holder.tv_start_address.setText(inq_list.get(position).get("inquiry_source_addr"));
            holder.tv_stop_address.setText(inq_list.get(position).get("inquiry_destination_addr"));

            String labour = inq_list.get(position).get("NoOfLabour");
            if (inq_list.get(position).get("NoOfLabour").equalsIgnoreCase("") || inq_list.get(position).get("NoOfLabour").equalsIgnoreCase("0") || inq_list.get(position).get("NoOfLabour").equalsIgnoreCase(null)) {
                holder.numberof_labour.setText("-");
            } else {
                holder.numberof_labour.setText(labour);
            }
            String handyman = inq_list.get(position).get("NoOfHandiman");
            if (inq_list.get(position).get("NoOfHandiman").equalsIgnoreCase("") || inq_list.get(position).get("NoOfHandiman").equalsIgnoreCase("0") || inq_list.get(position).get("NoOfHandiman").equalsIgnoreCase(null)) {
                holder.numberof_handyman.setText("-");
            } else {
                holder.numberof_handyman.setText(handyman);
            }

            holder.order_km_btn.setText(inq_list.get(position).get("TotalDistance") + " " + inq_list.get(position).get("TotalDistanceUOM"));
            holder.order_km_btn.setAllCaps(true);

            try {
                if (inq_list.get(position).get("payment_status").equalsIgnoreCase("O")) {
                    holder.pament_type_tv.setText("online payment");
                } else if (inq_list.get(position).get("payment_status").equalsIgnoreCase("C")) {
                    holder.pament_type_tv.setText("cash payment");
                } else {
                    holder.pament_type_tv.setText("cash payment");
                }
            } catch (Exception e) {

            }
            String status = inq_list.get(position).get("status");

            if (inq_list.get(position).get("order_type_flag").equalsIgnoreCase("HT")) {
                if (status.equalsIgnoreCase("02")) {
                    setStatus = "UPCOMING";
                } else if (status.equalsIgnoreCase("05")) {
                    setStatus = "START FOR PICKUP";
                } else if (status.equalsIgnoreCase("07")) {
                    setStatus = "ONGOING";
                } else if (status.equalsIgnoreCase("45")) {
                    setStatus = "COMPLETED";
                }
            } else {
                if (status.equalsIgnoreCase("02")) {
                    setStatus = "UPCOMING";
                } else if (status.equalsIgnoreCase("05")) {
                    setStatus = "START FOR PICKUP";
                } else if (status.equalsIgnoreCase("06")) {
                    setStatus = "LOADING STARTED";
                } else if (status.equalsIgnoreCase("07")) {
                    setStatus = "START FOR DESTINATION";
                } else if (status.equalsIgnoreCase("08")) {
                    setStatus = "UNLOADING START";
                } else if (status.equalsIgnoreCase("45")) {
                    setStatus = "MOVING COMPLETED";
                }
            }

            holder.tv_status.setText(setStatus);

      /*  if (inq_list.get(position).get("totalamount_shipper").equalsIgnoreCase("0") || inq_list.get(position).get("totalamount_shipper").equalsIgnoreCase("") || inq_list.get(position).get("totalamount_shipper").equalsIgnoreCase(null)) {
            holder.tvtotal_amount.setText("-");
        } else {
            String totalAmount = inq_list.get(position).get("totalamount_shipper");
            if (totalAmount.contains(".00")) {
                totalAmount = totalAmount.replace(".00", "");
                holder.tvtotal_amount.setText("AED " + totalAmount);
            } else {
                holder.tvtotal_amount.setText("AED " + totalAmount);
            }
        }*/

            //set date & time formate
            if (inq_list.get(position).get("load_inquiry_shipping_date").equalsIgnoreCase("")) {
                holder.tv_shipping_date.setText("-");
            } else {
                String jsonDate = inq_list.get(position).get("load_inquiry_shipping_date");
                String date[] = jsonDate.split(" ");
                String finalStr = date[0];
                DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
                DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");

                try {
                    Date date2 = inputFormat.parse(finalStr);
                    holder.tv_shipping_date.setText(outputFormat.format(date2));
                } catch (Exception e) {
                }
            }

            if (inq_list.get(position).get("load_inquiry_shipping_time").equalsIgnoreCase("")) {
                holder.tv_shipping_time.setText("-");
            } else {
                final String startTime = inq_list.get(position).get("load_inquiry_shipping_time");

                String time[] = startTime.split(":");//18:20:00
                String time1 = time[0];
                String time2 = time[1];
                holder.tv_shipping_time.setText(time1 + ":" + time2);
            }
            //set background color
            if (inq_list.get(position).get("color_code").equalsIgnoreCase("O")) {
                holder.tv_status.setBackgroundResource(R.drawable.round_corner_fill_orage);
                holder.tv_status.setTextColor(Color.WHITE);
            } else if (inq_list.get(position).get("color_code").equalsIgnoreCase("G")) {
                holder.tv_status.setBackgroundResource(R.drawable.round_corner_fill_green);
                holder.tv_status.setTextColor(Color.WHITE);
            } else if (inq_list.get(position).get("color_code").equalsIgnoreCase("R")) {
                holder.tv_status.setBackgroundResource(R.drawable.round_corner_fill_red);
                holder.tv_status.setTextColor(Color.WHITE);
            } else {
                holder.tv_status.setBackgroundResource(R.drawable.round_corner_fill_white);
                holder.tv_status.setTextColor(Color.BLACK);
            }
            try {
                if (inq_list.get(position).get("order_type_flag").equalsIgnoreCase("HT")) {
                    holder.order_km_btn.setVisibility(View.GONE);
                    holder.pament_type_tv.setVisibility(View.GONE);
                    holder.helper_other_view.setVisibility(View.GONE);

                    holder.pending_lable.setText("Number Of Day");
                    holder.tvtotal_earning.setText(inq_list.get(position).get("Hiretruck_NoofDay"));

                    holder.moving_type_iv.setImageResource(R.drawable.hiring_truck_icon);

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.goto_map.getLayoutParams();
                    params.setMargins(0, 10, 0, 10);
                    holder.goto_map.setLayoutParams(params);
                    holder.middel_view.setVisibility(View.GONE);
                    holder.destination_add_view.setVisibility(View.GONE);

                    holder.source_add_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 5f));

                } else if (inq_list.get(position).get("order_type_flag").equalsIgnoreCase("GL") || inq_list.get(position).get("order_type_flag").equalsIgnoreCase("GN")) {
                    holder.moving_type_iv.setImageResource(R.drawable.moving_goods_icon);
                } else {
                    //set total cost formate
                    if (inq_list.get(position).get("rem_amt_to_receive").equalsIgnoreCase("0") || inq_list.get(position).get("rem_amt_to_receive").equalsIgnoreCase("") || inq_list.get(position).get("rem_amt_to_receive").equalsIgnoreCase(null)) {
                        holder.tvtotal_earning.setText("-");
                    } else {
                        String totalEarn = inq_list.get(position).get("rem_amt_to_receive");
                        if (totalEarn.contains(".00")) {
                            totalEarn = totalEarn.replace(".00", "");
                            holder.tvtotal_earning.setText("AED " + totalEarn);
                        } else {
                            holder.tvtotal_earning.setText("AED " + totalEarn);
                        }
                    }
                    holder.moving_type_iv.setImageResource(R.drawable.moving_home_icon);
                }

                holder.goto_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (inq_list.get(position).get("order_type_flag").equalsIgnoreCase("HT")) {
                            Intent iii = new Intent(context, MapActivityforHT.class);
                            Constants.click_position = position;
                            context.startActivity(iii);
                        } else {
                            Intent ii = new Intent(context, DriverMapActivity.class);
                            Constants.click_position = position;
                            context.startActivity(ii);
                        }

                        ((Activity) context).overridePendingTransition(
                                R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
