package com.example.zxg.customizablemockserver;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zxg on 2018/3/19.
 */

public class ApiRecyclerViewAdapger extends RecyclerView.Adapter<ApiRecyclerViewAdapger.ViewHolder> {

    private List<ApiModel> mApiModelList;
    private ModifyCallback mModifyCallback;

    public ApiRecyclerViewAdapger(List<ApiModel> apiModelList, ModifyCallback modifyCallback) {
        mApiModelList = apiModelList;
        mModifyCallback = modifyCallback;
    }

    public void setData(List<ApiModel> apiModelList) {
        mApiModelList = apiModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_api_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mApiModelList == null || mApiModelList.size() <= 0 ||
                position < 0 || position >= mApiModelList.size()) {
            return;
        }

        ApiModel apiModel = mApiModelList.get(position);
        holder.api_item_method.setText(apiModel.getApiMethod());
        holder.api_item_name.setText(apiModel.getApiName());
        holder.api_item_content.setText(apiModel.getApiContent());
    }

    @Override
    public int getItemCount() {
        if (mApiModelList == null || mApiModelList.size() <= 0) {
            return 0;
        }
        return mApiModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView api_item_method, api_item_name, api_item_content;

        public ViewHolder(View itemView) {
            super(itemView);
            api_item_method = (TextView) itemView.findViewById(R.id.api_item_method);
            api_item_name = (TextView) itemView.findViewById(R.id.api_item_name);
            api_item_content = (TextView) itemView.findViewById(R.id.api_item_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mModifyCallback.modify(api_item_method.getText().toString(), api_item_name.getText().toString(), api_item_content.getText().toString());
                }
            });
        }
    }

    public interface ModifyCallback {
        void modify(String method, String name, String content);
    }
}
