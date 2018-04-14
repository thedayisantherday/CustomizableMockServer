package com.example.zxg.customizablemockserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zy.mocknet.Main;
import com.zy.mocknet.MockNet;
import com.zy.mocknet.application.MockRequestExecutor;
import com.zy.mocknet.application.handler.BlockHandler;
import com.zy.mocknet.application.handler.LogHandler;
import com.zy.mocknet.application.handler.VerifyHeaderHandler;
import com.zy.mocknet.application.handler.VerifyParamHandler;
import com.zy.mocknet.common.logger.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_start_server, tv_stop_server, tv_edit_api, tv_request;

    private MockNet mMockNet;
    private MockRequestExecutor mMockRequestExecutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mMockNet = MockNet.create();
        mMockRequestExecutor = mMockNet.getExecutor();
        mMockRequestExecutor.setIsInitHandler(false);
        mMockRequestExecutor.addUserHandler(new BlockHandler());
        mMockRequestExecutor.addUserHandler(new VerifyParamHandler());
        mMockRequestExecutor.addUserHandler(new VerifyHeaderHandler());
        mMockRequestExecutor.addUserHandler(new LogHandler());
        mMockRequestExecutor.addUserHandler(new CustomConnectionHandler(this));
    }

    private void initView() {
        tv_start_server = (TextView) findViewById(R.id.tv_start_server);
        tv_start_server.setOnClickListener(this);
        tv_stop_server = (TextView) findViewById(R.id.tv_stop_server);
        tv_stop_server.setOnClickListener(this);
        tv_edit_api = (TextView) findViewById(R.id.tv_edit_api);
        tv_edit_api.setOnClickListener(this);
        tv_request = (TextView) findViewById(R.id.tv_request);
        tv_request.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start_server:
                mMockNet.start();
                Toast.makeText(MainActivity.this, "tv_start_server", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_stop_server:
                mMockNet.stop();
                Toast.makeText(MainActivity.this, "tv_stop_server", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_edit_api:
                Intent intent = new Intent(MainActivity.this, EditApiActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_request:
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = "http://127.0.0.1:8088/test";
                requestQueue.add(new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.d(response);
                        toast(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.d("error");
                        toast("error");
                    }
                }));
                break;
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
