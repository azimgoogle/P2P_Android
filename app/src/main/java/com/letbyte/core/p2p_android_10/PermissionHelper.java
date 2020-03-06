package com.letbyte.core.p2p_android_10;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-12-30 at 11:07 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: meshsdk.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-12-30 at 11:07 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-12-30 at 11:07 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class PermissionHelper {

    /**
     * @param context
     * @param permissions
     * @return true if provided list empty or all item has granted permissions
     */
    public boolean hasPermissions(Context context, List<String> permissions) {
        List<String> list = getNotGrantedPermissions(context, permissions);
        return list != null && list.size() > 0;
    }

    public List<String> getNotGrantedPermissions(Context context, List<String> permissions) {

        if(permissions != null && permissions.size() > 0) {

            List<String> permissionsNotGranted = new ArrayList<>();

            for(String permission : permissions) {

                if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.
                        PERMISSION_GRANTED) {

                    permissionsNotGranted.add(permission);
                }
            }

            return permissionsNotGranted;

        }

        return null;

    }

    public List<String> getShouldShowPermissions(Activity activity, List<String> permissions) {

        if(permissions != null && permissions.size() > 0) {

            List<String> permissionsNotGranted = new ArrayList<>();

            for(String permission : permissions) {

                if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

                    permissionsNotGranted.add(permission);
                }
            }

            return permissionsNotGranted;

        }

        return null;
    }

    public boolean isLocationProviderEnabled(Context context) {
        if(context == null) {
            return false;
        }

        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
