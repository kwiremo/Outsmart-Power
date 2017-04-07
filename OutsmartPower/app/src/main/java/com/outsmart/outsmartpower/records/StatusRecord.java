package com.outsmart.outsmartpower.records;

/**
 * Created by Rene Moise on 3/30/2017.
 */

import com.outsmart.outsmartpower.Support.Constants;

import org.json.JSONObject;

/**
 * Status Record is received form the smart-outlet but it is never sent to it. At least for now!
 * This will be used to extract statuses received from the smart-outlet. Then this record will be
 * sent to the mainPage to update the UI.
 */
public class StatusRecord extends BaseRecord{

    Boolean status1, status2, status3, status4;
    public StatusRecord(String JSONString) {
        processString(JSONString);
    }

    private void processString(String JSONString){
        try {
            JSONObject json = new JSONObject(JSONString);


            if (Integer.parseInt(json.getString("s1")) == 1)
                status1 = true;
            else
                status1 = false;

            if (Integer.parseInt(json.getString("s2")) == 1)
                status2 = true;
            else
                status2 = false;

            if (Integer.parseInt(json.getString("s3")) == 1)
                status3 = true;
            else
                status3 = false;

            if (Integer.parseInt(json.getString("s4")) == 1)
                status4 = true;
            else
                status4 = false;

            smartOutletID = (json.getString(Constants.ID_CONTENT));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Boolean getStatus1() {
        return status1;
    }

    public Boolean getStatus2() {
        return status2;
    }

    public Boolean getStatus3() {
        return status3;
    }

    public Boolean getStatus4() {
        return status4;
    }
}
