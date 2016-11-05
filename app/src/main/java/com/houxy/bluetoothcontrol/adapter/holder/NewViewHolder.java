package com.houxy.bluetoothcontrol.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.base.BaseViewHolder;
import com.houxy.bluetoothcontrol.bean.NewDeviceHeader;

import butterknife.Bind;

/**
 * Created by Houxy on 2016/10/31.
 */

public class NewViewHolder extends BaseViewHolder<NewDeviceHeader> {

    @Bind(R.id.headerName)
    TextView headerName;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    public NewViewHolder(ViewGroup root) {
        super(root, R.layout.item_new);
    }


    @Override
    public void bindData(NewDeviceHeader newDeviceHeader) {

        if(!newDeviceHeader.getHeaderName().isEmpty()){
            headerName.setText(newDeviceHeader.getHeaderName());
        }

        if(newDeviceHeader.getProgressBarState()){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
