package com.outsmart.outsmartpower;

import android.os.AsyncTask;

/**
 * Created by Rene Moise on 1/14/2017.
 *
 * Description: This class is a singleton UDP server that keeps listening for packets and update
 * the smartoutlet and the UI.
 *
 * This class will take three parameters since it extends an ansynchronous task. They are described
 * below:
 * Params, the type of the parameters sent to the task upon execution.
 * Progress, the type of the progress units published during the background computation.
 * Result, the type of the result of the background computation.
 *
 * params will be a smartOutlet object.
 */
public class UDPServer extends AsyncTask<Object, Object, Object>{
    private static UDPServer ourInstance = new UDPServer();

    public static UDPServer getInstance() {
        return ourInstance;
    }

    //private constructor (singleton)
    private UDPServer() {
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        //super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object obj) {
        //super.onPostExecute(result);
    }
}