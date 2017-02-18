package com.outsmart.outsmartpower.managers;

/**
 * Created by Rene Moise on 2/14/2017.
 */

import android.content.Context;
import android.widget.Toast;

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

    }
}
