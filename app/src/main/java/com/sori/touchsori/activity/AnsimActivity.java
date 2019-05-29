package com.sori.touchsori.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sori.touchsori.R;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.data.ApiContactListData;
import com.sori.touchsori.data.ApiContactsData;
import com.sori.touchsori.data.ApiDefault;
import com.sori.touchsori.data.ApiScheduleLoadData;
import com.sori.touchsori.data.ApiSirenLoadData;
import com.sori.touchsori.dialog.CustomAnsimDialog;
import com.sori.touchsori.dialog.CustomAnsimUpdateDialog;
import com.sori.touchsori.dialog.CustomCheckBoxDialog;
import com.sori.touchsori.dialog.CustomEditTextDialog;
import com.sori.touchsori.search.SearchActivity;
import com.sori.touchsori.utill.CustomToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnsimActivity extends BaseActivity implements View.OnClickListener{


    TextView ansimSosTv , ansimTimeTv , ansimContactLoadTv;
    CheckBox ansimSirenCb;
    CheckBox ansimAramIbCb;
    String alarmState , sirenState;

    TextView beforTimeTv , beforHourTv , afterHourTv , afterTimeTv;

    LinearLayout ansimTimeRl;

    CustomToolbar mCustomToolbar;

    CustomEditTextDialog mCustomEditTextDialog;
    CustomCheckBoxDialog mCustomCheckBoxDialog;
    CustomAnsimDialog mCustomAnsimDialog;

    private Context mContext;

    ApiSirenLoadData apiSirenLoadData;
    ApiScheduleLoadData apiScheduleLoadData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ansim_view);
        mContext = this;


        initView();

        //getScheduleLoadApi();
        getSirenLoadApi();



    }


    private void initView() {
        mCustomToolbar = findViewById(R.id.ansim_toolbar);
        mCustomToolbar.setToolbarTitle("안심귀가 설정");
        ansimSosTv = findViewById(R.id.ansim_sos_tv);
        ansimTimeTv = findViewById(R.id.ansim_time_setting_tv);
        ansimContactLoadTv = findViewById(R.id.ansim_contacts_load_tv);
        ansimSirenCb = findViewById(R.id.siren_cv);

      //  ansimTimeRl = findViewById(R.id.ansim_content_rl);

        ansimAramIbCb = findViewById(R.id.ansim_alarm_btn);

   //     beforHourTv = findViewById(R.id.ansim_beforehour_tv);
   //     beforTimeTv = findViewById(R.id.ansim_beforetime_tv);
   //     afterHourTv = findViewById(R.id.ansim_afterhour_tv);
   //     afterTimeTv = findViewById(R.id.ansim_aftertime_tv);

  //      ansimTimeRl.setOnClickListener(this);
        ansimTimeTv.setOnClickListener(this);
        ansimContactLoadTv.setOnClickListener(this);
        ansimSosTv.setOnClickListener(this);

        if (utils.getAlarmStatus().equals("on")) {
            ansimAramIbCb.setChecked(true);
        }else {
            ansimAramIbCb.setChecked(false);
        }

        ansimSirenCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.siren_cv : {
                        if (isChecked) {
                            sirenState = "on";
                        }else {
                            sirenState = "off";
                        }
                        break;
                    }
                }
            }
        });

        ansimSirenCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSirenApi();
            }
        });

        ansimAramIbCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.ansim_alarm_btn : {
                        if (isChecked) {
                            alarmState = "on";
                            utils.saveAlaramStatus("on");
                        //    getAlarmOnOffApi();
                        }else {
                            alarmState = "off";
                        //    getAlarmOnOffApi();
                            utils.saveAlaramStatus("off");
                        }
                        break;
                    }
                }
            }
        });

//        ansimTimeRl.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                mCustomAnsimDialog = new CustomAnsimDialog(mContext);
//                mCustomAnsimDialog.setCancelable(true);
//                mCustomAnsimDialog.show();
//                utils.dialogSizeSetting((Activity) mContext, mCustomAnsimDialog);
//
//                mCustomAnsimDialog.onClickDeleteListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // 삭제 api 호출
//
//                    }
//                });
//
//                mCustomAnsimDialog.onClickUpdateListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mCustomAnsimDialog.dismiss();
//                        CustomAnsimUpdateDialog mCustomAnsimUpdateDialog = new CustomAnsimUpdateDialog(mContext);
//                        mCustomAnsimUpdateDialog.setCancelable(true);
//                        mCustomAnsimUpdateDialog.show();
//
//
//                    }
//                });
//
//
//                return true;
//            }
//        });

    }

    private void getSirenLoadApi() {

        apiUtil.getSirenLoad().enqueue(new Callback<ApiSirenLoadData>() {
            @Override
            public void onResponse(Call<ApiSirenLoadData> call, Response<ApiSirenLoadData> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        sirenState = "off";
                        ansimSirenCb.setChecked(false);
                        utils.saveSiren("off");
                    }else {
                        // 키고 ..
                        apiSirenLoadData = response.body();
                        if (apiSirenLoadData.getStatus().equals("on")) {
                            ansimSirenCb.setChecked(true);
                            utils.saveSiren("on");
                        }else {
                            ansimSirenCb.setChecked(false);
                            utils.saveSiren("off");
                        }
                    }
                }else {
                    try {
                        String message;
                        String code;
                        JSONObject jsonError = new JSONObject(response.errorBody().string());
                        code = jsonError.getString("code");
                        if (code.equals("1201")){
                            refreshToken();
                            getSirenLoadApi();
                        }
                        message = jsonError.getString("message");
                        showMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiSirenLoadData> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

//
//    private void getScheduleLoadApi() {
//        try {
//         //   jsonObject.put("deviceId", deviceObj.get("username").getAsString());
//
//            apiUtil.scheduleLoad().enqueue(new Callback<ApiScheduleLoadData>() {
//                @Override
//                public void onResponse(Call<ApiScheduleLoadData> call, Response<ApiScheduleLoadData> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body() == null) {
//
//                        }else {
//                            apiScheduleLoadData = response.body();
//                            // 여기서 데이터 값 받아처리 ...
//                            scheduleDetailLoad(apiScheduleLoadData);
//
//                        }
//                    }else {
//                        try {
//                            String message;
//                            String code;
//                            JSONObject jsonError = new JSONObject(response.errorBody().string());
//                            code = jsonError.getString("code");
//                            if (code.equals("1201")){
//                                refreshToken();
//
//                            }
//                            message = jsonError.getString("message");
//                            showMessage(message);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                @Override
//                public void onFailure(Call<ApiScheduleLoadData> call, Throwable t) {
//                    showMessage(t.getMessage());
//                    t.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void scheduleDetailLoad(ApiScheduleLoadData data) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("configId", data.getConfigId());
//            jsonObject.put("serviceId", data.getServiceId());
//            jsonObject.put("deviceId", data.getDeviceId());
//            jsonObject.put("configGroup", data.getConfigGroup());
//            jsonObject.put("configCode", data.getConfigCode());
//            jsonObject.put("data", data.getData());
//            jsonObject.put("Status", data.getStatus());
//            jsonObject.put("processStep", data.getProcessStep());
//
//
//            apiUtil.scheduleDetailLoad(jsonObject).enqueue(new Callback<ApiScheduleLoadData>() {
//                @Override
//                public void onResponse(Call<ApiScheduleLoadData> call, Response<ApiScheduleLoadData> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body() == null) {
//                            showMessage("알림을 설정해주세요");
//                        }else {
//                            apiScheduleLoadData = response.body();
//                        }
//                    } else {
//                        try {
//                            String message;
//                            String code;
//                            JSONObject jsonError = new JSONObject(response.errorBody().string());
//                            code = jsonError.getString("code");
//                            if (code.equals("1201")){
//                                refreshToken();
//
//                            }
//                            message = jsonError.getString("message");
//                            showMessage(message);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ApiScheduleLoadData> call, Throwable t) {
//                    t.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void getAlarmOnOffApi() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            //   jsonObject.put("deviceId", deviceObj.get("username").getAsString());
//            jsonObject.put("status", alarmState);
//            jsonObject.put("configCode", alarmState);
//            apiUtil.scheduleOnOff(jsonObject).enqueue(new Callback<Void>() {
//                @Override
//                public void onResponse(Call call, Response response) {
//                    if (response.isSuccessful()) {
//                        showMessage(alarmState + " : 저장되었습니다");
//                    }else {
//                        try {
//                            JSONObject jsonError = new JSONObject(response.errorBody().string());
//                            String message = jsonError.getString("message");
//                            String code = jsonError.getString("code");
//                            if (code.equals("1201")){
//                                refreshToken();
//
//                            }
//                            showMessage(message);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call call, Throwable t) {
//                    t.printStackTrace();
//                    showMessage(t.getMessage());
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void saveSirenApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            //   jsonObject.put("deviceId", deviceObj.get("username").getAsString());
            jsonObject.put("status", sirenState);
            jsonObject.put("data", "null");
            apiUtil.getSirenSave(jsonObject).enqueue(new Callback<ApiSirenLoadData>() {
                @Override
                public void onResponse(Call<ApiSirenLoadData> call, Response<ApiSirenLoadData> response) {
                    if (response.isSuccessful()) {
                        apiSirenLoadData = response.body();
                        showMessage(apiSirenLoadData.getStatus() + "저장 되었습니다");
                    }else {
                        try {
                            String message;
                            String code;
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            code = jsonError.getString("code");
                            if (code.equals("1201")){
                                refreshToken();

                            }
                            message = jsonError.getString("message");
                            showMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiSirenLoadData> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ArrayList<ApiContactListData> sosList;
    public void getContactsLoad() {

        try {
            //   jsonObject.put("deviceId", deviceObj.get("username").getAsString());
            apiUtil.getContacts().enqueue(new Callback<ApiContactsData>() {
                @Override
                public void onResponse(Call<ApiContactsData> call, Response<ApiContactsData> response) {
                    if (response.isSuccessful()) {
                    //    JsonObject contacts = response.body().getContacts();
                        JsonArray js;
                         sosList = new ArrayList<>();
                         sosList.clear();
                        try {
                            js = response.body().generalList();

                            for (JsonElement jsonElement : js) {
                                sosList.add(new ApiContactListData(jsonElement.getAsJsonObject()));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            showMessage(e.getMessage());
                        }

                        Intent intent = new Intent(mContext, SearchActivity.class);
                        intent.putExtra("type", "sos");
                        intent.putExtra("sosItems" , sosList);
                        mContext.startActivity(intent);
                    }else {
                        try {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            String message = jsonError.getString("message");
                            String code = jsonError.getString("code");
                            if (code.equals("1201")){
                                refreshToken();
                                getContactsLoad();
                            }
                            showMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 조회 실패
                    }
                }

                @Override
                public void onFailure(Call<ApiContactsData> call, Throwable t) {
                    showMessage(t.getMessage());
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addContacts(String name , String phoneNumber) {
        JSONObject jsonObject = new JSONObject();
        try {
            //   jsonObject.put("deviceId", deviceObj.get("username").getAsString());
            jsonObject.put("name", name);
            jsonObject.put("phone", phoneNumber);
            apiUtil.addContacts(jsonObject).enqueue(new Callback<ApiContactsData>() {
                @Override
                public void onResponse(Call<ApiContactsData> call, Response<ApiContactsData> response) {
                    if (response.isSuccessful()) {

                        showMessage("저장되었습니다");

                    }else {
                        try {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            String message = jsonError.getString("message");
                            String code = jsonError.getString("code");
                            if (code.equals("1201")){
                                refreshToken();

                            }
                            showMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 조회 실패
                    }
                }

                @Override
                public void onFailure(Call<ApiContactsData> call, Throwable t) {
                    showMessage(t.getMessage());
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ansim_sos_tv : {
                mCustomEditTextDialog = new CustomEditTextDialog(this);
                mCustomEditTextDialog.setCancelable(true);
                mCustomEditTextDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mCustomEditTextDialog.show();

                utils.dialogSizeSetting(this , mCustomEditTextDialog);

                break;
            }

            case R.id.ansim_contacts_load_tv : {
                //수신자 관리..
                getContactsLoad();
                break;
            }


            case R.id.ansim_time_setting_tv : {
                //안심 귀가 시간 설정
                CustomAnsimUpdateDialog mCustomAnsimUpdateDialog = new CustomAnsimUpdateDialog(mContext);
                mCustomAnsimUpdateDialog.setCancelable(true);
                mCustomAnsimUpdateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mCustomAnsimUpdateDialog.show();

                break;
            }


        }
    }

}
