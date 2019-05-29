package com.sori.touchsori.utill;

import android.content.Context;
import android.os.Build;

import java.util.HashMap;


/**
 * Created by innochal on 2018-01-18.
 */

public class EtcUtil {


    /**
     * ParseCount 5번에도 인식하는 단말을 리턴한다.
     * @return
     */
    public  static boolean isSoundParserCountDownDevice() {
        boolean isCountDownDevice = false;

        // 갤럭시6 엣지+,  갤럭시6 엣지, 갤럭시 6,
        // 갤럭시S7, 갤럭시S7 엣지
        // 갤럭시노트3, 노트3 네오, 노트4, 갤럭시노트5
        // V20
        if (Build.MODEL.contains("SM-G928") ||  Build.MODEL.contains("SM-G925") || Build.MODEL.contains("SM-G920")
                || Build.MODEL.contains("SM-G930") || Build.MODEL.contains("SM-G935")
                || Build.MODEL.contains("SM-N900") || Build.MODEL.contains("SM-N750") ||  Build.MODEL.contains("SM-N910") || Build.MODEL.contains("SM-N920")
                || Build.MODEL.contains("SM-F800")) {
            isCountDownDevice = true;
        }
        return isCountDownDevice;
    }

    /**
     * Battery가 많이 소모되는 단말인지 구분한다.
     * SM-G928(갤럭시노트5), SM-N920(갤럭시S6엣지+)는 배터리 많이 소모하는 모델임
     *
     * @return
     */
    public static boolean isBatteryCheckDevice() {
        //갤럭시6 엣지+, 갤럭시노트5, 갤럭시6 엣지, 갤럭시 6
        if (Build.MODEL.contains("SM-G928") || Build.MODEL.contains("SM-N920") || Build.MODEL.contains("SM-G925") || Build.MODEL.contains("SM-G920")) {
            return true;
        }
        return false;
    }


    /**
     * Gyroscope에 의해 움직임이 없는 경우 TouchService Stop을 해주는 단말을 리턴한다.
     * @return
     */
    public static boolean isGyroTouchServiceStopDevice() {
        //갤럭시6 엣지+, 갤럭시6 엣지, 갤럭시 6, 갤럭시 노트5
//        if (Build.MODEL.contains("SM-G928") || Build.MODEL.contains("SM-G920") || Build.MODEL.contains("SM-G925") || Build.MODEL.contains("SM-N920") ) {
//            return true;
//        }
        return false;
    }



    /**
     * km / m 를 표시한다.
     * @param radius
     * @return
     */
    public static String getSafetyZoneRadius(int radius) {
        String radiusStr = "";

        if(radius >= 1000) {
            radiusStr = String.format("%.1f", ((float)radius/1000)) + " km";
        }else {
            radiusStr = radius + " m";
        }
        return radiusStr;
    }

    /**
     * 안심영역 아이템에서 sender/receiver를 구분한다.
     * @param devicePhoneNumber
     * @param item
     * @return
     */
//    public static HashMap getSenderReceiverDivisionItem(String devicePhoneNumber, ItemSafetyZone item) {
//        HashMap hashMap = new HashMap();
//
//        if (item.getSenderHp().equals(devicePhoneNumber)) {
//            hashMap.put(Define.KEY_SENDER_HP, item.getSenderHp());
//            hashMap.put(Define.KEY_SENDER_NAME, item.getSenderName());
//            hashMap.put(Define.KEY_RECEIVER_HP, item.getReceiverHp());
//            hashMap.put(Define.KEY_RECEIVER_NAME, item.getReceiverName());
//        } else {
//            hashMap.put(Define.KEY_SENDER_HP, item.getReceiverHp());
//            hashMap.put(Define.KEY_SENDER_NAME, item.getReceiverName());
//            hashMap.put(Define.KEY_RECEIVER_HP, item.getSenderHp());
//            hashMap.put(Define.KEY_RECEIVER_NAME, item.getSenderName());
//        }
//        return hashMap;
//    }

}
