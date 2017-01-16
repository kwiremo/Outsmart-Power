package com.outsmart.outsmartpower.Support;

import android.app.Activity;

/**
 * Created by Rene Moise on 1/16/2017.
 */
public class ParentActivity {
    private static ParentActivity ourInstance = new ParentActivity();
    public static ParentActivity getInstance() {
        return ourInstance;
    }

    private static Activity mainActivity;

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
