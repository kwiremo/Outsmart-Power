package com.outsmart.outsmartpower.Support;

import android.app.Activity;

import com.outsmart.outsmartpower.managers.SettingsManager;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.managers.UIManager;

import java.util.Observable;

/**
 * Created by Rene Moise on 1/16/2017.
 */

public class BootlLoader extends Observable{

    public BootlLoader(Activity parentActivity)
    {
        ParentActivity.setParentActivity(parentActivity);
        addObserver(SettingsManager.getInstance()); //Add the Settings Manager observer.
        addObserver(SmartOutletManager.getInstance());  // Add the SmartOutletManager observer.
        //Set changed
        setChanged();
        //Notify observers that there is a change.
        notifyObservers();
        //Display to the user that the phone app started successfully.
        //Start server.
        UIManager.getInstance().disPlayMessage("Up and Running", ParentActivity.getParentActivity());

    }
}
