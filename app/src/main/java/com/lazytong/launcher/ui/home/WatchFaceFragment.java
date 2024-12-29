package com.lazytong.launcher.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cn.leaqi.drawer.SwipeDrawer;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.VolumeUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.lazytong.launcher.R;
import com.lazytong.launcher.listener.BrightnessObserver;
import com.lazytong.launcher.listener.VolumeChangeObserver;
import com.lazytong.launcher.model.Notification;
import com.lazytong.launcher.model.WatchFace;
import com.lazytong.launcher.model.WatchFaceBridge;
import com.lazytong.launcher.model.WatchFaceInfo;
import com.lazytong.launcher.ui.home.fastsettings.BluetoothItem;
import com.lazytong.launcher.ui.home.fastsettings.MobileNetworkItem;
import com.lazytong.launcher.ui.home.fastsettings.WifiSwitchItem;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.utils.SettingCenterManager;
import com.lazytong.launcher.utils.SharedPreferencesUtil;
import com.lazytong.launcher.utils.WatchFaceHelper;
import com.lazytong.launcher.widget.MyLinearLayoutManager;
import com.lazytong.launcher.widget.MyRecyclerView;
import com.lazytong.launcher.widget.RoundedSeekBar;
import com.lazytong.launcher.widget.SwitchIconButton;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class WatchFaceFragment extends Fragment{
    WatchFaceBridge watchFace;
    View watchFaceView;
    FrameLayout watchFaceBox;
    FrameLayout.LayoutParams layoutParams;
    SwipeDrawer swipeDrawer;

    MsgListAdapter msgListAdapter;

    BroadcastReceiver msgReceiver, msgRemovedReceiver, msgListAllReceiver, watchFaceChangeReceiver, batteryChangeReceiver, timeChangeReceiver, screenStatusChangeReceiver;
    
    VolumeChangeObserver volumeObserver;
    BrightnessObserver brightnessObserver;
    SwitchIconButton btn1,btn2,btn3;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watchface, container, false);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        
        watchFaceBox = view.findViewById(R.id.watchface_box);
        MyRecyclerView msgView = view.findViewById(R.id.msg_list);
        RoundedSeekBar volumeSeekBar = view.findViewById(R.id.volume_seekbar);
        RoundedSeekBar brightnessSeekBar = view.findViewById(R.id.brightness_seekbar);
        btn1 = view.findViewById(R.id.setting_center_btn1);
        btn2 = view.findViewById(R.id.setting_center_btn2);
        btn3 = view.findViewById(R.id.setting_center_btn3);
        swipeDrawer = view.findViewById(R.id.swipe_drawer);
        
        try {
        	JSONArray oldArray = new JSONArray((String)SharedPreferencesUtil.getData(SharedPreferencesUtil.SETTING_CENTER,"[{\"button\":\"button_wifi\"},{\"button\":\"button_mobiledata\"},{\"button\":\"button_bluetooth\"}]"));
            btn1.attach(SettingCenterManager.getButtonInstance(oldArray.getJSONObject(0).getString("button")));
            btn2.attach(SettingCenterManager.getButtonInstance(oldArray.getJSONObject(1).getString("button")));
            btn3.attach(SettingCenterManager.getButtonInstance(oldArray.getJSONObject(2).getString("button")));
        } catch(Exception err) {
            err.printStackTrace();
            btn1.attach(new WifiSwitchItem());
            btn2.attach(new MobileNetworkItem());
            btn3.attach(new BluetoothItem());    
        }
        
        volumeObserver = new VolumeChangeObserver(requireActivity());
        volumeObserver.registerReceiver();
        volumeSeekBar.setProgress((int)((double)volumeObserver.getCurrentMusicVolume()/volumeObserver.getMaxMusicVolume()*100));
        volumeObserver.setVolumeChangeListener(new VolumeChangeObserver.VolumeChangeListener(){
            @Override
            public void onVolumeChanged(int volume) {
                if(Math.abs(((double)volume/volumeObserver.getMaxMusicVolume()*100)-volumeSeekBar.getProgress())>=10) {
                	volumeSeekBar.setProgress((int)((double)volume/volumeObserver.getMaxMusicVolume()*100));
                }
                ILog.d("Volume Changed :"+volume+"Max:"+volumeObserver.getMaxMusicVolume());
            }
        });
        
        volumeSeekBar.setIconOnClickListener(v->{
            VolumeUtils.setVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_VIBRATE);
        });
        
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {}
            @Override
            public void onProgressChanged(SeekBar arg0,int arg1,boolean arg2) {
                if(arg1<=2) {
                	volumeSeekBar.setIconDrawable(getResources().getDrawable(R.drawable.icon_volume_0));
                }if(2<arg1&arg1<=50) {
                	volumeSeekBar.setIconDrawable(getResources().getDrawable(R.drawable.icon_volume_1));
                }if(arg1>50) {
                	volumeSeekBar.setIconDrawable(getResources().getDrawable(R.drawable.icon_volume_2));
                }
                VolumeUtils.setVolume(AudioManager.STREAM_MUSIC,(int)((double)arg1/100*volumeObserver.getMaxMusicVolume()),AudioManager.FLAG_SHOW_UI);
            }
        });
        

        brightnessSeekBar.setProgress((int)((double)BrightnessObserver.getCurrectBrightness()/BrightnessObserver.getMaxBrightness()*100));
        brightnessObserver = new BrightnessObserver(requireActivity(),new BrightnessObserver.BrightnessChangeListener(){
            @Override
            public void onBrightnessChanged(int brightness) {
                int delta = 15;
                if(Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
                	delta = 120;
                }
                if(Math.abs((double)brightnessSeekBar.getProgress()/100*BrightnessObserver.getMaxBrightness()-brightness)>delta) {
                	brightnessSeekBar.setProgress((int)((double)brightness/BrightnessObserver.getMaxBrightness()*100));
                    ILog.d("Brightness Changed :"+brightness+"Max:"+BrightnessObserver.getMaxBrightness());
                }
                
            }
            
        });
        brightnessObserver.register();
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {}
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {}
            @Override
            public void onProgressChanged(SeekBar arg0,int arg1,boolean arg2) {
                BrightnessObserver.setBrightness((int)((double)arg1/100*BrightnessObserver.getMaxBrightness()));
            }
        });

        screenStatusChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    switch (intent.getAction()) {
                        case Intent.ACTION_USER_PRESENT:
                            watchFace.onScreenOn();
                            break;
                        case Intent.ACTION_SCREEN_OFF:
                            watchFace.onScreenOff();
                            break;
                        case Intent.ACTION_SCREEN_ON:
                            break;
                    }
                	
                } catch(Exception err) {
                	
                }
            }
        };
        IntentFilter screenStatusChangeFilter = new IntentFilter();
        screenStatusChangeFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusChangeFilter.addAction(Intent.ACTION_USER_PRESENT);
        screenStatusChangeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        requireActivity().registerReceiver(screenStatusChangeReceiver, screenStatusChangeFilter);

        msgListAllReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<Notification> list = intent.getParcelableArrayListExtra("list");
                msgListAdapter = new MsgListAdapter(requireActivity(), list);
                msgView.setLayoutManager(new MyLinearLayoutManager(requireActivity()));
                msgView.setAdapter(msgListAdapter);
            }
        };
        requireActivity().registerReceiver(msgListAllReceiver, new IntentFilter("com.lazytong.launcher.ListAllNotification"));

        postGetAllNotification();

        msgView.setEmptyView(view.findViewById(R.id.empty_list_text));

        watchFaceBox.setOnLongClickListener(v->{
            Intent intent = new Intent(requireActivity(),ChooseWatchFaceActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            return false;
        });

        msgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Notification notification = intent.getParcelableExtra("notification");
                msgListAdapter.addSbn(notification);
            }
        };
        requireActivity().registerReceiver(msgReceiver, new IntentFilter("com.lazytong.launcher.NotificationReceived"));

        msgRemovedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Notification notification = intent.getParcelableExtra("notification");
                msgListAdapter.removeSbn(notification);
            }
        };
        requireActivity().registerReceiver(msgRemovedReceiver, new IntentFilter("com.lazytong.launcher.NotificationRemoved"));
        
        refreshWatchFace();
        
        watchFaceChangeReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshWatchFace();
            }
        };
        IntentFilter watchFaceChangeFilter = new IntentFilter("com.lazytong.launcher.WatchFaceChange");
        requireActivity().registerReceiver(watchFaceChangeReceiver,watchFaceChangeFilter);
        batteryChangeReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                int battery = level * 100 / scale;
                updateBattery(battery,status);
            }
        };
        requireActivity().registerReceiver(batteryChangeReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        timeChangeReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTime();
            }
        };
        IntentFilter timeChangeFilter = new IntentFilter();
        timeChangeFilter.addAction(Intent.ACTION_TIME_TICK);
        timeChangeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        requireActivity().registerReceiver(timeChangeReceiver,timeChangeFilter);
    }

    private void postGetAllNotification() {
        Intent intent = new Intent("com.lazytong.launcher.NOTIFICATION_LISTENER");
        intent.putExtra("command", "listAll");
        requireActivity().sendBroadcast(intent);
    }
    private void refreshWatchFace() {
    	watchFaceBox.removeAllViews();
        try {
            if(SharedPreferencesUtil.getData(SharedPreferencesUtil.NOW_WATCHFACE,"").equals("")) {
            	onNoWatchFace();
            }else{
                WatchFaceInfo wfInfo = WatchFaceHelper.getWatchfaceByPackage((String)SharedPreferencesUtil.getData(SharedPreferencesUtil.NOW_WATCHFACE,""));
                watchFaceView = WatchFaceHelper.loadWatchface(wfInfo.packageName, wfInfo.watchface);
                watchFace = new WatchFaceBridge(watchFaceView);
                if(watchFace != null) {
                //watchFace.setOnClickListener(v->{
                    //覆盖原Listener防止打不开样式切换界面
                //});
                    layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
                    watchFaceBox.addView(watchFaceView,layoutParams);
                    updateTime();
                    BatteryManager batteryManager = (BatteryManager)requireActivity().getSystemService(Context.BATTERY_SERVICE);
                    if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O) {
                        updateBattery(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),BatteryManager.BATTERY_STATUS_DISCHARGING);
                    }else{
                        updateBattery(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS));
                    }
                
                }else{
                    throw new RuntimeException("Unexpected null watchface");
                }
            }
        } catch (Exception err) {
            onWatchFaceLoadFailed();
            ILog.e("样式加载失败:" + err);
            err.printStackTrace();
            //FileIOUtils.writeFileFromString(watchFaceFolder + "/" + System.currentTimeMillis() + "watchface.log", err.toString());
        }
       
    }

    private void onWatchFaceLoadFailed() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        TextView tv = new TextView(requireActivity());
        tv.setText("样式加载失败");
        watchFaceBox.addView(tv, lp);
    }
    
    private void onNoWatchFace() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        TextView tv = new TextView(requireActivity());
        tv.setText("无样式\n长按此处添加");
        watchFaceBox.addView(tv, lp);
    }

    public WatchFaceBridge getWatchFace() {
        return this.watchFace;
    }

    public SwipeDrawer getSwipeDrawer() {
        return this.swipeDrawer;
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
        volumeObserver.unregisterReceiver();
        brightnessObserver.unregister();
        requireActivity().unregisterReceiver(watchFaceChangeReceiver);
        requireActivity().unregisterReceiver(batteryChangeReceiver);
        requireActivity().unregisterReceiver(timeChangeReceiver);
        requireActivity().unregisterReceiver(msgReceiver);
        requireActivity().unregisterReceiver(msgRemovedReceiver);
        requireActivity().unregisterReceiver(screenStatusChangeReceiver);
    }
    public void updateTime(){
        if(watchFace!=null) {
            try {
                watchFace.updateTime();
            } catch(Exception err) {
            	
            }
            //watchFaceView.updateViewLayout(watchFace, watchFaceParams);    //时间更新
        }
    }

    public void updateBattery(int i,int batteryStatus){
        if(watchFace!=null) {
            try {
                watchFace.updateBattery(i,batteryStatus);
            } catch(Exception err) {
            	err.printStackTrace();
            }
            //lwatchFaceBox.updateViewLayout(watchFace,layoutParams);    //电池更新
        }
    }
}
