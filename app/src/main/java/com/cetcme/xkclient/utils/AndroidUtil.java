package com.cetcme.xkclient.utils;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @todo: android 手机相关的的工具
 * @author: jf.team
 * @time: 15/11/30上午11:38
 */

public class AndroidUtil {

	/**
	 * Android调用手机中的应用市场，去评分的功能实现
	 */
	public static void goToMarket(Context context) {
		Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 拨打电话 <uses-permission android:name="android.permission.CALL_PHONE"/>
	 * 这种方式的特点就是，去到了拨号界面，但是实际的拨号是由用户点击实现的
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	public static void callPhone1(Context context, String phoneNumber) {
		if (StringUtils.isEmpty(phoneNumber) && StringUtils.isNumber(phoneNumber)) {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			Uri data = Uri.parse("tel:" + phoneNumber);
			intent.setData(data);
			context.startActivity(intent);
		}
	}

	/**
	 * 拨打电话 <uses-permission android:name="android.permission.CALL_PHONE"/>
	 * 这种方式的特点就是，直接拨打了你所输入的号码，所以这种方式对于用户没有直接的提示效果，Android推荐使用第一种方式，
	 * 如果是第二种的话，建议在之前加一个提示框，是否拨打号码，然后确定后再拨打。
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	public static void callPhone2(Context context, String phoneNumber) {
		if (!StringUtils.isEmpty(phoneNumber) && !StringUtils.isNumber(phoneNumber)) {
			Intent intent = new Intent(Intent.ACTION_CALL);
			Uri data = Uri.parse("tel:" + phoneNumber);
			intent.setData(data);
			context.startActivity(intent);
		}
	}

	/**
	 * 发送短信 <uses-permissionandroid:name="android.permission.SEND_SMS"/>
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	public static void sendSMS(Context context, String phoneNumber, String message) {
		if (!StringUtils.isEmpty(phoneNumber) && !StringUtils.isNumber(phoneNumber)) {
			Uri uri = Uri.parse("smsto://" + phoneNumber);
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra("sms_body", message);
			context.startActivity(intent);
		}
	}

	/**
	 * android 系统自带的分享 http://blog.csdn.net/xyz_lmn/article/details/16856843
	 * http://1002878825-qq-com.iteye.com/blog/1580032
	 * 
	 * @param context
	 * @param title
	 * @param text
	 */
	public static void share(Context context, String title, String text) {
		share(context, title, text, null);
	}

	/**
	 * android 系统自带的分享
	 * 
	 * @param context
	 * @param title
	 * @param text
	 * @param imgPath
	 */
	public static void share(Context context, String title, String text, String imgPath) {
		if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(text)) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/*");
			intent.putExtra(Intent.EXTRA_SUBJECT, title);
			intent.putExtra(Intent.EXTRA_TEXT, text);
			if (imgPath != null && !StringUtils.isEmpty(imgPath)) {
				File file = new File(imgPath);
				if (file != null && file.exists() && file.isFile()) {
					intent.setType("image/jpg");
					Uri uri = Uri.fromFile(file);
					intent.putExtra(Intent.EXTRA_STREAM, uri);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(Intent.createChooser(intent, title));
		}
	}

	public static boolean intentIsAvailable(Context context, Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
		return list.size() > 0;
	}

	/**
	 * 获取屏幕尺寸与密度. DisplayMetrics{density=1.5, width=480, height=854,
	 * scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
	 * DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0,
	 * xdpi=160.42105, ydpi=160.15764}
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		Resources mResources;
		if (context == null) {
			mResources = Resources.getSystem();
		} else {
			mResources = context.getResources();
		}
		DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
		return mDisplayMetrics;
	}

	/**
	 * 获取手机号码
	 * 
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getLine1Number() == null || telephonyManager.getLine1Number().length() < 11) {
			return null;
		} else {
			return telephonyManager.getLine1Number();
		}
	}

	/**
	 * IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称
	 * IMEI由15位数字组成的”电子串号”，它与每台手机一一对应，而且该码是全世界唯一的 其组成为： 1.
	 * 前6位数(TAC)是”型号核准号码”，一般代表机型 2. 接着的2位数(FAC)是”最后装配号”，一般代表产地 3.
	 * 之后的6位数(SNR)是”串号”，一般代表生产顺序号 4. 最后1位数(SP)通常是”0″，为检验码，目前暂备用
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getDeviceId() == null) {
			return "";
		} else {
			return telephonyManager.getDeviceId();
		}
	}

	/**
	 * IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)
	 * IMSI共有15位，其结构如下： MCC+MNC+MIN MCC：Mobile Country Code，移动国家码，共3位，中国为460;
	 * MNC:Mobile NetworkCode，移动网络码，共2位 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03
	 * 合起来就是（也是Android手机中APN配置文件中的代码）： 中国移动：46000 46002 中国联通：46001 中国电信：46003
	 * 举例，一个典型的IMSI号码为460030912121001
	 */
	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getSubscriberId() == null) {
			return "";
		} else {
			return telephonyManager.getSubscriberId();
		}
	}

	/**
	 * 
	 * 获取SSID地址.
	 * 
	 * @param context
	 * @return
	 */
	public static String getSSID(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info.getSSID() == null) {
			return null;
		} else {
			return info.getSSID();
		}
	}

	/**
	 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	 * 获取mac地址.
	 * 
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info.getMacAddress() == null) {
			return null;
		} else {
			return info.getMacAddress();
		}
	}

	/**
	 * <uses-permission
	 * android:name="android.permission.BLUETOOTH"></uses-permission> 获取蓝牙mac地址
	 * 
	 * @return
	 */

	public static String getBTMacAddress() {
		BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		String m_szBTMAC = m_BluetoothAdapter.getAddress();
		return m_szBTMAC;
	}

	/**
	 * < uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	 * <uses-permission android:name="android.permission.WAKE_LOCK"/>
	 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
	 * 获取本机IP地址
	 * 
	 * @return
	 */
	public static String getIpAddress(Context context) {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "."
				+ (ipAddress >> 24 & 0xFF);
		return ip;
	}

	/**
	 * 
	 * 获取可用内存.
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailMemory(Context context) {
		// 获取android当前可用内存大小
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		// 当前系统可用内存 ,将获得的内存大小规格化
		return memoryInfo.availMem;
	}

	/**
	 * 
	 * 获取 总内存.
	 * 
	 * @param context
	 * @return
	 */
	public static long getTotalMemory(Context context) {
		// 系统内存信息文件
		String file = "/proc/meminfo";
		String memInfo;
		String[] strs;
		long memory = 0;

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
			// 读取meminfo第一行，系统内存大小
			memInfo = bufferedReader.readLine();
			strs = memInfo.split("\\s+");
			for (String str : strs) {
				// LogUtil.d(AndroidUtil.class,str+"\t");
			}
			// 获得系统总内存，单位KB
			memory = Integer.valueOf(strs[1]).intValue() * 1024;
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Byte转位KB或MB
		return memory;
	}

	/**
	 * 得到设备屏幕的宽度
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 得到设备屏幕的高度
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 得到设备的密度
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 把密度转换为像素
	 */
	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}

	public static String getAndroidId(Context context) {
		String android_id = "";
		android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return android_id;
	}

	public static String getUUID(Context context) {
		String uuid = "";
		String imei = "";
		String mac_address = "";
		String bt_mac_address = "";
		String android_id = "";
		if (isPemision(context, "android.permission.READ_PHONE_STATE"))
			imei = getIMEI(context);
		if (isPemision(context, "android.permission.ACCESS_WIFI_STATE"))
			mac_address = getMacAddress(context);
		if (isPemision(context, "android.permission.BLUETOOTH"))
			bt_mac_address = getBTMacAddress();
		android_id = getAndroidId(context);
		if (!TextUtils.isEmpty(imei)) {
			uuid += imei;
		}
		if (!TextUtils.isEmpty(mac_address)) {
			uuid += mac_address;
		}
		if (!TextUtils.isEmpty(bt_mac_address)) {
			uuid += bt_mac_address;
		}
		if (!TextUtils.isEmpty(android_id)) {
			uuid += android_id;
		}
		uuid += getUniquePsuedoID();
		uuid = "a" + MD5Tools.md5(uuid);
		return uuid;
	}

	public static boolean isPemision(Context context, String p) {
		PackageManager pm = context.getPackageManager();
		boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(p, "com.jfpal.https"));
		if (permission) {
			Log.d("----", "有这个权限");
		} else {
			Log.d("----", "木有这个权限");
		}

		return permission;

	}
	
	//获得独一无二的Psuedo ID
	public static String getUniquePsuedoID() {
	       String serial = null;

	       String m_szDevIDShort = "35" +
	            Build.BOARD.length()%10+ Build.BRAND.length()%10 +

	            Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +

	            Build.DISPLAY.length()%10 + Build.HOST.length()%10 +

	            Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +

	            Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +

	            Build.TAGS.length()%10 + Build.TYPE.length()%10 +

	            Build.USER.length()%10 ; //13 位

	    try {
	        serial = Build.class.getField("SERIAL").get(null).toString();
	       //API>=9 使用serial号
	        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
	    } catch (Exception exception) {
	        //serial需要一个初始化
	        serial = "serial"; // 随便一个初始化
	    }
	    //使用硬件信息拼凑出来的15位号码
	    return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
	}


	/**
	 * 获取当前手机系统语言。
	 *
	 * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
	 */
	public static String getSystemLanguage() {
		return Locale.getDefault().getLanguage();
	}

	/**
	 * 获取当前系统上的语言列表(Locale列表)
	 *
	 * @return  语言列表
	 */
	public static Locale[] getSystemLanguageList() {
		return Locale.getAvailableLocales();
	}

	/**
	 * 获取当前手机系统版本号
	 *
	 * @return  系统版本号
	 */
	public static String getSystemVersion() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取手机型号
	 *
	 * @return  手机型号
	 */
	public static String getSystemModel() {
		return Build.MODEL;
	}

	/**
	 * 获取手机厂商
	 *
	 * @return  手机厂商
	 */
	public static String getDeviceBrand() {
		return Build.BRAND;
	}


}
