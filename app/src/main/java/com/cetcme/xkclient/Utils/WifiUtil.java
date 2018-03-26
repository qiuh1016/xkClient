package com.cetcme.xkclient.Utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by qiuhong on 14/03/2018.
 */

public class WifiUtil {

    public final static int WIFI_CIPHER_WPA = 0xA1;
    public final static int WIFI_CIPHER_WEP = 0xA2;
    public final static int WIFI_CIPHER_NONE = 0xA3;

    /**
     * wifi 信号强度 0：信号很好  1：信号较好  2：信号一般  3：信号较差  4：信号很差
     */
    public static int getIntLevel(ScanResult scanResult) {
        int level = scanResult.level;
        if (level <= 0 && level >= -50) {
            return 0;
        } else if (level < -50 && level >= -70) {
            return 1;
        } else if (level < -70 && level >= -80) {
            return 2;
        } else if (level < -80 && level >= -100) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * wifi 信号强度 0：信号很好  1：信号较好  2：信号一般  3：信号较差  4：信号很差
     */
    public static String getStringLevel(ScanResult scanResult) {
        int level = scanResult.level;
        if (level <= 0 && level >= -50) {
            return "信号很好";
        } else if (level < -50 && level >= -70) {
            return "信号较好";
        } else if (level < -70 && level >= -80) {
            return "信号一般";
        } else if (level < -80 && level >= -100) {
            return "信号较差";
        } else {
            return "信号很差";
        }
    }

    /**
     * wifi加密方式
     */
    public static int getType(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        int type = WIFI_CIPHER_WPA;
        if (!capabilities.isEmpty()) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                type = WIFI_CIPHER_WPA;
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                type = WIFI_CIPHER_WEP;
            } else {
                type = WIFI_CIPHER_NONE;
            }
        }
        return type;
    }

    public static String GetIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF)+ "." +
                ((i >> 24 ) & 0xFF );
    }

}
