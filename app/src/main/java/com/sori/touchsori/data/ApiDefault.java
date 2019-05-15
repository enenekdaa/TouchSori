package com.sori.touchsori.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ApiDefault {

    @SerializedName("header")
    JsonObject header;
    @SerializedName("data") JsonObject data;

    /**
     * 결과 data값 data 내에 필요 데이터가 다 있음
     * @return data
     */
    public JsonObject getData(){
        return data;
    }

    /**
     * 결과 헤더값
     * @return header
     */
    public JsonObject getHeader() {
        return header;
    }

    /**
     * 리스트 날짜지났는지 상태 Y/N
     * @return list_yn
     */
}
