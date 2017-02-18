package com.outsmart.outsmartpower;

import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;

import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Observable;

/**
 * Created by Rene Moise on 1/14/2017.
 *
 * Description: This class is a singleton UDP server that keeps listening for packets and update
 * the smartoutletmanager.
 *
 * This class will take three parameters since it extends an ansynchronous task. They are described
 * below:
 * Params, the type of the parameters sent to the task upon execution.
 * Progress, the type of the progress units published during the background computation.
 * Result, the type of the result of the background computation.
 *
 *
 * */
public class UDPServer extends AsyncTask<Object, Object, Object>{

    //Instance of this singleton class
    private static UDPServer ourInstance = new UDPServer();

    public static UDPServer getInstance() {
        return ourInstance;
    }

    private boolean serverRunning = false;      //for if server is running.
    private String packetReceived;

    //private constructor (singleton)
    private UDPServer() {
    }

    @Override
    protected Object doInBackground(Object[] objects) {

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

    }

    @Override
    protected void onPostExecute(Object obj) {
        //super.onPostExecute(result);
        stopServer();
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