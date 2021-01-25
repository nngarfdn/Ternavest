package com.example.ternavest.utils;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.ternavest.utils.AppUtils.locale;

public class DateUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";

    public static String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentTime(){
        DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, locale);
        Date time = new Date();
        return timeFormat.format(time);
    }

    public static String getFullDate(String date, boolean isSimple){
        if (isValidDateFormat(date)){
            int[] arrayDate = getArrayDate(date);
            String month = new DateFormatSymbols().getMonths()[(arrayDate[1])];
            if (isSimple) month = month.substring(0,3);
            return arrayDate[2] + " " + month + " " + arrayDate[0];
        } else return "-1";
    }

    // Konversi tanggal ke array
    public static int[] getArrayDate(String date){
        if (isValidDateFormat(date)){
            String[] stringArrayDate = date.split("-");
            int[] integerArrayDate = new int[3];
            for (int i = 0; i < 3; i++) integerArrayDate[i] = Integer.parseInt(stringArrayDate[i]);
            // Karena bulan di mulai dari 0, jadi dikurangi 1
            return new int[] {integerArrayDate[0], integerArrayDate[1]-1, integerArrayDate[2]};
        } else return null;
    }

    // Konversi waktu ke array
    public static int[] getArrayTime(String time){
        if (isValidTimeFormat(time)){
            String[] stringArrayTime = time.split(":");
            int[] integerArrayTime = new int[2];
            for (int i = 0; i < 2; i++) integerArrayTime[i] = Integer.parseInt(stringArrayTime[i]);
            return new int[] {integerArrayTime[0], integerArrayTime[1]};
        } else return null;
    }

    // Mencari selisih antara 2 tanggal
    public static int differenceOfDates(String newerDate, String olderDate){
        if (isValidDateFormat(newerDate) && isValidDateFormat(olderDate)){
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
                Date finalDate = dateFormat.parse(newerDate);
                Date currentDate = dateFormat.parse(olderDate);

                // Bagi 100rb karena aslinya long
                double difference = (double) ((finalDate.getTime()-currentDate.getTime())/100000);
                return (int) ((difference / (24*60*60*1000))*100000);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }
        return -1;
    }

    /*
     * Fungsi private
     * */

    private static boolean isValidDateFormat(String date){
        if (date != null){
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
                dateFormat.parse(date);
                return true;
            } catch (ParseException e) {
                return false;
            }
        } else return false;
    }

    private static boolean isValidTimeFormat(String time){
        if (time != null){
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, locale);
                timeFormat.parse(time);
                return true;
            } catch (ParseException e) {
                return false;
            }
        } else return false;
    }
}
