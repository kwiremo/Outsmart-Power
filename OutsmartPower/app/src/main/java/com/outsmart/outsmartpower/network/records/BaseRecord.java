package com.outsmart.outsmartpower.network.records;

import org.json.JSONObject;

/**
 * Created by Rene Moise on 3/4/2017.
 */

/**
 * This is a baseRecord class. It implements the record interface and defines one field: Data.
 * This is the data that will be sent to the smart-outlet. Also, It does not give any implementation
 * to the toJSONString that it implements form the interface. Child classes will have to provide
 * implementations.
 */
public class BaseRecord implements RecordInterface {

    JSONObject data;
    int smartOutletID;

    public BaseRecord() {
        data = new JSONObject();
    }

    @Override
    public String toJSONString() {
        return null;
    }

    public int getSmartOutletID() {
        return smartOutletID;
    }
}
