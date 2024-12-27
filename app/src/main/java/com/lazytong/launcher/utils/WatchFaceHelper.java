package com.lazytong.launcher.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.FileIOUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lazytong.launcher.R;
import com.lazytong.launcher.WearLauncherApp;
import com.lazytong.launcher.model.WatchFace;
import com.lazytong.launcher.model.WatchFaceInfo;
import dalvik.system.DexClassLoader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lazytong.launcher.model.WatchFace.*;
import org.xmlpull.v1.XmlPullParserException;

public class WatchFaceHelper {
    public static List<WatchFaceInfo> getWatchfaces() {
    	PackageManager pm = WearLauncherApp.getContext().getPackageManager();
        Intent intent = new Intent("com.lazytong.launcher.WATCHFACE");
        List<WatchFaceInfo> watchfaceList = new ArrayList<>();
        try {
            for(ResolveInfo info : pm.queryIntentActivities(intent,0)) {
                WatchFaceInfo watchface = new WatchFaceInfo();
                watchface.packageName = info.activityInfo.packageName;
                //watchface.displayName = info.loadLabel(pm).toString();
                Context context = WearLauncherApp.getContext().createPackageContext(info.activityInfo.packageName,Context.CONTEXT_IGNORE_SECURITY);
                XmlResourceParser watchfaceCfgParser = context.getResources().getXml(context.getResources().getIdentifier("watchface_config","xml",context.getPackageName()));
                int event = watchfaceCfgParser.getEventType();
                while(event!=XmlResourceParser.END_DOCUMENT) {
                    if(event==XmlResourceParser.START_TAG) {
                        watchface.preview = context.getDrawable(watchfaceCfgParser.getAttributeResourceValue(null,"preview",0));
                        watchface.settingsActivityName = watchfaceCfgParser.getAttributeValue(null,"settings_activity");
                        watchface.displayName = watchfaceCfgParser.getAttributeValue(null,"name");
                        watchface.watchface = watchfaceCfgParser.getAttributeValue(null,"watchface");
                        break;
                    }
                    event=watchfaceCfgParser.next();
                }
                watchfaceList.add(watchface);
            }
        	
        } catch(Exception err) {
        	err.printStackTrace();
        }
        return watchfaceList;
    }
    public static WatchFaceInfo getWatchfaceByPackage(String packageName) throws IOException,XmlPullParserException,PackageManager.NameNotFoundException{
    	WatchFaceInfo watchface = new WatchFaceInfo();
        watchface.packageName = packageName;
        Context context = WearLauncherApp.getContext().createPackageContext(packageName,Context.CONTEXT_IGNORE_SECURITY);
        XmlResourceParser watchfaceCfgParser = context.getResources().getXml(context.getResources().getIdentifier("watchface_config","xml",context.getPackageName()));
        int event = watchfaceCfgParser.getEventType();
        while(event!=XmlResourceParser.END_DOCUMENT) {
            if(event==XmlResourceParser.START_TAG) {
                watchface.preview = context.getDrawable(watchfaceCfgParser.getAttributeResourceValue(null,"preview",0));
                watchface.settingsActivityName = watchfaceCfgParser.getAttributeValue(null,"settings_activity");
                watchface.displayName = watchfaceCfgParser.getAttributeValue(null,"name");
                watchface.watchface = watchfaceCfgParser.getAttributeValue(null,"watchface");
                break;
            }
            event=watchfaceCfgParser.next();
        }
        return watchface;
    }
    public static View loadWatchface(String packageName,String watchFaceClassName) throws PackageManager.NameNotFoundException,ClassNotFoundException,IllegalAccessException,NoSuchMethodException,InstantiationException,InvocationTargetException{
        //PackageManager pm = WearLauncherApp.getContext().getPackageManager();
        Context watchfaceContext = WearLauncherApp.getContext().createPackageContext(packageName,Context.CONTEXT_INCLUDE_CODE|Context.CONTEXT_IGNORE_SECURITY);
        ClassLoader watchfaceClassLoader = watchfaceContext.getClassLoader();
        Class<?> watchfaceClass = watchfaceClassLoader.loadClass(watchFaceClassName);
        View watchface = (View)watchfaceClass.getConstructor(Context.class).newInstance(getFitDisplayContext(watchfaceContext));
        return watchface;
    }
    
    private static Context getFitDisplayContext(Context old){
        Context newContext = old;

        float density = (float) old.getResources().getDisplayMetrics().widthPixels / 320;
        try{
            Configuration configuration = old.getResources().getConfiguration();
            configuration.smallestScreenWidthDp = 320;
            configuration.densityDpi = (int) (320 * density); 
            newContext = old.createConfigurationContext(configuration);
        }catch (Exception e){
            Toast.makeText(newContext, "调整缩放失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return newContext;
    }
}