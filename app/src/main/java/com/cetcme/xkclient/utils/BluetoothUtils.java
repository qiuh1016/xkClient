package com.cetcme.xkclient.utils;

import android.bluetooth.BluetoothAdapter;

/**
 * 项目名称：android
 * 类描述：
 * 创建人：ZHANGHANJUN
 * 创建时间：2016/11/15 14:04
 * 修改人：Administrator
 * 修改时间：2016/11/15 14:04
 * 修改备注：
 *
 * @version 邮箱：1178075841@qq.com
 */


public class BluetoothUtils {
    /**
     * 当前 Android 设备是否支持 Bluetooth
     *
     * @return true：支持 Bluetooth false：不支持 Bluetooth
     */
    public static boolean isBluetoothSupported() {
        return BluetoothAdapter.getDefaultAdapter() != null ? true : false;
    }

    /**
     * 当前 Android 设备的 bluetooth 是否已经开启
     *
     * @return true：Bluetooth 已经开启 false：Bluetooth 未开启
     */
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }

        return false;
    }

    /**
     * 强制开启当前 Android 设备的 Bluetooth
     *
     * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
     */
    public static boolean turnOnBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.enable();
        }

        return false;
    }



    /**
     * 强制关闭当前 Android 设备的 Bluetooth
     *
     * @return true：强制关闭 Bluetooth　成功　false：强制关闭 Bluetooth 失败
     */
    public static boolean turnOffBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.disable();
        }
        return false;
    }

}
