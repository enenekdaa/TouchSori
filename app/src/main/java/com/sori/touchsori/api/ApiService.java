package com.sori.touchsori.api;


import com.sori.touchsori.data.ApiAuthorizationData;
import com.sori.touchsori.data.ApiContactsData;
import com.sori.touchsori.data.ApiDefault;
import com.sori.touchsori.data.ApiDeviceData;
import com.sori.touchsori.data.ApiScheduleLoadData;
import com.sori.touchsori.data.ApiSirenLoadData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * API 서비스
 * */
public interface ApiService {


    // 로그인
    @Headers({"Content-Type:application/json" ,   "serviceId:pp" , "appKey:test"})
    @POST("/auth/v1/pp/login")
    Call<ApiAuthorizationData> loginPost(@Body Map<String , String> temp_map);

    // 토큰갱신
    @Headers({"Content-Type:application/json" ,  "serviceId:pp" , "appKey:test"})
    @POST("/auth/v1/pp/refresh-token")
    Call<ApiAuthorizationData> refreshTokenPost(@Header("authorization") String authorization , @Header("uid") String uid);


    // 비회원 등록
    @Headers({"Content-Type:application/json" , "appKey:test"})
    @POST("/safecity-api-develop/v1/pp/things/devices/autojoin")
    Call<ApiDeviceData> deviceAdd(@Body Map<String , String > temp_map);


    // 현재위치 조회 3
    @Headers({"Content-Type:application/json" , "appKey:test"})
    @POST("/safecity-api-develop//vi/pp/devices/{deviceId}/location/current/request")
    Call<Void> locationLoad3(@Header("authorization") String authorization , @Path("deviceId") String deviceId , @Body Map<String , String > temp_map);
//    //디바이스 정보 수정
//    @FormUrlEncoded
//    @Headers({"Content-Type:application/json" ,  "serviceId:pp" , "appKey:test"})
//    @POST("/safecity-api-develop/v1/pp/devices/{deviceId}/protect/modify")
//    Call<Void> updateDevice(@Path("deviceId") String deviceId , @Header("authorization") String authorization , @Body Map<String , String> temp_map);
//

    // 일반 연락처 추가
    @Headers({"Content-Type: application/json" ,  "serviceId:pp" , "appKey:test"})
    @POST("/safecity-api-develop/v1/pp/devices/{deviceId}/contacts/general/add")
    Call<Void> addContacts(@Path("deviceId") String deviceId , @Header("authorization") String authorization , @Body Map<String , String> temp_map);

    // 일반 연락처 조회
    @Headers({"Content-Type: application/json" ,  "serviceId:pp" , "appKey:test"})
    @GET("/safecity-api-develop/v1/pp/devices/{deviceId}/contacts/general/load")
    Call<ApiContactsData> getContacts(@Path("deviceId") String deviceId , @Header("authorization") String authorization);

    // 일반 연락처 삭제
    @Headers({"Content-Type: application/json" , "serviceId:pp" , "appKey:test"})
    @POST("/safecity-api-develop/v1/pp/devices/{deviceId}/contacts/general/remove")
    Call<Void> deleteContacts(@Path("deviceId") String deviceId , @Header("authorization") String authorization , @Body Map<String , String> temp_map);

    //연락처 수정
    @Headers({"Content-Type:application/json" ,  "serviceId:pp" , "appKey:test"})
    @POST("/safecity-api-develop/v1/pp/devices/{deviceId}/contacts/general/modify")
    Call<ApiDefault> updateAddress(@Header("authorization") String authorization , @Body Map<String , String> temp_map);
//
//    //스케줄 목록 조회
//    @Headers({"Content-Type:application/json"  , "serviceId:pp" , "appKey:test"})
//    @GET("/safecity-api-develop/v1/pp/devices/{deviceId}/settings/schedule/load")
//    Call<ApiScheduleLoadData> getScheduleLoad(@Path ("deviceId") String deviceId , @Header("authorization") String authorization , @QueryMap Map<String , String> temp_map);
//
//
//    //스케줄 알림 조회
//    @Headers({"Content-Type:application/json" , "Accept: application/json", "serviceId:pp" , "appKey:test"})
//    @POST("/safecity-api-develop//v1/pp/devices/{deviceId}/settings/schedule/{configCode}/load")
//    Call<ApiScheduleLoadData> getScheduleDetailLoad(@Path ("deviceId") String deviceId  , @Path ("configCode") String configCode , @Header("authorization") String authorization , @Body Map<String , String> temp_map);
//
//    //스케줄 등록
//    @Headers({"Content-Type:application/json" ,  "serviceId:pp" , "appKey:test"})
//    @POST(" /v1/pp/devices/{deviceId}/settings/schedule/0/add")
//    Call<ApiScheduleLoadData> addSchedule(@Path ("deviceId") String deviceId  , @Header("authorization") String authorization , @Body Map<String , String> temp_map);
//
//    //스케줄 알림 on/ off
//    @Headers({"Content-Type:application/json" ,  "Accept: application/json", "serviceId:pp" , "appKey:test"})
//    @GET("/safecity-api-develop/v1/pp/devices/{deviceId}/settings/schedule/{configCode}/status/{status}")
//    Call<Void> getScheduleOnOff(@Path("deviceId") String configCode, @Path("configCode") String deviceId , @Path("status") String status , @Header("authorization") String authorization);
//
//
//
//    //스케줄 알림설정 수정
//    @Headers({"Content-Type:application/json" ,  "Accept: application/json", "serviceId:pp" , "appKey:test"})
//    @POST("/safecity-api-develop/v1/pp/devices/{deviceId}/settings/schedule/{configCode}/modify")
//    Call<ApiDefault> updateAlarmSetting(@Header("authorization") String authorization , @Body Map<String , String> temp_map);

    //싸이렌 동작 설정 저장
    @Headers({"Content-Type:application/json" , "serviceId:pp" , "appKey:test"})
    @POST("/safecity-api-develop/v1/pp/devices/{deviceId}/settings/sos/siren/save")
    Call<ApiSirenLoadData> sirenSettingSave(@Path("deviceId") String deviceId ,@Header("authorization") String authorization , @Body Map<String , String> temp_map);

    //싸이렌 동작 설정 조회
    @Headers({"Content-Type:application/json" , "serviceId:pp" , "appKey:test"})
    @GET("/safecity-api-develop/v1/pp/devices/{deviceId}/settings/sos/siren/load")
    Call<ApiSirenLoadData> sirenSettingLoad(@Path("deviceId") String deviceId , @Header("authorization") String authorization , @QueryMap Map<String , String> temp_map);

//
//    //안심존 체크박스 조회
//    @Headers({"Content-Type:application/json" ,  "Accept: application/json", "serviceId:pp" , "appKey:test"})
//    @GET("/safecity-api-develop/v1/pp/devices/{deviceId}/settings/zone/loctype/load")
//    Call<ApiDefault> getAnsimZoneLoad(@Header("authorization") String authorization , @QueryMap Map<String , String> temp_map);
//
//    //안심존 체크박스 저장
//    @Headers({"Content-Type:application/json" ,  "Accept: application/json", "serviceId:pp" , "appKey:test"})
//    @GET("/safecity-api-develop/v1/pp/devices/{deviceId}/settings/zone/loctype/save")
//    Call getAnsimZoneSave(@Header("authorization") String authorization , @QueryMap Map<String , String> temp_map);



}
