package kr.co.cntt.api.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.api.AdminService;
import kr.co.cntt.core.service.api.RiderService;
import kr.co.cntt.core.service.api.StoreService;
import kr.co.cntt.core.service.api.TrackerService;
import kr.co.cntt.core.util.AES256Util;
import kr.co.cntt.core.util.CustomEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthentificationTokenFilter extends OncePerRequestFilter {

    @Value("${api.tracker.key}")
    private String tKey;

    @Autowired
    private CustomAuthentificateService customAuthentificateService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private RiderService riderService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TrackerService trackerService;

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, UsernameNotFoundException {
        RequestWrapper request;
        String requestUri = servletRequest.getRequestURI();
        log.debug("======= api request uri : {}", requestUri);

        User userInfo = null;

        if (requestUri.startsWith("/API") && !(requestUri.contains("getToken.do")) && !(requestUri.contains("putToken.do"))
                && !(requestUri.contains("versionCheck.do")) && !requestUri.contains("getTracker.do") && !(requestUri.contains("getSignUpDefaultInfo.do"))) {
            try {
                //log.debug("======= try");
                request = new RequestWrapper(servletRequest);
                //log.debug("======= request.getJsonBody() : {}", request.getJsonBody());
                JsonObject extractJbody = extractJbody(request.getJsonBody());
                String authToken = extractJbody.get("token").getAsString();
                String authLevel = extractJbody.get("level").getAsString();

                log.debug("======= authToken : {}", authToken);
                log.debug("======= authLevel : {}", authLevel);

                String username = tokenManager.getUsernameFromToken(authToken);

                log.debug("======= username : {}", username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Rider 쪽 Login ID  및 AccessToken 값 체크 Start
                    Rider riderInfo = new Rider();
                    Store storeInfo = new Store();
                    Admin adminInfo = new Admin();
                    User trackerInfo = new User();

                    if (authLevel.equals("3")) {
                        riderInfo.setAccessToken(authToken);
                        riderInfo.setLoginId(username);

                        userInfo = riderService.selectRiderTokenLoginCheck(riderInfo);

                        //					if(checkRiderCount < 1){
                        //						throw new UsernameNotFoundException("No found for username or token ");
                        //					}
                        // Rider 쪽 Login ID  및 AccessToken 값 체크 End
                    } else if (authLevel.equals("2")) {
                        storeInfo.setAccessToken(authToken);
                        storeInfo.setLoginId(username);

                        userInfo = storeService.selectStoreTokenLoginCheck(storeInfo);
                    } else if (authLevel.equals("1")) {
                        adminInfo.setAccessToken(authToken);
                        adminInfo.setLoginId(username);

                        userInfo = adminService.selectAdminTokenLoginCheck(adminInfo);
                    } else if (authLevel.equals("4")) {
                        trackerInfo.setAccessToken(authToken);
                        trackerInfo.setLoginId(username);

                        userInfo = trackerService.selectTrackerTokenLoginCheck(trackerInfo);
                    }

//                    log.debug("=======> userInfo.getLoginId() : {}", userInfo.getLoginId());
                    if(userInfo != null) {
                        if (username.equals(userInfo.getLoginId())) {
                            ActorDetails actorDetails = this.customAuthentificateService.loadUserCustomByUsername(username);

                            if (actorDetails == null) {
                                //                            log.debug("=======> actorDetails  null");
                                //                            Actor actor = new Actor(username, username);
                                //                            Actor actor = new Actor(username, username, authLevel);
                                //                            actorDetails = new ActorDetails(actor, null);
                                Actor actor = customAuthentificateService.createActor(userInfo.getLoginId(), userInfo.getLoginPw(), authLevel);
                                actorDetails = customAuthentificateService.loadUserByUsername(actor.getLoginId());
                            }

                            if (!username.equals(actorDetails.getUsername())) {
                                //                            log.debug("=======> !username.equals(actorDetails.getUsername()) ");
                                Actor actor = customAuthentificateService.createActor(userInfo.getLoginId(), userInfo.getLoginPw(), authLevel);
                                actorDetails = customAuthentificateService.loadUserByUsername(actor.getLoginId());
                            }

                            log.debug("======= actorDetails.getUsername() : {}", actorDetails.getUsername());
                            if (tokenManager.validateCustomToken(authToken, actorDetails)) {
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        actorDetails, null, actorDetails.getAuthorities());
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                logger.info("authenticated device " + username + ", setting security context");
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            }
                        }
                    }
                }

                // TODO : filter chain stop 시점을 찾아야한다. 오류인 경우 어떻게 할지.. exception 공통구현 필요
                chain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestUri.contains("getTracker.do")) {
            try {
                request = new RequestWrapper(servletRequest);
                AES256Util aesUtil = new AES256Util(tKey);

//                String decParam = aesUtil.aesDecode(request.getParameter("encParam"));
//                Map<String, String> query_pairs = new LinkedHashMap<>();
//                String[] pairs = decParam.split("&");
//                for (String pair : pairs) {
//                    int idx = pair.indexOf("=");
//                    query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
//                }
//
//                String authToken = query_pairs.get("token");
//                String authLevel = query_pairs.get("level");

                String decParam = aesUtil.aesDecode(CustomEncryptUtil.decodeBase64(request.getParameter("encParam")));

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(decParam);

                String authToken = jsonObject.get("token").toString();
                String authLevel = jsonObject.get("level").toString();

                log.debug("======= authToken : {}", authToken);
                log.debug("======= authLevel : {}", authLevel);

                String username = tokenManager.getUsernameFromToken(authToken);

                log.debug("======= username : {}", username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    User trackerInfo = new User();

                    if (authLevel.equals("4")) {
                        trackerInfo.setAccessToken(authToken);
                        trackerInfo.setLoginId(username);

                        userInfo = trackerService.selectTrackerTokenLoginCheck(trackerInfo);
                    }

                    log.debug("=======> userInfo.getLoginId() : {}", userInfo.getLoginId());
                    if (username.equals(userInfo.getLoginId())) {
                        ActorDetails actorDetails = this.customAuthentificateService.loadUserCustomByUsername(username);

                        if (actorDetails == null) {
                            //Actor actor = new Actor(username, username, authLevel);
                            //actorDetails = new ActorDetails(actor, null);

                            Actor actor = customAuthentificateService.createActor(userInfo.getLoginId(), userInfo.getLoginPw(), authLevel);
                            actorDetails = customAuthentificateService.loadUserByUsername(actor.getLoginId());
                        }

                        if(!username.equals(actorDetails.getUsername())){
//                            log.debug("=======> !username.equals(actorDetails.getUsername()) ");
                            Actor actor = customAuthentificateService.createActor(userInfo.getLoginId(), userInfo.getLoginPw(), authLevel);
                            actorDetails = customAuthentificateService.loadUserByUsername(actor.getLoginId());
                        }

                        log.debug("======= actorDetails : {}", actorDetails);
                        if (tokenManager.validateCustomToken(authToken, actorDetails)) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    actorDetails, null, actorDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            logger.info("authenticated device " + username + ", setting security context");
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }

                // TODO : filter chain stop 시점을 찾아야한다. 오류인 경우 어떻게 할지.. exception 공통구현 필요
                chain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            chain.doFilter(servletRequest, response);
        }
    }

//    public static String getHeadersToken(HttpServletRequest servletRequest) {
//        if (servletRequest == null) {
//            return "";
//        }
//
//        String accessToken = servletRequest.getHeader("token");
//
//        return accessToken;
//    }


//    public static String extractToken(String request) {
//        if (request == null || "".equals(request)) {
//            return request;
//        }
//        log.debug("request : {}", request);
//        JsonObject json = new JsonParser().parse(request).getAsJsonObject();
//
//        JsonObject jheader = json.getAsJsonArray("header").get(0).getAsJsonObject();
//        return jheader.get("token").getAsString();
//    }

    public static JsonObject extractJbody(String request) {
        if (request == null || "".equals(request)) {
            return null;
        }
        log.debug("request : {}", request);
        JsonObject json = new JsonParser().parse(request).getAsJsonObject();

        JsonObject jbody = json.getAsJsonObject("body").getAsJsonObject();

        JsonObject jheader_obj = new JsonObject();
        jheader_obj.addProperty("token", jbody.get("token").getAsString());
        jheader_obj.addProperty("level", jbody.get("level").getAsString());
        return jheader_obj;
    }
}
