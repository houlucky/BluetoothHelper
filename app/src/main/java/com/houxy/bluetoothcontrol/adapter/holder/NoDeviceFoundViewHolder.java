package com.houxy.bluetoothcontrol.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.base.BaseViewHolder;
import com.houxy.bluetoothcontrol.bean.NoDeviceFoundHeader;

import butterknife.Bind;

/**
 * Created by Houxy on 2016/11/5.
 */

public class NoDeviceFoundViewHolder extends BaseViewHolder<NoDeviceFoundHeader> {

    @Bind(R.id.headerName)
    TextView headerNameTv;

    public NoDeviceFoundViewHolder(ViewGroup root) {
        super(root, R.layout.item_no_device_found);
    }

    @Override
    public void bindData(NoDeviceFoundHeader noDeviceFoundHeader) {
        if(!noDeviceFoundHeader.getHeaderName().isEmpty()){
            headerNameTv.setText(noDeviceFoundHeader.getHeaderName());
        }
    }
}
