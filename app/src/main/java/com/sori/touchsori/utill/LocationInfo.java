package com.sori.touchsori.utill;


import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sori.touchsori.SoriApplication;

public class LocationInfo {
    private static final String TAG = LocationInfo.class.getSimpleName();   // 디버그 태그
    private Context mContext;                                               // 콘텍스트
    private SoriApplication soriApplication;                                      // 전역 (Application) 변수
    private static LocationInfo mInstance;                                  // 인스턴스
    private Location bestLocation;                                          // 최적의 위치정보

    private LocationInfo(Context context) {
        mContext = context;
        bestLocation = null;
        // 로케이션
        mLM = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // 전역 (Application) 변수
        if (soriApplication == null) soriApplication = (SoriApplication) mContext;
    }

    public static LocationInfo getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocationInfo(context);
        }
        return mInstance;
    }

    /**
     * 위치정보 업데이트
     */
    public void updateLocationInfo(){
        mLocationHandler.sendEmptyMessage(LOCATION_HANDLE_EVENT_START);
        mLocationHandler.sendEmptyMessageDelayed(LOCATION_HANDLE_EVENT_STOP, 15 * 1000);                  //위치 정확도를 위해 15초로 설정

    }

    /**
     * 위치정보 중지
     */
    public void stopLocationInfo(){
        mLocationHandler.removeMessages(LOCATION_HANDLE_EVENT_START);
        mLocationHandler.removeMessages(LOCATION_HANDLE_EVENT_STOP);
        mLM.removeUpdates(mLocationListener);
    }

    /**
     * 위치정보 리스너
     */
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged location.getAccuracy() : " + location.getAccuracy());

            if (location.getAccuracy() > 30.0) {
                return;
            }

            if (bestLocation == null) {
                bestLocation = location;
                return;
            }

            if(bestLocation.getAccuracy() < location.getAccuracy()) {
                bestLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    /**
     * 현재 위치정보
     * @return
     */
    public Location getCurrentLocation() {
        if (bestLocation == null){
            if (soriApplication == null) soriApplication = (SoriApplication) mContext;
            if (soriApplication.checkPermission(null, Manifest.permission.ACCESS_FINE_LOCATION, 1001)) {
                bestLocation = mLM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        return bestLocation;
    }

    private LocationManager mLM;                        // 위치정보 매니저
    private final int LOCATION_HANDLE_EVENT_START = 1;  // 시작
    private final int LOCATION_HANDLE_EVENT_STOP = 2;   // 중지
    private Handler mLocationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == LOCATION_HANDLE_EVENT_START) {
                bestLocation = null;
                if (soriApplication == null) soriApplication = (SoriApplication) mContext;
                if (soriApplication.checkPermission(null, Manifest.permission.ACCESS_FINE_LOCATION, 1001)) {
                    mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                }
            } else if(msg.what == LOCATION_HANDLE_EVENT_STOP) {
                mLM.removeUpdates(mLocationListener);
                if(bestLocation == null){
                    bestLocation = mLM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }
    };
}
