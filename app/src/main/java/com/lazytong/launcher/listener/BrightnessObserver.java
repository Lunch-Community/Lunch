package com.lazytong.launcher.listener;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import com.lazytong.launcher.WearLauncherApp;
import com.lazytong.launcher.utils.ILog;

public class BrightnessObserver {
    public interface BrightnessChangeListener {
        /**
         * 系统亮度变化
         * @param bright
         */
        void onBrightnessChanged(int brightness);
    }
    private Context context;
    private BrightnessChangeListener listener;
    private ContentObserver observer = new ContentObserver(new Handler(Looper.getMainLooper())){
        @Override
        public void onChange(boolean arg0) {
            super.onChange(arg0);
            try {
            	int brightness = Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
                listener.onBrightnessChanged(brightness);
            } catch(Exception err) {
            	ILog.e("Cant get screen brightness???");
            }
            
        }
        
    };
    public BrightnessObserver(Context context,BrightnessChangeListener listener){
        this.context = context;
        this.listener = listener;
    }
    public void register() {
    	context.getContentResolver()
        .registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),true,observer);
    }
    public void unregister() {
    	context.getContentResolver()
        .unregisterContentObserver(observer);
    }
    public static int getMaxBrightness() {
    	if(Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {//only xiaomi can do
    		return 4095;//小米最大亮度为4095
    	};
        return 255;
    }
    public static void setBrightness(int brightness) {
    	Settings.System.putInt(WearLauncherApp.getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,brightness);
    }
    public static int getCurrectBrightness(){
        try {
        	return Settings.System.getInt(WearLauncherApp.getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        } catch(Exception err) {
        	ILog.e("Cant get screen brightness???");
        }
    	return 0;
    }
}
