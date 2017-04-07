package com.outsmart.outsmartpower.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.outsmart.outsmartpower.SmartOutlet;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.Support.ParentActivity;
import com.outsmart.outsmartpower.managers.SmartOutletManager;
import com.outsmart.outsmartpower.network.UDPManager;
import com.outsmart.outsmartpower.records.ControlRecord;
import com.outsmart.outsmartpower.records.PowerRecord;
import com.outsmart.outsmartpower.records.StatusRecord;

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
    private TextView tv_power1, tv_power2, tv_power3, tv_power4;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //SmartOutletManager.getInstance().addObserver(MainPageUI.getInstance());
        //Initialize the showActiveSmartoutlet
        showActiveTV = (TextView) getActivity().findViewById(R.id.deviceNameTV);
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

        btn_More1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UDPManager.getInstance().sendPacket(new ControlRecord("on4"),
                        smartOutletManager.getActiveSmartOutlet().getIpAddress());
            }
        });

        //TODO: Find a way to send packets to this fragment.
        //smartOutletManager.addObserver(myInstance);
    }

    public class DisplayPowerFragmentObserver implements Observer{



        @Override
        public void update(Observable observable, Object o) {

            if(observable.getClass().equals(SmartOutletManager.class)){

                if(o.getClass() != null) {
                    if (o.getClass().equals(SmartOutlet.class)) {
                        showActiveTV.setText(((SmartOutlet) o).getNickname());
                    } else if (o.getClass().equals(StatusRecord.class)) {
                        tglbtn_OffOn1.setChecked(((StatusRecord) o).getStatus1());
                        tglbtn_OffOn2.setChecked(((StatusRecord) o).getStatus2());
                        tglbtn_OffOn3.setChecked(((StatusRecord) o).getStatus3());
                        tglbtn_OffOn4.setChecked(((StatusRecord) o).getStatus4());
                    } else if (o.getClass() == PowerRecord.class){
                        PowerRecord record = (PowerRecord)o;
                        tv_power1.setText(record.getPower1()+"");
                        tv_power1.setText(record.getPower2()+"");
                        tv_power1.setText(record.getPower3()+"");
                        tv_power1.setText(record.getPower4()+"");
                    }
                }
            }
        }
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
}
