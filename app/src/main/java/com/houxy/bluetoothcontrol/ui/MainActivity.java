package com.houxy.bluetoothcontrol.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.houxy.bluetoothcontrol.R;
import com.houxy.bluetoothcontrol.adapter.BtAdapter;
import com.houxy.bluetoothcontrol.base.i.OnItemClickListener;
import com.houxy.bluetoothcontrol.bean.Device;
import com.houxy.bluetoothcontrol.utils.DensityUtil;
import com.houxy.bluetoothcontrol.utils.RecyclerViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.IConnectionListener;
import top.wuhaojie.bthelper.MessageItem;
import top.wuhaojie.bthelper.OnBtStateChangeListener;
import top.wuhaojie.bthelper.OnReceiveMessageListener;
import top.wuhaojie.bthelper.OnSearchDeviceListener;
import top.wuhaojie.bthelper.OnSendMessageListener;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private BtHelperClient btHelperClient;
    private BtAdapter btAdapter;
    private ArrayList<Device> devices;
    private Device mDevice;//配对成功的设备


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btHelperClient = BtHelperClient.getDefault();
        initView();
    }

    private void loadDevice() {
        btHelperClient.searchDevices(new OnSearchDeviceListener() {

            @Override
            public void onStartDiscovery() {
                if( btAdapter.getBondedNum() + 1 == devices.size()){
                    devices.add(devices.size(), null);
                    btAdapter.notifyItemInserted(devices.size()-1);
                }

                if( btAdapter.getBondedNum() + 2 < devices.size() ){
                    int j=0;
                    for (int i=btAdapter.getBondedNum()+2; i<devices.size(); i++){
                        devices.remove(i);
                        j++;
                    }
                    btAdapter.notifyItemRangeRemoved(btAdapter.getBondedNum()+2, j);
                }
            }

            @Override
            public void onNewDeviceFound(BluetoothDevice device) {
                Log.d("TAG", "FOUND : " + device.getName());

                devices.add(devices.size(), new Device(device.getName(), device.getAddress()));
                btAdapter.notifyItemInserted(devices.size()-1);
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
        btAdapter = new BtAdapter();
        btAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

                btHelperClient.connectDevice(devices.get(position).getDeviceAddress(), new IConnectionListener() {

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
                        devices.get(position).setDeviceState(true);
                        btAdapter.notifyItemChanged(position);
                        toolbar.getMenu().findItem(R.id.action_send).setVisible(true);
                        mDevice = devices.get(position);
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

        devices = new ArrayList<>();
        //这个是已配对设备的占位符
        devices.add(null);
        //蓝牙是异步操作,只有蓝牙完全开启的时候，我们才能够获取到已绑定的蓝牙设备的信息
        btHelperClient.setOnBtStateChangeListener(new OnBtStateChangeListener() {
            @Override
            public void OnBtStateON() {
                if(null != btHelperClient.getBondedDevices() && btAdapter.getBondedNum() == 0){

                    for(BluetoothDevice bluetoothDevice : btHelperClient.getBondedDevices()){
                        devices.add(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    }
                    btAdapter.setBondedNum(devices.size() - 1);
                    btAdapter.notifyItemRangeInserted(1, btAdapter.getBondedNum());
                }
            }

            @Override
            public void OnBtStateOFF() {

            }
        });

        if(null != btHelperClient.getBondedDevices()){

            for(BluetoothDevice bluetoothDevice : btHelperClient.getBondedDevices()){
                devices.add(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
            }
            btAdapter.setBondedNum(devices.size() - 1);
        }

        btAdapter.setDevices(devices);
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


}
