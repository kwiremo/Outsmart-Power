package com.outsmart.outsmartpower;

import android.os.AsyncTask;

import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.managers.UDPManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene Moise Kwibuka
 *
 * Edited by Christian Wagner 2/25/2017
 *
 * Name: UDP Client
 *
 * Description: This class sends a packet to a remote entity specified by a given port, and IP add.
 * It also sends and receives 'hello' packets that ensure the android application is still connected
 * to a particular smart outlet
 */

public class UDPClient implements Observer{
    private static UDPClient ourInstance = new UDPClient();
    public static UDPClient getInstance() {
        return ourInstance;
    }

    private UDPClient() {
    }

    //Method that calls the UDP Manager's send packet method
    public void sendUDOClient(){
        //TODO implement
        //UDPManager.getInstance().sendPacket();
    }
    //Update method to respond to notifications from the UDPManager that a packet came in
    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(UDPManager.class)){

        }
    }
}
