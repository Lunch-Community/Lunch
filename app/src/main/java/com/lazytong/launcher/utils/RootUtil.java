package com.lazytong.launcher.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.blankj.utilcode.util.ShellUtils;
import java.util.HashSet;
import java.util.Set;

public class RootUtil {
    public static boolean isAccessGiven() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("id", true);
        if (result.result != 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isSystemApplication(Context context, String packageName) {
        PackageManager mPackageManager = context.getPackageManager();
        try {
            final PackageInfo packageInfo =
                    mPackageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            ILog.e(e.getMessage());
        }
        return false;
    }
}
