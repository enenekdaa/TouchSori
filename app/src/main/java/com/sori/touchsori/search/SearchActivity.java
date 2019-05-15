package com.sori.touchsori.search;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sori.touchsori.R;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.data.ApiContactListData;
import com.sori.touchsori.data.ApiContactsData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    FrameLayout toolbarFl;
    EditText searchEdt;

    RelativeLayout searchRl , backRl;
    TextView titleTv;

    Context mContext;
    List<ContactItem> dataList;
    SearchAdapter searchAdapter;
    // 긴급 수신자 : sos   , 주소록 : contact
    private String type = "contact";
    private ArrayList<ApiContactListData> sosList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        type = getIntent().getStringExtra("type" );
        initView();

    }

    private void initView () {

        recyclerView = findViewById(R.id.recycler_view);

        toolbarFl = findViewById(R.id.toolbar_search_fl);
        searchEdt = findViewById(R.id.serach_edt);

        backRl = findViewById(R.id.rl_custom_toolbar_back);
        searchRl = findViewById(R.id.toolbar_search_rl);
        titleTv = findViewById(R.id.tv_custom_toolbar_title);
        titleTv.setText(getResources().getString(R.string.sms_with));

        backRl.setOnClickListener(this);
        searchRl.setOnClickListener(this);

        toolbarFl.setVisibility(View.VISIBLE);
        searchEdt.setVisibility(View.GONE);


        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = searchEdt.getText().toString().toLowerCase(Locale.getDefault());
                searchAdapter.filter(text);
            }
        });

        if (type.equals("sos")) {
            sosList = (ArrayList<ApiContactListData>) getIntent().getSerializableExtra("sosItems");
            searchAdapter = new SearchAdapter(mContext, sosList ,false);
            searchEdt.setVisibility(View.GONE);
            searchRl.setVisibility(View.GONE);
        }else {
            dataList = getContactList();
            searchAdapter = new SearchAdapter(mContext, dataList , true);
            searchEdt.setVisibility(View.GONE);
            searchRl.setVisibility(View.VISIBLE);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(searchAdapter);

    }

    public ArrayList<ContactItem> getContactList() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER ,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };

        String[] selectionArgs = null;
        String ssortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor cursor = getApplicationContext().getContentResolver().query(uri , projection , null , selectionArgs , ssortOrder);

        LinkedHashSet<ContactItem> haslist = new LinkedHashSet<>();
        ArrayList<ContactItem> contactIList;

        if (cursor.moveToFirst()) {
            do {
                ContactItem contactItem = new ContactItem();
                contactItem.setUser_Number(cursor.getString(0));
                contactItem.setUser_Name(cursor.getString(1));

                haslist.add(contactItem);
            } while (cursor.moveToNext());
        }
        contactIList = new ArrayList<>(haslist);
        for (int i = 0; i < contactIList.size(); i ++) {
            contactIList.get(i).setId(i);
        }
        return contactIList;
    }


    public void deleteContact(String phoneNumber) {
        JSONObject jsonObject = new JSONObject();
        try {
            //   jsonObject.put("deviceId", deviceObj.get("username").getAsString());
            jsonObject.put("phone", phoneNumber);
            apiUtil.deleteContacts(jsonObject).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        showMessage("삭제완료");
                        searchAdapterDialog.dismiss();
                        finish();
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
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

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


    SearchAdapterDialog searchAdapterDialog;

    public void longClickDialogShow(final String name , final String number , String clickType) {
        searchAdapterDialog = new SearchAdapterDialog(mContext);
        searchAdapterDialog.setCancelable(true);
        searchAdapterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        searchAdapterDialog.show();

        if (clickType.equals("delete")) {
            searchAdapterDialog.setTitleTv(this.getResources().getString(R.string.setting_sos_delete));
            searchAdapterDialog.okDeleteListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteContact(number);
                    searchAdapterDialog.dismiss();
                }
            });
        }else {
            searchAdapterDialog.setTitleTv(mContext.getResources().getString(R.string.setting_sos_insert));
            searchAdapterDialog.okDeleteListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addContacts(name , number);
                    searchAdapterDialog.dismiss();
                }
            });
        }

        utils.dialogSizeSetting(this , searchAdapterDialog);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_custom_toolbar_back : {
                finish();

                break;
            }

            case R.id.toolbar_search_rl : {
                toolbarFl.setVisibility(View.GONE);
                searchEdt.setVisibility(View.VISIBLE);
                searchEdt.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


                break;
            }
        }
    }

}
