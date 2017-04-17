package com.outsmart.outsmartpower.managers;

import android.app.Activity;

import com.outsmart.outsmartpower.DatabaseOperations;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.SmartOutlet;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.network.UDPManager;
import com.outsmart.outsmartpower.records.EchoRequestRecord;
import com.outsmart.outsmartpower.records.PowerRecord;
import com.outsmart.outsmartpower.records.StatusRecord;
import com.outsmart.outsmartpower.ui.DisplayPowerFragment;
import com.outsmart.outsmartpower.ui.GetClickedItemListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene Moise on 2/14/2017.
 */
public class SmartOutletManager extends Observable implements Observer,
        GetClickedItemListFragment.OnReceivedClickedListItem{

    private static SmartOutletManager ourInstance = new SmartOutletManager();

    private SmartOutlet activeSmartOutlet;
    private boolean smart_OutletConnected;
    /**
     * This mainactivity stores a reference to the mainActivity.
     */
    private Activity mainActivity;

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

    /**
     * The UDPManager is needed to send the echo request when the phone app is first launched or
     * when we set a new active smart outlet.
     */
    private UDPManager udpManager;

    private SmartOutletManager() {
        smartOutletList = new ArrayList<>();
    }

    //Get the smart outlet informations.


    public static SmartOutletManager getInstance() {
        return ourInstance;
    }

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
            //Initialize the udpManager
            udpManager = UDPManager.getInstance();

            //Initialize mainActivity.
            mainActivity = ParentActivity.getParentActivity();
            //Save the database reference
            databaseOperations = DatabaseOperations.getInstance();

            //Initialize SettingsRecord with data from the databasesmartOutletList = databaseOperations.getSmartOutlerInfo();

            //Initialize smartOutlet
            smart_OutletConnected = false;

            /**
             * This will check to see if there is an active outsmart. if there is it will update
             * both the screen and the activesmart outlet field.
             */
            smartOutletList = databaseOperations.getSmartOutlerInfo();
            if(smartOutletList.size() > 0) {
                String activeID = databaseOperations.getActiveSmartOutlet();
                for(int i = 0; i<smartOutletList.size(); i++){
                    if(smartOutletList.get(i).getSmart_Outlet_Device_ID().equals( activeID)){
                        setActiveSmartOutlet(smartOutletList.get(i));
                        updateUITitle(activeSmartOutlet.getNickname());
                        break;
                    }
                }
            }
        }
    }

    private void updateUITitle(String nickname) {
        try {
            DisplayPowerFragment displayPowerFragment = (DisplayPowerFragment) mainActivity.
                    getFragmentManager().findFragmentById(R.id.wifiListFragmentContainer);

            if (displayPowerFragment != null) {
                displayPowerFragment.updateSmartOutletTitle(nickname);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveSmartOutlet(SmartOutlet info){
        setActiveSmartOutlet(info);
        databaseOperations.addSmartOutletInfo(info);
        databaseOperations.updateActiveSmartOutlet(info.getSmart_Outlet_Device_ID());
        smartOutletList.add(info);
        updateUITitle(info.getNickname());
    }

    public void setActiveSmartOutlet(SmartOutlet activeSmartOutlet) {
        this.activeSmartOutlet = activeSmartOutlet;
        sendEchoRequest();
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

       //updateObservers();
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
        if(broadSmartOutletNetw.equals(activeSmartOutlet.getNickname())){
            databaseOperations.removeActiveSmartOutlet(activeSmartOutlet.getSmart_Outlet_Device_ID());
        }

        if(smartOutletList.size() > 0){
            setActiveSmartOutlet(smartOutletList.get(smartOutletList.size()-1));
            databaseOperations.updateActiveSmartOutlet(activeSmartOutlet.getSmart_Outlet_Device_ID());
            updateUITitle(activeSmartOutlet.getNickname());
        }
        else {
            activeSmartOutlet = null;
            updateUITitle("--");
        }
    }

    /**
     * If a status record is received and it is currently on the screen update the screen.
     * Else, Ignore for now. Later we might want to still save statuses of all records.
     * But for now, i am not saving it in the database.
     * @param record
     */
    public void receiveStatusRecord(StatusRecord record){
        if(activeSmartOutlet.getSmart_Outlet_Device_ID() == record.getSmartOutletID()) {
            DisplayPowerFragment displayPowerFragment = (DisplayPowerFragment) mainActivity.
                    getFragmentManager().findFragmentById(R.id.wifiListFragmentContainer);

            if(displayPowerFragment != null){
                displayPowerFragment.updateStatus(record);
            }
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
    public void receivePowerRecord(PowerRecord record) {
        if (activeSmartOutlet != null) {
            if (record.getSmartOutletId().equals( activeSmartOutlet.getSmart_Outlet_Device_ID())) {
                DisplayPowerFragment displayPowerFragment = (DisplayPowerFragment) mainActivity.
                        getFragmentManager().findFragmentById(R.id.wifiListFragmentContainer);

                if (displayPowerFragment != null) {
                    displayPowerFragment.updatePowerRecords(record);
                }
            }
        }
        //Save the record in the database
        databaseOperations.savePowerRecord(record);
    }

    /**
     * This function is called by the graph activity. It requests all the records that fall in the
     * specified range. For example, the graph activity might request all records between now and
     * today at midnight. Or all records between last week and today.
     * @param startSeconds
     * @param endSeconds
     * @return
     */
    public ArrayList<PowerRecord> getRecordsInRange(int startSeconds, int endSeconds){
        return databaseOperations.getAllRecordsInRange(
                activeSmartOutlet.getSmart_Outlet_Device_ID(),startSeconds, endSeconds);
    }

    @Override
    public void receiveClickedItem(int chosenOutsmart) {
        setActiveSmartOutlet(smartOutletList.get(chosenOutsmart));
    }

    /**
     * This function is called to send an echo request to the active smart outlet.
     * Sending an echo request to a smart outlet insures that the remote has our ip address
     * and that it can send us packets.
     */
    public void sendEchoRequest(){
        udpManager.startTimerSendingSetupPackets(
                new EchoRequestRecord(),activeSmartOutlet.getIpAddress());
    }

    public double getAverageCostToday(){
        double averagePower = getAveragePowerToday();

        return averagePower * SettingsManager.getInstance().getCost()*
                DateManager.getNumberOfHoursSinceMidnight()/1000/3600;
    }

    private double getAveragePowerToday(){
        double averagePower = 0;
        for(PowerRecord powerRecord:getRecordsInRange(DateManager.getTodayMidnightSeconds(),
                DateManager.getNowSeconds())){
            averagePower = powerRecord.getPower1() + powerRecord.getPower2() +
                    powerRecord.getCurrent_3() + powerRecord.getPower4();
        }
        return averagePower;
    }
}
