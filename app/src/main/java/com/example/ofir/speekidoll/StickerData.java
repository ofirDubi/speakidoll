package com.example.ofir.speekidoll;


import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ofir on 03/03/2018.
 */

public class StickerData {
    static int counter = 0;
    static int last_updated = 0;

    static byte[] getData(int uid){

        File file = new File( "/sdcard/"+String.valueOf(uid)+".wav");
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Log.d("DEBUG", "bytes array length is: " + String.valueOf(bytes.length));
        return bytes;
    }

    static String generateUid(){
        return String.valueOf(counter++);
    }
}
