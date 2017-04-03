package com.outsmart.outsmartpower.managers;

import com.outsmart.outsmartpower.DATE_FORMAT;
import com.outsmart.outsmartpower.Support.Constants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Rene Moise Kwibuka
 *
 * Name: DateManager Class
 *
 * Description: TODO describe this class
 */

/*
 * Date formats that we will be using  in this project..
 * "dd/MM/yyyy"     day first.
 * "MM/dd/yyyy"     month first.
 * h:mm a           regular time.
 * HH:mm            military time.
 */
public class DateManager extends Timestamp{



    private long milliseconds;
    private int dataRecordedSeconds;

    public DateManager(int seconds)
    {
        super(seconds*1000);
        setDataRecordedSeconds(seconds);
        setMilliseconds(((long)seconds)*1000);
    }

    public void setDataRecordedSeconds(int dataRecordedSeconds) {
        this.dataRecordedSeconds = dataRecordedSeconds;
    }

    public int getDataRecordedSeconds(){return dataRecordedSeconds;}

    public long getDataRecordedMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public String  getMilitaryTime()
    {
        return getFormattedDateOrTime("HH:mm");
    }

    public String getStandardTime()
    {
        return getFormattedDateOrTime("h:mm a");
    }

    public String getDate(DATE_FORMAT date_format)
    {
        if(date_format == DATE_FORMAT.dayFirst)
            return getFormattedDateOrTime("dd/MM/yyyy" );
        else if (date_format == DATE_FORMAT.monthFirst)
            return getFormattedDateOrTime("MM/dd/yyyy" );
        else
            return null;
    }

    private String getFormattedDateOrTime(String format)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

    public static int getNowSeconds()
    {
        return (int)(System.currentTimeMillis()/1000);
    }

    /**
     * This function returns the time since 1970 to midnight today. The time is shifted 6 hours forward
     * because apparently they started counting seconds at 6:00PM. Shifting it will make it today's
     * milliseconds at midnight.
     *
     */
    public static int getTodayMidnightSeconds(){
        //Modulus 86400 is getting seconds past to midnight today.
        int midnightSeconds = getNowSeconds() - (getNowSeconds()% Constants.DAY_SECONDS);

        //Add 6 hours to make it 12:00 Am
        return (midnightSeconds + (6*3600));
    }

    /**
     *
     * This returns the time elapsed since 1970 to 7 days ago. We get today midnight seconds and
     * substract 7 days' seconds.
     */
    public static int getThiWeekSeconds(){
        return getTodayMidnightSeconds() - (Constants.WEEK_DAYS * Constants.DAY_SECONDS);
    }

    public static int getThirtDays() {return getTodayMidnightSeconds() - (Constants.MONTH_DAYS*Constants.DAY_SECONDS);}
}
