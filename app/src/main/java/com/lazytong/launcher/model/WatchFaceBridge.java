package com.lazytong.launcher.model;
import java.lang.reflect.InvocationTargetException;

public class WatchFaceBridge {
    Object watchface;
    public WatchFaceBridge(Object watchface){
    	this.watchface = watchface;
    }
    
    public void onWatchfaceInvisible() throws NoSuchMethodException,IllegalAccessException,InvocationTargetException{
        watchface.getClass().getMethod("onWatchfaceInvisible").invoke(watchface);
    }

    public void onWatchfaceVisible() throws NoSuchMethodException,IllegalAccessException,InvocationTargetException {
        watchface.getClass().getMethod("onWatchfaceVisible").invoke(watchface);
    }

    public void onScreenOff() throws NoSuchMethodException,IllegalAccessException,InvocationTargetException {
        watchface.getClass().getMethod("onScreenOff").invoke(watchface);
    }

    public void onScreenOn() throws NoSuchMethodException,IllegalAccessException,InvocationTargetException {
        watchface.getClass().getMethod("onScreenOn").invoke(watchface);
    }

    public void updateBattery(int i, int batteryStatus) throws NoSuchMethodException,IllegalAccessException,InvocationTargetException {
        watchface.getClass().getMethod("updateBattery",int.class,int.class).invoke(watchface,i,batteryStatus);
    }

    public void updateTime() throws NoSuchMethodException,IllegalAccessException,InvocationTargetException{
        watchface.getClass().getMethod("updateTime").invoke(watchface);
    };
}
