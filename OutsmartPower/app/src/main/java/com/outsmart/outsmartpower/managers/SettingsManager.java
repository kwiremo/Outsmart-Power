package com.outsmart.outsmartpower.managers;

import com.outsmart.outsmartpower.DATE_FORMAT;
import com.outsmart.outsmartpower.DatabaseOperations;
import com.outsmart.outsmartpower.SettingsRecord;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.TIME_FORMAT;
import com.outsmart.outsmartpower.UNIT_PREFERENCE;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene Moise on 2/14/2017.
 */

/**
 * The settings managet is intended to setup all the settings initial values. It is not fully
 * implemented because we did not implement the settings page. Now it setup an initial value of
 * the cost to 10 cents.
 */
public class SettingsManager extends Observable implements Observer{
    private static SettingsManager ourInstance = new SettingsManager();
    public static SettingsManager getInstance() {
        return ourInstance;
    }

    /**
     * The settingsManager class has a settingsRecord. This record is initialized when the
     * bootloader notifies it that the router is up and running. It is initialized by the data
     * gotten from the database.
     */
     SettingsRecord settingsRecord;

    /**
     * DatabaseOperation Instance. Settings Manager will have access to the database. It will
     * Save or retrieve data as needed.
     */
    DatabaseOperations databaseOperations;

    private SettingsManager() {
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass() == BootlLoader.class)
        {
            //Save the database reference
            databaseOperations = DatabaseOperations.getInstance();

            //Save settings
            setSettings(new SettingsRecord(0.10, DATE_FORMAT.monthFirst,
                    TIME_FORMAT.standard, UNIT_PREFERENCE.W));

            //Initialize SettingsRecord with data from the database
            settingsRecord = databaseOperations.getSetSettings();
        }
    }

    /**
     * Returns the cost from the database.
     * @return
     */
    public double getCost(){
        return settingsRecord.getCost();
    }

    /**
     * It saves a settings record passed to it.
     * @param record
     */
    public void setSettings(SettingsRecord record){
        databaseOperations.addSettingsRecord(record);
    }
}
