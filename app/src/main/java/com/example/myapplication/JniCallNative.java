package com.example.myapplication;

public class JniCallNative {
    public native String Decrypt(String plaintext);
    public native String Encrypt(String plaintext);
}
