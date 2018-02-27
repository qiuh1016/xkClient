package com.cetcme.xkclient.utils;

import java.math.BigDecimal;

public class GisUtil {
	private static final String DMS_FORMAT = "%s%d°%d′%s\"";
	public static final double DISDANCE_PER_LONGITUDE = 111712.69150641055729984301412873;
	public static final double DISDANCE_PER_LATITUDE = 102834.74258026089786013677476285;

	public static Double dmsToDeg(String deg) {
		Double geo = 0.0;
		int tempIndex = 0;
		if (deg.length() > 1) {
			tempIndex = deg.indexOf("°");
			if (tempIndex > -1) {
				geo = Double.parseDouble(deg.substring(0, tempIndex));
				if (tempIndex < deg.length() - 1) {
					deg = deg.substring(tempIndex + 1, deg.length());
				}
			}

			tempIndex = deg.indexOf("′");
			if (tempIndex > -1) {
				geo += (double) (Math.round((Double.parseDouble(deg.substring(0, tempIndex)) / 60) * 1000000)
						/ 1000000.0);
				if (tempIndex < deg.length() - 1) {
					deg = deg.substring(tempIndex + 1, deg.length());
				}
			}

			tempIndex = deg.length();
			if (tempIndex > 0) {
				geo += (double) (Math.round(((Double.parseDouble(deg.substring(0, tempIndex - 1)) / 60) / 60) * 1000000)
						/ 1000000.0);
			}
		}

		return geo;
	}

	/**
	 * 四舍五入保留小数点
	 * 
	 * @param value
	 * @param digits
	 * @return
	 */
	public static String format(double value, int digits) {
		return String.format("%." + digits + "f", value);
	}
	

	
	/**
	 * 判断点是否在圆形范围内
	 */
	public static boolean isPointInCycle(GpsPosition point, GpsPosition circlePoint, double radius) {
		BigDecimal bd1 = new BigDecimal(point.longitude - circlePoint.longitude);
		bd1 = bd1.multiply(bd1);
		bd1 = bd1.multiply(new BigDecimal(DISDANCE_PER_LONGITUDE));
		bd1 = bd1.multiply(new BigDecimal(DISDANCE_PER_LONGITUDE));
		
		BigDecimal bd2 = new BigDecimal(point.latitude - circlePoint.latitude);
		bd2 = bd2.multiply(bd2);
		bd2 = bd2.multiply(new BigDecimal(DISDANCE_PER_LATITUDE));
		bd2 = bd2.multiply(new BigDecimal(DISDANCE_PER_LATITUDE));
		
		bd1 = bd1.add(bd2);
		
		BigDecimal bd3 = new BigDecimal(radius);
		bd3 = bd3.multiply(bd3);
		
		return (bd1.compareTo(bd3) <= 0);
		
//		double distance = (point.longitude - circlePoint.longitude) * (point.longitude - circlePoint.longitude)
//				* (double) DISDANCE_PER_LONGITUDE * (double) DISDANCE_PER_LONGITUDE
//				+ (point.latitude - circlePoint.latitude) * (point.latitude - circlePoint.latitude)
//						* (double) DISDANCE_PER_LATITUDE * (double) DISDANCE_PER_LATITUDE;
//		return (distance <= radius * radius);
	}
}
