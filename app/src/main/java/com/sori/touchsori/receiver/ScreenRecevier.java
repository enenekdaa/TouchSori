package com.sori.touchsori.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.service.GyroService;
import com.sori.touchsori.utill.EtcUtil;
import com.sori.touchsori.utill.LogUtil;

import static android.app.PendingIntent.getBroadcast;
import static android.content.Context.ALARM_SERVICE;
import static com.sori.touchsori.utill.Define.ACTION_WAKE_LOCK;
import static com.sori.touchsori.utill.Define.ALARM_ID_WAKE_LOCK;


/**
 * Created by Dongnam on 2017. 6. 7..
 */

public class ScreenRecevier extends BroadcastReceiver {
    private static final String TAG = ScreenRecevier.class.getSimpleName(); // 디버그 태그
    private Context mContext;                                               // 콘텍스트

    private SoriApplication mApp;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onReceive() -> Start !!!");
        if (intent == null) return;
        LogUtil.d(TAG, "onReceive() -> action : " + intent.getAction());

        // 콘텍스트
        mContext = context.getApplicationContext();



        if (mApp == null) mApp = (SoriApplication) mContext;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) { // 롤리팝 (Ver.5.0)
            if (Intent.ACTION_SCREEN_ON.equalsIgnoreCase(intent.getAction())) {
                if(EtcUtil.isGyroTouchServiceStopDevice()) {
                    //   GyroService.getInstance(mContext).stopGyronfo();

                    LogUtil.d(TAG, "onReceive() -> getLocationCount : " + mApp.getLocationCount());
                    mApp.startTouchsoriService(TAG);
                    // 문자 등으로 screenOn이 된 경우 초기화
                    if ((mApp.getIsSoundParserStop()) && mApp.isGyroStopService()) {
                        GyroService.getInstance(mContext).initChangeGyro();
                        GyroService.getInstance(mContext).startTouchsoriService();
                        mApp.setIsGyroStopService(false);

                    }
                }
            } else if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(intent.getAction())) {
                mApp.startTouchsoriService(TAG);
                if(EtcUtil.isGyroTouchServiceStopDevice()
                        || mApp.getIsSoundParserStop()) {
                    if(false == mApp.isMessageSending()) {
                        GyroService.getInstance(mContext).startGyroInfo();
                    }
                }
            }
        }
    }

    /**
     * WakeLock 알람 등록
     */
    private void registerWakeLockAlarm() {
        Intent intent = new Intent(mContext, WakeLockReceiver.class);
        intent.setAction(ACTION_WAKE_LOCK);
        PendingIntent pendingIntent = getBroadcast(
                mContext,
                ALARM_ID_WAKE_LOCK,
                intent,
                0);

        // TODO WakeLock 알람 타임
//        long intervalTime = 1800 * 1000; // 30분
//        long intervalTime = 1200 * 1000; // 20분
//        long intervalTime = 600 * 1000; // 10분
//        long intervalTime = 60 * 1000; // 1분
        long intervalTime = 10 * 1000; // 10초
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += intervalTime;

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, intervalTime, pendingIntent);
    }

    /**
     * WakeLock 알람 해제
     */
    private void unRegisterWakeLockAlarm() {
        Intent intent = new Intent(mContext, WakeLockReceiver.class);
        intent.setAction(ACTION_WAKE_LOCK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                ALARM_ID_WAKE_LOCK,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /**
     * WakeLock 알람 등록 여부
     *
     * @return
     */
    private boolean isRegisterWakeLockAlarm() {
        Intent intent = new Intent(mContext, WakeLockReceiver.class);
        intent.setAction(ACTION_WAKE_LOCK);
        PendingIntent pendingIntent = getBroadcast(
                mContext,
                ALARM_ID_WAKE_LOCK,
                intent,
                PendingIntent.FLAG_NO_CREATE);
        return (pendingIntent != null);
    }
}