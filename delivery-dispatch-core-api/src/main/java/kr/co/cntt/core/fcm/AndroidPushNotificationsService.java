package kr.co.cntt.core.fcm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import kr.co.cntt.core.model.order.Order;
import org.apache.poi.ss.formula.functions.T;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AndroidPushNotificationsService {

    private static final String FIREBASE_SERVER_KEY = "AAAA5gnPZyk:APA91bEloN3gdZW1GF1FWaFQY5NvQOSSnbjaxEPmxOMiEP3jMjyp_T5G4wBP241C7EACkS-kGYUbo3a8KxDePSWY2nB8umGZSVRaqLjojeu7RrqFT-H_kQ5iTYJQBERO9ZpEXQf_FL69";

    @Async
    public CompletableFuture<FirebaseResponse> send(String token, Object obj) {
        HttpEntity<String> request = null;
        try {
            JSONObject body = new JSONObject();
            // JsonArray registration_ids = new JsonArray();
            // body.put("registration_ids", registration_ids);
            body.put("to", token);
            body.put("priority", "high");
            // body.put("dry_run", true);

            JSONObject notification = new JSONObject();
            notification.put("body", "body string here");
            notification.put("title", "title string here");
            // notification.put("icon", "myicon");

            Gson gson = new GsonBuilder().serializeNulls().create();

            JSONObject data = new JSONObject();
            data.put("obj", gson.toJson(obj));
//            data.put("key2", "value2");


            body.put("notification", notification);
            body.put("data", data);

            System.out.println("body" + body.toString());

            request = new HttpEntity<>(body.toString());

            RestTemplate restTemplate = new RestTemplate();

            ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
            interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
            restTemplate.setInterceptors(interceptors);

            FirebaseResponse firebaseResponse = restTemplate.postForObject("https://fcm.googleapis.com/fcm/send", request, FirebaseResponse.class);

            return CompletableFuture.completedFuture(firebaseResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Async
    public CompletableFuture<FirebaseResponse> sendGroup(ArrayList<String> tokens) {
        HttpEntity<String> request = null;
        try {
            JSONObject body = new JSONObject();
            JsonArray registration_ids = new JsonArray();
            for(String token : tokens){
                registration_ids.add(token);
            }

            body.put("registration_ids", registration_ids);

            body.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("body", "body string here");
            notification.put("title", "title string here");
            // notification.put("icon", "myicon");

            JSONObject data = new JSONObject();
            data.put("key1", "value1");
            data.put("key2", "value2");

            body.put("notification", notification);
            body.put("data", data);

            request = new HttpEntity<>(body.toString());

            RestTemplate restTemplate = new RestTemplate();

            ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
            interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
            restTemplate.setInterceptors(interceptors);

            FirebaseResponse firebaseResponse = restTemplate.postForObject("https://fcm.googleapis.com/fcm/send", request, FirebaseResponse.class);

            return CompletableFuture.completedFuture(firebaseResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*@Async
    public CompletableFuture<GroupResponse> group(HttpEntity<String> entity) {

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);

        GroupResponse response = restTemplate.postForObject("https://fcm.googleapis.com/fcm/send", entity, GroupResponse.class);

        return CompletableFuture.completedFuture(response);
    }*/

}