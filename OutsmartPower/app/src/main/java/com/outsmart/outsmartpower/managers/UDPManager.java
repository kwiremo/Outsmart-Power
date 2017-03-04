package com.outsmart.outsmartpower.managers;

import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;

import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.UDPClient;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Observable;

/**
 * Created by Rene Moise on 1/14/2017.
 *
 * Edited by Christian Wagner 2/25/2017
 *
 * Name: UDPManager Class
 *
 * Description: This class is a singleton UDP server that keeps listening for packets and update
 * the smartoutletmanager.
 *
 * This class' receiveUDPPacket and sendUDPPacket classes will take three parameters since they
 * extend an ansynchronous task. They are described below:
 *
 * Params: the type of the parameters sent to the task upon execution.
 * Progress: the type of the progress units published during the background computation.
 * Result: the type of the result of the background computation.
 * */
public class UDPManager extends Observable {

    //Instance of this singleton class
    private static UDPManager ourInstance = new UDPManager();

    //Getter for ourInstance
    public static UDPManager getInstance() {
        return ourInstance;
    }

    private boolean serverRunning = false;      //for if server is running.
    private String packetReceived;

    //Private class that receives packets
    private class receiveUDPPacket extends AsyncTask<Object, Object, Object> {

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
            } finally {
                stopServer();
                if (ds != null) {
                    ds.close();
                }
            }
            return packetBuffer;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            if (packetReceived == null);
        }

        @Override
        protected void onPostExecute(Object obj) {
            //super.onPostExecute(result);
            stopServer();
        }
    }

    //Class to send UDP packets
    private class sendUDPPacket extends AsyncTask<Object, Object, Object>{
        //The packet that will be sent
        String packetToSend;

        @Override
        protected Object doInBackground(Object[] objects) {
            //Get the first object to send in the list
            if(objects[0] == null)
                packetToSend = objects[0].toString();

            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket();
                DatagramPacket dp;

                dp = new DatagramPacket(packetToSend.getBytes(), packetToSend.length(),
                        InetAddress.getByName(Constants.getInstance().REMOTE_IP_ADDRESS),
                        Constants.REMOTE_PORT);

                ds.setBroadcast(true);
                ds.send(dp);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ds != null) {
                    ds.close();
                }
            }
            return null;
        }
    }

    //private constructor (singleton)
    private UDPManager() {

    }

    //Method that sends a packet via the sendUDPPacket Class
    public void sendPacket(){

    }

    //Method that recieves a packet via the receivedUDPPacketClass
    public void receivePacket(){

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