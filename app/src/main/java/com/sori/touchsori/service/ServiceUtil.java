package com.sori.touchsori.service;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import static com.sori.touchsori.utill.Define.MONITOR_SERVICE_START;
import static com.sori.touchsori.utill.Define.MONITOR_SERVICE_STOP;

/**
 * Created by Dongnam on 2017. 6. 15..
 */

public class ServiceUtil {
    public static final String TAG = ServiceUtil.class.getSimpleName();    // 디버그 태그

    /**
     * 서비스 실행 유무 확인
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startMonitorService(Context context) {
        Intent intent = new Intent(context, MonitorService.class);
        intent.setAction(MONITOR_SERVICE_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

    }

    public void stopMonitorService(Context context) {
        // 모니터링 서비스 중지
        Intent intent = new Intent(context, MonitorService.class);
        intent.setAction(MONITOR_SERVICE_STOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public void startTouchSoriService(Context context, String action, String extraName, int extraValue) {
        Intent intent = new Intent(context, TouchService.class);
        intent.setAction(action);
        intent.putExtra(extraName, extraValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
//        context.startService(intent);
    }

    public void stopTouchSoriService(Context context, String action, String extraName, int extraValue) {
        Intent intent = new Intent(context, TouchService.class);
        intent.setAction(action);
        intent.putExtra(extraName, extraValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
