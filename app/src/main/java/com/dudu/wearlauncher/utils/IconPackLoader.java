package com.lazytong.launcher.utils;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import com.lazytong.launcher.WearLauncherApp;
import com.lazytong.launcher.model.IconPack;
import com.tencent.bugly.proguard.i;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

public class IconPackLoader {
    public static List<IconPack> queryIconPacks() {
    	PackageManager pm = WearLauncherApp.getContext().getPackageManager();
        Intent intent = new Intent("com.novalauncher.THEME");
        List<IconPack> packList = new ArrayList<>();
        for(ResolveInfo info : pm.queryIntentActivities(intent,0)) {
        	IconPack pack = new IconPack();
            pack.icon = info.loadIcon(pm);
            pack.name = info.loadLabel(pm).toString();
            pack.packageName = info.activityInfo.packageName;
            packList.add(pack);
        }
        return packList;
    }
    public static Map<String,Drawable> getIconMap(String packageName,Context context) throws PackageManager.NameNotFoundException,RuntimeException,IOException{
        Context iconPackContext = WearLauncherApp.getContext().createPackageContext(packageName,Context.CONTEXT_IGNORE_SECURITY);
        Resources iconPackResources = iconPackContext.getResources();
        List<String> appList = PackageManagerEx.getAppComponentList(context);
        HashMap<String,Drawable> iconMap = new HashMap<>();
        int appfilterXmlId = iconPackResources.getIdentifier("appfilter","xml",packageName);
        if(appfilterXmlId==0) {
        	throw new RuntimeException("appfilter.xml NOT FOUND,ARE U SURE IT IS AN ICONPACK?");
        }
        XmlResourceParser appfilterParser = iconPackResources.getXml(appfilterXmlId);
        try{
            int event = appfilterParser.getEventType();
            while(event!=XmlPullParser.END_DOCUMENT) {
                if(event==XmlPullParser.START_TAG){
                    if(appfilterParser.getName().equals("item")) {
                        String component = appfilterParser.getAttributeValue(null,"component");
                        if(component.contains("ComponentInfo{")) {
                            component = component.substring(component.indexOf("{")+1,component.indexOf("}"));
                            if(appList.contains(component)) {
                            	String drawableName = appfilterParser.getAttributeValue(null,"drawable");
                                Drawable drawable = iconPackResources.getDrawable(iconPackResources.getIdentifier(drawableName,"drawable",packageName));
                                iconMap.put(component,drawable);
                                ILog.w("Map:"+component);
                            }
                        }
                    }
                }
            	event=appfilterParser.next();
            }
        }catch(Exception e){
            throw new IOException("Xml parse failed,check your appfilter.xml and retry",e.fillInStackTrace());
        }
        appfilterParser.close();
    	return iconMap;
    }
}
