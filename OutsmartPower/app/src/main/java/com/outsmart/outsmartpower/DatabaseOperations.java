package com.outsmart.outsmartpower;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.drm.DrmStore;
import android.media.audiofx.BassBoost;
import android.util.Log;

import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by christian.wagner on 1/16/17.
 * Edited by Rene Moise Kwibuka on 1/18/2017/
 */

public class DatabaseOperations extends SQLiteOpenHelper {
    private static DatabaseOperations ourInstance = new DatabaseOperations(
            ParentActivity.getParentActivity());

    public static DatabaseOperations getInstance() {
        return ourInstance;
    }
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase SQ;

    //CREATE RECORD TABLE QUERRY.
    private String CREATE_RECORD_TABLE_QUERRY = "CREATE TABLE "+ Constants.RECORD_TABLE_NAME + "(" +
            Constants.RECORD_ID + " integer primary key autoincrement, " +
            Constants.SECONDS + " REAL," +
            Constants.CURRENT_1 + " REAL," + Constants.CURRENT_2 + " REAL," +
            Constants.CURRENT_3 + " REAL," + Constants.CURRENT_4 + " REAL," +
            Constants.VOLTAGE +  " REAL," + Constants.OUTSMART_DEVICE_ID + " REAL)";

    //CREATE OUTSMART DEVICE TABLE
    private String CREATE_OUTSMART_DEVICES_TABLE = "CREATE TABLE "+ Constants.DEVICE_TABLE_NAME + "(" +
            Constants.DEVICE_RECORD_ID + " integer primary key autoincrement, " + Constants.DEVICE_NAME + " CHARACTER(30)," +
            Constants.IP_ADDRESS + " CHARACTER(15)," +
            Constants.DEVICE_SSID + " CHARACTER(15)," + Constants.DEVICE_PASSWORD + " CHARACTER(15)," + Constants.DEVICE_ID + " REAL)";

    //CREATE SETTINGS TABLE
    /*
    *   Cost Column in dollars.
    *   Date Format Column (0 for mm/dd/year and 1 for dd/mm/year) Default mm/dd/year
    *   Time Format Column (0 for standard time and 1 for military time) Default military time.
    *   Unit Preference Column (0 for Kwh and 1 for KW) Default KWh
    */

    private String CREATE_SETTINGS_TABLE = "CREATE TABLE "+ Constants.SETTINGS_TABLE_NAME + "(" +
            Constants.COST + " REAL," +
            Constants.DATE_FORMAT + " INTEGER," +
            Constants.TIME_FORMAT + " INTEGER," +
            Constants.UNIT_PREFERENCE + " INTEGER)";

    private DatabaseOperations(Context context) {
        super(context, Constants.DATABASE_NAME,null,DATABASE_VERSION );
       // Log.e("DATABASE_OPERATIONS", "Database Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD_TABLE_QUERRY);
        db.execSQL(CREATE_OUTSMART_DEVICES_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);

        //Insert default data in the settings table
        //SettingsRecord defaultRecord = new SettingsRecord(1,DATE_FORMAT.monthFirst,
                //TIME_FORMAT.military,UNIT_PREFERENCE.Kwh);
        //this.addSettingsRecord(defaultRecord);

       // Log.e("TABLE", "Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DEVICE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.SETTINGS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DEVICE_TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void addSettingsRecord(SettingsRecord record)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues( );
        cv.put(Constants.COST, record.getCost());
        cv.put(Constants.DATE_FORMAT, record.getDateFormat().ordinal());
        cv.put(Constants.TIME_FORMAT, record.time_format.ordinal());
        cv.put(Constants.UNIT_PREFERENCE, record.unit_pref.ordinal());
        db.insert(Constants.SETTINGS_TABLE_NAME, null, cv);

        db.close();
    }

    public void addDataRecord(OutsmartDeviceDataRecord dataRecord)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues( );
        cv.put(Constants.SECONDS, dataRecord.getRecordTime().getDataRecordedSeconds());
        cv.put(Constants.CURRENT_1, dataRecord.getCurrent_1());
        cv.put(Constants.CURRENT_2, dataRecord.getCurrent_2());
        cv.put(Constants.CURRENT_3, dataRecord.getCurrent_3());
        cv.put(Constants.CURRENT_4, dataRecord.getCurrent_4());
        cv.put(Constants.VOLTAGE, dataRecord.getVoltage());
        cv.put(Constants.OUTSMART_DEVICE_ID, dataRecord.getOutsmart_device_id());

        db.insert(Constants.RECORD_TABLE_NAME, null, cv);
        db.close();
    }

    //Add smart outlet information
    public void addSmartOutletInfo(SmartOutlet smout)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues( );
        cv.put(Constants.DEVICE_NAME, smout.getNickname());
        cv.put(Constants.DEVICE_SSID, smout.getSsid());
        cv.put(Constants.IP_ADDRESS, smout.getIpAddress());
        cv.put(Constants.DEVICE_PASSWORD, smout.getPassword());

        db.close();
    }

    public ArrayList<OutsmartDeviceDataRecord> getAllRecordsAfter(int smID, int seconds)
    {
        ArrayList<OutsmartDeviceDataRecord> RecordList = new ArrayList<OutsmartDeviceDataRecord>();

//        //Define columns that we want
//        String[] columns = {Constants.SECONDS, Constants.CURRENT_1, Constants.CURRENT_2,
//                Constants.CURRENT_3, Constants.CURRENT_4, Constants.VOLTAGE
//        };


        String selectQuery = "SELECT " + Constants.SECONDS + " , " + Constants.CURRENT_1 + " , " + Constants.CURRENT_2 + " , " +
        Constants.CURRENT_3 + " , " +  Constants.CURRENT_4 + " , " + Constants.VOLTAGE + " FROM " + Constants.RECORD_TABLE_NAME +
                " WHERE " + Constants.OUTSMART_DEVICE_ID + "==" + smID + " AND " + Constants.SECONDS + " >= " + seconds;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DateManager recordTime = new DateManager(cursor.getInt(0));
                double current_1 = Double.parseDouble(cursor.getString(1));
                double current_2 = Double.parseDouble(cursor.getString(2));
                double current_3 = Double.parseDouble(cursor.getString(3));
                double current_4 = Double.parseDouble(cursor.getString(4));
                double voltage = Double.parseDouble(cursor.getString(5));

                OutsmartDeviceDataRecord contact = new OutsmartDeviceDataRecord(recordTime,current_1,current_2,current_3,
                        current_4,voltage,smID);
                RecordList.add(contact);
            } while (cursor.moveToNext());
        }
        // return contact list
        return RecordList;
    }
}
