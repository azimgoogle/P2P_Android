package com.letbyte.core.p2p_android_10;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * This activity ensure all provided permission from user. Additionally if not configured only to
 * check system permission it ensures Location provider and BT discoverability activeness
 */
public class RequestActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_DSC = 107;
    public static final String PERMISSION_REQUEST = "PERMISSION_REQUEST";
    public static final String PERMISSION_REQUEST_ONLY_SYSTEM_PERMISSION = "PERMISSION_REQUEST_" +
            "ONLY_SYSTEM_PERMISSION";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int REQUEST_SYSTEM_PERMISSIONS = 106;
    public static final int REQUEST_OVERLAY_PERMISSSION = 202;
    public static final String REQUEST_CODE = "request_code";
    private AlertDialog mAlertDialog;
    private PermissionHelper mPermissionHelper = new PermissionHelper();
    private List<String> mRequestedPermissions;
    private boolean mIsSystemPermissionOnly;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        int requestFrom = getIntent().getIntExtra("req", 0);

        Intent intent = getIntent();
        if (intent != null) {
            mRequestedPermissions = intent.getStringArrayListExtra(PERMISSION_REQUEST);
            mIsSystemPermissionOnly = intent.getBooleanExtra(
                    PERMISSION_REQUEST_ONLY_SYSTEM_PERMISSION, false);

            mRequestedPermissions = mPermissionHelper.getNotGrantedPermissions(getApplicationContext(),
                    mRequestedPermissions);

            requestMissingPermissionsWhileRequired();

        }

    }


    public boolean checkAndRequestLocationPermission() {
        if (!mPermissionHelper.isLocationProviderEnabled(getApplicationContext())) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Check and Request System write permission
     *
     * @return permission status
     */
    private boolean checkAndRequestHotspotPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("title");
                    builder.setMessage("Need to allow");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            mAlertDialog.cancel();
                            RequestActivity.this.openSettings();
                        }
                    });
                    mAlertDialog = builder.create();
                    if (!mAlertDialog.isShowing()) {
                        builder.show();
                    }
                    return false;
                }
            }
            return true;
        }
        return true;

    }

    /**
     * Open system write setting
     */
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        String packageName = getPackageName();
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUEST_SYSTEM_PERMISSIONS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            requestMissingPermissionsWhileRequired();
        } else if (requestCode == REQUEST_SYSTEM_PERMISSIONS /*&& checkAndRequestHotspotPermission() && checkAndRequestLocationPermission()*/) {
            requestMissingPermissionsWhileRequired();
        } else if (requestCode == REQUEST_ENABLE_DSC) {
            if (resultCode == 3600) {
                closeCurrentActivity();
            } else {
                closeCurrentActivity();
            }
        }
    }

    private void closeCurrentActivity() {

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                // Check for both permissions
                requestMissingPermissionsWhileRequired();
            }
            break;

            default:
        }
    }

    private void requestMissingPermissionsWhileRequired() {


        if (checkAndRequestLocationPermission()) {

            closeCurrentActivity();
        }
    }

    /**
     * Permission required window
     *
     * @param message    details message
     * @param okListener click listen handler
     */

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("ok", okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * System setting open for permanent denial
     */

    private void openSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings" );
        builder.setMessage("Details");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", RequestActivity.this.getPackageName(), null);
                intent.setData(uri);
                RequestActivity.this.startActivityForResult(intent, REQUEST_SYSTEM_PERMISSIONS);
            }
        });
        builder.show();
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Loc")
                .setMessage("Details")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        RequestActivity.this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                REQUEST_SYSTEM_PERMISSIONS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void openOverlayPermissionActivity() {

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSSION);

    }

    private boolean isOverlayAllow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Window overlay");
            builder.setMessage("This permission is required to use the awesome feature");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    mAlertDialog.cancel();
                    RequestActivity.this.openOverlayPermissionActivity();
                }
            });
            mAlertDialog = builder.create();
            if (!mAlertDialog.isShowing()) {
                builder.show();
            }

            return false;
        }

        return true;
    }

    private void setStatusBarColor() {
        int statusBarColor = Color.BLUE;
        if (statusBarColor > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(statusBarColor);
            }
        }
    }
}
