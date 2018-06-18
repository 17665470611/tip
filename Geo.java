package com.cloud.common.map;

import org.apache.commons.lang3.math.NumberUtils;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;

import ch.hsr.geohash.GeoHash;

import com.cloud.common.object.HMap;

/**
 * geo������
 * @Description: TODO 
 * @author tfwang 
 * @date 2017��5��26�� ����4:38:37 
 *
 */
@SuppressWarnings("unchecked")
public class Geo {

	/**������ϸ��--5����*/
	public static int GEO_SIZE_5 = 5;

	/**������ϸ��--6����*/
	public static int GEO_SIZE_6 = 6;

	/**
	 * ��ȡGeoCode
	 * @param lon ����
	 * @param lat γ��
	 * @param size ������ϸ��
	 */
	public static String getGeoCode(double lon, double lat, int size) {
		GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 5);
		return geoHash.toBase32();
	}

	/**
	 *����������γ�ȵص��ֱ�߾���
	 * @param lon1 ��һ��λ��γ��
	 * @param lat1 ��һ��λ��γ��
	 * @param lon2 �ڶ���λ�þ���
	 * @param lat2 �ڶ���λ�õ�γ��
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
	 * ���ݾ�γ�Ȼ�ȡGeohash
	 * @param lon
	 * @param lat
	 * @return
	 */

	public static HMap getGeoHash(String lon, String lat, int size) {
		// �ƶ��豸��γ��
		HMap hmap = new HMap();
		int i = 1;
		GeoHash geoHash = GeoHash.withCharacterPrecision(NumberUtils.toDouble(lat), NumberUtils.toDouble(lon), size);
		// ��ǰ
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
