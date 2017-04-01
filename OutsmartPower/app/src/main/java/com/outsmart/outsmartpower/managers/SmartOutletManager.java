package com.outsmart.outsmartpower.managers;

import android.app.Fragment;

import com.outsmart.outsmartpower.DatabaseOperations;
import com.outsmart.outsmartpower.OutsmartDeviceInfo;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.network.records.StatusRecord;
import com.outsmart.outsmartpower.ui.DisplayPowerFragment;

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
    private OutsmartDeviceInfo activeSmartOutlet;
    private boolean smart_OutletConnected;

    /**
     * The OutsmartDeviceInfo class represents info for a single remote outsmart device that is
     * already saved in the database. We need to have this list at the start of the program.
     * This manager fetched a list from the database.
     */
    private List<OutsmartDeviceInfo> smartOutletList;

    /**
     * DatabaseOperation Instance. SmartOutletManager will have access to the database. It will
     * Save or retrieve data as needed.
     */
    DatabaseOperations databaseOperations;

    private SmartOutletManager() {
    }

    //Get the smart outlet informations.


    public List<OutsmartDeviceInfo> getSmartOutletList() {
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

            //Initialize SettingsRecord with data from the database
            smartOutletList = databaseOperations.getSmartOutlerInfo();

            //Initialize smartOutlet
            smart_OutletConnected = false;

            activeSmartOutlet = smartOutletList.get(0);

            DisplayPowerFragment mainPage = (DisplayPowerFragment) ParentActivity.
                    getParentActivity().getFragmentManager().findFragmentById(R.id.content_main);
            mainPage.receiveActiveSmartOutlet(activeSmartOutlet.getNickname());

            //Add the main page observer.
            notifyObservers();
        }
    }

    public void saveSmartOutlet(OutsmartDeviceInfo info){
        activeSmartOutlet = info;
        databaseOperations.addSmartOutletInfo(info);
        smartOutletList.add(info);
        updateObservers();
        //TODO: Make sure to update the list of available smart Outlet.
    }

    public void setActiveSmartOutlet(OutsmartDeviceInfo activeSmartOutlet) {
        this.activeSmartOutlet = activeSmartOutlet;
        updateObservers();
    }

    /**
     * It returns the active smart outlet.
     */
    public OutsmartDeviceInfo getActiveSmartOutlet() {
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
        for(OutsmartDeviceInfo smOut:smartOutletList){
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
}
