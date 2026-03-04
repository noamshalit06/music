package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsUtil {
    private static final int READ_MEDIA_AUDIO_PERMISSION = 1;

    public static void handlePermissionsResult(MainActivity activity, int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case READ_MEDIA_AUDIO_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissions", "Got READ_MEDIA_AUDIO permission");
                    activity.readPermissionsGranted();
                }
        }
    }


    private static void showPermissionExplanationDialog(MainActivity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("This app needs audio access")
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                                READ_MEDIA_AUDIO_PERMISSION);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    public static void RequestReadingAudioPermissions(MainActivity activity) {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_MEDIA_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("permissions", "has READ_MEDIA_AUDIO permission");
            activity.readPermissionsGranted();


        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.READ_MEDIA_AUDIO)) {
            showPermissionExplanationDialog(activity);
        }
        else {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.READ_MEDIA_AUDIO },
                    READ_MEDIA_AUDIO_PERMISSION);
            Log.d("permissions", "request READ_MEDIA_AUDIO permission");
        }
    }


}
