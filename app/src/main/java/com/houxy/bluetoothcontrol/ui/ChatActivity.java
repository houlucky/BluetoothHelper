package com.houxy.bluetoothcontrol.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.houxy.bluetoothcontrol.R;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    public static Intent getIntentStartActivity(Context context, String deviceName){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("DEVICE_NAME", deviceName);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        if(null != getSupportActionBar()){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
