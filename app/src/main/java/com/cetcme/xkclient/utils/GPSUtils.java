package com.cetcme.xkclient.utils;

import java.util.HashMap;
import java.util.Map;

public class GPSUtils {

	private final static double PI = Math.PI;
	
	private final static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	public static Map<String, Double> delta(double lat, double lon) {
		Map<String, Double> map = new HashMap<String, Double>();
		double a = 6378245.0; //  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
		double ee = 0.00669342162296594323; //  ee: 椭球的偏心率。
		double dLat = transformLat(lon - 105.0, lat - 35.0);
		double dLon = transformLon(lon - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * PI;
		double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        map.put("lat", dLat);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        map.put("lon", dLon);
        
        return map;
	}
	
	//WGS-84 to GCJ-02
	public static Map<String, Double> gcj_encrypt(double wgsLat, double wgsLon) {
		Map<String, Double> map = new HashMap<String, Double>();
        if (outOfChina(wgsLat, wgsLon)){
        	map.put("lat", wgsLat);
        	map.put("lon", wgsLon);
        	return map;
        }
 
        Map<String, Double> dmap = delta(wgsLat, wgsLon);
        double dLat = dmap.get("lat");
        double dLon = dmap.get("lon");
        map.put("lat", wgsLat + dLat);
    	map.put("lon", wgsLon + dLon);
        return map;
    }
	
	//GCJ-02 to WGS-84
	public static Map<String, Double> gcj_decrypt(double gcjLat, double gcjLon) {
		Map<String, Double> map = new HashMap<String, Double>();
        if (outOfChina(gcjLat, gcjLon)){
        	map.put("lat", gcjLat);
        	map.put("lon", gcjLon);
        	
        	return map;
        }
 
        Map<String, Double> dmap = delta(gcjLat, gcjLon);
        double dLat = dmap.get("lat");
        double dLon = dmap.get("lon");
        map.put("lat", gcjLat - dLat);
    	map.put("lon", gcjLon - dLon);
        
        return map;
    }
	
	//GCJ-02 to BD-09
	public static Map<String, Double> bd_encrypt(double gcjLat, double gcjLon) {
		Map<String, Double> map = new HashMap<String, Double>();
		double x = gcjLon, y = gcjLat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bdLon = z * Math.cos(theta) + 0.0065;
		double bdLat = z * Math.sin(theta) + 0.006;
		map.put("lat", bdLat);
    	map.put("lon", bdLon);
        return map;
    }
	
	//BD-09 to GCJ-02
	public static Map<String, Double> bd_decrypt(double bdLat, double bdLon) {
		Map<String, Double> map = new HashMap<String, Double>();
		double x = bdLon - 0.0065, y = bdLat - 0.006;  
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);  
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);  
		double gcjLon = z * Math.cos(theta);  
		double gcjLat = z * Math.sin(theta);
		map.put("lat", gcjLat);
    	map.put("lon", gcjLon);
    	
        return map;
	}
	
	public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004d || lon > 137.8347d)
            return true;
        if (lat < 0.8293d || lat > 55.8271d)
            return true;
        
        return false;
	}

	public static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        
        return ret;
	}
	
	public static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        
        return ret;
    }
	
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		for(int i=0;i<1000000;i++){
			double wgsLon = 120.6586667;
			double wgsLat = 30.7709833;
			Map<String, Double> GCJMap = GPSUtils.gcj_encrypt(wgsLat, wgsLon);
			double gcjLon = GCJMap.get("lon");
			double gcjLat = GCJMap.get("lat");
			Map<String, Double> bdMap = GPSUtils.bd_encrypt(gcjLat, gcjLon);
			double bdLon = bdMap.get("lon");
			double bdLat = bdMap.get("lat");
			System.out.println(wgsLon+"=========="+wgsLat);
			System.out.println(bdLon+"=========="+bdLat);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

}