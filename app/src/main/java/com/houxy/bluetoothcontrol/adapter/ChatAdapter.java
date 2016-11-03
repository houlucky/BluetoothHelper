package com.houxy.bluetoothcontrol.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.houxy.bluetoothcontrol.C;
import com.houxy.bluetoothcontrol.adapter.holder.ReceiveTxtViewHolder;
import com.houxy.bluetoothcontrol.adapter.holder.SendTxtViewHolder;
import com.houxy.bluetoothcontrol.base.BaseViewHolder;
import com.houxy.bluetoothcontrol.bean.DataItem;

import java.util.ArrayList;

/**
 * Created by Houxy on 2016/11/3.
 */

public class ChatAdapter extends RecyclerView.Adapter{

    private ArrayList<DataItem> dataItems;

    public ChatAdapter(ArrayList<DataItem> dataItems){
        this.dataItems = dataItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if( viewType == C.MESSAGE_TYPE_RECEIVE_TXT){
            return new ReceiveTxtViewHolder(parent);
        }else if(viewType == C.MESSAGE_TYPE_SEND_TXT){
            return new SendTxtViewHolder(parent);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(null != dataItems.get(position).getData()){
            ((BaseViewHolder)holder).bindData(dataItems.get(position).getData());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }
}
