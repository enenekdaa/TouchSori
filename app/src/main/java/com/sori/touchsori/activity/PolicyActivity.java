package com.sori.touchsori.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sori.touchsori.R;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.utill.CustomToolbar;

import org.w3c.dom.Text;

public class PolicyActivity extends BaseActivity {

    private TextView policyDoneTv;
    private CustomToolbar toolbar;

    private TextView policyContentTv;
    private String type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policy_activity);
        type = getIntent().getStringExtra("textType");
        initView();

    }
    private void initView() {
        policyDoneTv = findViewById(R.id.policy_done_tv);

        policyDoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar = findViewById(R.id.policy_toolbar);
        policyContentTv = findViewById(R.id.policy_tv);

        if (type.equals("1")) {
            toolbar.setToolbarTitle("개인정보 취급방침");
            policyContentTv.setText(this.getResources().getString(R.string.text1));
        }else {
            toolbar.setToolbarTitle("위치기반 서비스 이용약관");
            policyContentTv.setText(this.getResources().getString(R.string.text2));
        }


    }
}
