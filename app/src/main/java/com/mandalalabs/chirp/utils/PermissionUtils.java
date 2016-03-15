package com.mandalalabs.chirp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionUtils {
    public static ArrayList<String> getMissingPermissions(Context context, String[] permissions) {
        ArrayList<String> missingPermissions = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permissions[i]);
            }
        }
        return missingPermissions;
    }
}
