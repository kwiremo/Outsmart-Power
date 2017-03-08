package com.outsmart.outsmartpower.network.records;

import org.json.JSONObject;

/**
 * Created by Rene Moise on 3/4/2017.
 */

public class Record implements IRecord {

    JSONObject data;

    public Record() {
        data = new JSONObject();
    }

    @Override
    public String toJSONString() {
        return null;
    }
}
