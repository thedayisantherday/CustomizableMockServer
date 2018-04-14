package com.example.zxg.customizablemockserver;

import android.content.Context;
import android.content.SharedPreferences;

import com.zy.mocknet.application.handler.Handler;
import com.zy.mocknet.application.handler.chain.HandlerChain;
import com.zy.mocknet.server.bean.Headers;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;
import com.zy.mocknet.server.bean.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * It gets Response from MockConnection by Request.
 * Created by zxg on 2018/3/25.
 */

public class CustomConnectionHandler extends Handler{

    private Context mContext;

    public CustomConnectionHandler(Context context) {
        mContext = context;
    }

    @Override
    public Response handle(Request request, HandlerChain handlerChain, int i) {
        String method = request.getMethod();
        String url = request.getRequestUri();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("apiPreferences", Context.MODE_PRIVATE);
        String oldApiKeys = sharedPreferences.getString("apiKeys", "");
        if (!oldApiKeys.contains(method + "_" + url)) {
            return Response.create404Response();
        }

        String content = sharedPreferences.getString(method + "_" + url, "");
        // 存在该接口时，组装response
        Response response = new Response();
        response.setStatusCode(200);
        response.setHttpVersion("HTTP/1.1");
        response.setReasonPhrase("OK");

        Map<String, String> responseHead = new HashMap<>();
        responseHead.put("Content-Length", content.length() + "");
        Headers headers = new Headers();
        headers.addHeader(responseHead);
        response.setHeaders(headers);

        ResponseBody body = new ResponseBody();
        byte[] contentByte = content.getBytes();
        body.setContent(contentByte, contentByte.length);
        response.setBody(body);

        return response;
    }
}
