package com.outsmart.outsmartpower;

import com.outsmart.outsmartpower.managers.DateManager;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Rene Moise on 1/28/2017.
 */
public class DateManagerTest {
    @Test
    public void getTodayMilliseconds() throws Exception {
       int result =  DateManager.getNowSeconds();
        int expected = 1485626663;
        assertTrue(result > expected);

        System.out.println((result%86400)+"");
        System.out.println(result);
        int midnight = result - (result%86400);
        DateManager date = new DateManager(DateManager.getThiWeekSeconds());
        System.out.println(date.getStandardTime());
        System.out.println(date.getDate(DATE_FORMAT.dayFirst));

        //UDPClient.getInstance().execute("Packet");
       // Log.e("TIME", ;

    }
}