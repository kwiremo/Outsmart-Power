package com.outsmart.outsmartpower;

/**
 * Created by Rene Moise on 1/28/2017.
 */
public class GraphManager {
    private static GraphManager ourInstance = new GraphManager();
    public static GraphManager getInstance() {
        return ourInstance;
    }

    private GraphManager() {
    }
}
