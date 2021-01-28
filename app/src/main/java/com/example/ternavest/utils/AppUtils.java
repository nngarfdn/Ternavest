package com.example.ternavest.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ternavest.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtils {
    public static final String VERIF_APPROVED = "setuju";
    public static final String VERIF_REJECT = "tolak";
    public static final String VERIF_PENDING = "pending";

    public static final String LEVEL_PETERNAK = "peternak";
    public static final String LEVEL_INVESTOR = "investor";

    public static final String PAY_APPROVED = "setuju";
    public static final String PAY_REJECT = "tolak";
    public static final String PAY_PENDING = "pending";

    public static Locale locale = new Locale("in", "ID");

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getRupiahFormat(long amount){
        String country = "ID", language = "in";
        return "Rp" + NumberFormat.getNumberInstance(new Locale(language, country)).format(amount);
    }

    public static String getRupiahFormat(double amount){
        String country = "ID", language = "in";
        return "Rp" + NumberFormat.getNumberInstance(new Locale(language, country)).format(amount);
    }

    public static boolean isValidPhone(String number){
        if (number.length() < 2) return false;
        else return number.charAt(0) == '6' && number.charAt(1) == '2';
    }

    public static void loadImageFromUrl(ImageView imageView, String url){
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.load_image)
                .error(R.drawable.upload)
                .into(imageView);
    }

    public static void loadProfilePicFromUrl(ImageView imageView, String url){
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_no_profile_pic)
                .error(R.drawable.ic_no_profile_pic)
                .into(imageView);
    }

    public static String createIdFromCurrentDate(){
        String currentDate, currentTime;

        DateFormat timeFormat = new SimpleDateFormat("HHmmssSSS", locale);
        Date time = new Date();
        currentTime = timeFormat.format(time);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", locale);
        Date date = new Date();
        currentDate = dateFormat.format(date);

        return currentDate + currentTime;
    }
}
