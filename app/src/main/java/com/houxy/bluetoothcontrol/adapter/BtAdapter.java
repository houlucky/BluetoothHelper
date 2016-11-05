package com.houxy.bluetoothcontrol.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.houxy.bluetoothcontrol.C;
import com.houxy.bluetoothcontrol.adapter.holder.BondedViewHolder;
import com.houxy.bluetoothcontrol.adapter.holder.DeviceViewHolder;
import com.houxy.bluetoothcontrol.adapter.holder.NewViewHolder;
import com.houxy.bluetoothcontrol.adapter.holder.NoDeviceFoundViewHolder;
import com.houxy.bluetoothcontrol.base.BaseViewHolder;
import com.houxy.bluetoothcontrol.base.i.OnItemClickListener;
import com.houxy.bluetoothcontrol.bean.DataItem;
import com.houxy.bluetoothcontrol.bean.Device;
import com.houxy.bluetoothcontrol.bean.NewDeviceHeader;
import com.houxy.bluetoothcontrol.bean.NoDeviceFoundHeader;

import java.util.ArrayList;

/**
 * Created by Houxy on 2016/10/31.
 */

public class BtAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private ArrayList<DataItem> dataItems;
    private OnItemClickListener onItemClickListener;

    public BtAdapter(ArrayList<DataItem> dataItems){
        this.dataItems = dataItems;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == C.DATA_TYPE_DEVICE_BONDED_HEADER){
            return new BondedViewHolder(parent);
        }else if(viewType == C.DATA_TYPE_DEVICE_NEW_HEADER){
            return new NewViewHolder(parent);
        }else if(viewType == C.DATA_TYPE_DEVICE_NEW || viewType == C.DATA_TYPE_DEVICE_BONDED){
            return new DeviceViewHolder(parent, onItemClickListener);
        }else if(viewType == C.DATA_TYPE_NO_DEVICE_FOUND){
            return new NoDeviceFoundViewHolder(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        switch (dataItems.get(position).getType()){
            case C.DATA_TYPE_DEVICE_BONDED:
            case C.DATA_TYPE_DEVICE_NEW:
                ((DeviceViewHolder)holder).bindData((Device)dataItems.get(position).getData());
                break;
            case C.DATA_TYPE_DEVICE_BONDED_HEADER:
                break;
            case C.DATA_TYPE_DEVICE_NEW_HEADER:
                ((NewViewHolder)holder).bindData((NewDeviceHeader) dataItems.get(position).getData());
                break;
            case C.DATA_TYPE_NO_DEVICE_FOUND:
                ((NoDeviceFoundViewHolder)holder).bindData((NoDeviceFoundHeader)dataItems.get(position).getData());
                break;
            default:break;
        }

    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataItems.get(position).getType();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
