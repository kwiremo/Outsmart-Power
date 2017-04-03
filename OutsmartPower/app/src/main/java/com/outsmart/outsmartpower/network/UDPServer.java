package com.outsmart.outsmartpower.network;

import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.records.StatusRecord;
import com.outsmart.outsmartpower.ui.UIManager;

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

    //Get the smart-outlet manager instance
    private SmartOutletManager smartOutletManager;

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
                        reportOutsmartCred reportOutsmartCred = (reportOutsmartCred)ParentActivity.getInstance().getParentActivity();
                        if(reportOutsmartCred != null){
                            reportOutsmartCred.onOutsmartCredReceived(id, ipAdd);
                        }
                        break;
                    case Constants.REPL_RECORD:
                        smartOutletManager.setSmart_OutletConnected(true);
                        UIManager.getInstance().disPlayMessage("smart-outlet connected!");
                        break;
                    case Constants.CONT_RECORD:
                        smartOutletManager.receiveStatusRecord(new StatusRecord(dataReceived));
                        break;
                    default:
                        return;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(o.getClass().equals(BootlLoader.class)){
            //Initialize the smart-outlet manager
            smartOutletManager = SmartOutletManager.getInstance();
        }
    }

}
