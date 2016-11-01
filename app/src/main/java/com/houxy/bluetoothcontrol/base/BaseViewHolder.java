package com.houxy.bluetoothcontrol.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.houxy.bluetoothcontrol.base.i.OnItemClickListener;
import com.houxy.bluetoothcontrol.base.i.OnItemLongClickListener;

import butterknife.ButterKnife;


/**
 * Created by Houxy on 2016/10/31.
 */

public class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public BaseViewHolder(ViewGroup root, int res) {
        super(LayoutInflater.from(root.getContext()).inflate(res, root, false));
        ButterKnife.bind(this, itemView);
    }

    public BaseViewHolder(ViewGroup root, int res, OnItemClickListener onItemClickListener) {
        this(root, res);
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }

    public BaseViewHolder(ViewGroup root, int res, OnItemClickListener onItemClickListener,
                          OnItemLongClickListener onItemLongClickListener) {
        this(root, res);
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void bindData(){}

    public void bindData(T t){}

    public Context getContext() {
        return itemView.getContext();
    }

    @Override public void onClick(View v) {
        if (onItemClickListener != null){
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    @Override public boolean onLongClick(View v) {
        if (onItemLongClickListener != null){
            onItemLongClickListener.onItemLongClick(getAdapterPosition());
        }
        return true;
    }


}
