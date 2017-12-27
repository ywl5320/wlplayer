package com.ywl5320.libwlplayer;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xiang on 2017/11/2.
 */

public class Utils {

    public static String loadStringFromAssets(Context context, String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] b = new byte[2048];
            while ((len = is.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            String s = new String(bos.toByteArray());
            bos.close();
            is.close();
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
