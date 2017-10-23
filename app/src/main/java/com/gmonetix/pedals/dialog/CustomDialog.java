package com.gmonetix.pedals.dialog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.util.Const;
import com.gmonetix.pedals.util.SharedPref;
import com.gmonetix.pedals.util.Util;

/**
 * @author Gmonetix
 */

public class CustomDialog {

    public static MaterialDialog privacyPolicy(Context context){
        return new MaterialDialog.Builder(context)
                .title("Privacy Policy")
                .content(context.getString(R.string.privacy_policy))
                .positiveText("OK")
                .build();
    }

    public static MaterialDialog termsAndConditions(Context context){
        return new MaterialDialog.Builder(context)
                .title("Terms & Conditions")
                .content(context.getString(R.string.terms_and_conditions))
                .positiveText("OK")
                .build();
    }

    public static void showTextDialog(Context context, String title, String content) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .canceledOnTouchOutside(false)
                .positiveText("OK")
                .build().show();
    }

    public static void showContactUsDialog(final Context context, final SharedPref sharedPref) {
        new MaterialDialog.Builder(context)
                .title("Contact Us")
                .items(R.array.contact_us)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + sharedPref.getAdminPhone()));
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(context, "permission not granted", Toast.LENGTH_SHORT).show();
                                } else context.startActivity(callIntent);
                                break;
                            case 1:
                                Util.sendEmail(context,sharedPref.getName(),sharedPref.getAdminEmail());
                                break;
                            case 2:
                                Util.openUrl(context,Const.PEDALS_WEBSITE);
                                break;
                            case 3:
                                Util.openUrl(context, Const.PEDALS_FB_PAGE);
                                break;
                        }
                    }
                })
                .show();
    }

}
