package com.outsmart.outsmartpower;

import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;

import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
 *
 * + getActiveSmartOutlet returns the smartoutlet that the server is currently talking to.
 */
public class UDPServer extends AsyncTask<Object, Object, Object>{

    //Instance of this singleton class
    private static UDPServer ourInstance = new UDPServer();

    public static UDPServer getInstance() {
        return ourInstance;
    }

    //Active SmartOutlet
    private SmartOutlet activeSmartOutlet;
    private boolean serverRunning = false;      //for if server is running.
    private String packetReceived;

    //private constructor (singleton)
    private UDPServer() {
    }

    @Override
    protected Object doInBackground(Object[] smartOutlets) {

        //only limit to one smart outlet for now.
        Object smOut = smartOutlets[0];

        if(!setActiveSmartOutlet(smOut))
            return null;

        //set serverRunning to true.
        startServer();

        Log.e("TAG", "Server running");

        byte[] packetBuffer = new byte[Constants.BUFFER_SIZE];
        DatagramPacket dp = new DatagramPacket(packetBuffer, packetBuffer.length);
        DatagramSocket ds = null;

        try {
            ds = new DatagramSocket(Constants.SERVER_PORT);

            while (serverRunning) {
                ds.receive(dp);
                packetReceived = new String(packetBuffer, 0, dp.getLength());
                publishProgress(packetReceived);
                Thread.sleep(Constants.SERVER_SLEEP_TIME);
            }

        } catch (Exception e) {
            Toast.makeText(ParentActivity.getParentActivity(), "Server interrupted!",
                    Toast.LENGTH_SHORT).show();
        }
        finally {
            stopServer();
            if (ds != null) {
                ds.close();
            }
        }
        return  packetBuffer;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        if(packetReceived == null)
            return;

        //if packet received store it to the concerned outsmartOutlet.
        //activeSmartOutlet.store(packetReceived);

    }

    @Override
    protected void onPostExecute(Object obj) {
        //super.onPostExecute(result);
        stopServer();
    }

    public SmartOutlet getActiveSmartOutlet()
    {
        return activeSmartOutlet;
    }

    private boolean setActiveSmartOutlet(Object smOut)
    {
        //If object passed is null or if the object passed is not an SmartOutlet, return false else
        //true.
        if(smOut == null || smOut.getClass() != SmartOutlet.class)
            return false;

        activeSmartOutlet = (SmartOutlet) smOut;
        return true;
    }

    public boolean isServerRunning()
    {
        return serverRunning;
    }

    public void stopServer()
    {
        serverRunning = false;
    }

    private void startServer()
    {
        serverRunning = false;

    }
}