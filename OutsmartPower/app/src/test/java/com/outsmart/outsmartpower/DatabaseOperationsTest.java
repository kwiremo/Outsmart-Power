package com.outsmart.outsmartpower;

import com.outsmart.outsmartpower.managers.DateManager;
import com.outsmart.outsmartpower.records.PowerRecord;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Created by Rene Moise on 1/28/2017.
 */
public class DatabaseOperationsTest {
    @Test
    public void getAllRecordsAfter() throws Exception {

        DatabaseOperations Db = DatabaseOperations.getInstance();
        PowerRecord record = new PowerRecord(new DateManager(1485626052),1.1,2.2,3.3,4.4,240.4,222);
        Db.addDataRecord(record);
        List<PowerRecord> records = Db.getAllRecordsInRange(222,DateManager.getTodayMidnightSeconds());
        double expected = 1.1;
        double result = records.get(0).getCurrent_1();
        assertEquals(expected,result);
    }
}