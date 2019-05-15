package com.sori.touchsori.utill;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class TypefaceUtil {

    public static void overrideFont(Context context , String defaultFontName , String customFontFileName) {
        try {
            final Typeface customTypeface = Typeface.createFromAsset(context.getResources().getAssets() , customFontFileName);

            final Field defaulTypefaceFieId = Typeface.class.getDeclaredField(defaultFontName);
            defaulTypefaceFieId.setAccessible(true);
            defaulTypefaceFieId.set(null , customTypeface);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
