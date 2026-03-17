package com.example.myapplication;

public class JniCallNative {
    static {
        System.loadLibrary("blackbox"); // Must match your CMake add_library name
    }
    public static native String Decrypt(String plaintext);
    public static native String Encrypt(String plaintext);
}
