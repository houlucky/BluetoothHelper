package com.houxy.bluetoothcontrol.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.houxy.bluetoothcontrol.C;
import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.adapter.ChatAdapter;
import com.houxy.bluetoothcontrol.bean.DataItem;

import top.wuhaojie.bthelper.receiver.BroadcastType;
import top.wuhaojie.bthelper.receiver.BtConnectionLostReceiver;
import top.wuhaojie.bthelper.receiver.MessageReceiver;
import com.houxy.bluetoothcontrol.utils.DensityUtil;
import com.houxy.bluetoothcontrol.utils.RecyclerViewUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.bean.MessageItem;
import top.wuhaojie.bthelper.i.OnSendMessageListener;

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
    private BtConnectionLostReceiver mBtConnectionLostReceiver;
    private ArrayList<DataItem> dataItems;
    private ChatAdapter mChatAdapter;
    private String deviceName;
    private int connectType;
    private LinearLayoutManager mLayoutManager;

    public static Intent getIntentStartActivity(Context context, String deviceName, int type){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("DEVICE_NAME", deviceName);
        intent.putExtra("CONNECT_TYPE", type);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        deviceName = getIntent().getStringExtra("DEVICE_NAME");
        ButterKnife.bind(this);
        initView();
        initReceiver();
    }

    private void initReceiver() {

        mMessageReceiver = new MessageReceiver() {
            @Override
            protected void OnReceiveMessage(String message) {
                DataItem<String> dataItem = new DataItem<>();
                dataItem.setData(deviceName + " : " + message);
                dataItem.setType(C.MESSAGE_TYPE_RECEIVE_TXT);
                dataItems.add(dataItem);
                mChatAdapter.notifyItemInserted(dataItems.size()-1);
                scrollToBottom();
            }
        };

        registerReceiver(mMessageReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_RECEIVED_MESSAGE));

        mBtConnectionLostReceiver = new BtConnectionLostReceiver() {
            @Override
            public void OnConnectionLost() {
                sendDataBt.setEnabled(false);
                sendDataBt.setTextColor(Color.GRAY);
                sendDataBt.setText("连接断开");
                Toast.makeText(ChatActivity.this, "连接中断,请重新连接...zzZ", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(mBtConnectionLostReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_CONNECTION_LOST));

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
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new RecyclerViewUtil.SpaceItemDecoration(DensityUtil.dip2px(this, 10)));
        dataEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    scrollToBottom();
                }
                return false;
            }
        });

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
                            scrollToBottom();
                            dataEt.setText("");
                        }

                        @Override
                        public void onConnectionLost() {
                            //在这里监听的连接中断的话要尝试发送一次消息才能监听到
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scrollToBottom() {
        mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
        unregisterReceiver(mBtConnectionLostReceiver);
    }
}
