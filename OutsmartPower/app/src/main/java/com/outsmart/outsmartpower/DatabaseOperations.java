package com.outsmart.outsmartpower;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.managers.DateManager;
import com.outsmart.outsmartpower.records.PowerRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian.wagner on 1/16/17.
 * Edited by Rene Moise Kwibuka on 1/18/2017/
 */

/**
 * This class stores all data in the local database. Classes use it to retrieve all stored data.
 */
public class DatabaseOperations extends SQLiteOpenHelper {
    private static DatabaseOperations ourInstance = new DatabaseOperations(
            ParentActivity.getParentActivity());

    public static DatabaseOperations getInstance() {
        return ourInstance;
    }
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase SQ;
    //CREATE ACTIVE SMART OUTLET TABLE.
    private String CREATE_ACTIVE_SMARTOUTLET_QUERRY = "CREATE TABLE "+ Constants.ACTIVE_SMAEROUTLET_TABLE_NAME + "(" +
            Constants.SMART_OUTLET_ID + " CHARACTER(4))";

    //CREATE RECORD TABLE QUERRY.
    private String CREATE_RECORD_TABLE_QUERRY = "CREATE TABLE "+ Constants.RECORD_TABLE_NAME + "(" +
            Constants.RECORD_ID + " integer primary key autoincrement, " +
            Constants.SECONDS + " REAL," +
            Constants.CURRENT_1 + " REAL," + Constants.CURRENT_2 + " REAL," +
            Constants.CURRENT_3 + " REAL," + Constants.CURRENT_4 + " REAL," +
            Constants.VOLTAGE +  " REAL," + Constants.SMART_OUTLET_ID + " CHARACTER(4))";

    //CREATE OUTSMART DEVICE TABLE
    private String CREATE_OUTSMART_DEVICES_TABLE = "CREATE TABLE "+ Constants.DEVICE_TABLE_NAME + "(" +
            Constants.DEVICE_RECORD_ID + " integer primary key autoincrement, " + Constants.DEVICE_NAME + " CHARACTER(30)," +
            Constants.IP_ADDRESS + " CHARACTER(15)," +
            Constants.DEVICE_SSID + " CHARACTER(15)," + Constants.DEVICE_PASSWORD + " CHARACTER(15)," + Constants.DEVICE_ID + " CHARACTER(4))";

    //CREATE SETTINGS TABLE
    /**
    *   Cost Column in dollars.
    *   Date Format Column (0 for mm/dd/year and 1 for dd/mm/year) Default mm/dd/year
    *   Time Format Column (0 for standard time and 1 for military time) Default military time.
    *   Unit Preference Column (0 for wh and 1 for KW) Default Wh
    */

    private String CREATE_SETTINGS_TABLE = "CREATE TABLE "+ Constants.SETTINGS_TABLE_NAME + "(" +
            Constants.COST + " REAL," +
            Constants.DATE_FORMAT + " INTEGER," +
            Constants.TIME_FORMAT + " INTEGER," +
            Constants.UNIT_PREFERENCE + " INTEGER)";

    //Create IP Address table
    private String CREATE_IPADDRESS_TABLE = "CREATE TABLE "+ Constants.IPADDRESS_NAME + "(" +
            Constants.IP_ADDRESS_COL + " CHARACTER(15))";


    private DatabaseOperations(Context context) {
        super(context, Constants.DATABASE_NAME,null,DATABASE_VERSION );
       // Log.e("DATABASE_OPERATIONS", "Database Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD_TABLE_QUERRY);
        db.execSQL(CREATE_OUTSMART_DEVICES_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);
        db.execSQL(CREATE_ACTIVE_SMARTOUTLET_QUERRY);
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
        db.execSQL("DELETE FROM "  + Constants.SETTINGS_TABLE_NAME);
        ContentValues cv = new ContentValues( );
        cv.put(Constants.COST, record.getCost());
        cv.put(Constants.DATE_FORMAT, record.getDateFormat().ordinal());
        cv.put(Constants.TIME_FORMAT, record.time_format.ordinal());
        cv.put(Constants.UNIT_PREFERENCE, record.unit_pref.ordinal());
        db.insert(Constants.SETTINGS_TABLE_NAME, null, cv);

        db.close();
    }

    public void savePowerRecord(PowerRecord dataRecord)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues( );
        cv.put(Constants.SECONDS, dataRecord.getRecordTime().getDataRecordedSeconds());
        cv.put(Constants.CURRENT_1, dataRecord.getCurrent_1());
        cv.put(Constants.CURRENT_2, dataRecord.getCurrent_2());
        cv.put(Constants.CURRENT_3, dataRecord.getCurrent_3());
        cv.put(Constants.CURRENT_4, dataRecord.getCurrent_4());
        cv.put(Constants.VOLTAGE, dataRecord.getVoltage());
        cv.put(Constants.SMART_OUTLET_ID, dataRecord.getSmartOutletId());

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
        cv.put(Constants.DEVICE_ID, smout.getSmart_Outlet_Device_ID());

        db.insert(Constants.DEVICE_TABLE_NAME, null, cv);
        db.close();
    }

    public ArrayList<PowerRecord> getAllRecordsInRange(String smID, int startSeconds, int endSeconds)
    {
        ArrayList<PowerRecord> RecordList = new ArrayList<PowerRecord>();

        /**
         * This querry was supposed to be used below to filter only the data that fall in range of
         * the specified seconds and that belongs to the specified ID. I used <= and =>; and BETWEEN;
         * and IN to compare data but they all did not work. If they worked it would prevent the
         * phone app to load all data to memory. But since I cannot to get it work, I will load all
         * stored data and compare them using java rather than using sql.
        String selectQuery = "SELECT " + Constants.SECONDS + " , " + Constants.CURRENT_1 + " , " + Constants.CURRENT_2 + " , " +
        Constants.CURRENT_3 + " , " +  Constants.CURRENT_4 + " , " + Constants.VOLTAGE + " FROM " + Constants.RECORD_TABLE_NAME
                +
                " WHERE " + Constants.SMART_OUTLET_ID + "=='" + smID + "' AND " + Constants.SECONDS + " IN (" + startSeconds
                + "," + endSeconds + ");";
         **/

        String selectQuery = "SELECT " + Constants.SECONDS + " , " + Constants.CURRENT_1 + " , " +
                Constants.CURRENT_2 + " , " + Constants.CURRENT_3 + " , " +  Constants.CURRENT_4 +
                " , " + Constants.VOLTAGE + " FROM " + Constants.RECORD_TABLE_NAME;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DateManager recordTime = new DateManager(cursor.getInt(0));
                if(recordTime.getDataRecordedSeconds() >= startSeconds &&
                        recordTime.getDataRecordedSeconds() <= endSeconds) {
                    double current_1 = Double.parseDouble(cursor.getString(1));
                    double current_2 = Double.parseDouble(cursor.getString(2));
                    double current_3 = Double.parseDouble(cursor.getString(3));
                    double current_4 = Double.parseDouble(cursor.getString(4));
                    double voltage = Double.parseDouble(cursor.getString(5));


                    PowerRecord powerRecord = new PowerRecord(recordTime, current_1, current_2, current_3,
                            current_4, voltage, smID);
                    RecordList.add(powerRecord);
                }
            } while (cursor.moveToNext());
        }
        // return contact list
        return RecordList;
    }

    public List<SmartOutlet> getSmartOutlerInfo()
    {
        String selectQuery = "SELECT " + Constants.DEVICE_NAME + " , " + Constants.DEVICE_SSID + " , " + Constants.IP_ADDRESS + " , " +
                Constants.DEVICE_PASSWORD + " , " +  Constants.DEVICE_ID + " FROM " + Constants.DEVICE_TABLE_NAME;

        //This is a list of all smartDevices that are saved.
        ArrayList<SmartOutlet> smartOutletList = new ArrayList<>();

        SmartOutlet info = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String ssid = cursor.getString(1);
                String ip_address = cursor.getString(2);
                String password = cursor.getString(3);
                String id = cursor.getString(4);
                info = new SmartOutlet(name,ssid,password,ip_address,id);
                smartOutletList.add(info);
            } while (cursor.moveToNext());
        }
        // return contact list
        return smartOutletList;
    }

    public void removeSmartOutlet(String broadcastedOutletNet){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.DEVICE_TABLE_NAME + "\n WHERE " + Constants.DEVICE_SSID +
                " == '" + broadcastedOutletNet + "'");
        db.close();
    }

    /**
     *   Cost Column in dollars.
     *   Date Format Column (0 for mm/dd/year and 1 for dd/mm/year) Default mm/dd/year
     *   Time Format Column (0 for standard time and 1 for military time) Default military time.
     *   Unit Preference Column (0 for wh and 1 for KW) Default Wh
     */
    public SettingsRecord getSetSettings(){

        String selectQuery = "SELECT *" + " FROM " + Constants.SETTINGS_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        double cost = 0;
        int dateFormat = 0;
        int timeFormat = 0;
        int unitPreference = 0;
        DATE_FORMAT date_format;
        TIME_FORMAT time_format;
        UNIT_PREFERENCE unit_preference;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cost= cursor.getDouble(0);
                dateFormat = cursor.getInt(1);
                timeFormat = cursor.getInt(2);
                unitPreference = cursor.getInt(3);
            } while (cursor.moveToNext());
        }

        if(dateFormat == 0)
            date_format = DATE_FORMAT.monthFirst;
        else
            date_format = DATE_FORMAT.dayFirst;

        if(timeFormat == 0)
            time_format = TIME_FORMAT.standard;
        else
            time_format = TIME_FORMAT.military;

        if(unitPreference == 0)
            unit_preference = UNIT_PREFERENCE.W;
        else
            unit_preference = UNIT_PREFERENCE.Kwh;

        db.close();

        return new SettingsRecord(cost, date_format, time_format, unit_preference);
    }

    /**
     * This functions adds, updates or remove an active smartoutlet.
     */

    /**
     * SOmething very important to learn here. Notice that I call getActiveSmartOutlet() to see if
     * there is any saved outlet. The purpose is if there is already an outlet, to just update it.
     * When we call getActiveSmartOutlet, we open the database for edit. If we do this after opening
     * it again in the following method, the exception is thrown because you can not have two
     * objects opening the database for edit at the same time. Just placing it above re-opening it
     * for edit fixed this problem that I was having.
     * @param smID
     */
    public void updateActiveSmartOutlet(String smID){

        String activeID = getActiveSmartOutlet();
        SQLiteDatabase db = this.getWritableDatabase();

        if(activeID != "") {
            db.execSQL("UPDATE " + Constants.ACTIVE_SMAEROUTLET_TABLE_NAME + "\n SET " + Constants.SMART_OUTLET_ID +
                    " = '" + smID + "'");
        }
        else
        {
            ContentValues cv = new ContentValues( );
            cv.put(Constants.SMART_OUTLET_ID, smID);
            synchronized (db) {
                db.insert(Constants.ACTIVE_SMAEROUTLET_TABLE_NAME, null, cv);
            }
        }
        db.close();
    }

    public String getActiveSmartOutlet(){
        String selectQuery = "SELECT " + Constants.SMART_OUTLET_ID + " FROM " + Constants.ACTIVE_SMAEROUTLET_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String smID = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                smID= cursor.getString(0);
            } while (cursor.moveToNext());
        }
        db.close();
        return smID;
    }

    public void removeActiveSmartOutlet(String smID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.ACTIVE_SMAEROUTLET_TABLE_NAME + "\n WHERE " + Constants.SMART_OUTLET_ID +
                " == '" + smID + "'");
        db.close();
    }
}
