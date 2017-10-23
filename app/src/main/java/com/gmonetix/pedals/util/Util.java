package com.gmonetix.pedals.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.gmonetix.pedals.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.io.ByteArrayOutputStream;

/**
 * @author Gmonetix
 */

public class Util {

    public static byte[] getByteArray(Bitmap bmp, int quality){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    public static boolean isGoogleServicesAvailable(Context context) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(context);
        return code == ConnectionResult.SUCCESS ;
    }

    public static void goToGooglePlay(Context context, String id) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + id)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + id)));
        }
    }

    public static void openUrl(Context context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
    }

    public static void sendEmail(Context context, String name, String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query from Pedals by : " + name);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Write your query here");
        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean validateEmail(String email) {
        return !(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.length() < 8 || email.length() > 50);
    }

    public static boolean validatePhone(String phone) {
        return !(phone.isEmpty() || phone.length() != 10 || !phone.matches("[0-9]*"));
    }

    public static boolean validatePassword(String password) {
        return !(password.isEmpty() || password.length() < 6 || password.length() > 100);
    }

    public static void shareApp(Context context){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Pedals");
            String sAux = "\n" + context.getString(R.string.share_app_text) + "\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.gmonetix.pedals";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            context.startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            Log.e("ERROR",""+e.getMessage());
        }
    }

}
