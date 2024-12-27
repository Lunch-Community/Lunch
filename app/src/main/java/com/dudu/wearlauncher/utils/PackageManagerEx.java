package com.lazytong.launcher.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.lazytong.launcher.R;
import com.lazytong.launcher.WearLauncherApp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PackageManagerEx {
    public static List<ResolveInfo> getAppList(Context context) {

        List<ResolveInfo> appList = PmUtils.getAllApps();
        Iterator<ResolveInfo> iterator = appList.iterator();
        List<String> hiddenList = new ArrayList();
        hiddenList.addAll(
                Arrays.asList(
                        context.getResources().getStringArray(R.array.hidden_activities)));
        ILog.w(
                "hiddenActivities:"
                        + SharedPreferencesUtil.getData(
                                SharedPreferencesUtil.HIDDEN_ACTIVITIES, ""));
        hiddenList.addAll(
                Arrays.asList(
                        ((String)
                                        SharedPreferencesUtil.getData(
                                                SharedPreferencesUtil.HIDDEN_ACTIVITIES, ""))
                                .split(":")));
        while (iterator.hasNext()) {
            ResolveInfo info = iterator.next();
            if (hiddenList.contains(info.activityInfo.packageName + "/" + info.activityInfo.name)) {
                iterator.remove();
            }
        }
        return appList;
    }
    public static List<String> getAppComponentList(Context context) {
        return getAppList(context).stream().map(app->app.activityInfo.packageName+"/"+app.activityInfo.name).collect(Collectors.toList());
    }

    public static List<String> getLauncherActivities(Context context, String packageName) {
        List<String> launcherActivities = new ArrayList<>();
        try {
            PackageManager pm = context.getPackageManager();
            // 获取指定包名的 PackageInfo，包括 Activity 信息
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            // 遍历 Activity 信息，筛选出符合 LAUNCHER 和 MAIN 的 Activity
            if (packageInfo.activities != null) {
                for (ActivityInfo activityInfo : packageInfo.activities) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setPackage(packageName);

                    List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
                    for (ResolveInfo resolveInfo : resolveInfos) {
                        if (resolveInfo.activityInfo.name.equals(activityInfo.name)) {
                            launcherActivities.add(activityInfo.name);
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return launcherActivities;
    }
}
