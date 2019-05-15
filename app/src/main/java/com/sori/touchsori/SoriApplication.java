package com.sori.touchsori;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.sori.touchsori.api.ApiUtil;
import com.sori.touchsori.utill.TypefaceUtil;

import java.lang.reflect.Field;

import kr.co.innochal.touchsorilibrary.classes.TouchSori;

public class SoriApplication extends Application {

    public static SoriApplication soriApplication;
    public ApiUtil apiUtil;
    public TouchSori touchSori;

    public static SoriApplication getInstance() {return soriApplication;}

    @Override
    public void onCreate() {
        super.onCreate();
        soriApplication = this;
        apiUtil = new ApiUtil(this);
        TypefaceUtil.overrideFont(this , "SERIF" , "fonts/NanumSquare_acB.ttf");

    }


}
