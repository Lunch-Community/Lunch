package com.lazytong.launcher.ui.home.fastsettings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import com.blankj.utilcode.util.ShellUtils;
import com.lazytong.launcher.R;
import com.lazytong.launcher.WearLauncherApp;
import com.lazytong.launcher.listener.WLANListener;
import com.lazytong.launcher.model.FastSettingsItem;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.utils.RootUtil;
import com.lazytong.launcher.widget.SwitchIconButton;

public class WifiSwitchItem extends FastSettingsItem {
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
        return "WIFI";
    }

    public static int WIFI_CODE = 1919810;
    public Drawable drawable =
            WearLauncherApp.getContext().getResources().getDrawable(R.drawable.wifi_selector);
    private WifiManager wifiManager =
            (WifiManager) WearLauncherApp.getContext().getSystemService(Context.WIFI_SERVICE);
    public ButtonItemListener touchListener =
            new ButtonItemListener() {
                @Override
                public void onClick(boolean checked) {
                    if (RootUtil.isAccessGiven()) {
                        new Thread(()->{
                            
                            String enabled = "disable";
                            if (!checked) {
                                enabled = "enable";
                            }
                            ShellUtils.CommandResult result =
                                ShellUtils.execCmd("svc wifi " + enabled, true);
                            ILog.w("Success:"+result.successMsg+" Err:"+result.errorMsg);
                        }).start();
                        
                    } else {
                        ILog.w("尝试直接设置开启");
                        if (!wifiManager.setWifiEnabled(!checked)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Intent intent =
                                        new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                WearLauncherApp.getContext().startActivity(intent);
                                ILog.w("启动wifipanel");
                            }else{
                                ILog.e("您的设备暂不支持该功能");
                            }
                        }
                    }
                }

                @Override
                public void onLongClick(boolean checked) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    WearLauncherApp.getContext().startActivity(intent);
                }
            };
    public WLANListener wlanListener;

    @Override
    public void registerStateObserver(Context context) {
        super.registerStateObserver(context);
        wlanListener = new WLANListener(context);
        wlanListener.register(
                new WLANListener.WLANStateListener() {
                    @Override
                    public void onStateChanged() {}

                    @Override
                    public void onStateEnabled() {
                        
                    	button.setActivated(true);
                        
                    }

                    @Override
                    public void onStateEnabling() {}

                    @Override
                    public void onStateDisabling() {}

                    @Override
                    public void onStateDisabled() {
                        
                        button.setActivated(false);
                        
                    }

                    @Override
                    public void onStateUnknow() {
                        
                        button.setEnabled(false);
                        
                    }
                });
    }

    @Override
    public void unregisterStateObserver() {
        super.unregisterStateObserver();
        wlanListener.unregister();
    }
}
