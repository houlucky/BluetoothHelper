package com.houxy.bluetoothcontrol.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.base.BaseViewHolder;
import com.houxy.bluetoothcontrol.base.i.OnItemClickListener;
import com.houxy.bluetoothcontrol.bean.DataItem;
import com.houxy.bluetoothcontrol.bean.Device;

import butterknife.Bind;

/**
 * Created by Houxy on 2016/10/31.
 */

public class DeviceViewHolder extends BaseViewHolder<Device> {

    @Bind(R.id.deviceName)
    TextView deviceName;
    @Bind(R.id.deviceAddress)
    TextView deviceAddress;
    @Bind(R.id.deviceState)
    TextView deviceState;

    public DeviceViewHolder(ViewGroup root, OnItemClickListener onItemClickListener) {
        super(root, R.layout.item_device, onItemClickListener);
    }

    @Override
    public void bindData(Device device) {
        deviceAddress.setText(device.getDeviceAddress());
        deviceName.setText(device.getDeviceName());
        if( device.getDeviceState() ){
            deviceState.setVisibility(View.VISIBLE);
        }
    }
}
