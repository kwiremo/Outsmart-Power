package com.outsmart.outsmartpower.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.outsmart.outsmartpower.Activities.Graph;
import com.outsmart.outsmartpower.SmartOutlet;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.Support.BootlLoader;
import com.outsmart.outsmartpower.Support.Constants;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.helperclasses.DataWrapper;
import com.outsmart.outsmartpower.managers.DateManager;
import com.outsmart.outsmartpower.managers.SettingsManager;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.network.UDPManager;
import com.outsmart.outsmartpower.records.ControlRecord;
import com.outsmart.outsmartpower.records.PowerRecord;
import com.outsmart.outsmartpower.records.StatusRecord;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene Moise on 3/26/2017.
 */

/**
 * This fragment displays the current status of the connected smart-outlet. It displays what outlets
 * are on and off. It displays the power that is being drawn by individual outlets. And most
 * importantly, it provides interface to control individual outlets.
 */
public class DisplayPowerFragment extends Fragment {

    private Activity parentActivity;

    //Show the active smart Outlet name.
    private TextView showActiveTV;

    //DEFINE BUTTONS
    //On and aff toggle_buttons
    private ToggleButton tglbtn_OffOn1, tglbtn_OffOn2, tglbtn_OffOn3, tglbtn_OffOn4;

    //DEFINE TEXT VIEWS
    private TextView tv_power1, tv_power2, tv_power3, tv_power4, tv_cost;

    //Learn more buttons
    private Button btn_More1, btn_More2, btn_More3, btn_More4;

    //It has a reference to the OutsmartManager. It will be used to know what smart-outlet is
    //active at the moment. That is the device that will be sent the information.
    SmartOutletManager smartOutletManager;

    /**
     * We are using the content_main layout.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main,container, false);
    }

    /**
     * When the view is created, I create listeners for all the views that need listeners.
     * The statistics button listener gets power values and starts an activity. To improve the way
     * I handled them, a function would be created that takes in x values and y values to start
     * an activity with those data. However, I still insist that this function should have been
     * implemented inside the graph activity. (Look the graph comments.)
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //SmartOutletManager.getInstance().addObserver(MainPageUI.getInstance());
        //Initialize the showActiveSmartoutlet
        showActiveTV = (TextView) getActivity().findViewById(R.id.deviceNameTV);
        parentActivity = getActivity();

        //Initialize smartOutlet Manager
        smartOutletManager = SmartOutletManager.getInstance();

        //Initialize the showActiveSmartoutlet
        showActiveTV = (TextView) getActivity().findViewById(R.id.deviceNameTV);

        tv_cost = (TextView) getActivity().findViewById(R.id.costTV);
        //DEFINE BUTTON INSTANCES

        //Toggle buttons
        tglbtn_OffOn1 = (ToggleButton) parentActivity.findViewById(R.id.tglbtn_OffOn1);
        tglbtn_OffOn2 = (ToggleButton) parentActivity.findViewById(R.id.tglbtn_OffOn2);
        tglbtn_OffOn3 = (ToggleButton) parentActivity.findViewById(R.id.tglbtn_OffOn3);
        tglbtn_OffOn4 = (ToggleButton) parentActivity.findViewById(R.id.tglbtn_OffOn4);


        //Learn more buttons
        btn_More1 = (Button) parentActivity.findViewById(R.id.btn_more_1);
        btn_More2 = (Button) parentActivity.findViewById(R.id.btn_more_2);
        btn_More3 = (Button) parentActivity.findViewById(R.id.btn_more_3);
        btn_More4 = (Button) parentActivity.findViewById(R.id.btn_more_4);


        //Text INSTANCES
        tv_power1 = (TextView) parentActivity.findViewById(R.id.tv_power1);
        tv_power2=  (TextView) parentActivity.findViewById(R.id.tv_power2);
        tv_power3 = (TextView) parentActivity.findViewById(R.id.tv_power3);
        tv_power4 = (TextView) parentActivity.findViewById(R.id.tv_power4);

        //setEnabled(false);
        //BUTTON LISTENERS


        // attach an OnClickListener
        tglbtn_OffOn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(smartOutletManager.getActiveSmartOutlet() != null) {
                    if (tglbtn_OffOn1.isChecked()) {

                        UDPManager.getInstance().sendPacket(new ControlRecord("on1"),
                                smartOutletManager.getActiveSmartOutlet().getIpAddress());
                    } else {
                        UDPManager.getInstance().sendPacket(new ControlRecord("off1"),
                                smartOutletManager.getActiveSmartOutlet().getIpAddress());
                    }
                }
            }
        });

        tglbtn_OffOn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(smartOutletManager.getActiveSmartOutlet() != null) {
                    if (tglbtn_OffOn2.isChecked()) {
                        UDPManager.getInstance().sendPacket(new ControlRecord("on2"),
                                smartOutletManager.getActiveSmartOutlet().getIpAddress());
                    } else {
                        UDPManager.getInstance().sendPacket(new ControlRecord("off2"),
                                smartOutletManager.getActiveSmartOutlet().getIpAddress());
                    }
                }
            }
        });

        tglbtn_OffOn3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(smartOutletManager.getActiveSmartOutlet() != null) {
                    if (tglbtn_OffOn3.isChecked()) {
                        UDPManager.getInstance().sendPacket(new ControlRecord("on3"),
                                smartOutletManager.getActiveSmartOutlet().getIpAddress());
                    } else {
                        UDPManager.getInstance().sendPacket(new ControlRecord("off3"),
                                smartOutletManager.getActiveSmartOutlet().getIpAddress());
                    }
                }
            }
        });

        tglbtn_OffOn4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(smartOutletManager.getActiveSmartOutlet() != null) {
                    if (tglbtn_OffOn4.isChecked()) {
                        UDPManager.getInstance().sendPacket(new ControlRecord("on4"),
                                smartOutletManager.getActiveSmartOutlet().getIpAddress());
                    } else {
                        UDPManager.getInstance().sendPacket(new ControlRecord("off4"),
                                smartOutletManager.getActiveSmartOutlet().getIpAddress());
                    }
                }
            }
        });

        btn_More1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<PowerRecord> records = smartOutletManager.getRecordsInRange(
                        DateManager.getTodayMidnightSeconds(),DateManager.getNowSeconds());


                if(records.size() > 0) {
                    ArrayList<String> Y_Values = new ArrayList<>();
                    ArrayList<String> X_Values = new ArrayList<>();

                    for(int i = 0; i<records.size(); i++) {
                        Y_Values.add(records.get(i).getPower1()+"");
                        X_Values.add(records.get(i).getRecordTime().getMilitaryTime());
                    }

                    DataWrapper wrapper1 = new DataWrapper(X_Values);
                    DataWrapper wrapper2 = new DataWrapper(Y_Values);

                    Intent intent = new Intent(getActivity(), Graph.class);
                    intent.putExtra(Constants.X_VALUES, wrapper1);
                    intent.putExtra(Constants.Y_VALUES, wrapper2);
                    startActivity(intent);
                }
                else
                {
                    //Display to the user that there are not data to display
                    Toast toast = Toast.makeText(getActivity(), "No Data to Display", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        btn_More2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<PowerRecord> records = smartOutletManager.getRecordsInRange(
                        DateManager.getTodayMidnightSeconds(),DateManager.getNowSeconds());


                if(records.size() > 0) {
                    ArrayList<String> Y_Values = new ArrayList<>();
                    ArrayList<String> X_Values = new ArrayList<>();

                    for(int i = 0; i<records.size(); i++) {
                        Y_Values.add(records.get(i).getPower2()+"");
                        X_Values.add(records.get(i).getRecordTime().getMilitaryTime());
                    }

                    DataWrapper wrapper1 = new DataWrapper(X_Values);
                    DataWrapper wrapper2 = new DataWrapper(Y_Values);

                    Intent intent = new Intent(getActivity(), Graph.class);
                    intent.putExtra(Constants.X_VALUES, wrapper1);
                    intent.putExtra(Constants.Y_VALUES, wrapper2);
                    startActivity(intent);
                }
                else
                {
                    //Display to the user that there are not data to display
                    Toast toast = Toast.makeText(getActivity(), "No Data to Display", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        btn_More3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<PowerRecord> records = smartOutletManager.getRecordsInRange(
                        DateManager.getTodayMidnightSeconds(),DateManager.getNowSeconds());


                if(records.size() > 0) {
                    ArrayList<String> Y_Values = new ArrayList<>();
                    ArrayList<String> X_Values = new ArrayList<>();

                    for(int i = 0; i<records.size(); i++) {
                        Y_Values.add(records.get(i).getPower3()+"");
                        X_Values.add(records.get(i).getRecordTime().getMilitaryTime());
                    }

                    DataWrapper wrapper1 = new DataWrapper(X_Values);
                    DataWrapper wrapper2 = new DataWrapper(Y_Values);

                    Intent intent = new Intent(getActivity(), Graph.class);
                    intent.putExtra(Constants.X_VALUES, wrapper1);
                    intent.putExtra(Constants.Y_VALUES, wrapper2);
                    startActivity(intent);
                }
                else
                {
                    //Display to the user that there are not data to display
                    Toast toast = Toast.makeText(getActivity(), "No Data to Display", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        btn_More4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<PowerRecord> records = smartOutletManager.getRecordsInRange(
                        DateManager.getTodayMidnightSeconds(),DateManager.getNowSeconds());


                if(records.size() > 0) {
                    ArrayList<String> Y_Values = new ArrayList<>();
                    ArrayList<String> X_Values = new ArrayList<>();

                    for(int i = 0; i<records.size(); i++) {
                        Y_Values.add(records.get(i).getPower4()+"");
                        X_Values.add(records.get(i).getRecordTime().getMilitaryTime());
                    }

                    DataWrapper wrapper1 = new DataWrapper(X_Values);
                    DataWrapper wrapper2 = new DataWrapper(Y_Values);

                    Intent intent = new Intent(getActivity(), Graph.class);
                    intent.putExtra(Constants.X_VALUES, wrapper1);
                    intent.putExtra(Constants.Y_VALUES, wrapper2);
                    startActivity(intent);
                }
                else
                {
                    //Display to the user that there are not data to display
                    Toast toast = Toast.makeText(getActivity(), "No Data to Display", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        if(smartOutletManager.getActiveSmartOutlet() != null) {
            updateSmartOutletTitle(smartOutletManager.getActiveSmartOutlet().getNickname());
            updateCost();
        }
        else{
            updateSmartOutletTitle("--");
        }
    }

    /**
     * This class is called by SmartOutletManager when it receives the power record from the
     * smart outlet. It calls this function to update the power on the screen.
     * @param record
     */
    public void updatePowerRecords(PowerRecord record){
        tv_power1.setText(String.format("%.2f",record.getPower1())  +" w" );
        tv_power2.setText(String.format("%.2f",record.getPower2())  +" w" );
        tv_power3.setText(String.format("%.2f",record.getPower3())  +" w" );
        tv_power4.setText(String.format("%.2f",record.getPower4())  +" w" );

        updateCost();
    }

    /**
     * This class is called by SmartOutletManager when it receives the power record from the
     * smart outlet. It calls this function to update the power on the screen.
     * @param record
     */
    public void updateStatus(StatusRecord record){
        tglbtn_OffOn1.setChecked(record.getStatus1());
        tglbtn_OffOn2.setChecked(record.getStatus2());
        tglbtn_OffOn3.setChecked(record.getStatus3());
        tglbtn_OffOn4.setChecked(record.getStatus4());
    }

    /**
     * This is used to update the smart outlet title.
     * @param smartOutletName
     */
    public void updateSmartOutletTitle(String smartOutletName){
        showActiveTV.setText(smartOutletName);
    }

    /**
     * This updates the cost.
     */
    public void updateCost(){
        tv_cost.setText(String.format("Average Cost Today: \n" +
                 "%.2f $",smartOutletManager.getAverageCostToday()));
    }
}
