# Bluetooth Operation Library
[中文](https://github.com/houlucky/BluetoothHelper/blob/master/README_zh.md) | [English](https://github.com/houlucky/BluetoothHelper)

**A library can help you  operate Bluetooth quickly**

## Dependencies
Use Gradle
```gradle
dependencies {
    compile 'com.github.houlucky.bluetoothHelper:bthelper:0.3.0'
}
```

Or Maven
```xml
<dependency>
  <groupId>com.github.houlucky.bluetoothHelper</groupId>
  <artifactId>bthelper</artifactId>
  <version>0.3.0</version>
  <type>pom</type>
</dependency>
```


## Usage
First，Init BtHelper in your Application.
```
BtHelper.init(this);
```
Then，you can do wahtever you want.
- Get Instance
```java
btHelper = BtHelper.getDefault();
```
- Search Bluetooth
```
BtHelper.getDefault().searchDevices(new OnSearchDeviceListener() {

    @Override
    public void onStartDiscovery() {
    }

    @Override
    public void onNewDeviceFound(BluetoothDevice device) {
    }

    @Override
    public void onSearchCompleted(List<BluetoothDevice> bondedList, List<BluetoothDevice> newList) {
    }

    @Override
    public void onError(Exception e) {
    }
});
```
- Get bounded devices
```
ArrayList<BluetoothDevice> bondedDevices = BtHelper.getDefault().getBondedDevices()；
```
- Bluetooth  Pairing
```
BtHelper.getDefault().connectDevice(“mac addr”, new IConnectionListener() {

    @Override
    public void OnConnectionStart() {
    }

    @Override
    public void OnConnectionSuccess() {
    }

    @Override
    public void OnConnectionFailed(Exception e) {
    }
});
```

- Send message to the remote device.
 ```java
BtHelper.getDefault().sendMessage(new MessageItem("str"),new OnSendMessageListener() {
    @Override
    public void onSuccess(String s) {
    }

    @Override
    public void onConnectionLost() {
     
    }

    @Override
    public void onError(Exception e) {

    }
});
 ```
- Receive the message from the remote device
```
mMessageReceiver = new MessageReceiver() {
    @Override
    protected void OnReceiveMessage(String message) {
       
    }
};
//Register BroadcastReceiver
registerReceiver(mMessageReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_RECEIVED_MESSAGE));
```
- Close Connection
```java
 @Override
 protected void onDestroy() {
    super.onDestroy();
    BtHelper.getDefault().close();
 }
```




## License
```
Copyright 2016 houlucky  Licensed under the Apache License, Version 2.0 (the \"License\")
you may not use this file except in compliance with the License. You may obtain a copy of the License at 
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed 
on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied. 
See the License for the specific language governing permissions and limitations under the License.
```
