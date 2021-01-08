package kr.co.cntt.core.fcm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import kr.co.cntt.core.model.fcm.FcmBody;
import kr.co.cntt.core.model.order.Order;
import org.apache.poi.ss.formula.functions.T;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AndroidPushNotificationsService {

    // 구 API 인증 키
    private static final String FIREBASE_SERVER_KEY_OLD = "AAAA5gnPZyk:APA91bEloN3gdZW1GF1FWaFQY5NvQOSSnbjaxEPmxOMiEP3jMjyp_T5G4wBP241C7EACkS-kGYUbo3a8KxDePSWY2nB8umGZSVRaqLjojeu7RrqFT-H_kQ5iTYJQBERO9ZpEXQf_FL69";
    // 신 API 인증 키
    private static final String FIREBASE_SERVER_KEY = "AAAAKIBRSaU:APA91bE0n8Uy5TTe6DVCgTtF1vwFq5nNAtCXshVz9o9FSGsJhybDrrFmkNXMPaBP6__5NOFYHSpNGgvqu8Iof52ahDOHJBT-Iqy8U1ohcA-uUUQgHnnzRjdOwugY4kFChQ2_ew6oNBvZ";

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

            // IOS 개발자 추가
            body.put("content_available", true);
            // 20.11.02 iOS Silent PUSH 관련 내용 추가
            body.put("apns-push-type", "background");
            body.put("apns-priority", 5);

            // iOS 알림 내용
            JSONObject alert = new JSONObject();
            alert.put("title", ((JSONObject) data.get("obj")).get("title"));
            alert.put("body", ((JSONObject) data.get("obj")).get("message"));

            JSONObject aps = new JSONObject();
            aps.put("alert", alert);

            body.put("aps", aps);

            body.put("sound", "default");

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
    public CompletableFuture<FirebaseResponse> sendGroup(ArrayList<String> tokens, Object obj) {
        HttpEntity<String> request = null;
        try {
            JSONObject body = new JSONObject();
            JSONArray registration_ids = new JSONArray();
            for(String token : tokens){
                registration_ids.put(token);
            }

            body.put("registration_ids", registration_ids);

            body.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("body", "body string here");
            notification.put("title", "title string here");
            // notification.put("icon", "myicon");

            Gson gson = new GsonBuilder().serializeNulls().create();

            JSONObject data = new JSONObject();
            JSONObject dataObject= new JSONObject(gson.toJson(obj));
            data.put("obj", dataObject);

//            data.put("key2", "value2");


            //body.put("notification", notification);

            body.put("data", data);

            body.put("content_available",true);


            // 20.11.02 iOS Silent PUSH 관련 내용 추가
            body.put("apns-push-type", "background");
            body.put("apns-priority", 5);

            // iOS 알림 내용
            JSONObject alert = new JSONObject();
            alert.put("title", ((JSONObject) data.get("obj")).get("title"));
            alert.put("body", ((JSONObject) data.get("obj")).get("message"));

            JSONObject aps = new JSONObject();
            aps.put("alert", alert);

            body.put("aps", aps);

            body.put("sound", "default");


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            request = new HttpEntity<>(body.toString(), headers);

            RestTemplate restTemplate = new RestTemplate();


            ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
//            interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json;charset=utf-8"));
            restTemplate.setInterceptors(interceptors);

            FirebaseResponse firebaseResponse = restTemplate.postForObject("https://fcm.googleapis.com/fcm/send", request, FirebaseResponse.class);

            return CompletableFuture.completedFuture(firebaseResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Async
    public CompletableFuture<FirebaseResponse> sendGroup(FcmBody fcmBody, String strPlatform) {
        HttpEntity<String> request = null;
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            request = new HttpEntity<>(new Gson().toJson(fcmBody), headers);

            RestTemplate restTemplate = new RestTemplate();

            ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            if (strPlatform.equals("old")){
                interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY_OLD));
            }else{
                interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
            }
//            interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json;charset=utf-8"));
            restTemplate.setInterceptors(interceptors);

            FirebaseResponse firebaseResponse = restTemplate.postForObject("https://fcm.googleapis.com/fcm/send", request, FirebaseResponse.class);

            return CompletableFuture.completedFuture(firebaseResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
