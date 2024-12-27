package com.lazytong.launcher.listener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothObserver {
    private Context context;
    private BluetoothStateListener listener;
    private BluetoothStateReceiver receiver;

    public BluetoothObserver(Context context) {
        this.context = context;
        receiver = new BluetoothStateReceiver();
    }

    public void register(BluetoothStateListener listener) {
    	this.listener = listener;
        if(receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            context.registerReceiver(receiver, filter);
        }
    }
    public void unregister() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }
    private class BluetoothStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (state) {
                            case BluetoothAdapter.STATE_ON:
                                listener.onEnabled();
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                listener.onDisabled();
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        listener.onConnected();
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        listener.onDisconnected();
                        break;
                }
            }
        }
    }

    public interface BluetoothStateListener {
        void onEnabled();

        void onDisabled();

        void onConnected();

        void onDisconnected();
    }
}
