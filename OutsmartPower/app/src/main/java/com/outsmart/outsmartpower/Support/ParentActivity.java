package com.outsmart.outsmartpower.Support;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by Rene Moise on 1/16/2017.
 */

/**
 * It returns the context of the mainActivity.
 */
public class ParentActivity {
    private static ParentActivity ourInstance = new ParentActivity();

    public static ParentActivity getInstance() {
        return ourInstance;
    }

    private static Activity mainActivity;
    //private static Fragment mainFragmentActivity;

    private ParentActivity() {
    }


    //Get the activity parent
    public static Activity getParentActivity() {
        return mainActivity;
    }

    //Set the activity parent
    public static void setParentActivity(Activity myParentActivity) {
        mainActivity = myParentActivity;
    }
}