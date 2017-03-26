package com.outsmart.outsmartpower.ui;

/**
 * Created by Rene Moise on 2/14/2017.
 */

import android.content.Context;
import android.widget.Toast;

import com.outsmart.outsmartpower.OutsmartDeviceInfo;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.managers.SmartOutletManager;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This UIManager class will handle all interactions with the user.
 */
public class UIManager implements Observer{
    private static UIManager ourInstance = new UIManager();
    public static UIManager getInstance() {
        return ourInstance;
    }
    List<OutsmartDeviceInfo> deviceInfos;
    private UIManager() {
    }


    //To display on screen. Traditinally called a toast.
    public  void disPlayMessage(String message, int displayTime, Context context){
        Toast.makeText(context, message, displayTime).show();
    }

    //To display on screen. Traditinally called a toast.
    public  void disPlayMessage(String message, Context context){
        disPlayMessage(message, Toast.LENGTH_LONG, context); // default is long time
    }

    @Override
    public void update(Observable observable, Object o) {

        if(observable.getClass().equals(BootlLoader.class))
        {
            //TODO: Display them to the user.
            //Access the outsmartList
            deviceInfos = SmartOutletManager.getInstance().getOutSmartsInfoList();

            //If there is no device info, display the setup layout.
            if(deviceInfos.size() == 0){
                //Display the setup page
                //TOdO: Display the setup page
            }
            else
            {
                //Display a list of outsmartlist.
                //TODO: Display a list of smartDevices.
            }
        }
    }
}
