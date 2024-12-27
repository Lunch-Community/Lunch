package com.lazytong.launcher.utils;
import android.widget.Button;
import com.lazytong.launcher.model.FastSettingsItem;
import com.lazytong.launcher.ui.home.fastsettings.BluetoothItem;
import com.lazytong.launcher.ui.home.fastsettings.MobileNetworkItem;
import com.lazytong.launcher.ui.home.fastsettings.NullItem;
import com.lazytong.launcher.ui.home.fastsettings.WifiSwitchItem;
import com.google.gson.internal.ConstructorConstructor;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class SettingCenterManager {
    public static final String BUTTON_WIFI = "button_wifi";
    public static final String BUTTON_MOBILE_DATA = "button_mobiledata";
    public static final String BUTTON_BLUETOOTH = "button_bluetooth";
    public static final String BUTTON_ACTIVITY = "button_activity";
    public static final Map<String,Class> classMap = Map.of(BUTTON_WIFI,WifiSwitchItem.class,BUTTON_MOBILE_DATA,MobileNetworkItem.class,BUTTON_BLUETOOTH,BluetoothItem.class);
    public static FastSettingsItem getButtonInstance(String name,Object...args) {
        if(!classMap.containsKey(name)) {
        	return new NullItem();
        }
        try {
            for(Constructor i:classMap.get(name).getConstructors()) {
            	if(i.getParameterCount()==args.length) {
            		return (FastSettingsItem)i.newInstance(args);
            	}
            }
        } catch(Exception err) {
        	ILog.w(err.toString());
        }
        return new NullItem();
    }
    public static FastSettingsItem getButtonInstance(String name) {
        if(!classMap.containsKey(name)) {
        	return new NullItem();
        }
        try {
        	return (FastSettingsItem)classMap.get(name).getConstructor().newInstance();
        } catch(Exception err) {
        	ILog.w(err.toString());
        }
        return new NullItem();
    }
}
