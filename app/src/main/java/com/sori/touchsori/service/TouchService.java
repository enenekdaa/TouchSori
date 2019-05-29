package com.sori.touchsori.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.callback.CallbackSound;
import com.sori.touchsori.receiver.SoundReceiver;
import com.sori.touchsori.task.SoundPaserTask;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.LogUtil;
import com.sori.touchsori.utill.WakeLockUtil;

import java.util.Timer;
import java.util.TimerTask;


import static android.app.PendingIntent.getBroadcast;
import static com.sori.touchsori.utill.Define.ACTION_SOUND_PARSER_RUNNING;
import static com.sori.touchsori.utill.Define.ALARM_ID_SOUNDER_PARSER;
import static com.sori.touchsori.utill.Define.MONITOR_SERVICE_START;
import static com.sori.touchsori.utill.Define.MONITOR_SERVICE_STOP;
import static com.sori.touchsori.utill.Define.PARSE_TYPE_CALL;
import static com.sori.touchsori.utill.Define.PARSE_TYPE_EMERGENCY;
import static com.sori.touchsori.utill.Define.PARSE_TYPE_MEDIA;
import static com.sori.touchsori.utill.Define.PARSE_TYPE_NONE;
import static com.sori.touchsori.utill.Define.SOUND_PARSE_RESULT_BUTTON;
import static com.sori.touchsori.utill.Define.SOUND_PARSE_RESULT_BUTTON_READY;
import static com.sori.touchsori.utill.Define.SOUND_PARSE_RESULT_CANCEL;
import static com.sori.touchsori.utill.Define.SOUND_PARSE_RESULT_NOISE;
import static com.sori.touchsori.utill.Define.TOUCH_ACTION_AFTER_CALL;
import static com.sori.touchsori.utill.Define.TOUCH_ACTION_AFTER_CAMERA;
import static com.sori.touchsori.utill.Define.TOUCH_ACTION_EMERGNECY;
import static com.sori.touchsori.utill.Define.TOUCH_ACTION_START;

public class TouchService extends Service {
    private static final String TAG = TouchService.class.getSimpleName();   // 디버그 태그

    public static CallbackSound callbackSound;                              // 사운드 콜백
    private Context mContext;                                               // 콘텍스트
    private SoriApplication mApp;                                      // 전역 (Application) 변수
    private String mediaPath = "";                                          // 파일 경로
    private int serviceMode;                                                // 서비스 모드

    private final static int SOUND_PARSE_HANDLE_EVENT_NONE = 0;             // 없음
    private final static int SOUND_PARSE_HANDLE_EVENT_START = 1;            // 시작
    private final static int SOUND_PARSE_HANDLE_EVENT_STOP = 2;             // 종료
    private final static int SOUND_PARSE_HANDLE_EVENT_UPDATE = 3;           // 업데이트

//    public static boolean bStart;

    private long savedTime = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.i(TAG, "onCreate() -> Start !!!");
        super.onCreate();

        // 콘텍스트
        mContext = getApplicationContext();

        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;

        IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(mLocaleChangedReceiver, filter);

        // SoundParser
        callbackSound = new CallbackSound() {
            @Override
            public void OnSoundParseComplete(int resultType) {
                LogUtil.d(TAG, "OnSoundParseComplete() -> resultType : " + resultType);
                LogUtil.d(TAG, "OnSoundParseComplete() -> serviceMode : " + serviceMode);


                // TODO 롤리팝 이하 버전 Doze WakeLock
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // 마시멜로우 (Ver.6.0)
                    long currentTime = System.currentTimeMillis();
                    long delayed = (currentTime - savedTime);
                    if (delayed > 10000) {
                        if (resultType != SOUND_PARSE_RESULT_BUTTON) {

                            WakeLockUtil.wakeLockAcquire( mContext );
                        }
                    }
                    savedTime = System.currentTimeMillis();
                }

                if (mApp == null) mApp = (SoriApplication) mContext;

                //2018.01.24 Notification에서 Off시 Alarm 등록되도록 하는 문제로 주석처리
//                if (null == callbackSound || mApp.getIsSoundParserStop()) {
//                    FileUtil.writeLog(mContext,TAG, "OnSoundParseComplete()  null == callbackSound || mApp.getIsSoundParserStop()");
////                    stopSelf();
//                    // 사운드 알람 등록
//                    registerSoundParserTaskAlarm();
//                    return;
//                }

                LogUtil.d(TAG, "OnSoundParseComplete() -> getIsServiceStop : " + mApp.getIsServiceStop());

                // 터치 서비스 중지
                if (mApp.getIsServiceStop()) return;

                // 안심귀가 시간체크 중지
                if (serviceMode == PARSE_TYPE_EMERGENCY
                        && !mApp.checkEmergencyTime(true, true) && false == mApp.getIsSoundParserStop()) {
//                    bStart = false;


                    stopSelf();
                    // 사운드 알람 등록
                    registerSoundParserTaskAlarm();
                    return;
                }
                // SoundParserTask 중지
                if (false == mApp.getIsSoundParserStop() && resultType <= SOUND_PARSE_RESULT_CANCEL) {
                    // 사운드 알람 등록
//                    FileUtil.writeLog(mContext, TAG, "OnSoundParseComplete() resultType <= SOUND_PARSE_RESULT_CANCEL");

                    registerSoundParserTaskAlarm();
                    return;
                }

                // 버튼 인식
                if (resultType == SOUND_PARSE_RESULT_BUTTON) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(1000, 10));
                    } else {
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
                    }

                    // TODO WakeLock 테스트
//                    if (serviceMode == PARSE_TYPE_EMERGENCY) {
//                        WakeLockUtil.wakeLock(mContext);
//                        WakeLockUtil.releaseWakeLock();
//                    }
                }

                // 버튼 일부 인식
                if (resultType == SOUND_PARSE_RESULT_BUTTON_READY) {
                    // 버튼 타입
                    int buttonType = mApp.utils.getButtonType();
                    new SoundPaserTask(callbackSound, mContext).execute(buttonType);
                    return;
                }

                LogUtil.d(TAG, "OnSoundParseComplete() -> reqMode : " + serviceMode);
                switch (serviceMode) {
                    case PARSE_TYPE_EMERGENCY: {
                        // 전역 (Application) 변수
                        if (mApp == null) mApp = (SoriApplication) mContext;
                        if (resultType == SOUND_PARSE_RESULT_NOISE) {
                            resqutParser(SOUND_PARSE_HANDLE_EVENT_START, PARSE_TYPE_EMERGENCY);
                        } else if (resultType == SOUND_PARSE_RESULT_BUTTON) {
                            // 메시지 서비스 시작
                            Intent intent = new Intent(mContext, TouchMessageService.class);
                            intent.setAction(Define.MSG_SERVICE_ACTION_EMERGENCY);
                            startService(intent);

                            // TODO 테스트 (버튼인식)
//                            if (mApp == null) mApp = (TouchSoriApplication) mContext;
//                            mApp.setIsServiceStop(false);
//                            resqutParser(SOUND_PARSE_HANDLE_EVENT_START, PARSE_TYPE_EMERGENCY);

                            new Timer().schedule(new TimerTask() {
                                public void run() {
                                    // 버튼 타입
                                    int buttonType = mApp.utils.getButtonType();
                                    new SoundPaserTask(callbackSound, mContext).execute(buttonType);
                                }
                            }, 200);
                        }
                        break;
                    }
//                    case PARSE_TYPE_MEDIA:
//                        if (resultType == SOUND_PARSE_RESULT_BUTTON && !TextUtils.isEmpty(mediaPath)) {
//                            mCameraHandler.removeMessages(CAMERA_EVENT_START);      // 시작 메시지 중지
//                            mCameraHandler.removeMessages(CAMERA_EVENT_UPDATE);     // 업데이트 메시지 중지
//                            mCameraHandler.removeMessages(CAMERA_EVENT_STOP);       // 중지 메시지 중지
//
//                            camera_time_count = -1;
//
//                            // 전역 (Application) 변수
//                            if (mApp == null) mApp = (SoriApplication) mContext;
//
//                            LogUtil.d(TAG, "onCreate() -> PARSE_TYPE_MEDIA");
//                            // 터치소리 서비스 종료
//                            mApp.setIsServiceStop(true);
//                            LogUtil.d(TAG, "onCreate() -> getIsServiceStop : " + mApp.getIsServiceStop());
//
//                            // 이미지 뷰 화면 이동
//                            Intent intent = new Intent(mContext, ImageViewActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("file_path", mediaPath);
//                            startActivity(intent);
//                        }
//                        break;
                    case PARSE_TYPE_CALL:
                        if (resultType == SOUND_PARSE_RESULT_BUTTON) {

//                            mCallHandler.removeMessages(CALL_EVENT_START);      // 시작 메시지 중지
//                            mCallHandler.removeMessages(CALL_EVENT_UPDATE);     // 업데이트 메시지 중지
//                            mCallHandler.removeMessages(CALL_EVENT_STOP);       // 중지 메시지 중지
//
//                            call_time_count = -1;
//
//                            // 전역 (Application) 변수
//                            if (mApp == null) mApp = (SoriApplication) mContext;
//
//                            // 터치소리 서비스 종료
//                            mApp.setIsServiceStop(true);
//                            LogUtil.d(TAG, "onCreate() -> getIsServiceStop : " + mApp.getIsServiceStop());
//
//                            // 통화 종료 후 버튼 화면 이동
//                            Intent intent = new Intent(mContext, AfterCallActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
                        }
                        break;
                    case PARSE_TYPE_NONE:
                        break;
                    default:
                        break;
                }
            }
        };
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "onStartCommand() -> Start !!!");

        LogUtil.d(TAG, "onStartCommand() -> intent : " + intent);

        if (intent == null) return  START_REDELIVER_INTENT;
        String action = intent.getAction();
        LogUtil.d(TAG, "onStartCommand() -> action : " + action);

        switch (action) {
            case TOUCH_ACTION_START: {
                break;
            }
            case TOUCH_ACTION_EMERGNECY: {
                int type = intent.getIntExtra("start", Define.TOUCH_SERVICE_TYPE_END);
                LogUtil.d(TAG, "onStartCommand() -> type : " + type);

                // 전역 (Application) 변수
                if (mApp == null) mApp = (SoriApplication) mContext;

                switch(type) {
                    case Define.TOUCH_SERVICE_TYPE_START:
                        resqutParser(SOUND_PARSE_HANDLE_EVENT_START, PARSE_TYPE_EMERGENCY);
//                        if (mApp.checkEmergencyTime(true, true)) {
//
//                            resqutParser(SOUND_PARSE_HANDLE_EVENT_START, PARSE_TYPE_EMERGENCY);
//                            // ForegroundNotification 시작
//                       //     startForegroundNotificationOn();
//                        } else if(mApp.checkEmergencyTime(true, false) && mApp.getIsSoundParserStop()) {
//
//                            resqutParser(SOUND_PARSE_HANDLE_EVENT_START, PARSE_TYPE_EMERGENCY);
//                            // ForegroundNotification 시작
//                    //        startForegroundNotificationOff();
//                        }else {
//
//                            resqutParser(SOUND_PARSE_HANDLE_EVENT_STOP, PARSE_TYPE_EMERGENCY);
//
//                            stopForegroundNotification();
//                        }
                        break;
                    case Define.TOUCH_SERVICE_TYPE_END:
                        resqutParser(SOUND_PARSE_HANDLE_EVENT_STOP, PARSE_TYPE_EMERGENCY);
                        // ForegroundNotification 종료
                        stopForegroundNotification();
                        break;
                    case Define.TOUCH_SERVICE_TYPE_LOCATION:
                        resqutParser(SOUND_PARSE_HANDLE_EVENT_STOP, PARSE_TYPE_EMERGENCY);
                        // ForegroundNotification 종료
//                        stopForegroundNotification();
                        break;
                    case Define.TOUCH_SERVICE_TYPE_STOP:
                        resqutParser(SOUND_PARSE_HANDLE_EVENT_START, PARSE_TYPE_EMERGENCY);
                        // ForegroundNotification 중지
                     //   startForegroundNotificationOff();
                        break;
                }
                break;
            }
            case TOUCH_ACTION_AFTER_CAMERA: {
                mediaPath = intent.getStringExtra("file_path");
                resqutParser(SOUND_PARSE_HANDLE_EVENT_START, PARSE_TYPE_MEDIA);

                // 마시멜루우 버전 이하이면 카메라 핸들러 시작
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { // 롤리팝 (Ver.5.0)
                    mCameraHandler.removeMessages(CAMERA_EVENT_START);      // 시작 메시지 중지
                    mCameraHandler.removeMessages(CAMERA_EVENT_UPDATE);     // 업데이트 메시지 중지
                    mCameraHandler.removeMessages(CAMERA_EVENT_STOP);       // 중지 메시지 중지
                    mCameraHandler.sendEmptyMessage(CAMERA_EVENT_START);    // 시작 메시지 시작
                } else {
                    if (Build.MODEL.contains("SM-N750")) {
                        mCameraHandler.removeMessages(CAMERA_EVENT_START);      // 시작 메시지 중지
                        mCameraHandler.removeMessages(CAMERA_EVENT_UPDATE);     // 업데이트 메시지 중지
                        mCameraHandler.removeMessages(CAMERA_EVENT_STOP);       // 중지 메시지 중지
                        mCameraHandler.sendEmptyMessage(CAMERA_EVENT_START);    // 시작 메시지 시작
                    }
                }
                break;
            }
            case TOUCH_ACTION_AFTER_CALL: {
                resqutParser(SOUND_PARSE_HANDLE_EVENT_START, PARSE_TYPE_CALL);

                mCallHandler.removeMessages(CALL_EVENT_START);      // 시작 메시지 중지
                mCallHandler.removeMessages(CALL_EVENT_UPDATE);     // 업데이트 메시지 중지
                mCallHandler.removeMessages(CALL_EVENT_STOP);       // 중지 메시지 중지
                mCallHandler.sendEmptyMessage(CALL_EVENT_START);    // 시작 메시지 시작
                break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy()");

        callbackSound = null;
        unregisterReceiver(mLocaleChangedReceiver);
        super.onDestroy();
    }

    /**
     * 파싱타입 작업
     * @param type
     * @param serviceMode
     */
    private void resqutParser(int type, int serviceMode) {
        this.serviceMode = serviceMode;
        LogUtil.d(TAG, "resqutParser() -> serviceMode : " + this.serviceMode);

        switch (type) {
            case SOUND_PARSE_HANDLE_EVENT_START: {
                // 사운드 알람 등록
                registerSoundParserTaskAlarm();
                break;
            }
            case SOUND_PARSE_HANDLE_EVENT_UPDATE: {
                // 사운드 알람 등록
                registerSoundParserTaskAlarm();
                break;
            }
            case SOUND_PARSE_HANDLE_EVENT_STOP: {
                // 서비스 종료
                stopSelf();
                // 사운드 알람 등록
                registerSoundParserTaskAlarm();
                break;
            }
            case SOUND_PARSE_HANDLE_EVENT_NONE: {
                // 서비스 종료
                stopSelf();
                // 사운드 알람 등록
                registerSoundParserTaskAlarm();
                break;
            }
            default:
                break;
        }
    }

    /**
     * SoundParserTask 알람 등록
     */
    private void registerSoundParserTaskAlarm() {
        LogUtil.i(TAG, "registerSoundParserTaskAlarm() -> Start !!!");


        Intent intent = new Intent(mContext, SoundReceiver.class);
        intent.setAction(ACTION_SOUND_PARSER_RUNNING);
        PendingIntent pendingIntent = getBroadcast(
                mContext,
                ALARM_ID_SOUNDER_PARSER,
                intent,
                0);
        LogUtil.d(TAG, "registerSoundParserTaskAlarm() -> pendingIntent : " + pendingIntent);


        long firstTime = System.currentTimeMillis();
        firstTime += Define.DELAY_TIME_SERVICE_2500;

        AlarmManager alarmManager = (AlarmManager) getSystemService( mContext.ALARM_SERVICE );
        if (Build.MODEL.contains( "LG" )) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo( firstTime, null );
                alarmManager.setAlarmClock( info, pendingIntent );
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {               // 마시멜로우 (Ver.6.0 이상)
            alarmManager.setAndAllowWhileIdle( AlarmManager.RTC_WAKEUP, firstTime, pendingIntent );

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   // 키켓 (Ver.4.4 이상)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,  firstTime, pendingIntent);
        } else {                                                            // 기타
            alarmManager.set(AlarmManager.RTC_WAKEUP,  firstTime, pendingIntent);
        }
    }

    /**
     * 카메라 핸들러
     */
    private final int CAMERA_EVENT_START = 1;   // 카메라 이벤트 시작
    private final int CAMERA_EVENT_UPDATE = 2;  // 카메라 작동 이벤트 시간 업데이트
    private final int CAMERA_EVENT_STOP = 3;    // 카메라 이벤트 중지

    private int camera_time_count;
    private Handler mCameraHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CAMERA_EVENT_START:
                    camera_time_count = 11;
                    this.sendEmptyMessage(CAMERA_EVENT_UPDATE);
                    break;
                case CAMERA_EVENT_UPDATE:
                    if (camera_time_count == -1) return;
                    camera_time_count--;
                    // TODO 타이머 테스트
//                    ToastUtil.show(mContext, "::: " + camera_time_count + " :::");

                    if (camera_time_count > 0) {
                        this.sendEmptyMessageDelayed(CAMERA_EVENT_UPDATE, 1000);
                    } else {
                        this.sendEmptyMessage(CAMERA_EVENT_STOP);
                    }
                    break;
                case CAMERA_EVENT_STOP: {
                    startEmergencyService("CAMERA_EVENT_STOP");
                    break;
                }
            }
        }
    };

    /**
     * 통화 종료 핸들러
     */
    private final int CALL_EVENT_START = 1;   // 통화종료 이벤트 시작
    private final int CALL_EVENT_UPDATE = 2;  // 통화종료 이벤트 시간 업데이트
    private final int CALL_EVENT_STOP = 3;    // 통화종료 이벤트 중지

    private int call_time_count;
    private Handler mCallHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CALL_EVENT_START:
                    call_time_count = 11;
                    this.sendEmptyMessage(CALL_EVENT_UPDATE);
                    break;
                case CALL_EVENT_UPDATE:
                    if (call_time_count == -1) return;
                    call_time_count--;

                    // TODO 타이머 테스트
//                    ToastUtil.show(mContext, "::: " + call_time_count + " :::");
                    if (call_time_count > 0) {
                        this.sendEmptyMessageDelayed(CALL_EVENT_UPDATE, 1000);
                    } else {
                        this.sendEmptyMessage(CALL_EVENT_STOP);
                    }
                    break;
                case CALL_EVENT_STOP: {
                    startEmergencyService("CALL_EVENT_STOP");
                    break;
                }
            }
        }
    };

    /**
     * 서비스 시작 Notification
     */
//    public void startForegroundNotificationOn() {
//
//        // 모니터링 서비스  노티피케이션 취소
////        ServiceUtil serviceUtil = new ServiceUtil();
////        serviceUtil.stopMonitorService(mContext);
//
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.view_notification_on);
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        Intent buttonIntent = new Intent(this, NotificationReceiver.class);
//        PendingIntent buttonPendingIntent = PendingIntent.getBroadcast(this, 0,  buttonIntent, 0);
//
//        remoteViews.setOnClickPendingIntent(R.id.view_notification_btn_switch, buttonPendingIntent);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 롤리팝 이상 (Ver.5.0 Above)
//            TextView textView = new TextView(mContext);
//            textView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Notification_Title);
//            remoteViews.setTextColor(R.id.view_notification_tv_title, textView.getCurrentTextColor());
//            textView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Notification_Line2);
//            remoteViews.setTextColor(R.id.view_notification_tv_contect, textView.getCurrentTextColor());
//            remoteViews.setTextViewText(R.id.view_notification_tv_contect, getString(R.string.notification_safty_service_start));
//        }
//
//        NotificationCompat.Builder builder;
//        if (Build.VERSION.SDK_INT >= 26) {
//            String CHANNEL_ID = "snwodeer_service_channel";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "SnowDeer Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            channel.setSound(null, null);
//            channel.setShowBadge(false);
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
//                    .createNotificationChannel(channel);
//
//            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        } else {
//            builder = new NotificationCompat.Builder(this);
//        }
//
//        builder.setSmallIcon(R.drawable.ic_launcher)
//                .setContent(remoteViews)
//                .setContentIntent(buttonPendingIntent)
//                .setContentTitle(getString(R.string.app_name)).setPriority(Notification.PRIORITY_MIN)
//                .setGroup(Define.NOTIFICATION_ID_FOREGROUND_GROUP_KEY)
//                .setContentText(getString(R.string.notification_safty_service_stop))
//                .setContentIntent(notificationPendingIntent).build();
//
//        startForeground(Define.NOTIFICATION_ID_FOREGROUND_SERVICE, builder.build());
//    }

    /**
     * 서비스 중지 Notification
     */
//    public void startForegroundNotificationOff() {
//        // 모니터링 서비스  노티피케이션 취소
////        ServiceUtil serviceUtil = new ServiceUtil();
////        serviceUtil.stopMonitorService(mContext);
//
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.view_notification_off);
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        Intent buttonIntent = new Intent(this, NotificationReceiver.class);
//        PendingIntent buttonPendingIntent = PendingIntent.getBroadcast(this, 0,  buttonIntent, 0);
//
//        remoteViews.setOnClickPendingIntent(R.id.view_notification_btn_switch, buttonPendingIntent);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 롤리팝 이상 (Ver.5.0 Above)
//            TextView textView = new TextView(mContext);
//            textView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Notification_Title);
//            remoteViews.setTextColor(R.id.view_notification_tv_title, textView.getCurrentTextColor());
//            textView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Notification_Line2);
//            remoteViews.setTextColor(R.id.view_notification_tv_contect, textView.getCurrentTextColor());
//            remoteViews.setTextViewText(R.id.view_notification_tv_contect, getString(R.string.notification_safty_service_stop));
//        }
//
//        NotificationCompat.Builder builder;
//        if (Build.VERSION.SDK_INT >= 26) {
//            String CHANNEL_ID = "snwodeer_service_channel";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "SnowDeer Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            channel.setSound(null, null);
//            channel.setShowBadge(false);
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
//                    .createNotificationChannel(channel);
//
//            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        } else {
//            builder = new NotificationCompat.Builder(this);
//        }
//
//        builder.setSmallIcon(R.drawable.ic_launcher_gray)
//                .setContent(remoteViews)
//                .setContentIntent(buttonPendingIntent)
//                .setContentTitle(getString(R.string.app_name)).setPriority(Notification.PRIORITY_MIN)
//                .setGroup(Define.NOTIFICATION_ID_FOREGROUND_GROUP_KEY)
//                .setContentText(getString(R.string.notification_safty_service_stop))
//                .setContentIntent(notificationPendingIntent).build();
//
//        startForeground(Define.NOTIFICATION_ID_FOREGROUND_SERVICE, builder.build());
//    }

    /**
     * Foreground 서비스 종료
     */
    private void stopForegroundNotification() {

        stopForeground(true);

        // 모니터링 서비스 시작
//        ServiceUtil serviceUtil = new ServiceUtil();
//        serviceUtil.startMonitorService(mContext);

    }

    private BroadcastReceiver mLocaleChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(null == callbackSound || mApp.getIsSoundParserStop()) {
                stopForegroundNotification();
           //     startForegroundNotificationOff();
            }else {
                stopForegroundNotification();
            //    startForegroundNotificationOn();
            }
        }
    };

    /**
     *
     * @param where
     */
    private void startEmergencyService(String where) {
        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;
        // 터치소리 설정 확인

            // 터치소리 서비스 중지 해제
            mApp.setIsServiceStop(false);
            LogUtil.d(TAG, "onCreate() -> getIsServiceStop() : " + mApp.getIsServiceStop());

            // 터치소리 서비스 시작
            Intent intent = new Intent(mContext, TouchService.class);
            intent.setAction(TOUCH_ACTION_EMERGNECY);
            intent.putExtra("start", Define.TOUCH_SERVICE_TYPE_START);
            startService(intent);

    }
}
