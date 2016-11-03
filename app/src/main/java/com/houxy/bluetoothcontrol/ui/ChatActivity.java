package com.houxy.bluetoothcontrol.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.houxy.bluetoothcontrol.C;
import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.adapter.ChatAdapter;
import com.houxy.bluetoothcontrol.bean.DataItem;
import com.houxy.bluetoothcontrol.bean.MessageModel;
import com.houxy.bluetoothcontrol.receiver.MessageReceiver;
import com.houxy.bluetoothcontrol.utils.DensityUtil;
import com.houxy.bluetoothcontrol.utils.RecyclerViewUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.MessageItem;
import top.wuhaojie.bthelper.OnSendMessageListener;

/**
 * Created by Houxy on 2016/11/2.
 */

public class ChatActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.dataEt)
    EditText dataEt;
    @Bind(R.id.sendDataBt)
    Button sendDataBt;
    private MessageReceiver mMessageReceiver;
    private ArrayList<DataItem> dataItems;
    private ChatAdapter mChatAdapter;
    private String deviceName;

    public static Intent getIntentStartActivity(Context context, String deviceName){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("DEVICE_NAME", deviceName);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        deviceName = getIntent().getStringExtra("DEVICE_NAME");
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        if(null != getSupportActionBar()){
            getSupportActionBar().setTitle("与" + deviceName + "通信中");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dataItems = new ArrayList<>();
        mChatAdapter = new ChatAdapter(dataItems);
        recyclerView.setAdapter(mChatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendDataBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dataEt.getText().toString().isEmpty()){
                    BtHelperClient.getDefault().sendMessage(new MessageItem(dataEt.getText().toString()), new OnSendMessageListener() {
                        @Override
                        public void onSuccess(String s) {
                            Log.d("TAG", "SEND : " + s);
                            DataItem<String> dataItem = new DataItem<String>();
                            dataItem.setData("我 : " + s);
                            dataItem.setType(C.MESSAGE_TYPE_SEND_TXT);
                            dataItems.add(dataItem);
                            mChatAdapter.notifyItemInserted(dataItems.size()-1);
                        }

                        @Override
                        public void onConnectionLost() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }
        });

        mMessageReceiver = new MessageReceiver() {
            @Override
            protected void OnReceiveMessage(String message) {
                Log.d("TAG", "RECE : " + message);
                DataItem<String> dataItem = new DataItem<>();
                dataItem.setData(deviceName + " : " + message);
                dataItem.setType(C.MESSAGE_TYPE_RECEIVE_TXT);
                dataItems.add(dataItem);
                mChatAdapter.notifyItemInserted(dataItems.size()-1);
            }
        };

        IntentFilter intentFilter = new IntentFilter("com.houxy.action.MESSAGE");
        registerReceiver(mMessageReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }
}
