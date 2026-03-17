package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class DialogsUtil {
    public static void showVibrationPermissionExplanationDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("This app needs vibration permissions")
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.VIBRATE},
                                PermissionsUtil.VIBRATE_PERMISSION);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showReadingAudioPermissionExplanationDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("This app needs audio access")
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                                PermissionsUtil.READ_MEDIA_AUDIO_PERMISSION);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
