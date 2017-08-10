package com.fxb.receiver.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/7/19 0019.
 */

public class Sp {
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("magicreceiver", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStrings(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("magicreceiver", 0);
        return sp.getString(key, "");
    }
}
