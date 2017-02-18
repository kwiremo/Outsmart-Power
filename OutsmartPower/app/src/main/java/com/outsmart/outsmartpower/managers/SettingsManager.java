package com.outsmart.outsmartpower.managers;

import com.outsmart.outsmartpower.DatabaseOperations;
import com.outsmart.outsmartpower.SettingsRecord;
import com.outsmart.outsmartpower.Support.BootlLoader;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene Moise on 2/14/2017.
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

            //Initialize SettingsRecord with data from the database
            settingsRecord = databaseOperations.getSetSettings();
        }
    }
}
