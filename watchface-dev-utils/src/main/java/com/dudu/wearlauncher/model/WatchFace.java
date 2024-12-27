package com.lazytong.launcher.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.lazytong.launcher.utils.ILog;
import dalvik.system.DexClassLoader;

import java.lang.reflect.Field;

public abstract class WatchFace extends FrameLayout {

    public WatchFace(Context context) {
        this(context,null);
    }

    public WatchFace(Context context, AttributeSet attributeSet) {
        this(context,attributeSet,0);
    }

    public WatchFace(Context context, AttributeSet attributeSet, int i) {
        super(context,attributeSet,i);
        initView();
    }
    public void initView(){
        
    }

    public void onWatchfaceInvisible() {

    }

    public void onWatchfaceVisible() {

    }

    public void onScreenOff() {

    }

    public void onScreenOn() {
    }

    public void updateBattery(int i, int batteryStatus) {
    }

    public void updateStep(int i) {
    }
    public void updateTime(){
        
    }
}