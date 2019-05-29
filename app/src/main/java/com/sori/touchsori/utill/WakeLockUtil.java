package com.sori.touchsori.utill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

/**
 * Created by Dongnam on 2017. 6. 8..
 */

public class WakeLockUtil {
    private static final String TAG = WakeLockUtil.class.getSimpleName();   // 디버그 태그
    private static PowerManager.WakeLock mWakeLock;

    private static Context mContext = null;

    /**
     * wakeLockWithScreenOn
     * @param context
     */
    @SuppressLint("InvalidWakeLockTag")
    public static void wakeLock(Context context) {
        LogUtil.i(TAG, "wakeLock() -> Start !!!");
        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TOUCHSORI");
        mWakeLock.acquire();
        mContext = context.getApplicationContext();

    }

    /**
     * WakeLock
     * @param context
     */
    @SuppressLint("InvalidWakeLockTag")
    public static void wakeLockWithScreenOn(Context context) {
        LogUtil.i(TAG, "wakeLockWithScreenOn() -> Start !!!");
        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "TOUCHSORI");
        mWakeLock.acquire();

    }


    /**
     * Release
     */
    public static void releaseWakeLock() {
        LogUtil.i(TAG, "releaseWakeLock() -> Start !!!");
        if (mWakeLock != null) {

            mWakeLock.release();
            mWakeLock = null;
        }
    }
    @SuppressLint("InvalidWakeLockTag")
    public static void wakeLockAcquire(Context context) {
        LogUtil.i(TAG, "releaseWakeLock() -> Start !!!");

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE );
       PowerManager.WakeLock wakeLock = powerManager.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TOUCHSORI" );
        wakeLock.acquire(1000);
        wakeLock.release();
    }

}
