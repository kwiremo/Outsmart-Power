package com.outsmart.outsmartpower.ui;

/**
 * Created by Rene Moise on 2/14/2017.
 */

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.widget.Toast;

import com.outsmart.outsmartpower.SmartOutlet;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.managers.SmartOutletManager;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This UIManager class will handle all interactions with the user.
 */
public class UIManager implements Observer{
    //The unique instance of this class.
    private static UIManager ourInstance = new UIManager();

    Activity parentActivity;
    private UIManager() {
    }

    //To display on screen. Traditionally called a toast.
    public  void disPlayMessage(String message, int displayTime, Context context){
        Toast.makeText(context, message, displayTime).show();
    }

    //To display on screen. Traditionally called a toast.
    public  void disPlayMessage(String message, Context context){
        disPlayMessage(message, Toast.LENGTH_LONG, context); // default is long time
    }

    //To display on screen. Traditionally called a toast.
    public  void disPlayMessage(String message){
        disPlayMessage(message, Toast.LENGTH_LONG, parentActivity.getBaseContext());
    }

    /**
     * Returns the UIManager only instance.
     * @return
     */
    public static UIManager getInstance() {
        return ourInstance;
    }

    //This method starts a fragment that displays the list of smartoutlets to the user
    public void displayAvailableSmartOutlet(){
        GetClickedItemListFragment fragment = new GetClickedItemListFragment();
        FragmentManager fragmentManager = parentActivity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wifiListFragmentContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Called when the observables call notifyobservers.
    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass().equals(BootlLoader.class)) {
            parentActivity = ParentActivity.getParentActivity();
        }
    }
}
