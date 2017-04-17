package com.outsmart.outsmartpower.records;

import com.outsmart.outsmartpower.Support.Constants;

/**
 * Created by Rene Moise on 3/30/2017.
 */

/**
 * The control record is created and sent when the on/off button are clicked. The packet toggles
 * the smart outlets. The smart outlet respond with another control record signifying whether it
 * successfully toggled the outlet.
 */
public class ControlRecord extends BaseRecord {

    //This field contains what outlet to control.
    private String outletToTurnOff;
    public ControlRecord(String outletToTurnOff) {
        this.outletToTurnOff = outletToTurnOff;
    }

    @Override
    public String toJSONString() {
        try{
            data.put(Constants.TYPE_LABEL, Constants.CONT_RECORD);
            data.put(Constants.OUTLET_TO_TOGGLE_LABEL,outletToTurnOff);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return data.toString();
    }
}
