package com.houxy.bluetoothcontrol.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.houxy.bluetoothcontrol.C;
import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.adapter.BtAdapter;
import com.houxy.bluetoothcontrol.base.i.OnItemClickListener;
import com.houxy.bluetoothcontrol.bean.DataItem;
import com.houxy.bluetoothcontrol.bean.Device;
import com.houxy.bluetoothcontrol.utils.DensityUtil;
import com.houxy.bluetoothcontrol.utils.RecyclerViewUtil;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import top.wuhaojie.bthelper.BroadcastType;
import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.IConnectionListener;
import top.wuhaojie.bthelper.OnSearchDeviceListener;
import top.wuhaojie.bthelper.receiver.BtConnectionLostReceiver;
import top.wuhaojie.bthelper.receiver.BtStateReceiver;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private BtHelperClient btHelperClient;
    private BtAdapter btAdapter;
    private ArrayList<DataItem> dataItems;
    private Device mDevice;//配对成功的设备
    private BtStateReceiver mBtStateReceiver;
    private BtConnectionLostReceiver mBtConnectionLostReceiver;
    private static boolean isFirstSearch=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btHelperClient = BtHelperClient.getDefault();
        initView();
        initReceiver();
    }

    private void initReceiver() {

        //蓝牙是异步操作,只有蓝牙完全开启的时候，我们才能够获取到已绑定的蓝牙设备的信息
        mBtStateReceiver = new BtStateReceiver(){

            @Override
            public void OnBtStateOFF() {
                invisibleSendAndChangeItemState();
            }

            @Override
            public void OnBtStateON() {
//                if(null != btHelperClient.getBondedDevices() && btAdapter.getBondedNum() == 0){
//
//                    for(BluetoothDevice bluetoothDevice : btHelperClient.getBondedDevices()){
//                        devices.add(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
//                    }
//                    btAdapter.setBondedNum(devices.size() - 1);
//                    btAdapter.notifyItemRangeInserted(1, btAdapter.getBondedNum());
//                }


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
    }

    private void loadDevice() {
        btHelperClient.searchDevices(new OnSearchDeviceListener() {

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


                if( isFirstSearch ){
                    DataItem<String> dataItem = new DataItem<String>();
                    dataItem.setType(C.DATA_TYPE_DEVICE_NEW_HEADER);
                    dataItem.setData("可用设备");
                    dataItems.add(dataItem);
                    btAdapter.notifyItemInserted(dataItems.size()-1);
                    isFirstSearch = false;
                }else {
                    int j=0;
                    for(DataItem dataItem : dataItems){
                        if( dataItem.getType() == C.DATA_TYPE_DEVICE_NEW ){
                            dataItems.remove(dataItem);
                            j++;
                        }
                    }
                    btAdapter.notifyItemRangeRemoved(dataItems.size(), j);
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
                btHelperClient.connectDevice(device.getDeviceAddress(), new IConnectionListener() {

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
        if(null != btHelperClient.getBondedDevices()){

            for(BluetoothDevice bluetoothDevice : btHelperClient.getBondedDevices()){
                DataItem<Device> deviceItem = new DataItem<>();
                deviceItem.setType(C.DATA_TYPE_DEVICE_BONDED);
                deviceItem.setData(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                dataItems.add(deviceItem);
            }
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
                loadDevice();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
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
