package com.outsmart.outsmartpower.network;

import android.os.AsyncTask;

import android.os.Handler;
import android.text.format.Time;
import android.widget.Toast;

import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.records.CredentialBaseRecord;
import com.outsmart.outsmartpower.records.PowerRecord;
import com.outsmart.outsmartpower.records.RecordInterface;
import com.outsmart.outsmartpower.records.StatusRecord;
import com.outsmart.outsmartpower.ui.UIManager;

import org.json.JSONObject;

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
 * Description: This class is responsible of receiving from and sending packets to the smart outlet.
 * When the packets is received it is handled according to its types as it is described in the
 * function that handles received packets.
 *
 * This class' receiveUDPPacket and sendUDPPacket classes will take three parameters since they
 * extend an asynchronous task. They are described below:
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

    /**
     * This variable is set to true if the timer is running.
     */
    boolean isTimeRunning;
    /**
     * This holds a reference to the smartOutletManager class
     */
    SmartOutletManager smartOutletManager;

    /**
     *    Private class that receives packets
     */
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
                ParentActivity.getParentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ParentActivity.getParentActivity(), "Server interrupted!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } finally {
                stopServer();
            }
            return null;
        }

        /**
         * This always called when the publishProgress is called in the doInBackground above.
         * @param values
         */
        @Override
        protected void onProgressUpdate(Object... values) {
            if (values[0] != null){
                String updatePacket = values[0].toString();
                processPackets(updatePacket);
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
            if(pktInfo != null) {
                try {
                    pktInfo.getSocket().send(pktInfo.getPacket());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    //private constructor (singleton)
    private UDPManager() {
        timer = new Timer();
        numberOfPacketsSent = 0;
        isTimeRunning = false;
    }

    @Override
    public void update(Observable o, Object arg) {
        //Once booted, initialize
        if(o.getClass().equals(BootlLoader.class)){
            startServer();

            smartOutletManager = SmartOutletManager.getInstance();

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

    public void startServer() {
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
            e.printStackTrace();
        }
    }

    /**
     * When this function is used to send a packet, the packet is constantly send every 2 seconds
     * until 10 tries and then it gives up until the user retries.
     * @param packetToSend
     * @param ipAddress
     */
    public void startTimerSendingSetupPackets(final  RecordInterface packetToSend, final String ipAddress) {
        timer = new Timer();
        timer.schedule(new sendSetupPackets(packetToSend,ipAddress),0,2000);
        isTimeRunning = true;
    }

    /**
     * This is run when a credential record or echo request record is to be sent during setup.
     * We wil start sending it every other second until we hear back from the remote.
     * If we receive a credential packet, we will stop a timer. If the timer runs out, we will
     * notify the user that the setup was a failure.
     */
    private class sendSetupPackets extends TimerTask {
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
            if(numberOfPacketsSent >=20){
                setupFailed();  //It also stops the timer.
            }
        }
    }

    /**
     * Any class that implements this interface will give the implementation of the function.
     * When called the id and the ip is passed. Currently, the mainActivity is calling it because
     * it is the one responsible of creating a new smart outlet.
     */
    public interface reportOutsmartCred{
        void onOutsmartCredReceived(String id, String ip);
    }

    /**
     * This function is knows what kind of a packet that was received from the remote smart outlet.
     * If it is the setup packet (that contains remote credentials), it notifies the
     * SmartOutletManager class. The smartoutletManager class returns whether the packet was valid
     * or not. If it was, the timer is stopped. If it was not valid, the setup packets continues to
     * be sent.
     * @param dataReceived
     */
    private void processPackets(String dataReceived){

            JSONObject json;
            try {
                json = new JSONObject(dataReceived);
            }
            catch (Exception e){
                e.printStackTrace();
                return;
            }

            try {
                String type = json.getString(Constants.TYPE_LABEL);

                switch (type){
                    case Constants.CRED_RECORD:
                        String ipAdd = json.getString(Constants.IP_CONTENT);
                        if(!ipAdd.equals("0.0.0.0")){
                            String id = (json.getString(Constants.ID_CONTENT));
                            reportOutsmartCred reportOutsmartCred = (reportOutsmartCred) ParentActivity.
                                    getInstance().getParentActivity();
                            if (reportOutsmartCred != null) {
                                reportOutsmartCred.onOutsmartCredReceived(id, ipAdd);
                            }
                            stopTimer();
                        }
                        break;
                    case Constants.REPL_RECORD:
                        smartOutletManager.setSmart_OutletConnected(true);
                        stopTimer();
                        UIManager.getInstance().disPlayMessage("smart-outlet connected!");
                        break;
                    case Constants.CONT_RECORD:
                        smartOutletManager.receiveStatusRecord(new StatusRecord(dataReceived));
                        break;
                    case Constants.PORE_RECORD:
                        PowerRecord record = new PowerRecord(dataReceived);
                        if(record!=null)
                            smartOutletManager.receivePowerRecord(record);
                        break;
                    default:
                        return;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
    }

    /**
     * When called, the timer is canceled and the number of packets is set back to zero.
     * @return
     */
    public void stopTimer(){
        timer.cancel();
        isTimeRunning = false;
        numberOfPacketsSent = 0;
    }

    /**
     * It runs on UI thread that the setup has failed. When the setup fails,
     */
    public void setupFailed(){
        stopTimer();
        ParentActivity.getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIManager.getInstance().disPlayMessage("Connection failed!\nPlease try again");
            }
        });
    }
}