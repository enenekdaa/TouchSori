package com.sori.touchsori;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sori.touchsori.api.ApiUtil;
import com.sori.touchsori.data.ApiContactListData;
import com.sori.touchsori.data.ApiContactsData;
import com.sori.touchsori.dialog.AlertDialogFinal;
import com.sori.touchsori.intro.IntroActivity;
import com.sori.touchsori.receiver.EmergencyRecevier;
import com.sori.touchsori.service.TouchService;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.LogUtil;
import com.sori.touchsori.utill.TypefaceUtil;
import com.sori.touchsori.utill.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.PendingIntent.getBroadcast;
import static com.sori.touchsori.utill.Define.ALARM_ID_EMERGENCY_START;
import static com.sori.touchsori.utill.Define.TOUCH_ACTION_EMERGNECY;

public class SoriApplication extends Application {

    public static SoriApplication soriApplication;
    public ApiUtil apiUtil;
    Context mContext;
    int PERMISSION_REQUEST_ALL = 1000;
    public Utils utils;


    private boolean isSoundParserStop = false;                                       // SoundParse Stop
    private int locationCount = -1;                                                 // 위치정보 발송 카운트
    private boolean isMessageSending = false;                                       // SMS 전송 중인지 확인하는 플래그

    // 서비스 중지 플래그
    private boolean isServiceStop = false;
    private static boolean bInitialized = true;                            // 초기화 여부

    private final String[] permissions = new String[]{                           // 퍼미션
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
    };


    public static SoriApplication getInstance() {return soriApplication;}

    @Override
    public void onCreate() {
        super.onCreate();
        soriApplication = this;
        mContext = getApplicationContext();
        utils = new Utils(this);
        apiUtil = new ApiUtil(this);
        TypefaceUtil.overrideFont(this , "SERIF" , "fonts/NanumSquare_acB.ttf");

    }

    /**
     * 퍼미션 체크
     *
     * @param activity
     * @return
     */
    public boolean checkPermissionAll(Activity activity) {
        boolean req_permission = false;

        // 퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 (Ver.6.0)
            req_permission = checkPermission(activity, permissions);
        }
        return !req_permission;
    }

    /**
     * 퍼미션 체크
     * @param activity
     * @param permission
     * @return
     */
    private boolean checkPermission(Activity activity, String[] permission) {
        boolean req_permission = false;

        // 퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 (Ver.6.0)
            for (int i = 0; i < permission.length; i++) {
                if (ContextCompat.checkSelfPermission(mContext, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                    req_permission = true;
                    break;
                }
            }
            if (req_permission && activity != null) {
                ActivityCompat.requestPermissions(activity, permission, PERMISSION_REQUEST_ALL);
            }
        }
        return req_permission;
    }

    /**
     * 퍼미션 체크
     *
     * @param activity
     * @param permission
     * @param res_type
     * @return
     */
    public boolean checkPermission(Activity activity, String permission, int res_type) {
        if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, res_type);
            return false;
        } else {
            return true;
        }
    }

    public void showMessage(String message) {
        Toast.makeText(mContext , message , Toast.LENGTH_SHORT).show();
    }


    public void onAlertDialog(String message) {
        Intent alertIntent = new Intent(this, AlertDialogFinal.class);
        alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        alertIntent.putExtra("message", message);

        startActivity(alertIntent);
    }






    /**
     * 터치소리 SoundParser 중지 정보
     *
     * @return
     */
    public boolean getIsSoundParserStop() {
        return isSoundParserStop;
    }

    public void setIsSoundPaserStop(boolean isSoundParserStop) {
        this.isSoundParserStop = isSoundParserStop;
    }



    /**
     * 서비스 중지
     *
     * @return
     */
    public boolean getIsServiceStop() {
        return isServiceStop;
    }
    /**
     * 서비스 중지 설정
     */
    public void setIsServiceStop(boolean isStop) {
        this.isServiceStop = isStop;
    }


    /**
     * 초기화 여부
     * @return
     */
    public boolean isInitialized() {
        return bInitialized;
    }

    /**
     * 초기화 여부
     * @return
     */
    public void setIsInitialized(boolean value) {
        bInitialized = value;
    }


    /**
     * 위치정보 카운트 설정
     *
     * @param locationCount
     */
    public void setLocationCount(int locationCount) {
        this.locationCount = locationCount;
    }

    /**
     * 위치정보 카운트
     *
     * @return
     */
    public int getLocationCount() {
        return this.locationCount;
    }
    /**
     * SMS 메시지를 보내는 중인지 설정한다.
     * @param messageSending
     */
    public void setMessageSending(boolean messageSending) {
        isMessageSending = messageSending;
    }

    /**
     * SMS 메시지를 보내는 중인지 가져온다.
     * @return
     */
    public boolean isMessageSending() {
        return isMessageSending;
    }

    /**
     * 안심귀가 시간 체크
     * @param bStart
     * @param checkIsSelected       emergency 설정에 selected된 것을 체크하는지 여부
     * @return
     */
    public boolean checkEmergencyTime(boolean bStart, boolean checkIsSelected) {
        LogUtil.i("checkEmergencyTime", "checkEmergencyTime() -> Start !!!");
        boolean startNow = false;

        if (bStart) {
//            // 안심귀가 사용 체크 유무
//            if ((!getConfig().isEmergency()) && checkIsSelected) {
//                startNow = false;
//                LogUtil.d("checkEmergencyTime", "checkEmergencyTime() -> bStart : " + bStart + ", startNow : " + startNow);
//                return startNow;
//            }
//            // 시작 시간
//            long timestampStart = getConfig().getTimeStart();
//            LogUtil.d("checkEmergencyTime", "checkEmergencyTime() -> timestampStart : " + timestampStart);
//            // 종료 시간
//            long timestampEnd = getConfig().getTimeEnd();
//            LogUtil.d("checkEmergencyTime", "checkEmergencyTime() -> timestampEnd : " + timestampEnd);
//
//            // 시작, 종료 시간
//            if (timestampStart == -1
//                    || timestampEnd == -1) {
//                startNow = false;
//                LogUtil.d("checkEmergencyTime", "checkEmergencyTime() -> bStart : " + bStart + ", startNow : " + startNow);
//                return startNow;
//            }
//
//            // 긴급 수신자
//            int sizeMembes = getConfig().getEmergencyInfo().members.size();
//            LogUtil.d(TAG, "checkEmergencyTime() -> sizeMembes : " + sizeMembes);
//            if (sizeMembes == 0) {
//                startNow = false;
//                LogUtil.d(TAG, "checkEmergencyTime() -> bStart : " + bStart + ", startNow : " + startNow);
//                return startNow;
//            }
//
//            // 24시간 설정
//            if (timestampStart == 0
//                    && timestampEnd == 0) {
//                startNow = true;
//                LogUtil.d("checkEmergencyTime", "checkEmergencyTime() -> bStart : " + bStart + ", startNow : " + startNow);
//                return startNow;
//            }

//            long currentTime = System.currentTimeMillis();
//            LogUtil.d(TAG, "checkEmergencyTime() -> currentTime : " + currentTime);
//            if (timestampStart > timestampEnd)
//                timestampStart = timestampStart - (24 * 60 * 60 * 1000);
//            LogUtil.d(TAG, "checkEmergencyTime() -> timestampStart : " + timestampStart);
//            if (currentTime >= timestampStart
//                    && currentTime < timestampEnd) {
//                startNow = true;
//                LogUtil.d(TAG, "checkEmergencyTime() -> bStart : " + bStart + ", startNow : " + startNow);
//                return startNow;
//            }
//            LogUtil.d(TAG, "checkEmergencyTime() -> bStart : " + bStart + ", startNow : " + startNow);
////            FileUtil.writeLog(mContext, TAG, "bStart : " + bStart + ", startNow : " + startNow);
            startNow = true;
        }
        return startNow;
    }

    // 재시작 ... 무한
    private PendingIntent pi_emergency_start;
    public void alarmForever() {
        Intent intent = new Intent(mContext, EmergencyRecevier.class);
        intent.setAction(Define.ACTION_EMERGENCY_TIME_START);
        pi_emergency_start = getBroadcast(
                mContext,
                ALARM_ID_EMERGENCY_START,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long currentTime = System.currentTimeMillis() + 3000;
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (currentTime < now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {               // 마시멜로우 (Ver.6.0 이상)
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi_emergency_start);
            long triggerTime = System.currentTimeMillis() + Math.abs(System.currentTimeMillis() - calendar.getTimeInMillis());
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(triggerTime, pi_emergency_start);
            alarmManager.setAlarmClock(info, pi_emergency_start);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   // 키켓 (Ver.4.4 이상)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi_emergency_start);
        } else {                                                            // 기타
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi_emergency_start);
        }
    }

    /**
     * TouchSerivce를 시작한다.
     * @param tag
     */
    public void startTouchsoriService(String tag) {
        //LogUtil.d(tag, "startTouchsoriService() -> isInitialized : " + getConfig().isInitialized());

        // 터치소리 설정 확인

            // 터치소리 서비스 중지 해제
            setIsServiceStop(false);
            LogUtil.d(tag, "startTouchsoriService() -> getIsServiceStop() : " + getIsServiceStop());

        // 터치소리 서비스 시작
        Intent intent = new Intent(mContext, TouchService.class);
        intent.setAction(TOUCH_ACTION_EMERGNECY);
        intent.putExtra("start", Define.TOUCH_SERVICE_TYPE_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(intent);
        } else {
            mContext.startService(intent);
        }

//                if (checkEmergencyTime(true, true)) {
//
//
//                } else {
//                    //서비스 종료 시 Gyroscope event도 종료
////                    if(EtcUtil.isGyroTouchServiceStopDevice() && (false == isSoundParserStop)) {
////                        GyroService.getInstance(mContext).stopGyronfo();
////                    }
//
//                    // 터치소리 서비스 중지
//                    Intent intent = new Intent(mContext, TouchService.class);
//                    intent.setAction(TOUCH_ACTION_EMERGNECY);
//                    intent.putExtra("start", Define.TOUCH_SERVICE_TYPE_END);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        mContext.startForegroundService(intent);
//                    } else {
//                        mContext.startService(intent);
//                    }
//
//                }

        }


    ArrayList<ApiContactListData> sosList;
    ArrayList<String> nameList;
    ArrayList<String> numList;
    public void getContactsLoad() {

        try {
            //   jsonObject.put("deviceId", deviceObj.get("username").getAsString());
            apiUtil.getContacts().enqueue(new Callback<ApiContactsData>() {
                @Override
                public void onResponse(Call<ApiContactsData> call, Response<ApiContactsData> response) {
                    if (response.isSuccessful()) {
                        //    JsonObject contacts = response.body().getContacts();
                        JsonArray js;
                        sosList = new ArrayList<>();
                        sosList.clear();
                        try {
                            js = response.body().generalList();

                            for (JsonElement jsonElement : js) {
                                sosList.add(new ApiContactListData(jsonElement.getAsJsonObject()));
                            }

                            ArrayList<String> nameList = new ArrayList<>();
                            ArrayList<String> numList = new ArrayList<>();
                            if (nameList.size() != 0) {
                                nameList.clear();
                            }
                            if (numList.size() != 0) {
                                numList.clear();
                            }
                            for (int i = 0 ; i < sosList.size() ; i++) {
                                nameList.add(sosList.get(i).getName());
                            }
                            for (int i = 0 ; i < sosList.size() ; i++) {
                                numList.add(sosList.get(i).getPhone());
                            }

                            utils.saveSosList(numList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showMessage(e.getMessage());
                        }

                    }else {
                        try {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            String message = jsonError.getString("message");
                            String code = jsonError.getString("code");
                            showMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 조회 실패
                    }
                }

                @Override
                public void onFailure(Call<ApiContactsData> call, Throwable t) {
                    showMessage(t.getMessage());
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
