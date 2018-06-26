package com.linuxluigi.opendecibelmeter.utli;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GravatarUserImage extends AsyncTask<String, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private ImageView bmImage;

    public GravatarUserImage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... emails) {
        // http://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d59?d=mm

        String email = emails[0];
        String hash = md5Hex(email);

        String gravatarUrl = "https://www.gravatar.com/avatar/" + hash + "?d=mm";

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(gravatarUrl).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }

    private String hex(byte[] array) {
        // source: https://de.gravatar.com/site/implement/images/java/
        StringBuilder sb = new StringBuilder();
        for (byte anArray : array) {
            sb.append(Integer.toHexString((anArray
                    & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    private String md5Hex(String message) {
        // source: https://de.gravatar.com/site/implement/images/java/
        try {
            MessageDigest md =
                    MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }
        return null;
    }
}
