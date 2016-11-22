package com.houxy.bluetoothcontrol.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.houxy.bluetoothcontrol.C;
import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.adapter.BtAdapter;
import com.houxy.bluetoothcontrol.adapter.ChatAdapter;
import com.houxy.bluetoothcontrol.base.i.OnItemClickListener;
import com.houxy.bluetoothcontrol.bean.DataItem;
import com.houxy.bluetoothcontrol.bean.Device;
import com.houxy.bluetoothcontrol.bean.NewDeviceHeader;
import com.houxy.bluetoothcontrol.bean.NoDeviceFoundHeader;
import com.houxy.bluetoothcontrol.utils.DensityUtil;
import com.houxy.bluetoothcontrol.utils.RecyclerViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import top.wuhaojie.bthelper.BtHelper;
import top.wuhaojie.bthelper.bean.MessageItem;
import top.wuhaojie.bthelper.i.IConnectionListener;
import top.wuhaojie.bthelper.i.OnSearchDeviceListener;
import top.wuhaojie.bthelper.i.OnSendMessageListener;
import top.wuhaojie.bthelper.receiver.BroadcastType;
import top.wuhaojie.bthelper.receiver.BtAcceptReceiver;
import top.wuhaojie.bthelper.receiver.BtConnectionLostReceiver;
import top.wuhaojie.bthelper.receiver.MessageReceiver;

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
    private BtAcceptReceiver mBtAcceptReceiver;
    private ArrayList<DataItem> dataItems;
    private ChatAdapter mChatAdapter;
    private String mDeviceName = "";
    private LinearLayoutManager mLayoutManager;

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
        initReceiver();
    }

    private void initReceiver() {

        mMessageReceiver = new MessageReceiver() {
            @Override
            protected void OnReceiveMessage(String message) {
                DataItem<String> dataItem = new DataItem<>();
//                StringBuilder stringBuilder = new StringBuilder(message);
//                stringBuilder.insert(1, ".");
//                dataItem.setData(mDeviceName + " : " + "distance --> "+ stringBuilder.toString() + "m");
                dataItem.setData(mDeviceName + " : "+ message);
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
                toolbar.getMenu().findItem(R.id.action_device_name).setTitle("未连接");
                Toast.makeText(ChatActivity.this, "连接中断,请重新连接...zzZ", Toast.LENGTH_SHORT).show();

                BtHelper.getDefault().close();
            }
        };
        registerReceiver(mBtConnectionLostReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_CONNECTION_LOST));

        mBtAcceptReceiver = new BtAcceptReceiver() {
            @Override
            public void OnAccept(BluetoothDevice device) {
                toolbar.getMenu().findItem(R.id.action_device_name).setTitle("连接到"+device.getName());
                sendDataBt.setEnabled(true);
                mDeviceName = device.getName();
            }
        };
        registerReceiver(mBtAcceptReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_ACCEPT_CONNECTION));
    }

    private void initView() {

        sendDataBt.setEnabled(false);
        sendDataBt.setTextColor(Color.GRAY);

        setSupportActionBar(toolbar);


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
                    BtHelper.getDefault().sendMessage(new MessageItem(dataEt.getText().toString()),
                            new OnSendMessageListener() {
                        @Override
                        public void onSuccess(String s) {
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

        switch (item.getItemId()){
            case R.id.action_select:
                showDeviceSelectDialog();
                break;
            case R.id.action_setting:
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void scrollToBottom() {
        mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
    }

    private void showDeviceSelectDialog() {


        final ArrayList<DataItem> dataItems = new ArrayList<>();
        final BtAdapter btAdapter = new BtAdapter(dataItems);


        DataItem<String> dataItem = new DataItem<>(C.DATA_TYPE_DEVICE_BONDED_HEADER, "已配对设备");
        dataItems.add(dataItem);
        if(null != BtHelper.getDefault().getBondedDevices()){

            for(BluetoothDevice bluetoothDevice : BtHelper.getDefault().getBondedDevices()){
                DataItem<Device> deviceItem = new DataItem<>();
                deviceItem.setType(C.DATA_TYPE_DEVICE_BONDED);
                deviceItem.setData(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                dataItems.add(deviceItem);
            }
        }


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View convertView = getLayoutInflater().inflate(R.layout.dialog_device_select_rv, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("选择连接设备");

        RecyclerView rv = (RecyclerView) convertView.findViewById(R.id.recyclerView);
        rv.setAdapter(btAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        alertDialog.setPositiveButton("扫描设备", null);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        final Button searchBt= dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if( null != searchBt){
            searchBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBt.setVisibility(View.GONE);
                    BtHelper.getDefault().searchDevices(new OnSearchDeviceListener() {

                        @Override
                        public void onStartDiscovery() {

                            DataItem<NewDeviceHeader> dataItem = new DataItem<NewDeviceHeader>();
                            dataItem.setType(C.DATA_TYPE_DEVICE_NEW_HEADER);
                            NewDeviceHeader newDeviceHeader = new NewDeviceHeader("可用设备", true);
                            dataItem.setData(newDeviceHeader);
                            dataItems.add(dataItem);
                            btAdapter.notifyItemInserted(dataItems.size()-1);
                        }

                        @Override
                        public void onNewDeviceFound(BluetoothDevice device) {
                            Log.d("TAG", "FOUND : " + device.getName());
                            DataItem<Device> deviceItem = new DataItem<Device>();
                            deviceItem.setType(C.DATA_TYPE_DEVICE_NEW);
                            deviceItem.setData(new Device(device.getName(), device.getAddress()));
                            dataItems.add(deviceItem);
                            btAdapter.notifyItemInserted(dataItems.size()-1);
                        }

                        @Override
                        public void onSearchCompleted(List<BluetoothDevice> bondedList, List<BluetoothDevice> newList) {

                            //隐藏progressbar
                            int pos = BtHelper.getDefault().getBondedDevices().size() + 1;
                            Log.d("TAG", ">>>>>>>>>>>>>>>" + pos +">>>>>>>>>>>>");
                            NewDeviceHeader newDeviceHeader = (NewDeviceHeader)dataItems.get(pos).getData();
                            newDeviceHeader.setProgressBarState(false);
                            btAdapter.notifyItemChanged(pos);
                            //如果没有搜索到可用设备
                            if(newList.size()==0){
                                DataItem<NoDeviceFoundHeader> dataItem = new DataItem<NoDeviceFoundHeader>();
                                dataItem.setType(C.DATA_TYPE_NO_DEVICE_FOUND);
                                NoDeviceFoundHeader noDeviceFoundHeader = new NoDeviceFoundHeader("没有搜到可用的设备...WoW");
                                dataItem.setData(noDeviceFoundHeader);
                                dataItems.add(dataItem);
                                btAdapter.notifyItemInserted(dataItems.size()-1);
                            }
                            Log.d("TAG", " search ok");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("TAG", "Search error : " + e.toString());
                        }
                    });
                }
            });
        }

        btAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                Device device = (Device) dataItems.get(position).getData();
                BtHelper.getDefault().connectDevice(device.getDeviceAddress(), new IConnectionListener() {

                    ProgressDialog progressDialog;

                    @Override
                    public void OnConnectionStart() {
                        progressDialog = new ProgressDialog(ChatActivity.this);
                        progressDialog.setMessage("正在配对...QqQ");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        dialog.dismiss();
                    }

                    @Override
                    public void OnConnectionSuccess() {
                        Device device = (Device) dataItems.get(position).getData();
                        device.setDeviceState(true);
                        btAdapter.notifyItemChanged(position);
                        toolbar.getMenu().findItem(R.id.action_device_name).setTitle("连接到"+device.getDeviceName());
                        sendDataBt.setEnabled(true);
                        sendDataBt.setTextColor(Color.WHITE);
                        mDeviceName = device.getDeviceName();

                        Toast.makeText(ChatActivity.this, "配对成功...hhh", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Log.d("TAG", "配对成功");
                    }

                    @Override
                    public void OnConnectionFailed(Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "配对失败...zzZ", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "配对失败" + e.toString());
                        BtHelper.getDefault().close();
                    }
                });
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                BtHelper.getDefault().close();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
        unregisterReceiver(mBtConnectionLostReceiver);
        unregisterReceiver(mBtAcceptReceiver);
    }
}
