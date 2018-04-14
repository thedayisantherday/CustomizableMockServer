package com.example.zxg.customizablemockserver;

/**
 * Created by zxg on 2018/3/21.
 */

public class ApiModel {
    private String apiMethod;
    private String apiName;
    private String apiContent;

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiContent() {
        return apiContent;
    }

    public void setApiContent(String apiContent) {
        this.apiContent = apiContent;
    }
}
