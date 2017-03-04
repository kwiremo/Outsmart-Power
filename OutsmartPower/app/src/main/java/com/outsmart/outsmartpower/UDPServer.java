package com.outsmart.outsmartpower;

import android.os.AsyncTask;

import com.outsmart.outsmartpower.managers.UDPManager;

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

public class UDPServer implements Observer {

    //Method that alerts the UDPServer when the UDPManager receives a packet. This class will process
    //only packets destined for itself
    @Override
    public void update(Observable o, Object arg) {

        //If the UDPManager is notifying with a power record, this is for destined for the server
        if(o.getClass() == UDPManager.class && arg.getClass() == OutsmartDeviceDataRecord.class){
            //TODO pass power record to approapriate location
        }
    }

}
