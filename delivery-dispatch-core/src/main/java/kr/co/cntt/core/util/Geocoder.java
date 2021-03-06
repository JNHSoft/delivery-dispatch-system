package kr.co.cntt.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.util.HashMap;
import java.util.Map;

/**
 * <p> kr.co.cntt.core.util </p>
 * <p> Geocoder.java </p>
 *
 * @author Aiden
 * @see
 */
public class Geocoder {

    private static final String apiKey = "AIzaSyBeYYK4f3VZhUuslLAChJnwedur8MxkbhU";

    /**
     * <p> Google Geocoder - Address to latitude, longitude
     *
     * @param address
     * @return
     * @throws Exception
     */
    public Map<String, String> getLatLng(String address) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();

        GeocodingResult[] results = GeocodingApi.geocode(context, address).await();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> map = new HashMap<>();
        if (results.length > 0) {
            map.put("lat", gson.toJson(results[0].geometry.location.lat));
            map.put("lng", gson.toJson(results[0].geometry.location.lng));
        }

        return map;
    }

}
