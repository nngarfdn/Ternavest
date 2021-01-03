package com.example.ternavest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                //.placeholder(R.drawable.image_empty)
                .into(imageView);
    }

    public static byte[] convertUriToByteArray(Context context, Uri uri){
        ByteArrayOutputStream stream = null;

        try {
            // Convert uri to bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

            // Convert bitmap to byte[]
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return stream.toByteArray();
    }

    public static byte[] getCompressedByteArray(byte[] image, boolean isResized){
        ByteArrayOutputStream stream = null;

        try {
            // Convert byte[] to bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(image));

            if (isResized){
                // Change bitmap size
                if (!(bitmap.getWidth() <= 1024)){
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1024,
                            (int) (bitmap.getHeight() * (1024.0/bitmap.getWidth())), true);
                }
            }

            // Compress bitmap and get byte[]
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return stream.toByteArray();
    }
}
