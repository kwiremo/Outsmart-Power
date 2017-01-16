package com.outsmart.outsmartpower.Support;

import android.app.Activity;

/**
 * Created by Rene Moise on 1/16/2017.
 */

public class BootlLoader {

    public BootlLoader(Activity parentActivity)
    {
        ParentActivity.setParentActivity(parentActivity);
    }
}
