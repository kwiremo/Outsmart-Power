package com.outsmart.outsmartpower.network;

import android.os.AsyncTask;

import android.os.Handler;
import android.text.format.Time;
import android.widget.Toast;

import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.records.CredentialBaseRecord;
import com.outsmart.outsmartpower.records.RecordInterface;
import com.outsmart.outsmartpower.ui.UIManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

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

    /**
     * This variable knows how many times it's been since we started sending packets to the remote
     * since we received a setup packet. After 10 times, we notify the user of the failure to setup.
     */
    private int numberOfPacketsSent;

    /**
     * This timer is used to send packets every 2 or so seconds to the remote until we hear back
     * from it.
     */
    Timer timer;


    //Private class that receives packets
    private class receiveUDPPacket extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object[] objects) {
            byte[] packetBuffer = new byte[Constants.BUFFER_SIZE];
            DatagramPacket dp = new DatagramPacket(packetBuffer, packetBuffer.length);
            try {
                while (serverRunning) {
                    receiveSocket.receive(dp);
                    packetReceived = new String(packetBuffer, 0, dp.getLength());
                    publishProgress(packetReceived);
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
                setChanged();
                notifyObservers(updatePacket);
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
        numberOfPacketsSent = 0;
    }

    @Override
    public void update(Observable o, Object arg) {
        //Once booted, initialize
        if(o.getClass().equals(BootlLoader.class)){
            startServer();
            addObserver(UDPServer.getOurInstance());
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

    public void stopServer() {
        serverRunning = false;
        receiveSocket.close();
        sendSocket.close();
    }

    private void startServer() {
        serverRunning = true;
        openSockets();
        new receiveUDPPacket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, receiveSocket);
    }

    //Method to send packets
    public void sendPacket(RecordInterface packetToSend, String ipAddress){
        String packetStringToSend = packetToSend.toJSONString();
        InetAddress IPAddress;

        //Try getting the ipAddress from the adjacency table using the frames destination address
        try {
            IPAddress = InetAddress.getByName(ipAddress);
            //Create packet to send
            DatagramPacket sendPacket = new DatagramPacket(packetStringToSend.getBytes(),
                    packetStringToSend.length(),
                    IPAddress,
                    Constants.REMOTE_PORT);
            //Send the frame
            new sendUDPPacket().execute(new PacketInformation(sendSocket,sendPacket));
        }
        catch(Exception e){
            e.printStackTrace();//TODO implement exception
        }
    }

    public void startTimerSendingSetupPackets(final  RecordInterface packetToSend, final String ipAddress) {
        timer = new Timer();
        timer.schedule(new sendSetupPackets(packetToSend,ipAddress),0,1000);
    }


    /**
     * This is run when a credential record or echo request record is to be sent during setup.
     * We wil start sending it every other second until we hear back from the remote.
     * If we receive a credential packet, we will stop a timer. If the timer runs out, we will
     * notify the user that the setup was a failure.
     */
    class sendSetupPackets extends TimerTask {
        RecordInterface packetToSend;
        String ipAddress;
        public sendSetupPackets(RecordInterface packetToSend, String ipAddress){
            this.packetToSend = packetToSend;
            this.ipAddress = ipAddress;
        }
        @Override
        public void run() {
            sendPacket(packetToSend,ipAddress);
            numberOfPacketsSent++;
            if(numberOfPacketsSent == 60){
                stopTimer();
            }
            //UIManager.getInstance().disPlayMessage("Sent Setup!");
        }
    }

    /**
     * TODO: This is done solely to be called by the UDPServer if it receives a credential record.
     * Once we move its functionality to this class, we won't need this function anymore.
     * @return
     */
    public void stopTimer(){
        timer.cancel();
        numberOfPacketsSent = 0;
    }
}