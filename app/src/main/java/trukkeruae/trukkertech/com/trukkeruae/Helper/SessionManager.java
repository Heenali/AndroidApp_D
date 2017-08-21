package trukkeruae.trukkertech.com.trukkeruae.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

import trukkeruae.trukkertech.com.trukkeruae.TrukkerUae.SplashScreen;

/**
 * Created by user2 on 6/13/2016.
 */
public class SessionManager {
    SharedPreferences pref;
    // Sharedpref file name
    private static final String PREF_NAME = "TrukkerApp";
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    //
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String ON_DUETY = "isOnDuety";
    private static final String BUSY_NEWORDER = "IsBusyNewOrder";

    public static final String KEY_NAME = "name";
    public static final String KEY_USERID = "user_id";
    public static final String KEY_INQNO = "load_inquiry_no";
    public static final String DRIVER_PHOTO = "driver_photo";
    public static final String KEY_NEWORDER = "new_order";
    public static final String KEY_DRIVERID = "driver_id";
    public static final String POSITION = "position";
    public static final String KEY_CLIENT_TYPE = "client_type";
    public static final String KEY_UNIQUE_ID = "unique_id";
    public static final String KEY_ROLE_ID = "role_id";
    public static final String FIRST_NAME = "first_name";
    public static final String KEY_TRUCKID = "truck_id";
    public static final String KEY_TRUCKSTATUS = "truck_status";
    public static final String COMPLETE_ORDER = "complete_order";
    public static final String KEY_ONDUETY = "";

    public static final String FROM_LAT = "from_lat";
    public static final String FROM_LNG = "from_lng";
    public static final String TO_LAT = "to_lat";
    public static final String TO_LNG = "to_lng";

    public static final String LOC_LAT = "latitude";
    public static final String LOC_LONG = "longitude";

    public static final String KEY_TRUCKLAT = "";
    public static final String KEY_TRUCKLNG = "";

    ArrayList<HashMap<String, String>> lat_long_array = new ArrayList<>();

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // commit changes
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
//

    /**
     * Create busy in new order session
     */
    public void saveBusyOrd(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(BUSY_NEWORDER, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getBusyOrd() {

        String uid = pref.getString(BUSY_NEWORDER, null);

        return uid;
    }

    //
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }

    public void saveInqNO(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(KEY_INQNO, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getInqId() {

        String uid = pref.getString(KEY_INQNO, null);

        return uid;
    }
    //

    public void saveDriverPhoto(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(DRIVER_PHOTO, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getDriverPhoto() {

        String uid = pref.getString(DRIVER_PHOTO, null);

        return uid;
    }

    //
    public void saveNewOrder(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(KEY_NEWORDER, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getNewOrder() {

        String uid = pref.getString(KEY_NEWORDER, null);

        return uid;
    }

    //
    public void saveTruckId(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(KEY_TRUCKID, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getTruckID() {

        String uid = pref.getString(KEY_TRUCKID, null);

        return uid;
    }

    public void saveDriverId(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(KEY_DRIVERID, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getDriverID() {

        String id = pref.getString(KEY_DRIVERID, null);

        return id;
    }

    public String getTruckLat() {

        String uid = pref.getString(KEY_TRUCKLAT, null);

        return uid;
    }

    public String getTruckLng() {

        String uid = pref.getString(KEY_TRUCKLNG, null);

        return uid;
    }

    public void setUserId(String uid, String first_name, String client_type, String unique_id, String role_id) {

        // Storing name in pref
        editor.putString(KEY_USERID, uid);
        editor.putString(FIRST_NAME, first_name);
        editor.putString(KEY_CLIENT_TYPE, client_type);
        editor.putString(KEY_UNIQUE_ID, unique_id);
        editor.putString(KEY_ROLE_ID, role_id);
        editor.commit();
    }

    public String getUserId() {

        String uid = pref.getString(KEY_USERID, null);

        return uid;
    }

    public String getRoleId() {

        String role_id = pref.getString(KEY_ROLE_ID, null);

        return role_id;
    }

    public String getUniqueId() {

        String unique_id = pref.getString(KEY_UNIQUE_ID, null);

        return unique_id;
    }

    public String getFirstName() {

        String first_name = pref.getString(FIRST_NAME, null);

        return first_name;
    }

    //store source & destination lat-long

    public void setBothLatLng(String from_lat, String from_lng, String to_lat, String to_lng) {
        editor.putString(FROM_LAT, from_lat);
        editor.putString(FROM_LNG, from_lng);
        editor.putString(TO_LAT, to_lat);
        editor.putString(TO_LNG, to_lng);
    }

    public String getFromLat() {
        String from_lat = pref.getString(FROM_LAT, null);
        return from_lat;
    }

    public String getFromLng() {
        String from_lng = pref.getString(FROM_LNG, null);
        return from_lng;
    }

    public String getToLat() {
        String to_lat = pref.getString(TO_LAT, null);
        return to_lat;
    }

    public String getToLng() {
        String to_lng = pref.getString(TO_LNG, null);
        return to_lng;
    }
    //close

    //get current location
    public void setLatLongLoc(String loc_lat, String loca_long) {
        editor.putString(LOC_LAT, loc_lat);
        editor.putString(LOC_LONG, loca_long);
        editor.commit();
    }

    public String getLatLoc() {
        String loc_lat = pref.getString(LOC_LAT, null);
        return loc_lat;
    }

    public String getLongLoc() {
        String loca_long = pref.getString(LOC_LONG, null);
        return loca_long;
    }

    //Truck Status
    public void saveLastTruckStatus(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(KEY_TRUCKSTATUS, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getLastTruckStatus() {

        String status = pref.getString(KEY_TRUCKSTATUS, null);

        return status;
    }

    //
    public void setLastCompleteOrder(String name) {
        // Storing login value as TRUE


        // Storing name in pref

        editor.putString(COMPLETE_ORDER, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getLastCompleteOrder() {

        String status = pref.getString(COMPLETE_ORDER, null);

        return status;
    }
    //

    public void saveDriverDuety(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(KEY_ONDUETY, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getDriverDuety() {

        String duety = pref.getString(KEY_ONDUETY, null);

        return duety;
    }

    //get ongoing position id
    public void saveListPosition(String name) {
        // Storing login value as TRUE


        // Storing name in pref
        editor.putString(POSITION, name);

        // Login Date
        //editor.putString(KEY_DATE, date);

        // commit changes
        editor.commit();
    }

    public String getListPosition() {

        String position = pref.getString(POSITION, null);
        return position;
    }

    // save location in shared
    public void saveLocation(String name) {
        // Storing name in pref
        editor.putString(POSITION, name);

        // commit changes
        editor.commit();
    }

    public String getLocation() {
        String position = pref.getString(POSITION, null);
        return position;
    }
}
