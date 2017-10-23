package com.gmonetix.pedals.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.util.RuntimePermission;

/**
 * @author Gmonetix
 */

public class PermissionRequest extends RuntimePermission {

    private static final int REQUEST_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_NETWORK_STATE},R.string.permission,REQUEST_PERMISSION);
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        startActivity(new Intent(PermissionRequest.this,LoginActivity.class));
        finish();
    }

}
