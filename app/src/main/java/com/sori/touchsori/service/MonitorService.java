package com.sori.touchsori.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.sori.touchsori.R;
import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.intro.IntroActivity;
import com.sori.touchsori.receiver.ScreenRecevier;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.EtcUtil;
import com.sori.touchsori.utill.LogUtil;

import java.util.Calendar;


import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.sori.touchsori.utill.Define.MONITOR_SERVICE_START;
import static com.sori.touchsori.utill.Define.MONITOR_SERVICE_STOP;
import static com.sori.touchsori.utill.Define.TOUCH_ACTION_EMERGNECY;

public class MonitorService extends Service {
    private static final String TAG = MonitorService.class.getSimpleName();


    private Context mContext;                                                           // 콘텍스트


    private SoriApplication mApp;                                                  // 전역 (Application) 변수
    private BroadcastReceiver screenReceiver;                                           // 스크린 리시버

    private BroadcastReceiver deviceIdleModeReceiver;                                   // Doze모드 리시버

    public MonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate() -> Start !!!");

        // 콘텍스트
        mContext = getApplicationContext();
        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;

        // 스크린 ON-OFF 리시버 등록
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenReceiver = new ScreenRecevier();
        mContext.registerReceiver(screenReceiver, screenFilter);



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 (Ver.6.0)
//            // Doze모드 리시버
//            IntentFilter deviceIdleModeilter = new IntentFilter();
//            deviceIdleModeilter.addAction(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
//            deviceIdleModeReceiver = new DeviceIdleModeReceiver();
//            mContext.registerReceiver(deviceIdleModeReceiver, deviceIdleModeilter);
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "onStartCommand() -> Start !!!");
        if (Build.VERSION.SDK_INT >= LOLLIPOP) { // 롤리팝 (Ver.5.0)

        }


        // 안심귀가 설정 여부

      ///   안심귀가 시작 시간 알람 등록
       //      mApp.registerEmergencyStartAlarm();

        // 안심귀가 종료 시간 알람 등록
        //     mApp.registerEmergencyEndAlarm();


        if (intent == null) return START_REDELIVER_INTENT;
        String action = intent.getAction();
        LogUtil.d(TAG, "onStartCommand() -> action : " + action);

        if (null != action) {
            switch (action) {
                case MONITOR_SERVICE_START: {
                    // Monotor ForegroundService 시작
//                    setMonitorServiceNotification();
                    break;
                }
                case MONITOR_SERVICE_STOP: {
//                    stopForeground(true);
                    break;
                }
                default:
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy() -> Start !!!");

//        // 리졸버 해제 - 2018.1.23 영상메시지 제거
//        if (mResolver != null) {
//            mResolver.unregisterContentObserver(mCameraObserver);
//            mResolver = null;
//        }
//        // 카메라 리시버 해제
//        if (cameraReceiver != null) {
//            unregisterReceiver(cameraReceiver);
//            cameraReceiver = null;
//        }
//        // 스크린 리시버 해제
//        if (screenReceiver != null) {
//            unregisterReceiver(screenReceiver);
//            screenReceiver = null;
//        }
//
//        // 헤드셋 리시버 해제
//        if (headsetReceiver != null) {
//            unregisterReceiver(headsetReceiver);
//            headsetReceiver = null;
//        }
//
//        // 배터리 리시버 해제
//        if (batteryReceiver != null) {
//            unregisterReceiver(batteryReceiver);
//            batteryReceiver = null;
//        }
//
//        // Doze모드 리시버 해제
//        if (deviceIdleModeReceiver != null) {
//            unregisterReceiver(deviceIdleModeReceiver);
//            deviceIdleModeReceiver = null;
//        }
        super.onDestroy();
    }

    /**
     * 터치소리 서비스 시작
     */
    private void startTouchsoriService() {
        LogUtil.i(TAG, "startTouchsoriService() -> Start !!!");
        // 전역 (Application) 변수
//        if (mApp == null) mApp = (TouchSoriApplication) mContext;
        mApp = (SoriApplication) mContext;

        LogUtil.d(TAG, "startTouchsoriService() -> isInitialized : " + mApp.isInitialized());
        // 터치소리 설정 확인
        if (mApp.isInitialized()) {
            // 터치소리 서비스 중지 해제
            mApp.setIsServiceStop(false);
            LogUtil.d(TAG, "startTouchsoriService() -> getIsServiceStop() : " + mApp.getIsServiceStop());

            // 안심귀가 사용 유무 확인
            if (mApp.isInitialized()) {
                if (mApp.checkEmergencyTime(true, true)) {
                    // 터치소리 서비스 시작
                    Intent intent = new Intent(mContext, TouchService.class);
                    intent.setAction(TOUCH_ACTION_EMERGNECY);
                    intent.putExtra("start", Define.TOUCH_SERVICE_TYPE_START);
                    mContext.startService(intent);

                } else {
                    //서비스 종료 시 Gyroscope event도 종료
                    if(EtcUtil.isGyroTouchServiceStopDevice() && (false == mApp.getIsSoundParserStop())) {
                        GyroService.getInstance(mContext).stopGyronfo();
                    }

                    // 터치소리 서비스 중지
                    Intent intent = new Intent(mContext, TouchService.class);
                    intent.setAction(TOUCH_ACTION_EMERGNECY);
                    intent.putExtra("start", Define.TOUCH_SERVICE_TYPE_END);
                    mContext.startService(intent);
                }
            }
        }
    }

    /**
     * 모니터 서비스 등록
     */
    public void setMonitorServiceNotification() {
        Intent notificationIntent = new Intent(this, IntroActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "monitor_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Monitor Service Channel",
                    NotificationManager.IMPORTANCE_LOW);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setContentTitle(getString(R.string.app_name)).setPriority(Notification.PRIORITY_MIN)
                .setGroup(Define.NOTIFICATION_ID_FOREGROUND_GROUP_KEY)
                .setContentText("실행")
                .setContentIntent(pendingIntent).build();

        startForeground(Define.NOTIFICATION_ID_FOREGROUND_SERVICE_MONOTOR, builder.build());
    }

    /**
     * Foreground 서비스 종료
     */
    private void stopForegroundNotification() {

        stopForeground(true);
    }
}