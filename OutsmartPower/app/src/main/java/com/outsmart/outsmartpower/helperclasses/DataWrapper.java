package com.outsmart.outsmartpower.helperclasses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rene Moise on 11/2/2016.
 */


public class DataWrapper implements Serializable {

    private ArrayList<String> dataToDisplay;

    public DataWrapper(ArrayList<String> data) {
        this.dataToDisplay = data;
    }

    public ArrayList<String> getData() {
        return this.dataToDisplay;
    }
}

