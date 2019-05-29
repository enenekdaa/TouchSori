package com.sori.touchsori.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.service.GyroService;
import com.sori.touchsori.service.TouchService;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.LogUtil;

import static com.sori.touchsori.utill.Define.TOUCH_ACTION_EMERGNECY;


public class GyroReceiver extends BroadcastReceiver {
    private static final String TAG = GyroReceiver.class.getSimpleName();       // 디버그 태그
    private SoriApplication mApp;                                          // 전역 (Application) 변수
    private Context mContext;                                                   // 콘텍스트

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onReceive() -> Start !!!");


        LogUtil.d(TAG, "onReceive() -> action : " + intent.getAction());

        mContext = context.getApplicationContext();
        if (mApp == null) mApp = (SoriApplication) mContext;

        if (Define.ACTION_GYRO_SERVICE_STOP.endsWith(intent.getAction()) ) {
            Intent touchServiceIntent = new Intent(mContext, TouchService.class);
            touchServiceIntent.setAction(TOUCH_ACTION_EMERGNECY);
            touchServiceIntent.putExtra("start", Define.TOUCH_SERVICE_TYPE_END);
            mContext.startService(touchServiceIntent);

            LogUtil.d(TAG, "[GYRO] Receive touch stop service");

            mApp.setIsGyroStopService(true);

        }else if(Define.ACTION_GYRO_EVENT_UPDATE.endsWith(intent.getAction())) {
            GyroService.getInstance(mContext).registerGyoroEventAlarm();
            GyroService.getInstance(mContext).registerGyroListener();
        }
    }

    private PendingIntent pi_gyro_update = null;
    private void setGyroUpdateAlarm() {
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, GyroReceiver.class);
        intent.setAction(Define.ACTION_GYRO_EVENT_UPDATE);
        pi_gyro_update = PendingIntent.getBroadcast(
                mContext,
                Define.ALARM_ID_GYRO_EVENT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerTime = System.currentTimeMillis() + 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {               // 마시멜로우 (Ver.6.0 이상)
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(triggerTime, pi_gyro_update);
            alarmManager.setAlarmClock(info, pi_gyro_update);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   // 키켓 (Ver.4.4 이상)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pi_gyro_update);
        } else {                                                            // 기타
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pi_gyro_update);
        }
    }
}
