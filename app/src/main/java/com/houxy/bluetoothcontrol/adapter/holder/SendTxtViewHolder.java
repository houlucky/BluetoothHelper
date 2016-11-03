package com.houxy.bluetoothcontrol.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.base.BaseViewHolder;

import butterknife.Bind;

/**
 * Created by Houxy on 2016/11/3.
 */

public class SendTxtViewHolder extends BaseViewHolder {

    @Bind(R.id.sendTxtTv)
    TextView sendTxtTv;

    public SendTxtViewHolder(ViewGroup root) {
        super(root, R.layout.item_send_txt);
    }


    @Override
    public void bindData(Object o) {
        String message = (String) o;
        sendTxtTv.setText(message);
    }
}
