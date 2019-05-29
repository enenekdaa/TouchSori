package com.sori.touchsori.api;

import android.content.Context;

import com.sori.touchsori.data.ApiAuthorizationData;
import com.sori.touchsori.data.ApiContactsData;
import com.sori.touchsori.data.ApiDefault;
import com.sori.touchsori.data.ApiDeviceData;
import com.sori.touchsori.data.ApiScheduleLoadData;
import com.sori.touchsori.data.ApiSirenLoadData;
import com.sori.touchsori.utill.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {

    /**
     * API 커넥
     */


    public static final String API_URL = "http://211.216.53.43";

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .addInterceptor(new Interceptor() {
//        @Override
//        public Response intercept(Interceptor.Chain chain) throws IOException {
//            Request original = chain.request();
//
//            // Request customization: add request headers
//            Request.Builder requestBuilder = original.newBuilder()
//                    .addHeader("Content-Type", "application/json");
//
//            Request request = requestBuilder.build();
//            return chain.proceed(request);
//        }
//    })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .client(okHttpClient)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    Context context;
    ApiService apiService;
    String serviceId = "pp";
    Utils utils;
    String deviceID, loginToken;

    String Ctype = "application/json";

    public ApiUtil(Context context) {
        this.context = context;
        utils = new Utils(context);
        apiService = retrofit.create(ApiService.class);
    }


    /**
     * 로그인 ...
     */

    public Call loginPost(final JSONObject jsonObject) {
        HashMap<String, String> tempMap = new HashMap<>();
        //     HashMap<String,String> headerMap = new HashMap<>();

        try {
            tempMap.put("username", jsonObject.getString("username"));
            tempMap.put("password", jsonObject.getString("password"));


        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ApiAuthorizationData> call = apiService.loginPost(tempMap);


        return call;
    }

    /**
     * 토큰 갱신
     */
    public Call refreshToken(String uid) {
        try {
            loginToken = utils.getLoginToken();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ApiAuthorizationData> call = apiService.refreshTokenPost(loginToken, uid);
        return call;
    }


    /**
     * 등록
     */
    public Call deviceAdd(final JSONObject jsonObject) {
        HashMap<String, String> tempMap = new HashMap<>();
        //     HashMap<String,String> headerMap = new HashMap<>();

        try {
            tempMap.put("serviceId", serviceId);
            tempMap.put("phoneNo", jsonObject.getString("phoneNo"));
            tempMap.put("deviceName", jsonObject.getString("deviceName"));
            tempMap.put("maker", jsonObject.getString("maker"));
            tempMap.put("imei", jsonObject.getString("imei"));
            tempMap.put("version", jsonObject.getString("version"));
            tempMap.put("iccid", jsonObject.getString("iccid"));
            tempMap.put("serialNo", jsonObject.getString("serialNo"));
            tempMap.put("model", jsonObject.getString("model"));
            tempMap.put("userId", jsonObject.getString("userId"));
            tempMap.put("passwd", jsonObject.getString("passwd"));


//            headerMap.put("Content-Type" ,"application/json");
//            headerMap.put("serviceId" , jsonObject.getString("serviceId"));
//            headerMap.put("appKey" , jsonObject.getString("appKey"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ApiDeviceData> call = apiService.deviceAdd(tempMap);


        return call;
    }

    /**
     * 사이렌 조회
     */
    public Call getSirenLoad() {
        HashMap<String, String> tempMap = new HashMap<>();
        //     HashMap<String,String> headerMap = new HashMap<>();

        try {
            deviceID = utils.getDeviceID();
            loginToken = utils.getLoginToken();
            tempMap.put("deviceId", deviceID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ApiSirenLoadData> call = apiService.sirenSettingLoad(deviceID, loginToken, tempMap);


        return call;
    }




    /**
     * 사이렌 설정
     */
    public Call getSirenSave(final JSONObject jsonObject) {
        HashMap<String, String> tempMap = new HashMap<>();
        //     HashMap<String,String> headerMap = new HashMap<>();
        try {
            deviceID = utils.getDeviceID();
            loginToken = utils.getLoginToken();
            //   tempMap.put("deviceId" , deviceID);
            tempMap.put("data", jsonObject.getString("data"));
            tempMap.put("status", jsonObject.getString("status"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ApiSirenLoadData> call = apiService.sirenSettingSave(deviceID, loginToken, tempMap);


        return call;
    }

    /**
     *  시리얼 수정
     */


    /**
     * 추가 설정 조회
     */

    public Call plusState(final JSONObject jsonObject) {
        HashMap<String, String> tempMap = new HashMap<>();
        //     HashMap<String,String> headerMap = new HashMap<>();

        try {
            tempMap.put("username", jsonObject.getString("username"));
            tempMap.put("password", jsonObject.getString("password"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ApiAuthorizationData> call = apiService.loginPost(tempMap);


        return call;
    }

    /**
     * 수신자 조회
     */
    public Call getContacts() {
        try {
            deviceID = utils.getDeviceID();
            loginToken = utils.getLoginToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ApiContactsData> call = apiService.getContacts(deviceID, loginToken);


        return call;
    }


    /**
     * 연락처 추가
     */
    public Call addContacts(final JSONObject jsonObject) {
        HashMap<String, String> tempMap = new HashMap<>();
        //     HashMap<String,String> headerMap = new HashMap<>();

        try {
            deviceID = utils.getDeviceID();
            loginToken = utils.getLoginToken();
            tempMap.put("deviceId", deviceID);
            // 설정 상세 json String .....
            tempMap.put("name", jsonObject.getString("name"));
            tempMap.put("phone", jsonObject.getString("phone"));


        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<Void> call = apiService.addContacts(deviceID, loginToken, tempMap);


        return call;
    }

    /**
     * 연락처 삭제
     */
    public Call deleteContacts(final JSONObject jsonObject) {
        HashMap<String, String> tempMap = new HashMap<>();
        //     HashMap<String,String> headerMap = new HashMap<>();

        try {
            deviceID = utils.getDeviceID();
            loginToken = utils.getLoginToken();
            tempMap.put("deviceId", deviceID);
            // 설정 상세 json String .....
            //tempMap.put("name" , jsonObject.getString("name"));
            tempMap.put("phone", jsonObject.getString("phone"));


        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<Void> call = apiService.deleteContacts(deviceID, loginToken, tempMap);


        return call;
    }


    /**
     * 현재 위치 호출 3
     */
    public Call locationCall3() {
        HashMap<String, String> tempMap = new HashMap<>();
        //     HashMap<String,String> headerMap = new HashMap<>();

        try {
            deviceID = utils.getDeviceID();
            loginToken = utils.getLoginToken();
            tempMap.put("deviceId", deviceID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<Void> call = apiService.locationLoad3(loginToken , deviceID,  tempMap);


        return call;
    }


//    /**
//     * 알람 시간 수정
//     */
//
//    public Call updateAlarmPost(final JSONObject jsonObject) {
//        HashMap<String, String> tempMap = new HashMap<>();
//        //     HashMap<String,String> headerMap = new HashMap<>();
//
//        try {
//            deviceID = utils.getDeviceID();
//            tempMap.put("deviceId", deviceID);
//            // 설정 상세 json String .....
//            tempMap.put("data", jsonObject.getString("data"));
//
//            tempMap.put("status", jsonObject.getString("status"));
//            tempMap.put("configCode", jsonObject.getString("configCode"));
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Call<ApiDefault> call = apiService.updateAlarmSetting(loginToken, tempMap);
//
//
//        return call;
//    }

//    /**
//     *  알람 설정 삭제
//     */
//
//    /**
//     * 알람 설정 on/off
//     */
//    String scheduleConfigCode;
//    String scheduleStatus;
//
//    public Call scheduleOnOff(final JSONObject jsonObject) {
//
//        //     HashMap<String,String> headerMap = new HashMap<>();
//
//        try {
//            deviceID = utils.getDeviceID();
//            loginToken = utils.getLoginToken();
//            scheduleConfigCode = jsonObject.getString("configCode");
//            scheduleStatus = jsonObject.getString("status");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Call call = apiService.getScheduleOnOff(deviceID, scheduleConfigCode, scheduleStatus, loginToken);
//
//        return call;
//    }
//
//
//    /**
//     * 스케줄 목록 조회
//     */
//
//    public Call scheduleLoad() {
//        HashMap<String, String> tempMap = new HashMap<>();
//        //     HashMap<String,String> headerMap = new HashMap<>();
//
//        try {
//            deviceID = utils.getDeviceID();
//            loginToken = utils.getLoginToken();
//            tempMap.put("deviceId", deviceID);
//            //   url = "/safecity-api-develop/v1/pp/devices/"+deviceID+"/settings/schedule/load";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Call<ApiScheduleLoadData> call = apiService.getScheduleLoad(deviceID, loginToken, tempMap);
//
//
//        return call;
//    }
//
//    /**
//     * 스케줄 알림 조회
//     */
//
//    public Call scheduleDetailLoad(final JSONObject jsonObject) {
//        HashMap<String, String> tempMap = new HashMap<>();
//
//        try {
//            deviceID = utils.getDeviceID();
//            loginToken = utils.getLoginToken();
//            tempMap.put("deviceId", deviceID);
//
//            tempMap.put("configId", jsonObject.getString("configId"));
//            tempMap.put("serviceId", jsonObject.getString("serviceId"));
//            tempMap.put("deviceId", jsonObject.getString("deviceId"));
//            tempMap.put("configGroup", jsonObject.getString("configGroup"));
//            tempMap.put("configCode", jsonObject.getString("configCode"));
//            tempMap.put("data", jsonObject.getString("data"));
//            tempMap.put("Status", jsonObject.getString("Status"));
//            tempMap.put("processStep", jsonObject.getString("processStep"));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Call<ApiScheduleLoadData> call = apiService.getScheduleDetailLoad(deviceID, tempMap.get("configCode"), loginToken, tempMap);
//
//
//        return call;
//    }
//
//    /**
//     * 디바이스 시리얼 넘버 수정
//     */
//    public Call updateDiviceInfo(final JSONObject jsonObject) {
//        HashMap<String, String> tempMap = new HashMap<>();
//        //     HashMap<String,String> headerMap = new HashMap<>();
//
//        try {
//            deviceID = utils.getDeviceID();
//            loginToken = utils.getLoginToken();
//            tempMap.put("deviceId", deviceID);
//            tempMap.put("name", jsonObject.getString("name"));
//            tempMap.put("id", jsonObject.getString("id"));
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Call<Void> call = apiService.updateDevice(deviceID, loginToken, tempMap);
//
//
//        return call;
//    }
//
//
//    /**
//     * 안심존 조회
//     */
//    public Call getAnsimZoneLoad(final JSONObject jsonObject) {
//        HashMap<String, String> tempMap = new HashMap<>();
//        //     HashMap<String,String> headerMap = new HashMap<>();
//
//        try {
//            deviceID = utils.getDeviceID();
//            loginToken = utils.getLoginToken();
//            tempMap.put("deviceId", deviceID);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Call<ApiDefault> call = apiService.getAnsimZoneLoad(loginToken, tempMap);
//
//
//        return call;
//    }
//
//    /**
//     * 안심존 저장
//     */
//    public Call getAnsimZoneSave(final JSONObject jsonObject) {
//        HashMap<String, String> tempMap = new HashMap<>();
//        //     HashMap<String,String> headerMap = new HashMap<>();
//
//        try {
//            deviceID = utils.getDeviceID();
//            loginToken = utils.getLoginToken();
//            tempMap.put("deviceId", deviceID);
//            tempMap.put("name", jsonObject.getString("username"));
//            tempMap.put("id", jsonObject.getString("password"));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Call call = apiService.updateDevice(deviceID, loginToken, tempMap);
//
//
//        return call;
//    }

}
