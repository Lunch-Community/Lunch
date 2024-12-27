package com.lazytong.launcher.listener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import com.lazytong.launcher.utils.ILog;

public class WLANListener {

    private Context mContext;
    private WLANBroadcastReceiver receiver;
    private WLANStateListener mWLANStateListener;

    public WLANListener(Context context) {
        mContext = context;
        receiver = new WLANBroadcastReceiver();
    }

    public void register(WLANStateListener listener) {
        mWLANStateListener = listener;
        if (receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            mContext.registerReceiver(receiver, filter);
        }
    }

    public void unregister() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    private class WLANBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                /** wifi状态改变 */
                if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                    if (mWLANStateListener != null) {
                        ILog.e("WLANBroadcastReceiver --> onReceive--> WIFI_STATE_CHANGED_ACTION");
                        mWLANStateListener.onStateChanged();
                    }
                }
                /**
                 * WIFI_STATE_DISABLED    WLAN已经关闭
                 * WIFI_STATE_DISABLING   WLAN正在关闭
                 * WIFI_STATE_ENABLED     WLAN已经打开
                 * WIFI_STATE_ENABLING    WLAN正在打开
                 * WIFI_STATE_UNKNOWN     未知
                 */
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (state) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        if (mWLANStateListener != null) {
                            ILog.e("WLANBroadcastReceiver --> onReceive--> WIFI_STATE_DISABLED");
                            mWLANStateListener.onStateDisabled();
                        }
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        if (mWLANStateListener != null) {
                            ILog.e("WLANBroadcastReceiver --> onReceive--> WIFI_STATE_DISABLING");
                            mWLANStateListener.onStateDisabling();
                        }
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        if (mWLANStateListener != null) {
                            ILog.e("WLANBroadcastReceiver --> onReceive--> WIFI_STATE_ENABLED");
                            mWLANStateListener.onStateEnabled();
                        }
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        if (mWLANStateListener != null) {
                            ILog.e("WLANBroadcastReceiver --> onReceive--> WIFI_STATE_ENABLING");
                            mWLANStateListener.onStateEnabling();
                        }
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        if (mWLANStateListener != null) {
                            ILog.e("WLANBroadcastReceiver --> onReceive--> WIFI_STATE_UNKNOWN");
                            mWLANStateListener.onStateUnknow();
                        }
                        break;
                }
            }
        }
    }


    public interface WLANStateListener {
        void onStateChanged();

        void onStateDisabled();

        void onStateDisabling();

        void onStateEnabled();

        void onStateEnabling();

        void onStateUnknow();

    }
}
