package com.lazytong.launcher.utils;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import com.lazytong.launcher.WearLauncherApp;
import java.util.ArrayList;
import java.util.List;

public class PmUtils {
    public static List<ResolveInfo> getAllApps() {
        List<ResolveInfo> appList;
    	Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        appList = WearLauncherApp.getContext().getPackageManager().queryIntentActivities(intent,0);
        return appList;
    }
}
