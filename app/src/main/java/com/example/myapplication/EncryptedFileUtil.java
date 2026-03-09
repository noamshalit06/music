package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptedFileUtil {
    public static String readData(Context context, String filename) {
        try {
            // Read the content of the file
            FileInputStream fin = context.openFileInput(filename);
            int a;
            StringBuilder temp = new StringBuilder();
            while ((a = fin.read()) != -1) {
                temp.append((char) a);
            }

            fin.close();
            String file_contents = temp.toString();
            return JniCallNative.Decrypt(file_contents);
        } catch (IOException e) {
            Log.e("Error", "ReadDataError");
            return "";
        }
    }

    public static void writeData(Context context, String data, String filename) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            String encrypted_data = JniCallNative.Encrypt(data);
            fos.write(encrypted_data.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e)
        {
            Log.e("Error", "WriteDataError");
        }
    }
}
