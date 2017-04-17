package com.outsmart.outsmartpower.helperclasses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rene Moise on 11/2/2016.
 */

/**
 * This class was created to be able to pass data from the mainActivity to the graph as one object.
 * When I used this approach I did not know some of the issues that this would create. I describe
 * issues in the graph class. And I propose a way to solve the issue.
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

