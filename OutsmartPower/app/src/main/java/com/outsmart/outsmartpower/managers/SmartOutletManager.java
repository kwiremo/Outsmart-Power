package com.outsmart.outsmartpower.managers;

import com.outsmart.outsmartpower.DatabaseOperations;
import com.outsmart.outsmartpower.SmartOutlet;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.records.PowerRecord;
import com.outsmart.outsmartpower.records.StatusRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene Moise on 2/14/2017.
 */
public class SmartOutletManager extends Observable implements Observer{
    private static SmartOutletManager ourInstance = new SmartOutletManager();
    public static SmartOutletManager getInstance() {
        return ourInstance;
    }
    private SmartOutlet activeSmartOutlet;
    private boolean smart_OutletConnected;

    /**
     * The SmartOutlet class represents info for a single remote outsmart device that is
     * already saved in the database. We need to have this list at the start of the program.
     * This manager fetched a list from the database.
     */
    private List<SmartOutlet> smartOutletList;

    /**
     * DatabaseOperation Instance. SmartOutletManager will have access to the database. It will
     * Save or retrieve data as needed.
     */
    DatabaseOperations databaseOperations;

    private SmartOutletManager() {
        smartOutletList = new ArrayList<SmartOutlet>();
    }

    //Get the smart outlet informations.


    public List<SmartOutlet> getSmartOutletList() {
        return smartOutletList;
    }

    /**
     * This is called when the observable to which this observer is subscribed notifies its observers.
     * If the bootloader notifies the observers, this OutsmartManager will load a list of
     * smart outlets already saved.
     */


    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass() == BootlLoader.class)
        {
            //Save the database reference
            databaseOperations = DatabaseOperations.getInstance();

            //Initialize SettingsRecord with data from the databasesmartOutletList = databaseOperations.getSmartOutlerInfo();

            //Initialize smartOutlet
            smart_OutletConnected = false;

            //activeSmartOutlet = smartOutletList.get(0);

            //Add the main page observer.
            notifyObservers();
        }
    }

    public void saveSmartOutlet(SmartOutlet info){
        activeSmartOutlet = info;
        databaseOperations.addSmartOutletInfo(info);
        smartOutletList.add(info);
        updateObservers();
        //TODO: Make sure to update the list of available smart Outlet.
    }

    public void setActiveSmartOutlet(SmartOutlet activeSmartOutlet) {
        this.activeSmartOutlet = activeSmartOutlet;
        updateObservers();
    }

    /**
     * It returns the active smart outlet.
     */
    public SmartOutlet getActiveSmartOutlet() {
        return activeSmartOutlet;
    }

    public boolean isSmart_OutletConnected() {
        return smart_OutletConnected;
    }

    public void setSmart_OutletConnected(boolean smart_OutletConnected) {
        this.smart_OutletConnected = smart_OutletConnected;
       updateObservers();
    }

    public boolean isRegistered(String broadSmartOutletNetw){
        for(SmartOutlet smOut:smartOutletList){
            if(smOut.getSsid().equals( broadSmartOutletNetw)){
                return true;
            }
        }
        return false;
    }

    public void removeOutlet(String broadSmartOutletNetw){
        databaseOperations.removeSmartOutlet(broadSmartOutletNetw);
        smartOutletList = databaseOperations.getSmartOutlerInfo();
    }

    /**
     * If a status record is received and it is currently on the screen update the screen.
     * Else, Ignore for now. Later we might want to still save statuses of all records.
     * But for now, i am not saving it in the database.
     * @param record
     */
    public void receiveStatusRecord(StatusRecord record){
        if(activeSmartOutlet.getSmart_Outlet_Device_ID() == record.getSmartOutletID()) {
            setChanged();
            notifyObservers(record);
        }
        else
        {
            //Ignore for now.
        }
    }

    private void updateObservers(){
        setChanged();
        notifyObservers();
    }

    /**
     * When the smart-outlet-manager receives a packet from the UDPManager, it checks if the
     * id of the sender matches the active smart-outlet. If so, it will update the UI.
     * Also, the record is saved in the database.
     * @param record
     */
    public void receivePowerRecord(PowerRecord record){
        //if(record.getSmartOutletId() == activeSmartOutlet.getSmart_Outlet_Device_ID()){
            notifyObservers(record);
        //}

        //Save the record in the database
        databaseOperations.savePowerRecord(record);
    }
}
