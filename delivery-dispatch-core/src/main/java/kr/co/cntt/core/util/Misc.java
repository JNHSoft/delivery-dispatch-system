package kr.co.cntt.core.util;

/**
 * <p> kr.co.cntt.core.util </p>
 * <p> Misc.java </p>
 *
 * @author Aiden
 * @see
 */
public class Misc {

    /**
     * <p> 두 지점 간 거리를 m단위로 계산
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     * @throws Exception
     */
    public int getHaversine(String lat1, String lon1, String lat2, String lon2) throws Exception {
        double radiansLat1 = Math.toRadians(Double.parseDouble(lat1));
        double radiansLon1 = Math.toRadians(Double.parseDouble(lon1));
        double radiansLat2 = Math.toRadians(Double.parseDouble(lat2));
        double radiansLon2 = Math.toRadians(Double.parseDouble(lon2));

        double dlat = radiansLat2 - radiansLat1;
        double dlon = radiansLon2 - radiansLon1;

        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(radiansLat1) * Math.cos(radiansLat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));

        int m = Integer.parseInt(String.valueOf(Math.round(6367 * c * 1000)));

        return m;
    }

}
