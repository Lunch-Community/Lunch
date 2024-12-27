package com.lazytong.launcher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class ScreenUtils {
    public static boolean isRoundScreen(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String screenType = sp.getString("screen_type", "unknown");
        Configuration cfg = new Configuration();
        if (screenType.equals("unknown")) {
            if (cfg.isScreenRound()) return true;
            else return false;
        }
        if (screenType.equals("round")) return true;
        if (screenType.equals("square")) return false;
        return false;
    }
}
