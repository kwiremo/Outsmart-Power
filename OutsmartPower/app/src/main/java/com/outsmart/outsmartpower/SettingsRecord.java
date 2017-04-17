package com.outsmart.outsmartpower;

/**
 * Created by Rene Moise Kwibuka
 *
 * Name: Settings BaseRecord
 *
 * Description: The setting record has the date format, time format, unit preference,and the cost.
 */

public class SettingsRecord {
    private double cost;    //cost of 1 kwh.



    DATE_FORMAT dateFormat; //date format desired.
    TIME_FORMAT time_format; //time format desired.
    UNIT_PREFERENCE unit_pref; //Unit preference desired.

    public SettingsRecord(double cost, DATE_FORMAT dateFormat,
                          TIME_FORMAT time_format, UNIT_PREFERENCE unit_pref)
    {
        setCost(cost);
        setTime_format(time_format);
        setDateFormat(dateFormat);
        setUnit_pref(unit_pref);
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setTime_format(TIME_FORMAT time_format) {
        this.time_format = time_format;
    }

    public void setDateFormat(DATE_FORMAT dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setUnit_pref(UNIT_PREFERENCE unit_pref) {
        this.unit_pref = unit_pref;
    }

    public DATE_FORMAT getDateFormat() {
        return dateFormat;
    }

    public TIME_FORMAT getTime_format() {
        return time_format;
    }

    public UNIT_PREFERENCE getUnit_pref() {
        return unit_pref;
    }

    public double getCost() {
        return cost;
    }

}
