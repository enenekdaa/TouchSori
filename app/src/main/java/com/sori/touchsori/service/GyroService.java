package com.sori.touchsori.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;


import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.receiver.GyroReceiver;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.EtcUtil;
import com.sori.touchsori.utill.LogUtil;

import static android.content.Context.ALARM_SERVICE;


/**
 * Created by innochal on 2017-08-08.
 */

public class GyroService {
    private static final String TAG = GyroService.class.getSimpleName();   // 디버그 태그
    private static final double M_PI = 3.14159265358979323846;

    private Context mContext;                                               // 콘텍스트
    private SoriApplication mApp;                                      // 전역 (Application) 변수
    private static GyroService mInstance;                                  // 인스턴스

    private SensorManager mSensorManager    = null;
    private boolean isChangeGyro = false;

    private Sensor mGgyroSensor             = null;
    private SensorEventListener mGyroLis;

    private GyroService(Context context) {
        mContext = context;

        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);

        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroLis = new GyroscopeListener();

        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;
    }

    public static GyroService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GyroService(context);
        }
        return mInstance;
    }

    /**
     * Gyro정보 시작
     */
    public void startGyroInfo() {
        LogUtil.d(TAG,"startGyroInfo  start");


        if (Build.MODEL.contains("SM-G928") || Build.MODEL.contains("SM-G920") || Build.MODEL.contains("SM-G925")) {
            mGyroEventHandler.removeMessages(GYRO_HANDLE_EVENT_START);
            mGyroEventHandler.removeMessages(GYRO_HANDLE_EVENT_UPDATE);
            mGyroEventHandler.removeMessages(GYRO_HANDLE_EVENT_STOP);

            if(null != mGgyroSensor) {
                mGyroEventHandler.sendEmptyMessage(GYRO_HANDLE_EVENT_START);
            }
        }else {
            cancelGyroEventAlarm();
            if(null != mGgyroSensor) {
                registerGyoroEventAlarm();
            }
        }
        LogUtil.d(TAG,"startGyroInfo  end");

    }

    /**
     * Gyro정보 중지
     */
    public void stopGyronfo(){
        if (Build.MODEL.contains("SM-G928") || Build.MODEL.contains("SM-G920") || Build.MODEL.contains("SM-G925")) {
            mGyroEventHandler.removeMessages(GYRO_HANDLE_EVENT_START);
            mGyroEventHandler.removeMessages(GYRO_HANDLE_EVENT_UPDATE);
            mGyroEventHandler.sendEmptyMessage(GYRO_HANDLE_EVENT_STOP);
        }else {
            cancelGyroTouchServiceEndAlarm();
            cancelGyroEventAlarm();
        }
        mSensorManager.unregisterListener(mGyroLis);
    }

    /**
     * Gyeoscope Listener
     */
    public class GyroscopeListener implements SensorEventListener {
        private short preRoll = 0;
        private short prePitch = 0;
        private short preYaw = 0;

        private double d_preRoll = 0;
        private double d_prePitch = 0;
        private double d_preYaw = 0;

        private boolean isRoll = false;
        private boolean isPitch = false;
        private boolean isYaw = false;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            short roll = (short)((sensorEvent.values[0] * 180) / M_PI);
            short pitch = (short)((sensorEvent.values[1] * 180) / M_PI);
            short yaw = (short)((sensorEvent.values[2]  * 180) / M_PI);

            LogUtil.d(TAG, "[GYRO] updateGyro roll = " + roll + ", pitch = " + pitch + ", yaw = " + yaw);
            LogUtil.d(TAG, "[GYRO] updateGyro preRoll = " + preRoll + ", prePitch = " + prePitch + ", preYaw = " + preYaw);

//            FileUtil.writeLog(mContext,TAG,  "[GYRO] updateGyro sensorEvent.values[0] = " + sensorEvent.values[0] + ", sensorEvent.values[1] = " + sensorEvent.values[1] + ", sensorEvent.values[2] = " + sensorEvent.values[2]);
//            FileUtil.writeLog(mContext,TAG,  "[GYRO] updateGyro d_preRoll= " + d_preRoll + ", d_prePitch = " + d_prePitch+ ", sd_preYaw = " + d_preYaw);
//
//            FileUtil.writeLog(mContext,TAG,  "[GYRO] updateGyro roll = " + roll + ", pitch = " + pitch + ", yaw = " + yaw);
//            FileUtil.writeLog(mContext,TAG, "[GYRO] updateGyro preRoll = " + preRoll + ", prePitch = " + prePitch + ", preYaw = " + preYaw);

            short roll1 = (short)(roll + 6);
            short roll2 = (short)(roll - 6);
            short pitch1 = (short)(pitch + 6);
            short pitch2 = (short)(pitch - 6);
            short yaw1 = (short)(yaw + 6);
            short yaw2 = (short)(yaw - 6);

            if (preRoll < roll1 && preRoll > roll2) {
                isRoll = true;
            } else {
                isRoll = false;
            }
            preRoll = roll;
            d_preYaw = sensorEvent.values[0];
            if (prePitch < pitch1 && prePitch > pitch2) {
                isPitch = true;
            } else {
                isPitch = false;
            }
            prePitch = pitch;
            d_prePitch = sensorEvent.values[1];

            if (preYaw < yaw1 && preYaw > yaw2) {
                isYaw = true;
            } else {
                isYaw = false;
            }
            preYaw = yaw;
            d_preYaw = sensorEvent.values[2];

            LogUtil.d(TAG, "[GYRO] isRoll = " + isRoll  + ", isPitch = " + isPitch + ", isYaw =" + isYaw);


            if (isRoll && isPitch && isYaw) {
                if (!isChangeGyro) {
                    isChangeGyro = true;
                    LogUtil.d(TAG, "[GYRO] setAlarm stop touch service");

                    setGyroTouchServiceEndAlarm();
                }
            } else {
                if (isChangeGyro) {
                    isChangeGyro = false;
                    LogUtil.d(TAG, "[GYRO] cancael gyro alarm");

                    cancelGyroTouchServiceEndAlarm();

                    LogUtil.d(TAG, "[GYRO] isRoll = " + isRoll  + ", isPitch = " + isPitch + ", isYaw =" + isYaw);


//                    setGyroTouchServiceEndAlarm();
                    if(mApp.isGyroStopService()) {
                        LogUtil.d(TAG, "[GYRO] start touch service");

                        mApp.setIsGyroStopService(false);
                        startTouchsoriService();
                    }
                }
            }
            mSensorManager.unregisterListener(mGyroLis);
            LogUtil.d(TAG, "onSensorChanged mSensorManager.unregisterListener!");

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

//    private PendingIntent pi_gyro_start = null;

    /**
     * Gyroscope에 의한 TouchService 종료 알람을 설정한다.
     */
    private void setGyroTouchServiceEndAlarm() {
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(mContext, GyroReceiver.class);
        intent.setAction(Define.ACTION_GYRO_SERVICE_STOP);
//        pi_gyro_start = PendingIntent.getBroadcast(
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                Define.ALARM_ID_GYRO_EMERGENCY_STOP,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerTime = System.currentTimeMillis() + Define.GYRO_STOP_SERVICE_TIME;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {               // 마시멜로우 (Ver.6.0 이상)
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent);
            alarmManager.setAlarmClock(info, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   // 키켓 (Ver.4.4 이상)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else {                                                            // 기타
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    /**
     * Gyroscope에 의한 TouchService 종료 알람을 취소한다.
     */
    private void cancelGyroTouchServiceEndAlarm() {
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(mContext, GyroReceiver.class);
        intent.setAction(Define.ACTION_GYRO_SERVICE_STOP);
//        pi_gyro_start = PendingIntent.getBroadcast(
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                Define.ALARM_ID_GYRO_EMERGENCY_STOP,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        LogUtil.d(TAG, "[GYRO] cancelGyroTouchServiceEndAlarm");

    }

    /**
     * Gyroscope Listener Reigst
     */
    public void registerGyroListener() {
        if(null != mGgyroSensor) {
            mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);

            LogUtil.d(TAG, "[GYRO] registerGyroListener");

        }
    }

    /**
     * Gyroscope Listener unRegist
     */
    public void unRegisterListener() {
        mSensorManager.unregisterListener(mGyroLis);
    }

    /**
     * Gyroscope Listener Register위한 알람 등록
     */
    public void registerGyoroEventAlarm() {
        Intent intent = new Intent(mContext, GyroReceiver.class);
        intent.setAction(Define.ACTION_GYRO_EVENT_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                Define.ALARM_ID_GYRO_EVENT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long intervalTime;

        if(Build.MODEL.contains("SM-N920")) {
            intervalTime = Define.GYRO_CHECK_TIME_3_SEC;
        }else {
            intervalTime = Define.GYRO_CHECK_TIME_1_SEC;
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {               // 마시멜로우 (Ver.6.0 이상)
            long triggerTime = System.currentTimeMillis() + intervalTime;
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent);
            alarmManager.setAlarmClock(info, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   // 키켓 (Ver.4.4 이상)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalTime, pendingIntent);
        } else {                                                            // 기타
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalTime, pendingIntent);
        }
        LogUtil.d(TAG, "[GYRO] registerGyoroEventAlarm");

    }

    /**
     * Gyroscope Listener Register위한 알람 취소
     */
    private void cancelGyroEventAlarm() {
        Intent intent = new Intent(mContext, GyroReceiver.class);
        intent.setAction(Define.ACTION_GYRO_EVENT_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                Define.ALARM_ID_GYRO_EVENT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        LogUtil.d(TAG, "[GYRO] cancelGyroEventAlarm");

    }

    /**
     *  gyro값이 변경된 되었는지 여부를 판단하는 flag를 초기화한다.
     */
    public void initChangeGyro (){
        isChangeGyro = false;
    }

    /**
     * 터치소리 서비스 시작
     */
    public void startTouchsoriService() {
        LogUtil.i(TAG, "startTouchsoriService() -> Start !!!");

        // 터치소리 설정 확인
        if (mApp.isInitialized()) {
            // 터치소리 서비스 중지 해제
            mApp.setIsServiceStop(false);
            LogUtil.d(TAG, "startTouchsoriService() -> getIsServiceStop() : " + mApp.getIsServiceStop());

            // 안심귀가 사용 유무 확인
            if (mApp.isInitialized()) {
//                WakeLockUtil.wakeLockWithScreenOn(mContext);
//                WakeLockUtil.releaseWakeLock();
//                FileUtil.writeLog(mContext, TAG, "GyroService startTouchsoriService wakeLockWithScreenOnBright");
//
//                MediaUtil.getInstance(mContext).soundStartFromFileResource(R.raw.emergencystart, false, null);

                // 터치소리 서비스 시작
                Intent intent = new Intent(mContext, TouchService.class);
                intent.setAction(Define.TOUCH_ACTION_EMERGNECY);
                intent.putExtra("start", Define.TOUCH_SERVICE_TYPE_START);
                mContext.startService(intent);


            }
//            else if(mApp.checkEmergencyTime(true, false) && (mApp.getIsSoundParserStop())) {                      // MIC만 off된 상태로 Touch 서비스 실행
////                WakeLockUtil.releaseWakeLock();
////                WakeLockUtil.wakeLockWithScreenOn(mContext);
////                WakeLockUtil.releaseWakeLock();
////                FileUtil.writeLog(mContext, TAG, "GyroService startTouchsoriService wakeLockWithScreenOnBright");
////
////                MediaUtil.getInstance(mContext).soundStartFromFileResource(R.raw.emergencystart, false, null);
//
//                // 터치소리 서비스 중지(MIC만 중지)
//                Intent intent = new Intent(mContext, TouchService.class);
//                intent.setAction(Define.TOUCH_ACTION_EMERGNECY);
//                intent.putExtra("start", Define.TOUCH_SERVICE_TYPE_STOP);
//                mContext.startService(intent);
//
//
//            }
            else {
                //서비스 종료 시 Gyroscope event도 종료
                if(EtcUtil.isGyroTouchServiceStopDevice()) {
                    stopGyronfo();
                }

                // 터치소리 서비스 중지
                Intent intent = new Intent(mContext, TouchService.class);
                intent.setAction(Define.TOUCH_ACTION_EMERGNECY);
                intent.putExtra("start", Define.TOUCH_SERVICE_TYPE_END);
                mContext.startService(intent);

            }
        }
    }

    private final int GYRO_HANDLE_EVENT_START = 1;      // 시작
    private final int GYRO_HANDLE_EVENT_UPDATE = 2;     // 업데이트
    private final int GYRO_HANDLE_EVENT_STOP = 3;       // 중지

    private Handler mGyroEventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == GYRO_HANDLE_EVENT_START) {
                if (mApp == null) mApp = (SoriApplication) mContext;
                mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);
                mGyroEventHandler.sendEmptyMessageDelayed(GYRO_HANDLE_EVENT_UPDATE, Define.GYRO_CHECK_TIME_1_SEC);
                LogUtil.d(TAG, "[GYRO] mGyroEventHandler GYRO_HANDLE_EVENT_START");

            }else if(msg.what == GYRO_HANDLE_EVENT_UPDATE) {
                mGyroEventHandler.sendEmptyMessage(GYRO_HANDLE_EVENT_START);
                LogUtil.d(TAG, "[GYRO] mGyroEventHandler GYRO_HANDLE_EVENT_UPDATE");

            }else if(msg.what == GYRO_HANDLE_EVENT_STOP) {
                LogUtil.d(TAG, "[GYRO] mGyroEventHandler GYRO_HANDLE_EVENT_STOP");

                mSensorManager.unregisterListener(mGyroLis);
            }
        }
    };
}
