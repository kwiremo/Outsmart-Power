package com.outsmart.outsmartpower.managers;

import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;

import com.outsmart.outsmartpower.PacketInformation;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.UDPClient;
import com.outsmart.outsmartpower.UDPServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

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
public class UDPManager extends Observable implements Observer{

    //Instance of this singleton class
    private static UDPManager ourInstance = new UDPManager();

    //Getter for ourInstance
    public static UDPManager getInstance() {
        return ourInstance;
    }

    private boolean serverRunning = false;      //for if server is running.
    private String packetReceived;

    //The port to send frames from
    private DatagramSocket sendSocket;

    //The port to send frames from
    private DatagramSocket receiveSocket;

    //Private class that receives packets
    private class receiveUDPPacket extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object[] objects) {



            Log.e("TAG", "Server running");

            byte[] packetBuffer = new byte[Constants.BUFFER_SIZE];
            DatagramPacket dp = new DatagramPacket(packetBuffer, packetBuffer.length);
            try {
                while (serverRunning) {
                    receiveSocket.receive(dp);
                    packetReceived = new String(packetBuffer, 0, dp.getLength());
                    publishProgress(packetReceived);
                    //Thread.sleep(Constants.SERVER_SLEEP_TIME);
                }

            } catch (Exception e) {
                Toast.makeText(ParentActivity.getParentActivity(), "Server interrupted!",
                        Toast.LENGTH_SHORT).show();
            } finally {
                stopServer();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            if (values[0] != null){
                String updatePacket = values[0].toString();
                Toast.makeText(ParentActivity.getParentActivity(), updatePacket,
                        Toast.LENGTH_SHORT).show();
                setChanged();
                notifyObservers(values[0]);
            };
        }

        @Override
        protected void onPostExecute(Object obj) {
            stopServer();
        }
    }

    //Class to send UDP packets
    private class sendUDPPacket extends AsyncTask<PacketInformation, Void, Void>{
        @Override
        protected Void doInBackground(PacketInformation... arg0) {
            PacketInformation pktInfo = arg0[0];
            try {
                pktInfo.getSocket().send(pktInfo.getPacket());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //private constructor (singleton)
    private UDPManager() {
    }

    @Override
    public void update(Observable o, Object arg) {
        //Once booted, initialize
        if(o.getClass().equals(BootlLoader.class)){

            startServer();
            addObserver(UDPServer.getOurInstance());

            //TODO Add UDP client and server as observers to this
        }
    }

    //Method to setup sockets for receiving and sending
    private void openSockets(){
        //Open the sendSocket
        try {
            sendSocket = new DatagramSocket();
        }
        catch(SocketException e){
            //Alert if socket failed to open
            e.printStackTrace();
        }

        //Open the receiveSocket
        try {
            receiveSocket = new DatagramSocket(Constants.SERVER_PORT);
        }
        catch(SocketException e){
            //Alert if socket failed to open
            e.printStackTrace();
        }
    }

    public boolean isServerRunning()
    {
        return serverRunning;
    }

    public void stopServer()
    {
        serverRunning = false;
        receiveSocket.close();
        sendSocket.close();
    }

    private void startServer()
    {
        serverRunning = true;
        openSockets();
        new receiveUDPPacket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, receiveSocket);
    }

    //Method to send packets
    public void sendPacket(String packetToSend, String ipAddress){

        InetAddress IPAddress;

        //Try getting the ipAddress from the adjacency table using the frames destination address
        try {
            IPAddress = InetAddress.getByName(ipAddress);
            //Create packet to send
            DatagramPacket sendPacket = new DatagramPacket(packetToSend.getBytes(),
                    packetToSend.length(),
                    IPAddress,
                    Constants.REMOTE_PORT);
            //Send the frame
            new sendUDPPacket().execute(new PacketInformation(sendSocket,sendPacket));
        }
        catch(Exception e){
            e.printStackTrace();//TODO implement exception
        }
    }
}