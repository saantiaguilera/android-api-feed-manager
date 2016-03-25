package com.santiago.feed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ivan on 14/07/15.
 */
public abstract class Utils {

    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

    public static Date dateFromString(String date) throws ParseException {

        if(date==null) return null;

        return DATE_FORMAT.parse(date);

    }
}
