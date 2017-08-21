package trukkeruae.trukkertech.com.trukkeruae.CommonClass;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user2 on 6/14/2016.
 */
public class Constants {

    public static String SENDER_ID = "945920188569";
    public static String Reg_mob_ID;

    public static boolean click = false;

    public static int currentlogin = 0;

    public static String distance = "";
    public static String duration = "";
    public static boolean onDuety = false;
    public static String itemLoadInqNo = "";
    public static String truck_id = "";
    public static String device_id = "";
    public static String last_status_at0 = "";
    public static String new_load_inq_req = "";

    public static String f_lat = "";
    public static String f_lon = "";

    public static double fus_lat = 0.0;
    public static double fus_lon = 0.0;

    public static double destLat = 0.0;
    public static double destLong = 0.0;

    public static double fb_lat = 0.00;
    public static double fb_lng = 0.00;

    public static int click_position;

    public static boolean scree = false;
    public static boolean scree_open = false;

    public static ArrayList<HashMap<String, String>> maptmpforLatlng = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> map2 = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> only_order_id = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> order_new = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> history_map = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> notification_list = new ArrayList<HashMap<String, String>>();
    public static HashMap<String, String> map5 = new HashMap<String, String>();

    public static final String[] OrderID = {
            "load_inquiry_no"
    };
    public static final String[] OrderKeys = {
            "order_id",
            "shipper_id",
            "load_inquiry_no",
            "driver_id",
            "truck_id",
            "owner_id",
            "inquiry_source_addr",
            "inquiry_source_city",
            "inquiry_source_state",
            "inquiry_source_lat",
            "inquiry_source_lng",
            "inquiry_destination_addr",
            "inquiry_destination_city",
            "inquiry_destination_state",
            "inquiry_destionation_lat",
            "inquiry_destionation_lng",
            "aprox_kms",
            "aprox_days",
            "load_inquiry_truck_type",
            "load_inquiry_shipping_date",
            "load_inquiry_shipping_time",
            "load_inquiry_delivery_date",
            "load_inquiry_delivery_time",
            "load_inquiry_material_type",
            "receiver_name",
            "receiver_mobile",
            "receiver_email",
            "distance_kms_to_origin",
            "approx_time_to_reach",
            "status",
            "active_flag",
            "NoOfTruck",
            "NoOfDriver",
            "NoOfLabour",
            "NoOfHandiman",
            "Total_cost",
            "shipper_name",
            "shipper_email",
            "shipper_mobile",
            "totaldriver_quot",
            "Isongoing",
            "color_code",
            "shippingdatetime",
            "totalamount_shipper",
            "TotalDistance",
            "TotalDistanceUOM",
            "driver_driverCharge",
            "driver_handimanCharge",
            "driver_labourcharge",
            "driver_baseCharge",
            "shipper_driverCharge",
            "shipper_handimanCharge",
            "shipper_labourcharge",
            "shipper_baseCharge",
            "payment_status",
            "rem_amt_to_receive",
            "source_full_add",
            "destination_full_add",
            "order_type_flag",
            "Hiretruck_NoofDay"
    };
    public static final String[] HistoryKeys = {
            "order_id",
            "shipper_id",
            "load_inquiry_no",
            "driver_id",
            "truck_id",
            "owner_id",
            "inquiry_source_addr",
            "inquiry_source_city",
            "inquiry_source_state",
            "inquiry_source_lat",
            "inquiry_source_lng",
            "inquiry_destination_addr",
            "inquiry_destination_city",
            "inquiry_destination_state",
            "inquiry_destionation_lat",
            "inquiry_destionation_lng",
            "aprox_kms",
            "aprox_days",
            "load_inquiry_truck_type",
            "load_inquiry_shipping_date",
            "load_inquiry_shipping_time",
            "load_inquiry_delivery_date",
            "load_inquiry_delivery_time",
            "load_inquiry_material_type",
            "receiver_name",
            "receiver_mobile",
            "receiver_email",
            "distance_kms_to_origin",
            "approx_time_to_reach",
            "status",
            "active_flag",
            "NoOfTruck",
            "NoOfDriver",
            "NoOfLabour",
            "NoOfHandiman",
            "Total_cost",
            "shipper_name",
            "shipper_email",
            "shipper_mobile",
            "totaldriver_quot",
            "totalamount_shipper",
            "TotalDistance",
            "TotalDistanceUOM",
            "payment_status",
            "order_type_flag",
            "Hiretruck_NoofDay"
    };
    public static final String[] NewOrderKeys = {
            "load_inquiry_no",
            "shipper_id",
            "truck_id",
            "owner_id",
            "inquiry_source_addr",
            "inquiry_source_lat",
            "inquiry_source_lng",
            "inquiry_destination_addr",
            "inquiry_destionation_lat",
            "inquiry_destionation_lng",
            "shippingdatetime",
            "NoOfHandiman",
            "NoOfLabour",
            "Total_cost"
    };
    public static final String[] NotificationKeys = {
            "Date",
            "Subject",
            "Message"
    };

    public static ArrayList<HashMap<String, String>> lat_long_array_ongoing = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> lat_long_array = new ArrayList<>();

    public static final String[] GetLatLngList =
            {
                    "load_inq_id",
                    "driver_id",
                    "truck_id",
                    "latitude",
                    "longitude",
            };
    public static final String[] GetLatLng =
            {
                    "load_inq_id",
                    "latitude",
                    "longitude",
            };
}
