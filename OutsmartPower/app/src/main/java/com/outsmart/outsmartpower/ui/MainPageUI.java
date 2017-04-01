package com.outsmart.outsmartpower.ui;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.outsmart.outsmartpower.OutsmartDeviceInfo;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.SmartOutlet;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.managers.UDPManager;
import com.outsmart.outsmartpower.network.records.ControlRecord;
import com.outsmart.outsmartpower.network.records.EchoRequestRecord;
import com.outsmart.outsmartpower.network.records.StatusRecord;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rene Moise on 3/29/2017.
 */
public class MainPageUI implements Observer {


    private Activity parentActivity;

    private static final MainPageUI ourInstance = new MainPageUI();

    //Show the active smart Outlet name.
    private TextView showActiveTV;
    //DEFINE BUTTONS
    //On and aff toggle_buttons
    private ToggleButton tglbtn_OffOn1, tglbtn_OffOn2, tglbtn_OffOn3, tglbtn_OffOn4;

    //DEFINE TEXT VIEWS
    private TextView tv_power1, tv_power2, tv_power3, tv_power4;

    //Learn more buttons
    private Button btn_More1, btn_More2, btn_More3, btn_More4;

    //It has a reference to the OutsmartManager. It will be used to know what smart-outlet is
    //active at the moment. That is the device that will be sent the information.
    SmartOutletManager smartOutletManager;

    private MainPageUI() {

        parentActivity = ParentActivity.getParentActivity();

        //Initialize smartOutlet Manager
        smartOutletManager = SmartOutletManager.getInstance();

        //Initialize the showActiveSmartoutlet
        showActiveTV = (TextView) parentActivity.findViewById(R.id.deviceNameTV);
        //DEFINE BUTTON INSTANCES

        //Toggle buttons
        tglbtn_OffOn1 = (ToggleButton) ParentActivity.getParentActivity().findViewById(R.id.tglbtn_OffOn1);
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
                if(tglbtn_OffOn1.isChecked()) {
                    UDPManager.getInstance().sendPacket(new ControlRecord("on1"),
                            smartOutletManager.getActiveSmartOutlet().getIpAddress());
                }
                else{
                    UDPManager.getInstance().sendPacket(new ControlRecord("off1"),
                            smartOutletManager.getActiveSmartOutlet().getIpAddress());
                }

            }
        });

        tglbtn_OffOn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(tglbtn_OffOn2.isChecked()) {
                    UDPManager.getInstance().sendPacket(new ControlRecord("on2"),
                            smartOutletManager.getActiveSmartOutlet().getIpAddress());
                }
                else{
                    UDPManager.getInstance().sendPacket(new ControlRecord("off2"),
                            smartOutletManager.getActiveSmartOutlet().getIpAddress());
                }
            }
        });

        tglbtn_OffOn3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(tglbtn_OffOn3.isChecked()) {
                    UDPManager.getInstance().sendPacket(new ControlRecord("on3"),
                            smartOutletManager.getActiveSmartOutlet().getIpAddress());
                }
                else{
                    UDPManager.getInstance().sendPacket(new ControlRecord("off3"),
                            smartOutletManager.getActiveSmartOutlet().getIpAddress());
                }

            }
        });

        tglbtn_OffOn4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(tglbtn_OffOn1.isChecked()) {
                    UDPManager.getInstance().sendPacket(new ControlRecord("on4"),
                            smartOutletManager.getActiveSmartOutlet().getIpAddress());
                }
                else{
                    UDPManager.getInstance().sendPacket(new ControlRecord("off4"),
                            smartOutletManager.getActiveSmartOutlet().getIpAddress());
                }

            }
        });

//        if(smartOutletManager.isSmart_OutletConnected() &&
//                smartOutletManager.getActiveSmartOutlet() != null)
//            setEnabled(true);
//        else
//            setEnabled(false);
    }

    public void setEnabled(boolean isClickable){
        //Toggle buttons
        tglbtn_OffOn1.setEnabled(isClickable);
        tglbtn_OffOn2 .setEnabled(isClickable);
        tglbtn_OffOn3.setEnabled(isClickable);
        tglbtn_OffOn4.setEnabled(isClickable);


        //Learn more buttons
        btn_More1.setEnabled(isClickable);
        btn_More2.setEnabled(isClickable);
        btn_More3.setEnabled(isClickable);
        btn_More4.setEnabled(isClickable);


        //Text INSTANCES
        tv_power1.setEnabled(isClickable);
        tv_power2.setEnabled(isClickable);
        tv_power3.setEnabled(isClickable);
        tv_power4.setEnabled(isClickable);
    }


    //Returns an instance for this singleton class.
    public static MainPageUI getInstance() {
        return ourInstance;
    }

    @Override
    public void update(Observable observable, Object o) {

        if(observable.getClass().equals(SmartOutletManager.class)){
//            if(smartOutletManager.isSmart_OutletConnected() &&
//                    smartOutletManager.getActiveSmartOutlet() != null)
//                setEnabled(true);
//            else
//                setEnabled(false);

            if(observable.getClass().equals(OutsmartDeviceInfo.class)){
                showActiveTV.setText(((OutsmartDeviceInfo) o).getNickname());
            }
            else if(observable.getClass().equals(StatusRecord.class)){
                tglbtn_OffOn1.setChecked(((StatusRecord) o).getStatus1());
                tglbtn_OffOn2.setChecked(((StatusRecord) o).getStatus2());
                tglbtn_OffOn3.setChecked(((StatusRecord) o).getStatus3());
                tglbtn_OffOn4.setChecked(((StatusRecord) o).getStatus4());
            }
        }

    }
}
