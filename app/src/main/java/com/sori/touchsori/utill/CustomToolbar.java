package com.sori.touchsori.utill;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sori.touchsori.R;

/**
 * Created by temp on 2017. 9. 25..
 */

public class CustomToolbar extends Toolbar {

    private Context mContext;
    private TextView mTvCustomToolbarTitle;
    private RelativeLayout mRlBack;

    public CustomToolbar(Context context) {
        super(context);
        init(context);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context mContext) {
        //커스텀뷰에 xml추가
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_toolbar, this, true);

        this.mContext = mContext;

        mTvCustomToolbarTitle = (TextView) findViewById(R.id.tv_custom_toolbar_title);
        mRlBack = (RelativeLayout) findViewById(R.id.rl_custom_toolbar_back);
        mRlBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).finish();
            }
        });

        //툴바 여백 제거
        setContentInsetsAbsolute(0, 0);
    }

    public void setToolbarTitle(String title) {
        mTvCustomToolbarTitle.setText(title);
    }

}
