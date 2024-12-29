package com.lazytong.launcher.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.ims.feature.MmTelFeature;
import com.lazytong.launcher.utils.ILog;
import com.lazytong.launcher.utils.MobileDataUtil;

public class MobileNetworkObserver {

    private Context mContext;
    private TelephonyManager telephonyManager;
    private NetWorkStateListener networkStateListener;
    private MNStateListener mMNStateListener;

    public MobileNetworkObserver(Context context) {
        mContext = context;
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public void register(MNStateListener listener) {
        mMNStateListener = listener;
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.S) {
            telephonyManager.listen(this.listener,PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        }else{
            networkStateListener = new NetWorkStateListener();
            telephonyManager.registerTelephonyCallback(mContext.getMainExecutor(),networkStateListener);
        }
    }

    public void unregister() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.S) {
            telephonyManager.listen(this.listener,PhoneStateListener.LISTEN_NONE);
        }else{
            telephonyManager.unregisterTelephonyCallback(networkStateListener);
        }
    }
    private PhoneStateListener listener = new PhoneStateListener(){
        @Override
        public void onDataConnectionStateChanged(int state) {
            super.onDataConnectionStateChanged(state);
            switch(state){
                case TelephonyManager.DATA_UNKNOWN:
                    mMNStateListener.onStateDisabled();
                    break;
                case TelephonyManager.DATA_DISCONNECTING:
                    mMNStateListener.onStateDisabled();
                    break;
                case TelephonyManager.DATA_DISCONNECTED:
                    mMNStateListener.onStateDisabled();
                    break;
                case TelephonyManager.DATA_CONNECTING:
                    mMNStateListener.onStateEnabled();
                    break;
                case TelephonyManager.DATA_CONNECTED:
                    mMNStateListener.onStateEnabled();
                    break;
                case TelephonyManager.DATA_SUSPENDED:
                    mMNStateListener.onStateDisabled();
                    break;
                default:
                    mMNStateListener.onStateDisabled();
                    
            }
        }
        
    };
    private class NetWorkStateListener extends TelephonyCallback implements TelephonyCallback.DataConnectionStateListener {
        @Override
        public void onDataConnectionStateChanged(int state, int type) {
            switch(state){
                case TelephonyManager.DATA_UNKNOWN:
                    mMNStateListener.onStateDisabled();
                    break;
                case TelephonyManager.DATA_DISCONNECTING:
                    mMNStateListener.onStateDisabled();
                    break;
                case TelephonyManager.DATA_DISCONNECTED:
                    mMNStateListener.onStateDisabled();
                    break;
                case TelephonyManager.DATA_CONNECTING:
                    mMNStateListener.onStateEnabled();
                    break;
                case TelephonyManager.DATA_CONNECTED:
                    mMNStateListener.onStateEnabled();
                    break;
                case TelephonyManager.DATA_SUSPENDED:
                    mMNStateListener.onStateDisabled();
                    break;
                default:
                    mMNStateListener.onStateDisabled();
                    
            }
        }
        
    }

    public interface MNStateListener {

        void onStateDisabled();

        void onStateEnabled();
    }
}
