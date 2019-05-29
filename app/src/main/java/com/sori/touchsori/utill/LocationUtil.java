package com.sori.touchsori.utill;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.sori.touchsori.SoriApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dongnam on 2017. 6. 5..
 */

public class LocationUtil {
    public static final String TAG = LocationUtil.class.getSimpleName();    // 디버그 태그

    /**
     * GPS 설정 체크
     *
     * @return
     */
    public static boolean isGPSService(Context context) {
        String gps = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!(gps.matches(".*gps.*") || gps.matches(".*network.*"))) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Location에 따른 Map Url을 가져온다.
     * @param cxt
     * @param location
     * @return
     */
    public static String getMapUrl(Context cxt, Location location, String address) {
        String mapUrl = "";
        String point = "";
        String encodeUrl = "";

        if(null != location) {
            point =  Double.toString(location.getLatitude()) + "," +  Double.toString(location.getLongitude());
        }


        SoriApplication app;

        // 전역 (Application) 변수
        app = (SoriApplication) cxt;

//        HashMap<String, String> countryInfo = app.getConfig().getEmergencyCountry();
//        String countryISO = countryInfo.get(Define.KEY_COUNTRY_ISO_CODE);

//        if("CN".equalsIgnoreCase(countryISO) || "zh".equalsIgnoreCase(countryISO) || "zh_CN".equalsIgnoreCase(countryISO)) {
//            mapUrl = "http://api.map.baidu.com/marker?location=";
//            mapUrl += point;
//
//            if(false == TextUtils.isEmpty(address)) {
//                String title = "";
//                try {
//                    title = URLEncoder.encode("&title="+address, "utf-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                mapUrl += title;
//            }
//
//            String encodingStr = "";
//            try {
//                encodingStr = URLEncoder.encode("&output=html","utf-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            mapUrl += encodingStr;
//        }else {
            mapUrl = "http://maps.google.com/maps?q=";

            if(false == TextUtils.isEmpty(address)) {
                address = address.trim();
                try {
                    String addressEncode = URLEncoder.encode(address, "utf-8");
                    mapUrl += addressEncode;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else {
                mapUrl += point;
            }
 //       }
        String shortUrl = naverShortUrl(mapUrl);
        return shortUrl;
    }


    /**
     * 위도, 경도로 부터 주소를 리턴한다.
     *
     * @param cxt
     * @param latitude
     * @param longitude
     * @return
     */
    public static String getAddress(Context cxt, double latitude, double longitude) {
        String address = "";

        Geocoder geocoder = new Geocoder(cxt, Locale.getDefault());
        try {
            List<Address> addrs = geocoder.getFromLocation(latitude, longitude, 1);
            String addr = addrs.get(0).getAddressLine(0);
            if (!TextUtils.isEmpty(addr)) {
                String rAddr = addr.replace("남한", "");
                address = rAddr.replace("대한민국", "");
            }
        } catch (Exception e) {
            address = "";
        }
        return address;
    }

    /**
     * GoogleMap에 위도, 경도에 해당하는 위치를 보여준다.
     *
     * @param cxt
     * @param app
     * @param latitude
     * @param longitude
     */
    public static void showGoogleMap(Context cxt, SoriApplication app, final String latitude, final String longitude) {
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(longitude);
            String address = getAddress(cxt, lat, lon);
            Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + "(" + address + ")");

            LogUtil.d(TAG, "showGoogleMap uri : " + uri);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(cxt.getPackageManager()) != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cxt.startActivity(intent);
            } else {
                app.showMessage("Google map is not installed..");
            }
        } else {
            app.showMessage("Location info can not be found...");
        }

    }
    /**
     * Naver Short URL 생성
     *
     * @param urlParam
     * @return
     */
    public static String naverShortUrl(String urlParam) {

        AsyncTask<String, Void, String> shortUrlTask = new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                Log.i(TAG, "onPreExecute() -> Start !!!");
            }

            @Override
            protected String doInBackground(String... params) {
                String clientId = "TKgANs4_W0093fOg4o8D";//애플리케이션 클라이언트 아이디값";
                String clientSecret = "2_akmdWViF";//애플리케이션 클라이언트 시크릿값";
                StringBuffer response = new StringBuffer();
                String returnUrl = "";
                HttpURLConnection con = null;
                try {
                    String text = params[0];
//					String text = "https://developers.naver.com/notice";
                    String apiURL = "https://openapi.naver.com/v1/util/shorturl";
                    URL url = new URL(apiURL);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    // post request
                    String postParams = "url=" + text;
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(postParams);
                    wr.flush();
                    wr.close();
                    int responseCode = con.getResponseCode();
                    BufferedReader br;
                    if (responseCode == 200) { // 정상 호출
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    LogUtil.d(TAG, "naverShortUrl ==>" + response.toString());
                } catch (Exception e) {
                    System.out.println(e);
                }
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONObject urlObject = (JSONObject) obj.get("result");
                    returnUrl = (String) urlObject.get("url");
                    return returnUrl;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return returnUrl;
            }

            @Override
            protected void onPostExecute(String s) {
                LogUtil.i(TAG, "onPostExecute() -> Start !!!");
            }
        };

        String urlOut = "";
        try {
            urlOut = shortUrlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlParam).get();
        } catch (InterruptedException e) {
            LogUtil.e(TAG, e.getMessage());
        } catch (ExecutionException e) {
            LogUtil.e(TAG, e.getMessage());
        }
        return urlOut;
    }

}
