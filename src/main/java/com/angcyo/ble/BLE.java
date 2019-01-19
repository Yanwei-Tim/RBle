package com.angcyo.ble;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.*;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by angcyo on 2018/12/01 16:39
 * <p>
 * https://github.com/Jasonchenlijian/FastBle 2.3.2
 */
public class BLE {
    public static final int REQUEST_ENBALE_BLE_CODE = 0x8088;

    public static void init(Application app, boolean debug) {
        BleManager.getInstance().init(app);

        BleManager.getInstance()
                .enableLog(debug)
                //重连次数, 重连间隔时间
                .setReConnectCount(1, 5000)
                //分包写入数量
                .setSplitWriteNum(20)
                //连接过渡时间
                .setConnectOverTime(10000)
                //操作超时时间
                .setOperateTimeout(5000);

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                // 只扫描指定的服务的设备，可选
                //.setServiceUuids(serviceUuids)
                //.setDeviceName(true, names)
                //// 只扫描指定广播名的设备，可选
                //.setDeviceMac(mac)
                // 连接时的autoConnect参数，可选，默认false
                //.setAutoConnect(isAutoConnect)
                // 扫描超时时间，可选，默认10秒
                .setScanTimeOut(10000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    /**
     * 设备是否支持BLE
     */
    public static boolean isSupportBle() {
        return BleManager.getInstance().isSupportBle();
    }

    /**
     * BLE是否打开
     */
    public static boolean isBlueEnable() {
        return BleManager.getInstance().isBlueEnable();
    }

    /**
     * 禁止BLE
     */
    public static void disableBluetooth() {
        BleManager.getInstance().disableBluetooth();
    }

    /**
     * 激活BLE, 不弹出权限对话框
     */
    public static void enableBluetooth() {
        BleManager.getInstance().enableBluetooth();
    }

    /**
     * 弹出权限对话框
     */
    public static void enableBluetooth(Activity activity) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, REQUEST_ENBALE_BLE_CODE);
    }

    public static void destroy() {
        BleManager.getInstance().destroy();
    }

    public static void setMtu(BleDevice bleDevice, int mtu) {
        BleManager.getInstance().setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                // 设置MTU失败
            }

            @Override
            public void onMtuChanged(int mtu) {
                // 设置MTU成功，并获得当前设备传输支持的MTU值
            }
        });
    }

    public static BluetoothGatt getBluetoothGatt(BleDevice bleDevice) {
        return BleManager.getInstance().getBluetoothGatt(bleDevice);
    }

    /**
     * 读取蓝牙信号强度
     */
    public static void readRssi(BleDevice bleDevice) {
        BleManager.getInstance().readRssi(
                bleDevice,
                new BleRssiCallback() {

                    @Override
                    public void onRssiFailure(BleException exception) {
                        // 读取设备的信号强度失败
                    }

                    @Override
                    public void onRssiSuccess(int rssi) {
                        // 读取设备的信号强度成功
                    }
                });
    }

    /**
     * 取消扫描
     */
    public static void cancelScan() {
        BleManager.getInstance().cancelScan();
    }

    /**
     * 开始扫描
     */
    public static void scan(final BleScanCallback callback) {
        /**
         * 扫描得到的BLE外围设备，会以BleDevice对象的形式，作为后续操作的最小单元对象。它本身含有这些信息：
         * String getName()：蓝牙广播名
         * String getMac()：蓝牙Mac地址
         * byte[] getScanRecord()： 被扫描到时候携带的广播数据
         * int getRssi() ：被扫描到时候的信号强度  [-127, 126]
         * */

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                L.i("开始扫描蓝牙:onScanStarted:" + success);
                if (callback != null) {
                    callback.onScanStarted(success);
                }
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                L.i("扫描:onScanning:" + "name:" + bleDevice.getName() + " mac:" + bleDevice.getMac() + " rssi:" + bleDevice.getRssi());
                if (callback != null) {
                    callback.onScanning(bleDevice);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                L.i("扫描完成:onScanFinished:" + scanResultList);
                if (callback != null) {
                    callback.onScanFinished(scanResultList);
                }
            }
        });
    }

    /**
     * 连接设备
     */
    public static void connect(final BleDevice bleDevice, BleGattCallback callback) {
        connectInner(bleDevice, callback);
    }

    public static void connect(final String bleMac, BleGattCallback callback) {
        connectInner(bleMac, callback);
    }

    private static void connectInner(final Object bleObj, final BleGattCallback callback) {
        BleGattCallback bleGattCallback = new BleGattCallback() {
            @Override
            public void onStartConnect() {
                L.i("开始连接设备:" + bleObj);
                if (callback != null) {
                    callback.onStartConnect();
                }
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                L.e("设备连接失败:" + bleDevice.getName() + " " + bleDevice.getMac() + " ->" + exception);
                if (callback != null) {
                    callback.onConnectFail(bleDevice, exception);
                }
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                L.i("设备连接成功:" + gatt + " " + status);
                if (callback != null) {
                    callback.onConnectSuccess(bleDevice, gatt, status);
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected /*主动断开*/, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                L.i("设备连接断开:" + isActiveDisConnected + " " + gatt + " " + status);
                if (callback != null) {
                    callback.onDisConnected(isActiveDisConnected, bleDevice, gatt, status);
                }
            }
        };

        if (bleObj instanceof String) {
            BleManager.getInstance().connect((String) bleObj, bleGattCallback);
        } else if (bleObj instanceof BleDevice) {
            BleManager.getInstance().connect((BleDevice) bleObj, bleGattCallback);
        }
    }

    public static BleDevice getBleDeviceByMac(String mac) {
        BluetoothDevice bluetoothDevice = BleManager.getInstance().getBluetoothAdapter().getRemoteDevice(mac);
        BleDevice bleDevice = new BleDevice(bluetoothDevice, 0, null, 0);
        return bleDevice;
    }

    public static void disconnectAllDevice() {
        BleManager.getInstance().disconnectAllDevice();
    }

    public static void disconnect(BleDevice bleDevice) {
        BleManager.getInstance().disconnect(bleDevice);
    }

    /**
     * 类似 UDP 发送
     * <p>
     * 进行BLE数据相互发送的时候，一次最多能发送20个字节
     */
    public static void notify(@NotNull final BleDevice bleDevice,
                              String uuid_service /*蓝牙设备通信规定的uuid*/,
                              String uuid_characteristic_notify,
                              final BleNotifyCallback callback) {
        BleManager.getInstance().notify(
                bleDevice,
                uuid_service,
                uuid_characteristic_notify,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        // 打开通知操作成功
                        callback.onNotifySuccess();
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        // 打开通知操作失败
                        L.e(exception.toString());
                        if (callback != null) {
                            callback.onNotifyFailure(exception);
                        }
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
                        L.i("notify收到数据:" + bleDevice.getMac() + "->" + HEX.formatHex(HEX.hexString(data), ' '));
                        if (callback != null) {
                            callback.onCharacteristicChanged(data);
                        }
                    }
                });

    }

    public static void stopNotify(BleDevice bleDevice,
                                  String uuid_service,
                                  String uuid_characteristic_notify) {
        BleManager.getInstance().stopNotify(bleDevice, uuid_service, uuid_characteristic_notify);
    }

    /**
     * 类似 TCP 连接
     */
    public static void indicate(BleDevice bleDevice,
                                String uuid_service,
                                String uuid_characteristic_indicate) {
        BleManager.getInstance().indicate(
                bleDevice,
                uuid_service,
                uuid_characteristic_indicate,
                new BleIndicateCallback() {
                    @Override
                    public void onIndicateSuccess() {
                        // 打开通知操作成功
                    }

                    @Override
                    public void onIndicateFailure(BleException exception) {
                        // 打开通知操作失败
                        L.e(exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
                    }
                });
    }

    public static void stopIndicate(BleDevice bleDevice,
                                    String uuid_service,
                                    String uuid_characteristic_indicate) {
        BleManager.getInstance().stopIndicate(bleDevice, uuid_service, uuid_characteristic_indicate);
    }

    public static void write(final BleDevice bleDevice,
                             String uuid_service,
                             String uuid_characteristic_write,
                             byte[] data,
                             final BleWriteCallback callback) {
        BleManager.getInstance().write(
                bleDevice,
                uuid_service,
                uuid_characteristic_write,
                data,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        L.d("写入数据:" + bleDevice.getMac() + "->" + current + ":" + total + "->" + HEX.formatHex(HEX.hexString(justWrite), ' '));
                        if (callback != null) {
                            callback.onWriteSuccess(current, total, justWrite);
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        L.e(exception.toString());
                        if (callback != null) {
                            callback.onWriteFailure(exception);
                        }
                    }
                });
    }

    public static void read(BleDevice bleDevice,
                            String uuid_service,
                            String uuid_characteristic_read,
                            final BleReadCallback callback) {
        BleManager.getInstance().read(
                bleDevice,
                uuid_service,
                uuid_characteristic_read,
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        L.d("读取数据:" + HEX.formatHex(HEX.hexString(data), ' '));
                        if (callback != null) {
                            callback.onReadSuccess(data);
                        }
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        L.e(exception.toString());
                        if (callback != null) {
                            callback.onReadFailure(exception);
                        }
                    }
                });
    }

}
