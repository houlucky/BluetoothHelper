package com.houxy.bluetoothcontrol.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.houxy.bluetoothcontrol.C;
import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.adapter.BtAdapter;
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
import top.wuhaojie.bthelper.receiver.BroadcastType;
import top.wuhaojie.bthelper.BtHelper;
import top.wuhaojie.bthelper.i.IConnectionListener;
import top.wuhaojie.bthelper.i.OnSearchDeviceListener;
import top.wuhaojie.bthelper.receiver.BtAcceptReceiver;
import top.wuhaojie.bthelper.receiver.BtConnectionLostReceiver;
import top.wuhaojie.bthelper.receiver.BtStateReceiver;


/**
 * 本来是用来展示可以连接设备的一个页面，
 * 但后来感觉用dialog来展示更加方便，所以已经弃用。
 */


@Deprecated
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private BtHelper mBtHelper;
    private BtAdapter btAdapter;
    private ArrayList<DataItem> dataItems;
    private Device mDevice;//配对成功的设备
    private BtStateReceiver mBtStateReceiver;
    private BtConnectionLostReceiver mBtConnectionLostReceiver;
    private BtAcceptReceiver mBtAcceptReceiver;
    private static boolean isFirstSearch=true;
    private static boolean isSearching = false;
    private int bondedDeviceNum=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mBtHelper = BtHelper.getDefault();
        initView();
        initReceiver();
    }

    private void initReceiver() {

        //蓝牙是异步操作,只有蓝牙完全开启的时候，我们才能够获取到已绑定的蓝牙设备的信息
        mBtStateReceiver = new BtStateReceiver(){

            @Override
            public void OnBtStateOFF() {
                invisibleSendAndChangeItemState();
                Toast.makeText(MainActivity.this, "你已经进入了没有蓝牙的异次元...aaa", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnBtStateON() {
                if(null != mBtHelper.getBondedDevices() && dataItems.size() == 1){

                    int j=0;
                    for(BluetoothDevice bluetoothDevice : mBtHelper.getBondedDevices()){
                        j++;
                        DataItem<Device> dataItem = new DataItem<Device>();
                        dataItem.setType(C.DATA_TYPE_DEVICE_BONDED);
                        dataItem.setData(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                        dataItems.add(dataItem);
                    }
                    btAdapter.notifyItemRangeInserted(1, j);
                }
                bondedDeviceNum = mBtHelper.getBondedDevices().size();

                Log.d("TAG", ">>>>>>>>>>>>>>>>>OnBtStateON>>>>>>>>>>>>");
            }
        };
        registerReceiver(mBtStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        mBtConnectionLostReceiver = new BtConnectionLostReceiver() {
            @Override
            public void OnConnectionLost() {
                invisibleSendAndChangeItemState();
            }
        };
        registerReceiver(mBtConnectionLostReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_CONNECTION_LOST));

        mBtAcceptReceiver = new BtAcceptReceiver() {

            @Override
            public void OnAccept(final BluetoothDevice device) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(device.getName() + "请求和你通信");
                builder.setPositiveButton("开始聊天", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = ChatActivity.getIntentStartActivity(MainActivity.this, device.getName());
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        };
        registerReceiver(mBtAcceptReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_ACCEPT_CONNECTION));
    }

    private void loadDevice() {
        mBtHelper.searchDevices(new OnSearchDeviceListener() {

            @Override
            public void onStartDiscovery() {
//                if( btAdapter.getBondedNum() + 1 == devices.size()){
//                    devices.add(devices.size(), null);
//                    btAdapter.notifyItemInserted(devices.size()-1);
//                }

//                if( btAdapter.getBondedNum() + 2 < devices.size() ){
//                    int j=0;
//                    for (int i=btAdapter.getBondedNum()+2; i<devices.size(); i++){
//                        devices.remove(i);
//                        j++;
//                    }
//                    btAdapter.notifyItemRangeRemoved(btAdapter.getBondedNum()+2, j);
//                }
                isSearching = true;
                if( isFirstSearch ){
                    DataItem<NewDeviceHeader> dataItem = new DataItem<NewDeviceHeader>();
                    dataItem.setType(C.DATA_TYPE_DEVICE_NEW_HEADER);
                    NewDeviceHeader newDeviceHeader = new NewDeviceHeader("可用设备", true);
                    dataItem.setData(newDeviceHeader);
                    dataItems.add(dataItem);
                    btAdapter.notifyItemInserted(dataItems.size()-1);
                    isFirstSearch = false;
                }else {

                    if( bondedDeviceNum + 2 < dataItems.size()){
                        int j=0;
                        for (int i=bondedDeviceNum+2; i<dataItems.size(); i++){
                            dataItems.remove(i);
                            j++;
                        }
                        btAdapter.notifyItemRangeRemoved(bondedDeviceNum+2, j);
                    }

                    NewDeviceHeader newDeviceHeader = (NewDeviceHeader) dataItems.get(dataItems.size()-1).getData();
                    newDeviceHeader.setProgressBarState(true);
                    btAdapter.notifyItemChanged(dataItems.size()-1);
                }
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
                int pos = bondedDeviceNum + 1;
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
                isSearching = false;
                Toast.makeText(MainActivity.this, "搜索完毕...qwq", Toast.LENGTH_SHORT).show();
                Log.d("TAG", " search ok");
            }

            @Override
            public void onError(Exception e) {
                Log.e("TAG", "Search error : " + e.toString());
            }
        });
    }

    private void initView() {

        setSupportActionBar(toolbar);
        if( getSupportActionBar() != null)
            getSupportActionBar().setTitle("蓝牙调试助手");
        dataItems = new ArrayList<>();
        btAdapter = new BtAdapter(dataItems);
        btAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                Device device = (Device) dataItems.get(position).getData();
                mBtHelper.connectDevice(device.getDeviceAddress(), new IConnectionListener() {

                    ProgressDialog progressDialog;

                    @Override
                    public void OnConnectionStart() {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("正在配对...QqQ");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        Log.d("TAG", "正在配对...QqQ");
                    }

                    @Override
                    public void OnConnectionSuccess() {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "配对成功...hhh", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "配对成功");
//                        devices.get(position).setDeviceState(true);
                        Device device = (Device) dataItems.get(position).getData();
                        device.setDeviceState(true);
                        btAdapter.notifyItemChanged(position);
                        toolbar.getMenu().findItem(R.id.action_send).setVisible(true);
//                        mDevice = devices.get(position);
                        mDevice = device;
                    }

                    @Override
                    public void OnConnectionFailed(Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "配对失败...zzZ", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "配对失败" + e.toString());
                    }
                });
            }
        });

        DataItem<String> dataItem = new DataItem<>(C.DATA_TYPE_DEVICE_BONDED_HEADER, "已配对设备");
        dataItems.add(dataItem);
        if(null != mBtHelper.getBondedDevices()){

            for(BluetoothDevice bluetoothDevice : mBtHelper.getBondedDevices()){
                DataItem<Device> deviceItem = new DataItem<>();
                deviceItem.setType(C.DATA_TYPE_DEVICE_BONDED);
                deviceItem.setData(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                dataItems.add(deviceItem);
            }
            bondedDeviceNum = mBtHelper.getBondedDevices().size();
        }

        recyclerView.setAdapter(btAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewUtil.SpaceItemDecoration(DensityUtil.dip2px(this, 10)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_send:
                Intent intent = ChatActivity.getIntentStartActivity(MainActivity.this, mDevice.getDeviceName());
                startActivity(intent);
                break;
            case R.id.action_setting:
                break;
            case R.id.action_scan:
                if(!isSearching){
                    loadDevice();
                }else {
                    Toast.makeText(this, "别着急，正在搜索中哦...aaa", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_select:
                showDeviceSelectDialog();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeviceSelectDialog() {


        final ArrayList<DataItem> dataItems = new ArrayList<>();
        btAdapter = new BtAdapter(dataItems);

        btAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                Device device = (Device) dataItems.get(position).getData();
                mBtHelper.connectDevice(device.getDeviceAddress(), new IConnectionListener() {

                    ProgressDialog progressDialog;

                    @Override
                    public void OnConnectionStart() {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("正在配对...QqQ");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        Log.d("TAG", "正在配对...QqQ");
                    }

                    @Override
                    public void OnConnectionSuccess() {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "配对成功...hhh", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "配对成功");
//                        devices.get(position).setDeviceState(true);
                        Device device = (Device) dataItems.get(position).getData();
                        device.setDeviceState(true);
                        btAdapter.notifyItemChanged(position);
                        toolbar.getMenu().findItem(R.id.action_send).setVisible(true);
//                        mDevice = devices.get(position);
                        mDevice = device;
                    }

                    @Override
                    public void OnConnectionFailed(Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "配对失败...zzZ", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "配对失败" + e.toString());
                    }
                });
            }
        });

        DataItem<String> dataItem = new DataItem<>(C.DATA_TYPE_DEVICE_BONDED_HEADER, "已配对设备");
        dataItems.add(dataItem);
        if(null != mBtHelper.getBondedDevices()){

            for(BluetoothDevice bluetoothDevice : mBtHelper.getBondedDevices()){
                DataItem<Device> deviceItem = new DataItem<>();
                deviceItem.setType(C.DATA_TYPE_DEVICE_BONDED);
                deviceItem.setData(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                dataItems.add(deviceItem);
            }
            bondedDeviceNum = mBtHelper.getBondedDevices().size();
        }


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View convertView = getLayoutInflater().inflate(R.layout.dialog_device_select_rv, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("选择连接设备");

        RecyclerView rv = (RecyclerView) convertView.findViewById(R.id.recyclerView);
        rv.setAdapter(btAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new RecyclerViewUtil.SpaceItemDecoration(DensityUtil.dip2px(this, 10)));

        alertDialog.setPositiveButton("扫描设备", null);
        AlertDialog dialog = alertDialog.create();
        dialog.show();

        final Button searchBt= dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if( null != searchBt){
            searchBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBt.setVisibility(View.GONE);
                    mBtHelper.searchDevices(new OnSearchDeviceListener() {

                        @Override
                        public void onStartDiscovery() {
                            isSearching = true;
                            if( isFirstSearch ){
                                DataItem<NewDeviceHeader> dataItem = new DataItem<NewDeviceHeader>();
                                dataItem.setType(C.DATA_TYPE_DEVICE_NEW_HEADER);
                                NewDeviceHeader newDeviceHeader = new NewDeviceHeader("可用设备", true);
                                dataItem.setData(newDeviceHeader);
                                dataItems.add(dataItem);
                                btAdapter.notifyItemInserted(dataItems.size()-1);
                                isFirstSearch = false;
                            }else {

                                if( bondedDeviceNum + 2 < dataItems.size()){
                                    int j=0;
                                    for (int i=bondedDeviceNum+2; i<dataItems.size(); i++){
                                        dataItems.remove(i);
                                        j++;
                                    }
                                    btAdapter.notifyItemRangeRemoved(bondedDeviceNum+2, j);
                                }

                                NewDeviceHeader newDeviceHeader = (NewDeviceHeader) dataItems.get(dataItems.size()-1).getData();
                                newDeviceHeader.setProgressBarState(true);
                                btAdapter.notifyItemChanged(dataItems.size()-1);
                            }
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
                            int pos = bondedDeviceNum + 1;
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
                            isSearching = false;
                            Toast.makeText(MainActivity.this, "搜索完毕...qwq", Toast.LENGTH_SHORT).show();
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




    }

    /**
     * 使发送数据按钮不可见并且改变设备的状态为未连接
     */
    private void invisibleSendAndChangeItemState() {
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_send);
        if( menuItem.isVisible() ){
            menuItem.setVisible(false);
            int j=0;
            for(DataItem dataItem: dataItems){
                if(dataItem.getType()==C.DATA_TYPE_DEVICE_NEW || dataItem.getType() == C.DATA_TYPE_DEVICE_BONDED){
                    Device device = (Device)dataItem.getData();
                    if(mDevice.equals(device)){
                        device.setDeviceState(false);
                        break;
                    }
                }
                j++;
            }

            btAdapter.notifyItemChanged(j);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBtConnectionLostReceiver);
        unregisterReceiver(mBtStateReceiver);
    }
}
