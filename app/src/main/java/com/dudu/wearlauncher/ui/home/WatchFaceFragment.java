package com.dudu.wearlauncher.ui.home;

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
import androidx.viewpager.widget.ViewPager;
import cn.leaqi.drawer.OnDrawerChange;
import cn.leaqi.drawer.OnDrawerState;
import cn.leaqi.drawer.SwipeDrawer;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.VolumeUtils;
import com.dudu.wearlauncher.R;
import com.dudu.wearlauncher.listener.BrightnessObserver;
import com.dudu.wearlauncher.listener.VolumeChangeObserver;
import com.dudu.wearlauncher.model.Notification;
import com.dudu.wearlauncher.model.WatchFaceBridge;
import com.dudu.wearlauncher.model.WatchFaceInfo;
import com.dudu.wearlauncher.ui.home.fastsettings.BluetoothItem;
import com.dudu.wearlauncher.ui.home.fastsettings.MobileNetworkItem;
import com.dudu.wearlauncher.ui.home.fastsettings.WifiSwitchItem;
import com.dudu.wearlauncher.utils.*;
import com.dudu.wearlauncher.widget.*;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WatchFaceFragment extends Fragment{
    WatchFaceBridge watchFace;
    View watchFaceView;
    MyFrameLayout watchFaceBox;
    FrameLayout.LayoutParams layoutParams;
    SwipeDrawer swipeDrawer;
    MyViewPager viewPager;

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

        swipeDrawer.setTopDragOpen((Boolean) SharedPreferencesUtil.getData(SharedPreferencesUtil.MSG_LIST_ENABLED, true));
        swipeDrawer.setBottomDragOpen((Boolean) SharedPreferencesUtil.getData(SharedPreferencesUtil.SETTING_CENTER_ENABLED, true));
        viewPager = ((HomeActivity)getActivity()).homeViewPager;
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
        volumeObserver.setVolumeChangeListener(volume -> {
            if (Math.abs(((double) volume / volumeObserver.getMaxMusicVolume() * 100) - volumeSeekBar.getProgress()) >= 10) {
                volumeSeekBar.setProgress((int) ((double) volume / volumeObserver.getMaxMusicVolume() * 100));
            }
            ILog.d("Volume Changed :" + volume + "Max:" + volumeObserver.getMaxMusicVolume());
        });
        
        volumeSeekBar.setIconOnClickListener(v->{
            VolumeUtils.setVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
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
                VolumeUtils.setVolume(AudioManager.STREAM_MUSIC,(int)((double)arg1/100*volumeObserver.getMaxMusicVolume()),AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        });
        

        brightnessSeekBar.setProgress((int)((double)BrightnessObserver.getCurrectBrightness()/BrightnessObserver.getMaxBrightness()*100));
        brightnessObserver = new BrightnessObserver(requireActivity(), brightness -> {
            int delta = 15;
            if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
                delta = 120;
            }
            if (Math.abs((double) brightnessSeekBar.getProgress() / 100 * BrightnessObserver.getMaxBrightness() - brightness) > delta) {
                brightnessSeekBar.setProgress((int) ((double) brightness / BrightnessObserver.getMaxBrightness() * 100));
                ILog.d("Brightness Changed :" + brightness + "Max:" + BrightnessObserver.getMaxBrightness());
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
                try{
                    BrightnessObserver.setBrightness((int)((double)arg1/100*BrightnessObserver.getMaxBrightness()));
                }catch(SecurityException e){
                    ErrorCatch.instance.handlePermissionException(e);//频率太快，全局catch不到
                }
            }
        });

        screenStatusChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    switch (intent.getAction()) {
                        case Intent.ACTION_USER_PRESENT:
                            watchFace.onScreenStateChanged(true);
                            break;
                        case Intent.ACTION_SCREEN_OFF:
                            watchFace.onScreenStateChanged(false);
                            break;
                        case Intent.ACTION_SCREEN_ON:
                            watchFace.onScreenStateChanged(true);
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

        
        msgListAdapter = new MsgListAdapter(requireActivity(), new ArrayList<Notification>());
        msgView.setLayoutManager(new MyLinearLayoutManager(requireActivity()));
        msgView.setAdapter(msgListAdapter);
        
        msgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(msgListAdapter != null) {
                    Notification notification = intent.getParcelableExtra("notification");
                    msgListAdapter.addSbn(notification);
                }
            }
        };
        

        msgRemovedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(msgListAdapter != null) {
                    Notification notification = intent.getParcelableExtra("notification");
                    msgListAdapter.removeSbn(notification);
                }
            }
        };
        
        msgListAllReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<Notification> list = intent.getParcelableArrayListExtra("list");
                msgListAdapter.addSbns(list);
            }
        };
        
        requireActivity().registerReceiver(msgReceiver, new IntentFilter("com.dudu.wearlauncher.NotificationReceived"));
        requireActivity().registerReceiver(msgRemovedReceiver, new IntentFilter("com.dudu.wearlauncher.NotificationRemoved"));
        requireActivity().registerReceiver(msgListAllReceiver, new IntentFilter("com.dudu.wearlauncher.ListAllNotification"));
        
        
        msgView.setEmptyView(view.findViewById(R.id.empty_list_text));
        ((TextView)view.findViewById(R.id.text_carrier)).setText(PhoneUtils.getSimOperatorName());
        watchFaceBox.setOnLongClickListener(v->{
            Intent intent = new Intent(requireActivity(),ChooseWatchFaceActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            return false;
        });

        swipeDrawer.setOnDrawerState(new OnDrawerState(){
            @Override
            public void onStart(int arg0) {
                // TODO: Implement this method
            }

            @Override
            public void onMove(int arg0, float arg1) {
                // TODO: Implement this method
            }

            @Override
            public void onOpen(int arg0) {
                viewPager.setScrollble(false);
            }

            @Override
            public void onClose(int arg0) {
                viewPager.setScrollble(true);
            }

            @Override
            public void onCancel(int arg0) {
                // TODO: Implement this method
            }


        });

        refreshWatchFace();
        
        watchFaceChangeReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshWatchFace();
            }
        };
        IntentFilter watchFaceChangeFilter = new IntentFilter("com.dudu.wearlauncher.WatchFaceChange");
        requireActivity().registerReceiver(watchFaceChangeReceiver,watchFaceChangeFilter);
        batteryChangeReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                int battery = level * 100 / scale;
                updateBattery(battery,status);
                ((TextView)view.findViewById(R.id.text_battery)).setText(battery+"%"+((status==BatteryManager.BATTERY_STATUS_CHARGING)?"+":""));
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
        postGetAllNotification();
        watchFaceBox.setLongClickListener(() -> {
            Intent intent = new Intent(requireActivity(), ChooseWatchFaceActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void postGetAllNotification() {
        Intent intent = new Intent("com.dudu.wearlauncher.NOTIFICATION_LISTENER");
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
            /*StackTraceElement[] elements = err.getStackTrace();
            StringBuilder sb = new StringBuilder();
            for(StackTraceElement element : elements) {
                sb.append(element);
                sb.append("\n");
            }

            Intent intent = new Intent(requireActivity(), CatchActivity.class);
            intent.putExtra("stack", sb.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //这句是安卓4必须有的
            requireActivity().startActivity(intent);*/
            onWatchFaceLoadFailed();
            ILog.e("表盘加载失败:" + err);
            err.printStackTrace();
            ILog.writeThrowableToFile(err, new File(requireActivity().getExternalCacheDir(), SharedPreferencesUtil.getData(SharedPreferencesUtil.NOW_WATCHFACE, "") + "-" + System.currentTimeMillis() + ".log"));
            //FileIOUtils.writeFileFromString(watchFaceFolder + "/" + System.currentTimeMillis() + "watchface.log", err.toString());
        }
       
    }

    private void onWatchFaceLoadFailed() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        TextView tv = new TextView(requireActivity());
        tv.setText("表盘加载失败");
        watchFaceBox.addView(tv, lp);
    }
    
    private void onNoWatchFace() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        TextView tv = new TextView(requireActivity());
        tv.setGravity(Gravity.CENTER);
        tv.setText("还没有表盘哦\n快长按这里添加吧");
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
