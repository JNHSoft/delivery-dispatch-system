package kr.co.deliverydispatch.service;

import java.util.Map;

public interface CommunityService {
    String sendPostApiServer(String sendUrl, Map<String, String> map);
}
