package com.sori.touchsori.intro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sori.touchsori.MainActivity;
import com.sori.touchsori.R;
import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.data.ApiAuthorizationData;
import com.sori.touchsori.service.ServiceUtil;
import com.sori.touchsori.signIn.DeviceInfo;
import com.sori.touchsori.signIn.SignInActivity;
import com.sori.touchsori.utill.CountryISOUtil;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import static com.sori.touchsori.utill.Define.AUTH_TOKEN;
import static com.sori.touchsori.utill.Define.KEY_COUNTRY_CODE;
import static com.sori.touchsori.utill.Define.KEY_HP;
import static com.sori.touchsori.utill.Define.KEY_MODE;
import static com.sori.touchsori.utill.Define.KEY_MODE_INSERT;
import static com.sori.touchsori.utill.Define.KEY_RESULT;
import static com.sori.touchsori.utill.Define.KEY_SAVED_SERIAL_NUMBER;
import static com.sori.touchsori.utill.Define.KEY_SERIAL_NUMBER;
import static com.sori.touchsori.utill.Define.KEY_TYPE;
import static com.sori.touchsori.utill.Define.KEY_UPDATE_DATE;
import static com.sori.touchsori.utill.Define.MONITOR_SERVICE_STOP;
import static com.sori.touchsori.utill.Define.SERVER_URL;
import static com.sori.touchsori.utill.Define.URL_SERIAL;

public class IntroActivity extends BaseActivity {

    private static final String TAG = IntroActivity.class.getSimpleName();
    public boolean firstLogin = false;
    ImageView introImg;

    TelephonyManager tm;
    Context mContext;
    String loginTp = "";
    String deviceId = "";
    private String serialNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_view);

        mContext = this;

        introImg = findViewById(R.id.intro_img);

     //   checkPermission();
        firstLogin = utils.isFirstLogin();
        loginTp = utils.getLoginToken();
        deviceId = utils.getDeviceID();




        if (soriApplication.checkPermissionAll(this)) {

            ServiceUtil serviceUtil = new ServiceUtil();
            boolean isRunningTouchService = ServiceUtil.isServiceRunning(mContext, "com.sori.touchsori.service");
            if (isRunningTouchService) {
                if (utils.getAlarmStatus().equals("on")) {

                    serviceUtil.startMonitorService(mContext);
                    soriApplication.setIsInitialized(true);
                    soriApplication.setIsSoundPaserStop(false);
                }else {
                    utils.saveAlaramStatus("off");
                    serviceUtil.stopMonitorService(mContext);
                    soriApplication.setIsInitialized(false);
                    soriApplication.setIsSoundPaserStop(true);

                }
            }else {
                if (utils.getAlarmStatus().equals("on")) {

                    serviceUtil.startMonitorService(mContext);
                    soriApplication.setIsInitialized(true);
                    soriApplication.setIsSoundPaserStop(false);
                }else {
                    serviceUtil.stopMonitorService(mContext);
                    soriApplication.setIsInitialized(false);
                    soriApplication.setIsSoundPaserStop(true);

                }
            }

            if (firstLogin && loginTp.equals("")) {
                Intent intent = new Intent(mContext , SignInActivity.class);
                startActivity(intent);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginTp = utils.getLoginToken();
        deviceId = utils.getDeviceID();
        if (firstLogin && !deviceId.equals("")) {
            serialRegist();
        }else if (firstLogin && deviceId.equals("")){
        }else {
            if (!firstLogin) {
            }else {
                finish();
            }
        }
    }

    /**
     * 퍼미션 결과
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            boolean complete = true;
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    complete = false;
                    break;
                }
            }

            if (complete) {
                // 인트로 화면 초기화
                utils.setIsFirstLogin(true);
                firstLogin = true;

                initView();
            } else {
                // 서비스 종료
//                Intent intent = new Intent(mContext, TouchService.class);
//                stopService(intent);
                finish();
            }
        }
    }

    private void initView() {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceObj.addProperty("phoneNo" , "01041381724");
        //  deviceObj.addProperty("phoneNo" , phoneNumber);
        deviceObj.addProperty("deviceName" , Build.DISPLAY);
        deviceObj.addProperty("maker" , Build.MANUFACTURER);
        deviceObj.addProperty("imei" ,"355239080979466");
        //  deviceObj.addProperty("imei" , tm.getDeviceId());
        deviceObj.addProperty("version" , tm.getDeviceSoftwareVersion());
        deviceObj.addProperty("iccid" , "iccid" );
        //+ Build.ID
        deviceObj.addProperty("model" , Build.MODEL);

        deviceObj.addProperty("username" , deviceObj.get("phoneNo").getAsString() + deviceObj.get("imei").getAsString());
        deviceObj.addProperty("password" , deviceObj.get("username").getAsString());


        utils.setDeviceInfo(deviceObj.toString());
        deviceInfo.setObject(utils.getDeviceInfo());

     //   userStateApi();
        if (firstLogin && loginTp.equals("")) {
            Intent intent = new Intent(mContext , SignInActivity.class);
            startActivity(intent);
        }


    }
//
//    PermissionListener permissionlistener = new PermissionListener() {
//        @Override
//        public void onPermissionGranted() {
//            Toast.makeText(getApplicationContext(), "권한 허가", Toast.LENGTH_SHORT).show();
//            try {
////                    phoneNumber = tm.getLine1Number();
////                    if(phoneNumber.startsWith("+82")){
////                        phoneNumber = phoneNumber.replace("+82", "0");
////                    }
//                deviceObj.addProperty("phoneNo" , "01066758566");
//                //  deviceObj.addProperty("phoneNo" , phoneNumber);
//                deviceObj.addProperty("deviceName" , Build.DISPLAY);
//                deviceObj.addProperty("maker" , Build.MANUFACTURER);
//                deviceObj.addProperty("imei" ,"354869090589003");
//                //  deviceObj.addProperty("imei" , tm.getDeviceId());
//                deviceObj.addProperty("version" , tm.getDeviceSoftwareVersion());
//                deviceObj.addProperty("iccid" , "iccid" );
//                //+ Build.ID
//                deviceObj.addProperty("model" , Build.MODEL);
//
//                deviceObj.addProperty("username" , deviceObj.get("phoneNo").getAsString() + deviceObj.get("imei").getAsString());
//                deviceObj.addProperty("password" , deviceObj.get("username").getAsString());
//
//                utils.setDeviceInfo(deviceObj.toString());
//                deviceInfo.setObject(utils.getDeviceInfo());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            initView();
//        }
//
//        @Override
//        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//            Toast.makeText(getApplicationContext(), "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    @SuppressLint("NewApi")
//    private void checkPermission() {
//        try {
//            new TedPermission(mContext)
//                    .setPermissionListener(permissionlistener)
//                    .setRationaleMessage("앱을 시작하기 위해 권한이 필요합니다")
//                    .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요.")
//                    .setPermissions()
//                    .check();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


    private void userStateApi() {
        JSONObject jsonObject = new JSONObject();
        try {
           // jsonObject.put("username", utils.stringHash(deviceObj.get("username").getAsString()));
            jsonObject.put("username", deviceObj.get("username").getAsString());
            jsonObject.put("password",  deviceObj.get("password").getAsString());


            apiUtil.loginPost(jsonObject).enqueue(new Callback<ApiAuthorizationData>() {
                @Override
                public void onResponse(Call<ApiAuthorizationData> call, Response<ApiAuthorizationData> response) {
                    String message;
                    if (response.isSuccessful()) {
                        String result =  response.body().getAuthorization();
                        deviceObj.addProperty("deviceId" , response.body().getDeviceId());
                        deviceObj.addProperty("role" , response.body().getRole());
                        deviceObj.addProperty("uid" , response.body().getUid());

                        utils.setDeviceInfo(deviceObj.toString());
                        utils.setDeviceID(deviceObj.get("deviceId").getAsString());
                        utils.saveLoginToken(result);

                        Intent intent = new Intent(IntroActivity.this , MainActivity.class);
                        goMainAct(intent);
                    }else{
//                        Intent intent = new Intent(IntroActivity.this , SignInActivity.class);
//                        goMainAct(intent);
                        try {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            message = jsonError.getString("message");
                            String code = jsonError.getString("code");

                            if (code.equals("1005")) {
                                showMessage(message + "\n 관리자에게 문의해주세요.");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.exit(0);
                                        finish();
                                    }
                                },3000);
                            }else {
                                System.exit(0);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ApiAuthorizationData> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void goMainAct(Intent intent) {
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        soriApplication.getContactsLoad();
        startActivity(intent);
        finish();
    }

    public void serialRegist() {
        // 시리얼 정보 저장 요청
        Thread requestInsertSerialThread = new RequestInsertSerialThread();
        requestInsertSerialThread.setPriority(Thread.MAX_PRIORITY);
        requestInsertSerialThread.start();

        try {
            requestInsertSerialThread.join();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }


        HashMap<String, Object> hashmap = ((RequestInsertSerialThread) requestInsertSerialThread).getResult();
        boolean result = (Boolean) hashmap.get(KEY_RESULT);
        LogUtil.d(TAG, "onRegistrationAction() -> result : " + result);
        utils.setRegistrated(true);
        utils.setSerialNumber(serialNumber);


        userStateApi();
    }

    /**
     * 시리얼 정보 저장 스레드
     */
    public class RequestInsertSerialThread extends Thread {
        private HashMap<String, Object> hashMap = new HashMap();

        public void run() {
            // 전역 (Application) 변수
            if (soriApplication == null) soriApplication = (SoriApplication) mContext;
            // 휴대 전화번호
            String hp = deviceObj.get("phoneNo").getAsString();
            LogUtil.d(TAG, "RequestInsertSerialThread() -> hp : " + hp);
            if (TextUtils.isEmpty(hp)) return;

            RequestSerial requestSerial = RequestSerial.retrofit.create(RequestSerial.class);
            Map<String, String> data = new HashMap<>();
            data.put(AUTH_TOKEN, "innochal001");
            data.put(KEY_MODE, "insert");
            data.put(KEY_HP, hp);
            data.put(KEY_COUNTRY_CODE, CountryISOUtil.getSIMCountryCode(mContext));
            serialNumber = utils.getSerialNumber();
            serialNumber = serialNumber.trim();
            data.put(KEY_SERIAL_NUMBER, serialNumber);
            LogUtil.d(TAG, "RequestInsertSerialThread() -> serialNumber : " + serialNumber.length());
            if (serialNumber.length() == 6) {
                data.put(KEY_TYPE, "1");
            } else {
                data.put(KEY_TYPE, "0");
            }
            LogUtil.d(TAG, "RequestUpdateSerialThread() -> data : " + data);


            Call<Serial> call = requestSerial.getSerial(data);

            try {
                Serial serial = call.execute().body();
                LogUtil.d(TAG, "RequestInsertSerialThread -> serial : " + serial);
                boolean result = serial.getResult();
                LogUtil.d(TAG, "RequestInsertSerialThread -> result : " + result);

                if (result) {
                    hashMap.put(KEY_RESULT, true);
                    if (serialNumber.length() == 6) {
                        LogUtil.d(TAG, "RequestInsertSerialThread -> result : " + result);
                        String serialNumber = serial.getSerialNumber();
                        LogUtil.d(TAG, "RequestInsertSerialThread -> serialNumber : " + serialNumber);
                        if (!TextUtils.isEmpty(serialNumber)) {
                            hashMap.put(KEY_SAVED_SERIAL_NUMBER, serialNumber);
                        }
                    }
                } else {
                    hashMap.put(KEY_RESULT, false);
                    if (serialNumber.length() == 9) {
                        hp = serial.getHp();
                        LogUtil.d(TAG, "RequestInsertSerialThread -> hp : " + hp);
                        if (!TextUtils.isEmpty(hp)) {
                            hashMap.put(KEY_HP, hp);
                        }
                        String updatedate = serial.getUpdatedate();
                        LogUtil.d(TAG, "RequestInsertSerialThread -> updatedate : " + updatedate);
                        if (!TextUtils.isEmpty(updatedate)) {
                            hashMap.put(KEY_UPDATE_DATE, updatedate);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public HashMap<String, Object> getResult() {
            return hashMap;
        }
    }

    /**
     * 시리얼 정보 요청
     */
    private class Serial {
        boolean result;             // 결과
        String hp;                  // 휴대폰 번호
        String update_date;         // 업데이트 날짜
        String saved_serial_number; // 저장된 시리얼 번호

        public boolean getResult() {
            return result;
        }

        public String getHp() {
            return hp;
        }

        public String getUpdatedate() {
            return update_date;
        }

        public String getSerialNumber() {
            return saved_serial_number;
        }
    }

    /**
     * 시리얼 정보 API 호출
     */
    private interface RequestSerial {
        @GET(URL_SERIAL)
        Call<Serial> getSerial(
                @QueryMap Map<String, String> params
        );

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .readTimeout(60, TimeUnit.SECONDS)
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
                .build();
    }


}
