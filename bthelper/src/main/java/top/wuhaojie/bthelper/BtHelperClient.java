package top.wuhaojie.bthelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import top.wuhaojie.bthelper.receiver.BtReceiver;
import top.wuhaojie.bthelper.receiver.BtStateReceiver;
import top.wuhaojie.bthelper.runn.ConnectDeviceRunnable;
import top.wuhaojie.bthelper.runn.ReadRunnable;
import top.wuhaojie.bthelper.runn.WriteRunnable;

/**
 * Bluetooth Helper as a Client.
 * Created by wuhaojie on 2016/9/7 18:57.
 */
public class BtHelperClient {

    private static final String DEVICE_HAS_NOT_BLUETOOTH_MODULE = "device has not bluetooth module!";
    private static final String TAG = BtHelperClient.class.getSimpleName();

    private Context mContext;
    private BluetoothSocket mSocket;
    private ConnectDeviceRunnable mConnectDeviceRunnable;
    private BluetoothAdapter mBluetoothAdapter;
    private volatile BtReceiver mReceiver;
    private BtStateReceiver mBtStateReceiver;
    private static volatile BtHelperClient sBtHelperClient;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

//    private InputStream mAcceptInputStream;
//    private OutputStream mAcceptOutputStream;

    /**
     * Obtains the BtHelperClient from the given context.
     *
     * @param context context
     * @return an instance of BtHelperClient
     */
    public static void init(Context context) {
        if (sBtHelperClient == null) {
            synchronized (BtHelperClient.class) {
                if (sBtHelperClient == null)
                    sBtHelperClient = new BtHelperClient(context);
            }
        }
    }

    public static BtHelperClient getDefault(){
        if( null == sBtHelperClient){
            throw new NullPointerException("you must be init BtHelperClient before you use it");
        }

        return sBtHelperClient;
    }

//    /**
//     * Obtains the BtHelperClient from the given context.
//     *
//     * @param context context
//     * @return an instance of BtHelperClient
//     */
//    public static BtHelperClient from(Context context) {
//        if (sBtHelperClient == null) {
//            synchronized (BtHelperClient.class) {
//                if (sBtHelperClient == null)
//                    sBtHelperClient = new BtHelperClient(context);
//            }
//        }
//        return sBtHelperClient;
//    }

    /**
     * private constructor for singleton
     *
     * @param context context
     */
    private BtHelperClient(Context context) {
        mContext = context.getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        requestEnableBt();
    }


    /**
     * Request for enable the device's bluetooth asynchronously.
     * Throw a NullPointerException if the device doesn't have a bluetooth module.
     */
    private void requestEnableBt() {
        if (mBluetoothAdapter == null) {
            throw new NullPointerException(DEVICE_HAS_NOT_BLUETOOTH_MODULE);
        }
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
    }


    /**
     * discovery the devices.
     *
     * @param listener listener for the process
     */
    public void searchDevices(OnSearchDeviceListener listener) {

        checkNotNull(listener);

        if (mBluetoothAdapter == null) {
            listener.onError(new NullPointerException(DEVICE_HAS_NOT_BLUETOOTH_MODULE));
            return;
        }

        if (mReceiver == null){
            mReceiver = new BtReceiver(listener);
        }

        // ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);

        // ACTION_DISCOVERY_FINISHED
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);

//        mNeed2unRegister = true;
//        if(!mBluetoothAdapter.isEnabled()){
//            mBluetoothAdapter.enable();
//        }

        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        mBluetoothAdapter.startDiscovery();
        listener.onStartDiscovery();
    }




    /**
     * Send a message to a remote device.
     * If the local device did't connected to the remote devices, it will call connectDevice(), then send the message.
     * If you want to get a response from the remote device, call another overload method, this method default will not obtain a response.
     *
//     * @param mac      the remote device's mac address
     * @param item     the message need to send
     * @param listener lister for the sending process
     */
    public void sendMessage(MessageItem item, OnSendMessageListener listener) {
        sendMessage(item, false, listener);
    }


    /**
     * Send a message to a remote device.
     * If the local device did't connected to the remote devices, it will call connectDevice(), then send the message.
     * You can obtain a response from the remote device, just as http.
     * However, it will blocked if didn't get response from the remote device.
     *
//     * @param mac          the remote device's mac address
     * @param item         the message need to send
     * @param listener     lister for the sending process
     * @param needResponse if need to obtain a response from the remote device
     */
    public void sendMessage(MessageItem item, boolean needResponse, OnSendMessageListener listener) {

        //Before Send a message to a remote device. you must be CONNECTED
//        if( mCurrStatus == STATUS.CONNECTED){
//            mMessageQueue.add(item);
            WriteRunnable writeRunnable = new WriteRunnable(item,listener, needResponse,mConnectDeviceRunnable.getOutputStream());
            mExecutorService.submit(writeRunnable);

//        }
    }



    private void receiveMessage() {

        OnReceiveMessageListener onReceiveMessageListener = new OnReceiveMessageListener() {
            @Override
            public void onNewLine(String s) {
                Intent intent = new Intent("com.houxy.action.MESSAGE");
                intent.putExtra("message", s);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onConnectionLost() {

            }

            @Override
            public void onError(Exception e) {

            }
        };
        ReadRunnable readRunnable = new ReadRunnable(onReceiveMessageListener, mConnectDeviceRunnable.getInputStream());
        mExecutorService.submit(readRunnable);
    }


    private Filter mFilter;

    /**
     * Set a filter use to check if a given response is an expect data.
     * Throw a NullPointerException if the parameter is null.
     *
     * @param filter a custom filter
     */
    public void setFilter(Filter filter) {
        if (filter == null)
            throw new NullPointerException("parameter filter is null");
        mFilter = filter;
    }


    /**
     * 如果你想和其他蓝牙设备进行通信，你必须先调用此方法
     * @param mac device address
     * @param listener connection listener
     */
    public void connectDevice(String mac, final IConnectionListener listener) {

        if (mac == null || TextUtils.isEmpty(mac))
            throw new IllegalArgumentException("mac address is null or empty!");
        if (!BluetoothAdapter.checkBluetoothAddress(mac))
            throw new IllegalArgumentException("mac address is not correct! make sure it's upper case!");

        IConnectionListener iConnectionListener = new IConnectionListener() {
            @Override
            public void OnConnectionStart() {
                listener.OnConnectionStart();
            }

            @Override
            public void OnConnectionSuccess() {
                listener.OnConnectionSuccess();
                //一旦连接成功就开始监听是否有数据发过来
                receiveMessage();
            }

            @Override
            public void OnConnectionFailed(Exception e) {
                listener.OnConnectionFailed(e);
            }
        };

        mConnectDeviceRunnable = new ConnectDeviceRunnable(mac, iConnectionListener);
        checkNotNull(mExecutorService);

        mExecutorService.submit(mConnectDeviceRunnable);

    }


    private void checkNotNull(Object o) {
        if (o == null)
            throw new NullPointerException();
    }

    public  ArrayList<BluetoothDevice> getBondedDevices(){
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> bondedDevices = new ArrayList<>();
        for(BluetoothDevice device:devices){
            bondedDevices.add(device);
            Log.d("TAG", "NAME :" + device.getAddress());
        }
        return bondedDevices;
    }

    public void setOnBtStateChangeListener(final OnBtStateChangeListener onBtStateChangeListener){

        mBtStateReceiver = new BtStateReceiver(new OnBtStateChangeListener() {
            @Override
            public void OnBtStateON() {
                onBtStateChangeListener.OnBtStateON();
            }

            @Override
            public void OnBtStateOFF() {
                onBtStateChangeListener.OnBtStateOFF();
            }
        });
        mContext.registerReceiver(mBtStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    /**
     * Closes the connection and releases any system resources associated
     * with the stream.
     */
    public void close() {
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.cancelDiscovery();

        mContext.unregisterReceiver(mReceiver);
        mContext.unregisterReceiver(mBtStateReceiver);

        if (mSocket != null) try {
            mSocket.close();
        } catch (IOException e) {
            mSocket = null;
        }

        mReceiver = null;

        sBtHelperClient = null;
    }

}
