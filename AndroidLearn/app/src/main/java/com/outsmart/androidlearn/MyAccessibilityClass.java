package com.outsmart.androidlearn;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * Created by Rene Moise on 2/19/2017.
 */

public class MyAccessibilityClass extends AccessibilityService {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();

        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Toast.makeText(this,"Hello", Toast.LENGTH_LONG);
    }

    @Override
    public void onInterrupt() {

    }
}
