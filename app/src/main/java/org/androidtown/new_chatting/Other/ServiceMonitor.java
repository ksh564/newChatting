package org.androidtown.new_chatting.Other;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import org.androidtown.new_chatting.Network.SocketService;

import java.util.List;

/**
 * Created by 김승훈 on 2017-08-02.
 */
public class ServiceMonitor {
    private static ServiceMonitor instance;
    private AlarmManager am;
    private Intent intent;
    private PendingIntent sender;
    private long interval = 5000;

    private ServiceMonitor() {}
    public static synchronized ServiceMonitor getInstance() {
        if (instance == null) {
            instance = new ServiceMonitor();
        }
        return instance;
    }


    public static class MonitorBR extends BroadcastReceiver {
        @Override


        public void onReceive(Context context, Intent intent) {

            if (isRunningService(context, SocketService.class) == false) {
                context.startService(new Intent(context, SocketService.class));
                }
           }
    }

    public void setInterVal(long interVal) {
        this.interval = interVal;
    }

    public void startMonitoring(Context context){
        am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, MonitorBR.class);
        sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, sender);
    }

    public void stopMonitoring(Context context) {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, MonitorBR.class);
        sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(sender);
        am = null;
        sender = null;
    }

    public boolean isMonitoring() {
        return (SocketService.mThread == null || SocketService.mThread.isAlive() == false) ? false : true;
    }


    private static boolean isRunningService(Context context, Class<?> cls) {
        boolean isRunning = false;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (info != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : info) {
                ComponentName compName = serviceInfo.service;
                String className = compName.getClassName();

                if (className.equals(cls.getName())) {
                    isRunning = true;
                    break;
                }
            }
        }

        return isRunning;
    }
}
