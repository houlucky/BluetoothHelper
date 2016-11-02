package com.houxy.bluetoothcontrol.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import top.wuhaojie.bthelper.OnSearchDeviceListener;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swRefresh)
    SwipeRefreshLayout swRefresh;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private BtHelperClient btHelperClient;
    private BtAdapter btAdapter;
    private ArrayList<Device> devices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btHelperClient = BtHelperClient.from(this);
        initView();
        loadDevice();
    }

    private void loadDevice() {
        btHelperClient.searchDevices(new OnSearchDeviceListener() {
            @Override
            public void onStartDiscovery() {
                swRefresh.setRefreshing(true);
            }

            @Override
            public void onNewDeviceFound(BluetoothDevice device) {

            }

            @Override
            public void onSearchCompleted(List<BluetoothDevice> bondedList, List<BluetoothDevice> newList) {
                swRefresh.setRefreshing(false);
                devices.add(0, null);
                for (BluetoothDevice bluetoothDevice : bondedList) {
                    Log.d("TAG", "name : " + bluetoothDevice.getName() + " addr:" + bluetoothDevice.getAddress());
                    devices.add(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    btAdapter.setBondedNum(bondedList.size());
                }
                devices.add(1 + bondedList.size(), null);
                for (BluetoothDevice bluetoothDevice : newList) {
                    Log.d("TAG", "name : " + bluetoothDevice.getName() + " addr:" + bluetoothDevice.getAddress());
                    devices.add(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                }

                Log.d("TAG", "bonded : " + bondedList.size() + " new : " + newList.size());
//                recyclerView.setAdapter(btAdapter);
                btAdapter.setDevices(devices);
                btAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                swRefresh.setRefreshing(false);
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
//        //这个是已配对设备的占位符
//        devices.add(null);
//        if(null != ACache.getDefault().getAsObject(C.BONDED_DEVICE)){
//            ArrayList<Device> devices = (ArrayList<Device>) ACache.getDefault().getAsObject(C.BONDED_DEVICE);
//            if (null !=devices){
//                this.devices.addAll(devices);
//                btAdapter.setBondedNum(devices.size());
//            }
//        }
//        //这个是新设备的占位符
//        devices.add(null);
        btAdapter.setDevices(devices);
        recyclerView.setAdapter(btAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewUtil.SpaceItemDecoration(DensityUtil.dip2px(this, 10)));
        swRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

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
                break;
            case R.id.action_setting:
                break;
            case R.id.action_scan:

                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
}
