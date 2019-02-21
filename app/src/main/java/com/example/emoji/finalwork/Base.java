package com.example.emoji.finalwork;

import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
public class Base {
    public static String getBase64OfFile(InputStream file){
        byte[] data = null;
        InputStream in = null;
        try{
            in = new BufferedInputStream(file);
            data = new byte[in.available()];
            in.read(data);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
}
