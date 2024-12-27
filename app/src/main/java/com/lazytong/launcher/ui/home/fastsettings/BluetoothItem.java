package com.lazytong.launcher.ui.home.fastsettings;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.lazytong.launcher.R;
import com.lazytong.launcher.WearLauncherApp;
import com.lazytong.launcher.listener.BluetoothObserver;
import com.lazytong.launcher.model.FastSettingsItem;
import com.lazytong.launcher.utils.BluetoothManager;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.utils.RootUtil;
import com.lazytong.launcher.widget.SwitchIconButton;

public class BluetoothItem extends FastSettingsItem {
    @Override
    public Drawable getDrawable() {
        return WearLauncherApp.getContext().getResources().getDrawable(R.drawable.bluetooth_selector);
    }
    @Override
    public String getSettingsName() {
        return "蓝牙";
    }
    
    @Override
    public ButtonItemListener getTouchListener() {
        return new ButtonItemListener() {
                @Override
                public void onClick(boolean checked) {
                    if (RootUtil.isSystemApplication(
                                    WearLauncherApp.getContext(), AppUtils.getAppPackageName())
                            || Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                        if (checked) {
                            BluetoothManager.turnOffBluetooth();
                        } else {
                            BluetoothManager.turnOnBluetooth();
                        }
                        ILog.w("直接设置蓝牙");
                    } else if (!BluetoothManager.isBluetoothSupported()) {
                        ILog.e("您的设备暂不支持该功能");
                    } else {
                        // 请求打开 Bluetooth
                        Intent requestBluetoothOn =
                                new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
                        requestBluetoothOn.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        // 设置 Bluetooth 设备可见时间
                        requestBluetoothOn.putExtra(
                                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                                600);
                        WearLauncherApp.getContext().startActivity(requestBluetoothOn);
                    }
                }

                @Override
                public void onLongClick(boolean checked) {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    WearLauncherApp.getContext().startActivity(intent);
                }
            };
        };
    
    public BluetoothObserver observer;

    @Override
    public void registerStateObserver(Context context) {
        super.registerStateObserver(context);
        observer = new BluetoothObserver(context);
        observer.register(
                new BluetoothObserver.BluetoothStateListener() {

                    @Override
                    public void onEnabled() {
                    
                        button.setActivated(true);
                        
                    }

                    @Override
                    public void onDisabled() {
                    
                        button.setActivated(false);
                        
                    }

                    @Override
                    public void onConnected() {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onDisconnected() {
                        // TODO: Implement this method
                    }
                });
    }

    @Override
    public void unregisterStateObserver() {
        super.unregisterStateObserver();
        observer.unregister();
    }
}
