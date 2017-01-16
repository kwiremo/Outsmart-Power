package com.outsmart.outsmartpower;

import android.os.AsyncTask;

import com.outsmart.outsmartpower.Support.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Rene Moise on 1/16/2017.
 *
 * Description: this class sends a packet to a remote entity specified by a given port, and IP add.
 *
 */
public class UDPClient extends AsyncTask<Object, Object, Object>{
    private static UDPClient ourInstance = new UDPClient();
    public static UDPClient getInstance() {
        return ourInstance;
    }

    private String packetToSend;
    private UDPClient() {
    }

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
