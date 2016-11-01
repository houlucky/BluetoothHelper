package com.houxy.bluetoothcontrol.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.houxy.bluetoothcontrol.adapter.holder.BondedViewHolder;
import com.houxy.bluetoothcontrol.adapter.holder.DeviceViewHolder;
import com.houxy.bluetoothcontrol.adapter.holder.NewViewHolder;
import com.houxy.bluetoothcontrol.base.BaseViewHolder;
import com.houxy.bluetoothcontrol.bean.Device;

import java.util.ArrayList;

/**
 * Created by Houxy on 2016/10/31.
 */

public class BtAdapter extends RecyclerView.Adapter{

    private ArrayList<Device> devices;
    private static final int BONDED = 0;
    private static final int NEW = 1;
    private static final int DEVICE = 2;
    public int bondedNum;

    public BtAdapter(){
        devices = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == BONDED){
            return new BondedViewHolder(parent);
        }else if(viewType == NEW){
            return new NewViewHolder(parent);
        }else if(viewType == DEVICE){
            return new DeviceViewHolder(parent, null);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(position == 0){
            ((BaseViewHolder)holder).bindData();
        }else if(position == 1+bondedNum){
            ((BaseViewHolder)holder).bindData();
        }else {
            ((BaseViewHolder)holder).bindData(devices.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return devices.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return BONDED;
        }

        if(position == 1+bondedNum){
            return NEW;
        }

        return DEVICE;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
}
