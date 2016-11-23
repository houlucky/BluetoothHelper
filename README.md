# 蓝牙操作库

**这是一个可以帮助你快速开发蓝牙的类库，欢迎Fork和Star！！！**

## 依赖
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


## 用法

首先，在Application调用`BtHelper.init(this)`初始化BtHelper

- 获取实例
```java
btHelper = BtHelper.getDefault();
```

- 搜索蓝牙
```java
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

- 获取到手机中已绑定的蓝牙设备
```java
ArrayList<BluetoothDevice> bondedDevices = BtHelper.getDefault().getBondedDevices()；
```
- 蓝牙设备的配对
```java
BtHelper.getDefault().connectDevice(“mac 地址”, new IConnectionListener() {

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

- 向远程蓝牙设备发送数据
```java
BtHelper.getDefault().sendMessage(new MessageItem("str"),new OnSendMessageListener() {
    @Override
    public void onSuccess(String s) {
    }

    @Override
    public void onConnectionLost() {
        //在这里监听的连接中断的话要尝试发送一次消息才能监听到
    }

    @Override
    public void onError(Exception e) {

    }
});
```

- 接收远程蓝牙发过来的数据
```java
mMessageReceiver = new MessageReceiver() {
    @Override
    protected void OnReceiveMessage(String message) {
        //在这里可以收到远程蓝牙发过来的数据
    }
};
//注册收消息的广播
registerReceiver(mMessageReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_RECEIVED_MESSAGE));
```

- 关闭连接
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
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed 
on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied. 
See the License for the specific language governing permissions and limitations under the License.
```
