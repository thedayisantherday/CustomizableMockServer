package com.example.zxg.customizablemockserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// 所有的key放在一个sharedPreference里面，所有的接口的值存放在一个map里面然后存放在sharedPreference
public class EditApiActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv_api;
    private ImageView iv_add_api;
    private AlertDialog mAlertDialog;
    private RadioButton api_item_post, api_item_get;
    private EditText api_item_name, api_item_content;

    private ApiRecyclerViewAdapger mApiRecyclerViewAdapger;

    private boolean isModify = false;

    // 下拉选择框 http://blog.csdn.net/zhuwentao2150/article/details/52171794

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_api);

        rv_api = (RecyclerView) findViewById(R.id.rv_api);
        ApiRecyclerViewAdapger.ModifyCallback modifyCallback = new ApiRecyclerViewAdapger.ModifyCallback() {
            @Override
            public void modify(String method, String name, String content) {
                isModify = true;
                showDialog();
                if ("GET".equals(method)) {
                    api_item_get.setChecked(true);
                    api_item_post.setChecked(false);
                } else {
                    api_item_get.setChecked(false);
                    api_item_post.setChecked(true);
                }
                api_item_name.setText(name);
                api_item_content.setText(content);
            }
        };
        mApiRecyclerViewAdapger = new ApiRecyclerViewAdapger(getApiModelList(), modifyCallback);
        rv_api.setAdapter(mApiRecyclerViewAdapger);
        rv_api.setHasFixedSize(true);
        LinearLayoutManager recyclerLayoutManagement = new LinearLayoutManager(this);
        rv_api.setLayoutManager(recyclerLayoutManagement);

        iv_add_api = (ImageView) findViewById(R.id.iv_add_api);
        iv_add_api.setOnClickListener(this);
    }

    private boolean addApi2Preference(String apiMethod, String apiName, String apiContent) {
        SharedPreferences sharedPreferences = getSharedPreferences("apiPreferences", Context.MODE_PRIVATE);

        String oldApiKeys = sharedPreferences.getString("apiKeys", "");
        if ((!isModify && oldApiKeys.contains(apiMethod + "_" + apiName))
                || (isModify && !oldApiKeys.contains(apiMethod + "_" + apiName))) {
            return false;
        }

        SharedPreferences.Editor apiEditor = sharedPreferences.edit();
        if (!isModify) {
            apiEditor.putString("apiKeys", oldApiKeys + "@@" + apiMethod + "_" + apiName);
            apiEditor.putString(apiMethod + "_" + apiName, apiContent);
//        apiEditor.apply(); // 提交到内存并异步提交数据，没有是否修改成功的返回值
            apiEditor.commit();
        } else {
            apiEditor.remove(apiMethod + "_" + apiName);
            apiEditor.commit();

            SharedPreferences.Editor apiEditor1 = sharedPreferences.edit();
            apiEditor1.putString(apiMethod + "_" + apiName, apiContent);
            apiEditor1.commit();
        }

        return true;
    }

    private List<ApiModel> getApiModelList() {
        List<ApiModel> apiModels = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("apiPreferences", Context.MODE_PRIVATE);
        String apiKeysStr = sharedPreferences.getString("apiKeys", "");
        String[] apiKeys = apiKeysStr.split("@@");

        if (apiKeys != null && apiKeys.length > 0) {
            for (int i = 0; i < apiKeys.length; i++) {
                ApiModel apiModel = new ApiModel();

                String[] keys = apiKeys[i].split("_");
                if (keys == null || keys.length < 2) {
                    continue;
                }
                apiModel.setApiMethod(keys[0]);
                apiModel.setApiName(keys[1]);

                String apiContent = sharedPreferences.getString(apiKeys[i], "");
                apiModel.setApiContent(apiContent);

                apiModels.add(apiModel);
            }
        }
        return apiModels;
    }

    private void showDialog () {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(EditApiActivity.this).create();
            mAlertDialog.setCanceledOnTouchOutside(false); //点击对话框外部对话框不消失
            mAlertDialog.show(); // 需要在setContentView()之前调用

            Window window = mAlertDialog.getWindow();
            window.setContentView(R.layout.layout_api_dialog);
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

            api_item_post = (RadioButton) window.findViewById(R.id.api_item_post);
            api_item_post.setOnClickListener(this);
            api_item_get = (RadioButton) window.findViewById(R.id.api_item_get);
            api_item_get.setOnClickListener(this);
            api_item_name = (EditText) window.findViewById(R.id.api_item_name);
            api_item_content = (EditText) window.findViewById(R.id.api_item_content);

            TextView tv_sure = (TextView) window.findViewById(R.id.tv_sure);
            tv_sure.setOnClickListener(this);
            TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(this);

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        } else {
            mAlertDialog.show();
        }

        if (isModify) {
            api_item_post.setEnabled(false);
            api_item_get.setEnabled(false);
            api_item_name.setEnabled(false);
        } else {
            api_item_get.setChecked(false);
            api_item_post.setChecked(true);
            api_item_name.setText("");
            api_item_content.setText("");

            api_item_post.setEnabled(true);
            api_item_get.setEnabled(true);
            api_item_name.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_add_api:
                isModify = false;
                showDialog();
                break;
            case R.id.api_item_post:
                api_item_get.setChecked(false);
                break;
            case R.id.api_item_get:
                api_item_post.setChecked(false);
                break;
            case R.id.tv_sure:
                String method = api_item_post.isChecked() ? api_item_post.getText().toString() : api_item_get.getText().toString();
                if (TextUtils.isEmpty(api_item_name.getText().toString())) {
                    Toast.makeText(EditApiActivity.this, "name不能为空！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (TextUtils.isEmpty(api_item_content.getText().toString())) {
                    Toast.makeText(EditApiActivity.this, "content不能为空！", Toast.LENGTH_SHORT).show();
                    break;
                }
                boolean isSucceed = addApi2Preference(method, api_item_name.getText().toString(), api_item_content.getText().toString());
                if (!isSucceed) {
                    Toast.makeText(EditApiActivity.this, "已经存在相同method和name的api，请重新输入！", Toast.LENGTH_SHORT).show();
                    break;
                }
                mApiRecyclerViewAdapger.setData(getApiModelList());
                mApiRecyclerViewAdapger.notifyDataSetChanged();
                isModify = false;
            case R.id.tv_cancel:
                mAlertDialog.dismiss();
                break;
        }
    }
}
