package com.outsmart.outsmartpower;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.managers.UDPManager;
import com.outsmart.outsmartpower.network.records.OutsmartDeviceDataRecord;

import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by christian.wagner on 2/25/17.
 *
 * Name: UDPServer Class
 *
 * Description: This class will receive notifications from the UDPManagerUpdater class that a packet
 * has arrived for the server. It will then process that packet.
 */

public class UDPServer extends Observable implements Observer{

    //Instantiate one and only object of this class.
    private static UDPServer ourInstance = new UDPServer();

    private UDPServer() {
        //addObserver();
    }

    public interface reportOutsmartCred{
        public void onOutsmartCredReceived(int id, String ip);
    }
    //To return a singleton instance.
    public static UDPServer getOurInstance() {
        return ourInstance;
    }

    //Method that alerts the UDPServer when the UDPManager receives a packet. This class will process
    //only packets destined for itself
    @Override
    public void update(Observable o, Object arg) {
        String dataReceived = arg.toString();

        //If the UDPManager is notifying with a power record, this is for destined for the server
        if(o.getClass() == UDPManager.class){
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
                        int id = Integer.parseInt(json.getString(Constants.ID_CONTENT));
                        OutsmartDeviceInfo info = new OutsmartDeviceInfo();
                        info.setIpAddress(ipAdd);
                        info.setSmart_Outlet_Device_ID(id);
                        notifyObservers(info);


                        Intent intent = new Intent(getClass().getName());
                        // You can also include some extra data.
                        intent.putExtra("ipAdd", ipAdd);
                        intent.putExtra("id", id);
                        LocalBroadcastManager.getInstance(ParentActivity.getParentActivity()).sendBroadcast(intent);

                        reportOutsmartCred reportOutsmartCred = (reportOutsmartCred)ParentActivity.getInstance().getParentActivity();
                        if(reportOutsmartCred != null){
                            reportOutsmartCred.onOutsmartCredReceived(id, ipAdd);
                        }
                        break;
                    default:
                        return;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
