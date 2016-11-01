package com.houxy.bluetoothcontrol.adapter.holder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.base.BaseViewHolder;
import com.houxy.bluetoothcontrol.base.i.OnItemClickListener;
import com.houxy.bluetoothcontrol.bean.Device;

import butterknife.Bind;

/**
 * Created by Houxy on 2016/10/31.
 */

public class DeviceViewHolder extends BaseViewHolder {

    @Bind(R.id.deviceName)
    private TextView deviceName;
    @Bind(R.id.deviceAddress)
    private TextView deviceAddress;

    public DeviceViewHolder(ViewGroup root, OnItemClickListener onItemClickListener) {
        super(root, R.layout.item_device, onItemClickListener);
    }

    @Override
    public void bindData(Object o) {
        Device device = (Device)o;
        deviceAddress.setText(device.getDeviceAddress());
        deviceName.setText(device.getDeviceName());
    }
}
