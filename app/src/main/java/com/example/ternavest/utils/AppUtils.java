package com.example.ternavest.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ternavest.R;
import com.squareup.picasso.Picasso;

public class AppUtils {
    public static final String VERIF_APPROVED = "setuju";
    public static final String VERIF_REJECT = "tolak";
    public static final String VERIF_PENDING = "pending";

    public static final String LEVEL_PETERNAK = "peternak";
    public static final String LEVEL_INVESTOR = "investor";

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void loadImageFromUrl(ImageView imageView, String url){
        if (url.isEmpty()) url = "null";
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                //.placeholder(R.drawable.image_empty)
                .into(imageView);
    }
}
