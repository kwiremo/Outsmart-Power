package com.outsmart.outsmartpower.network.records;

import android.util.Log;

import com.outsmart.outsmartpower.DateManager;
import com.outsmart.outsmartpower.Support.Constants;

import org.json.JSONObject;

/**
 * Created by Rene Moise on 1/20/2017.
 */

public class OutsmartDeviceDataRecord {
    private DateManager recordTime;
    private   double current_1;
    private   double current_2;
    private   double current_3;
    private   double current_4;
    private   double voltage;
    private   int outsmart_device_id;

    public OutsmartDeviceDataRecord(DateManager recordTime, double current_1, double current_2,
                                    double current_3, double current_4, double voltage,
                                    int outsmart_device_id) {
        this.recordTime = recordTime;
        this.current_1 = current_1;
        this.current_2 = current_2;
        this.current_3 = current_3;
        this.current_4 = current_4;
        this.voltage = voltage;
        this.outsmart_device_id = outsmart_device_id;
    }

    public OutsmartDeviceDataRecord(String JSONString, int outsmart_device_id)
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

            new OutsmartDeviceDataRecord(time, current_1, current_2, current_3, current_4,
                    voltage, outsmart_device_id);
        }

        catch ( Exception e)
        {
            Log.e("OUTSMARTDEVIEDATARECORD", "Processing json string failed");
        }
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

    public int getOutsmart_device_id() {
        return outsmart_device_id;
    }

    public void setOutsmart_device_id(int outsmart_device_id) {
        this.outsmart_device_id = outsmart_device_id;
    }
}
