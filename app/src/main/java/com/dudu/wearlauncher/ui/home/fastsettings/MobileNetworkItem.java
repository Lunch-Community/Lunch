package com.lazytong.launcher.ui.home.fastsettings;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.lazytong.launcher.R;
import com.lazytong.launcher.WearLauncherApp;
import com.lazytong.launcher.listener.MobileNetworkObserver;
import com.lazytong.launcher.model.FastSettingsItem;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.utils.MobileDataUtil;
import com.lazytong.launcher.utils.RootUtil;
import com.lazytong.launcher.widget.SwitchIconButton;

public class MobileNetworkItem extends FastSettingsItem {
    @Override
    public Drawable getDrawable() {
        return drawable;
    }
    @Override
    public ButtonItemListener getTouchListener() {
        return touchListener;
    }
    @Override
    public String getSettingsName() {
        return "移动数据";
    }
    
    
    
    public Drawable drawable = WearLauncherApp.getContext().getResources().getDrawable(R.drawable.mobile_network_selector);
    public ButtonItemListener touchListener = new ButtonItemListener(){
        @Override
        public void onClick(boolean checked) {
            if(RootUtil.isAccessGiven()) {
                new Thread(()->{
                    String enabled = "disable";
                        if (!checked) {
                            enabled = "enable";
                        }
                    ShellUtils.CommandResult result = ShellUtils.execCmd("svc data "+enabled,true);
                    ILog.w("Success:"+result.successMsg+" Err:"+result.errorMsg);
                }).start();
                
            }else if(RootUtil.isSystemApplication(WearLauncherApp.getContext(),AppUtils.getAppPackageName())) {
            	MobileDataUtil.setMobileDataEnabled(WearLauncherApp.getContext(),!checked);
                ILog.w("使用系统应用权限修改移动网络设置");
            }else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                Intent intent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                WearLauncherApp.getContext().startActivity(intent);
                ILog.w("启动data panel");
            }else{
                ILog.e("您的设备暂不支持该功能");
            }
                
            
        }
        @Override
        public void onLongClick(boolean checked) {
            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            WearLauncherApp.getContext().startActivity(intent);
        }
    };
    public MobileNetworkObserver mMNObserver;
    @Override
    public void registerStateObserver(Context context) {
        super.registerStateObserver(context);
        mMNObserver = new MobileNetworkObserver(context);
        mMNObserver.register(new MobileNetworkObserver.MNStateListener(){
            
            @Override
            public void onStateEnabled(){
        
                button.setActivated(true);
                
            }
            
            @Override
            public void onStateDisabled(){
                
                button.setActivated(false);
                
            }
            
        });
    }
    @Override
    public void unregisterStateObserver() {
        super.unregisterStateObserver();
        mMNObserver.unregister();
    }
}
