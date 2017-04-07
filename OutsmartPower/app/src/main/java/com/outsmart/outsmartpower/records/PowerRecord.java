package com.outsmart.outsmartpower.records;

import android.util.Log;

import com.outsmart.outsmartpower.managers.DateManager;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.ui.StringInputDialog;

import org.json.JSONObject;

/**
 * Created by Rene Moise on 1/20/2017.
 */

public class PowerRecord extends BaseRecord{
    private DateManager recordTime;
    private   double current_1;
    private   double current_2;
    private   double current_3;
    private   double current_4;
    private   double voltage;

    public PowerRecord(DateManager recordTime, double current_1, double current_2,
                       double current_3, double current_4, double voltage,
                       String smartOutletId) {
        this.recordTime = recordTime;
        this.current_1 = current_1;
        this.current_2 = current_2;
        this.current_3 = current_3;
        this.current_4 = current_4;
        this.voltage = voltage;
        this.smartOutletID = smartOutletId;
    }

    public PowerRecord(String JSONString)
    {
        try
        {
            JSONObject json = new JSONObject(JSONString);
            DateManager time = new DateManager(Integer.parseInt(json.getString(Constants.SECONDS)));
            Double current_1 = Double.parseDouble(json.getString(Constants.CURRENT_1));
            Double current_2 = Double.parseDouble(json.getString(Constants.CURRENT_2));
            Double current_3 = Double.parseDouble(json.getString(Constants.CURRENT_3));
            Double current_4 = Double.parseDouble(json.getString(Constants.CURRENT_4));
            Double voltage = Double.parseDouble(json.getString(Constants.VOLTAGE));
            smartOutletID = json.getString(Constants.ID_CONTENT);

            setRecordTime(time);
            setCurrent_1(current_1);
            setCurrent_2(current_2);
            setCurrent_3(current_3);
            setCurrent_4(current_4);
            setVoltage(voltage);
        }

        catch ( Exception e)
        {
            e.printStackTrace();
            Log.e("OUTSMARTDEVIEDATARECORD", "Processing json string failed");
        }
    }

    public double getPower1(){
        return getCurrent_1()*getVoltage();
    }

    public double getPower2(){
        return getCurrent_2()*getVoltage();
    }
    public double getPower3(){
        return getCurrent_3()*getVoltage();
    }
    public double getPower4(){
        return getCurrent_4()*getVoltage();
    }

    public DateManager getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(DateManager recordTime) {
        this.recordTime = recordTime;
    }

    public double getCurrent_1() {
        return current_1;
    }

    public void setCurrent_1(double current_1) {
        this.current_1 = current_1;
    }

    public double getCurrent_2() {
        return current_2;
    }

    public void setCurrent_2(double current_2) {
        this.current_2 = current_2;
    }

    public double getCurrent_3() {
        return current_3;
    }

    public void setCurrent_3(double current_3) {
        this.current_3 = current_3;
    }

    public double getCurrent_4() {
        return current_4;
    }

    public void setCurrent_4(double current_4) {
        this.current_4 = current_4;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public String getSmartOutletId() {
        return smartOutletID;
    }
}
