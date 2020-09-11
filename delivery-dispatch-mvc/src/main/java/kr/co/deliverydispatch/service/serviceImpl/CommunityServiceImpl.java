package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.service.ServiceSupport;
import kr.co.deliverydispatch.service.CommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service("communitySerivce")
public class CommunityServiceImpl extends ServiceSupport implements CommunityService {

    @Override
    public String sendPostApiServer(String sendUrl, Map<String, String> map) {

        String inputLine = null;
        StringBuffer outResult = new StringBuffer();

        try {
            log.debug("REST API START");

            URL url = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            StringBuffer buffer = new StringBuffer();

            // form Data 정렬
            if (map != null){
                Set key = map.keySet();

                for (Iterator iterator = key.iterator(); iterator.hasNext();){
                    String keyName = (String) iterator.next();
                    String valueName = map.get(keyName);

                    buffer.append(keyName).append("=").append(valueName).append("&");
                }
            }


            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(os);
            writer.write(buffer.toString());
            writer.flush();

            // 결과값을 리턴을 받는다.
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while ((inputLine = in.readLine()) != null){
                outResult.append(inputLine);
            }

            conn.disconnect();
            log.debug("REST API END");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return outResult.toString();
    }
}
