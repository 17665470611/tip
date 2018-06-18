package com.cloud.common.map;

import org.apache.commons.lang3.math.NumberUtils;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;

import ch.hsr.geohash.GeoHash;

import com.cloud.common.object.HMap;

/**
 * geo测试类
 * @Description: TODO 
 * @author tfwang 
 * @date 2017年5月26日 下午4:38:37 
 *
 */
@SuppressWarnings("unchecked")
public class Geo {

	/**搜索精细度--5级别*/
	public static int GEO_SIZE_5 = 5;

	/**搜索精细度--6级别*/
	public static int GEO_SIZE_6 = 6;

	/**
	 * 获取GeoCode
	 * @param lon 经度
	 * @param lat 纬度
	 * @param size 搜索精细度
	 */
	public static String getGeoCode(double lon, double lat, int size) {
		GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 5);
		return geoHash.toBase32();
	}

	/**
	 *计算两个经纬度地点的直线距离
	 * @param lon1 第一个位置纬度
	 * @param lat1 第一个位置纬度
	 * @param lon2 第二个位置经度
	 * @param lat2 第二个位置的纬度
	 */
	public static double getDistance(String lon1, String lat1, String lon2, String lat2) {
		try {
			SpatialContext geo = SpatialContext.GEO;
			double distance = geo.calcDistance(geo.makePoint(NumberUtils.toDouble(lon1), NumberUtils.toDouble(lat1)), geo.makePoint(NumberUtils.toDouble(lon2), NumberUtils.toDouble(lat2))) * DistanceUtils.DEG_TO_KM;
			return distance * 1000;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 根据经纬度获取Geohash
	 * @param lon
	 * @param lat
	 * @return
	 */

	public static HMap getGeoHash(String lon, String lat, int size) {
		// 移动设备经纬度
		HMap hmap = new HMap();
		int i = 1;
		GeoHash geoHash = GeoHash.withCharacterPrecision(NumberUtils.toDouble(lat), NumberUtils.toDouble(lon), size);
		// 当前
		hmap.put("geoHash" + i, geoHash.toBase32());
		// N, NE, E, SE, S, SW, W, NW
		GeoHash[] adjacent = geoHash.getAdjacent();
		for (GeoHash hash : adjacent) {
			hmap.put("geoHash" + (++i), hash.toBase32());
		}
		hmap.put("size", size);
		return hmap;
	}

}
