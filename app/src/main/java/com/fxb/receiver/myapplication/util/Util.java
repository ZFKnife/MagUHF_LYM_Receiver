package com.fxb.receiver.myapplication.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/29 0029.
 */

public class Util {
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
    public static String getTime() {
        Date date = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
