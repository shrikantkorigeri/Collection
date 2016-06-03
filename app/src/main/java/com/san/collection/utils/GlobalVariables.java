package com.san.collection.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SANTECH on 16/02/2016.
 */
public class GlobalVariables {

    public static String serverurl ="http://192.168.1.2:6060/chowmein/";
    public static String serverurl1 ="http://192.168.0.102/chowmeintest/";
    public static String chowmeinkpurl ="http://106.51.7.220:8080/chowmein/";
    public static String chowmeinbel ="http://122.166.204.14:6060/chowmein/";

    public static String getcurrentdate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currentDate = String.valueOf(dateFormat.format(date));
        return currentDate;
    }
}
