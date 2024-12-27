package com.lazytong.launcher.services;

import android.app.ActivityManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcelable;
import android.os.Process;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.blankj.utilcode.util.AppUtils;
import com.lazytong.launcher.model.Notification;
import com.lazytong.launcher.utils.ILog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NotificationListenerService extends android.service.notification.NotificationListenerService {

    NotificationServiceReceiver receiver;

    private boolean listenerConnected;
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        listenerConnected = true;
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        listenerConnected = false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        ensureCollectorRunning();
        registerAsSystemService(this,this);
        receiver = new NotificationServiceReceiver();
        registerReceiver(receiver, new IntentFilter("com.lazytong.launcher.NOTIFICATION_LISTENER"));
        ILog.v("Notification Service Created!!!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Intent intent = new Intent("com.lazytong.launcher.NotificationReceived");
        intent.putExtra("notification", sbn2Notification(sbn));
        sendBroadcast(intent);
        ILog.v("Notification Received:" + sbn.getNotification().toString());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Intent intent = new Intent("com.lazytong.launcher.NotificationRemoved");
        intent.putExtra("notification", sbn2Notification(sbn));
        sendBroadcast(intent);
        ILog.v("Notification Removed:" + sbn.getNotification().toString());
    }

    public Notification sbn2Notification(StatusBarNotification sbn) {
        
        return new Notification(
                sbn.getNotification().getSmallIcon(),
                AppUtils.getAppName(sbn.getPackageName()),
                sbn.getNotification().extras.getCharSequence(android.app.Notification.EXTRA_TITLE, "Title").toString(),
                sbn.getNotification().extras.getCharSequence(android.app.Notification.EXTRA_TEXT, "Content").toString(),
                sbn.getPostTime(),
                sbn.getKey(),
                sbn.getNotification().contentIntent);
    }

    private void ensureCollectorRunning() {
        ComponentName collectorComponent = new ComponentName(this, /*NotificationListenerService Inheritance*/ NotificationListenerService.class);
        ILog.v("ensureCollectorRunning collectorComponent: " + collectorComponent);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean collectorRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null ) {
            ILog.w("ensureCollectorRunning() runningServices is NULL");
            return;
        }
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(collectorComponent)) {
                ILog.w("ensureCollectorRunning service - pid: " + service.pid + ", currentPID: " + Process.myPid() + ", clientPackage: " + service.clientPackage + ", clientCount: " + service.clientCount
                        + ", clientLabel: " + ((service.clientLabel == 0) ? "0" : "(" + getResources().getString(service.clientLabel) + ")"));
                if (service.pid == Process.myPid() /*&& service.clientCount > 0 && !TextUtils.isEmpty(service.clientPackage)*/) {
                    collectorRunning = true;
                }
            }
        }
        if (collectorRunning) {
            ILog.d("ensureCollectorRunning: collector is running");
            return;
        }
        ILog.d("ensureCollectorRunning: collector not running, reviving...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(new ComponentName(this,NotificationListenerService.class));
        }else {
            toggleNotificationListenerService();
        }
    }

    private void toggleNotificationListenerService() {
        ILog.d("toggleNotificationListenerService() called");
        ComponentName thisComponent = new ComponentName(this, /*getClass()*/ NotificationListenerService.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }
    public static void registerAsSystemService(android.service.notification.NotificationListenerService ns, Context ctx) {
        String className = "android.service.notification.NotificationListenerService";
        try {

            @SuppressWarnings("rawtypes")
            Class notificationListenerService = Class.forName(className);

            //Parameters Types
            //you define the types of params you will pass to the method
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[3];
            paramTypes[0]= Context.class;
            paramTypes[1]= ComponentName.class;
            paramTypes[2] = int.class;

            Method register = notificationListenerService.getMethod("registerAsSystemService", paramTypes);

            //Parameters of the registerAsSystemService method (see official doc for more info)
            Object[] params= new Object[3];
            params[0]= ctx;
            params[1]= new ComponentName(ctx.getPackageName(), ctx.getClass().getCanonicalName());
            params[2]= -1; // All user of the device, -2 if only current user
            // finally, invoke the function on our instance
            register.invoke(ns, params);

        } catch (ClassNotFoundException e) {
            ILog.e("Class not found");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            ILog.e("No such method");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            ILog.e("InvocationTarget");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ILog.e("Illegal access");
            e.printStackTrace();
        }

    }
    class NotificationServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (listenerConnected) {
                if (intent.getStringExtra("command").equals("clearAll")) {
                    NotificationListenerService.this.cancelAllNotifications();
                }
                if (intent.getStringExtra("command").equals("cancelMsg")) {
                    String key = intent.getStringExtra("key");
                    NotificationListenerService.this.cancelNotification(key);
                }

                if (intent.getStringExtra("command").equals("listAll")) {
                    List<Notification> list = new ArrayList<>();
                    for (StatusBarNotification sbn : NotificationListenerService.this.getActiveNotifications()) {
                        list.add(sbn2Notification(sbn));
                    }
                    Intent intent2 = new Intent("com.lazytong.launcher.ListAllNotification");
                    intent2.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) list);
                    sendBroadcast(intent2);
                }
                if (intent.getStringExtra("command").equals("remove")) {
                    String key = intent.getStringExtra("key");
                    NotificationListenerService.this.cancelNotification(key);
                }

            }
        }
    }
}
