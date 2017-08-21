package trukkeruae.trukkertech.com.trukkeruae.Helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.Constants;

/**
 * Created by user2 on 6/13/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "trukkeruae.db";
    public static String Lock = "dblock";
    private static final int DATABASE_VERSION = 1;

    //Table Name
    private static final String TA_LATLNG_LIST = "Latlng_list";
    private static final String TA_LATLNG = "Latlng";
    private static final String TA_DRIVER_ID = "driver_id";
    private static final String TA_INQ_ID = "load_inq_id";
    private static final String TA_TRUCK_ID = "truck_id";
    private static final String TA_LAT = "latitude";
    private static final String TA_LNG = "longitude";

    private static final String CREATE_TABLE_LATLNG_LIST = "CREATE TABLE "
            + TA_LATLNG_LIST + "(" + TA_INQ_ID + " TEXT,"
            + TA_DRIVER_ID + " TEXT," + TA_TRUCK_ID + " TEXT,"
            + TA_LAT + " TEXT," + TA_LNG + " TEXT" + ")";

    private static final String CREATE_TABLE_LATLNG = "CREATE TABLE " + TA_LATLNG + "("
            + TA_INQ_ID + " TEXT," + TA_LAT + " TEXT," + TA_LNG + " TEXT" + ")";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deletetable(String tbl)
    {
        synchronized (Lock) {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                db.execSQL("delete from " + tbl);
                db.close();
            } catch (SQLiteDatabaseLockedException e) {
                // Log.e("LockedException", "LockedExceptionCalled");
                // db.close();
            }
        }
    }

    public boolean ischeckTableExist(String tblnm) {

        String selectQuery = "SELECT * FROM sqlite_master WHERE type = 'table' AND tbl_name = '" + tblnm + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            c.close();
            db.close();
            return true;
        } else {
            c.close();
            db.close();
            return false;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LATLNG_LIST);
        db.execSQL(CREATE_TABLE_LATLNG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_LATLNG_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_LATLNG);
    }


    //insert data of lat-long list
    public void InsertDataLatlng(String InquieryId, String DriverId, String TruckId, String Latitude, String Longitude) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO "
                + TA_LATLNG_LIST
                + "(" + TA_INQ_ID + ", " + TA_DRIVER_ID + ", " + TA_TRUCK_ID + ", " + TA_LAT + ", " + TA_LNG + ") values" +
                "('" + InquieryId + "','" + DriverId + "','" + TruckId + "','" + Latitude + "','" + Longitude + "')";

        Log.i("Insert query", sql);
        db.execSQL(sql);
        db.close();
    }

    public ArrayList<HashMap<String, String>> getLatLnglist() {

        String sql = "SELECT * FROM " + TA_LATLNG_LIST;

        ArrayList<HashMap<String, String>> GetAccountTypeMstList = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        String[] GetAccountTypeMstKey = Constants.GetLatLngList;
        Log.i("GetAccountTypeMstKey", GetAccountTypeMstKey[0]);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < GetAccountTypeMstKey.length; i++) {
                    map.put(GetAccountTypeMstKey[i], cursor.getString(cursor
                            .getColumnIndex(GetAccountTypeMstKey[i])));
                }
                GetAccountTypeMstList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return GetAccountTypeMstList;
    }

    //insert data of all lat-long
    public void InsertLatlng(String InquieryId, String Latitude, String Longitude) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO "
                + TA_LATLNG
                + "(" + TA_INQ_ID + ", "  + TA_LAT + ", " + TA_LNG + ") values" +
                "('" + InquieryId + "','" + Latitude + "','" + Longitude + "')";

        Log.i("Insert query", sql);
        db.execSQL(sql);
        db.close();
    }

    public ArrayList<HashMap<String, String>> getLatLng() {

        String sql = "SELECT * FROM " + TA_LATLNG;

        ArrayList<HashMap<String, String>> GetAccountTypeMstList = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        String[] GetAccountTypeMstKey = Constants.GetLatLng;
        Log.i("GetAccountTypeMstKey", GetAccountTypeMstKey[0]);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < GetAccountTypeMstKey.length; i++) {
                    map.put(GetAccountTypeMstKey[i], cursor.getString(cursor
                            .getColumnIndex(GetAccountTypeMstKey[i])));
                }
                GetAccountTypeMstList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return GetAccountTypeMstList;
    }
}
