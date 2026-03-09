package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Callable;

public class PermissionsUtil {
    public static final int READ_MEDIA_AUDIO_PERMISSION = 1;
    public static final int VIBRATE_PERMISSION = 2;

    public static void handlePermissionsResult(Activity activity, int requestCode, String[] permissions,
                                           int[] grantResults, Callable<Void> runOnPermissionGranted) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("permissions", "Got permission number " + Integer.toString(requestCode));
            try {
                runOnPermissionGranted.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void RequestReadingAudioPermissions(Activity activity, Callable<Void> runOnPermissionGranted) {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_MEDIA_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("permissions", "has READ_MEDIA_AUDIO permission");
            try {
                runOnPermissionGranted.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.READ_MEDIA_AUDIO)) {
            DialogsUtil.showReadingAudioPermissionExplanationDialog(activity);
        }
        else {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.READ_MEDIA_AUDIO },
                    READ_MEDIA_AUDIO_PERMISSION);
            Log.d("permissions", "request READ_MEDIA_AUDIO permission");
        }
    }



    public static void RequestVibratePermissions(Activity activity, Callable<Void> runOnPermissionGranted) {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.VIBRATE) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("permissions", "has VIBRATE permission");
            try {
                runOnPermissionGranted.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.VIBRATE)) {
            DialogsUtil.showVibrationPermissionExplanationDialog(activity);
        }
        else {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.VIBRATE },
                    VIBRATE_PERMISSION);
            Log.d("permissions", "request VIBRATE permission");
        }
    }


}
