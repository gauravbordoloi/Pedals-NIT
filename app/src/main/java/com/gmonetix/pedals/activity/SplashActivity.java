package com.gmonetix.pedals.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gmonetix.pedals.*;
import com.gmonetix.pedals.util.Const;
import com.gmonetix.pedals.util.NetworkConnectionUtil;
import com.gmonetix.pedals.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * @author Gmonetix
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(com.gmonetix.pedals.R.layout.activity_splash);

        if (!Util.isGoogleServicesAvailable(this)) {
            MaterialDialog dialog = new MaterialDialog.Builder(SplashActivity.this)
                    .title("Alert")
                    .content("Google Play Services not available ! Sorry to inform that the app won't run without google play services.")
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            SplashActivity.this.finish();
                        }
                    })
                    .build();
            dialog.show();
        } else {
            if (NetworkConnectionUtil.isConnectedToInternet(this)) {
                App.getDbReference().child(Const.SERVER).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        if (value != null && value.equals("ON")) {
                            startActivity(new Intent(SplashActivity.this,PermissionRequest.class));
                            SplashActivity.this.finish();
                        } else {
                            //server down
                            MaterialDialog dialog = new MaterialDialog.Builder(SplashActivity.this)
                                    .title("Alert")
                                    .content("Server under work ! Please visit later")
                                    .canceledOnTouchOutside(false)
                                    .cancelable(false)
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            SplashActivity.this.finish();
                                        }
                                    })
                                    .build();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        MaterialDialog dialog = new MaterialDialog.Builder(SplashActivity.this)
                                .title("Alert")
                                .content("Some error occurred !\n Error - " + error.getMessage())
                                .canceledOnTouchOutside(false)
                                .cancelable(false)
                                .positiveText("RETRY")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        SplashActivity.this.recreate();
                                    }
                                })
                                .build();
                        dialog.show();
                    }
                });
            } else {
                MaterialDialog dialog = new MaterialDialog.Builder(SplashActivity.this)
                        .title("Alert")
                        .content("No internet connection detected !")
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .positiveText("RETRY")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SplashActivity.this.recreate();
                            }
                        })
                        .build();
                dialog.show();
            }
        }

    }

}
