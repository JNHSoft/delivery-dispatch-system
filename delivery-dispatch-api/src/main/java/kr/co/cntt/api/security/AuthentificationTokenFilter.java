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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

        if (requestUri.startsWith("/API") && !(requestUri.contains("getToken.do")) && !(requestUri.contains("putToken.do")) && !(requestUri.contains("versionCheck.do"))) {
            try {
                //log.debug("======= try");
                request = new RequestWrapper(servletRequest);
                //log.debug("======= request.getJsonBody() : {}", request.getJsonBody());
                JsonObject extractJbody = extractJbody(request.getJsonBody());
                String authToken = extractJbody.get("token").getAsString();
                String authLevel = extractJbody.get("level").getAsString();

//                String authToken = extractToken(request.getJsonBody());
//                String authToken = getHeadersToken(servletRequest);
//				String authToken = request.getHeader("token");
//                String authLevel = request.getHeader("level");
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
                    int checkUserCount = 0;

                    if (authLevel.equals("3")) {
                        riderInfo.setAccessToken(authToken);
                        riderInfo.setLoginId(username);

                        checkUserCount = riderService.selectRiderTokenCheck(riderInfo);

                        //					if(checkRiderCount < 1){
                        //						throw new UsernameNotFoundException("No found for username or token ");
                        //					}
                        // Rider 쪽 Login ID  및 AccessToken 값 체크 End
                    } else if (authLevel.equals("2")) {
                        storeInfo.setAccessToken(authToken);
                        storeInfo.setLoginId(username);

                        checkUserCount = storeService.selectStoreTokenCheck(storeInfo);
                    } else if (authLevel.equals("1")) {
                        adminInfo.setAccessToken(authToken);
                        adminInfo.setLoginId(username);

                        checkUserCount = adminService.selectAdminTokenCheck(adminInfo);
                    } else if (authLevel.equals("4")) {
                        trackerInfo.setAccessToken(authToken);
                        trackerInfo.setLoginId(username);

                        checkUserCount = trackerService.selectTrackerTokenCheck(trackerInfo);
                    }

                    log.debug("=======> checkUserCount : {}", checkUserCount);
                    if (checkUserCount > 0) {
                        ActorDetails actorDetails = this.customAuthentificateService.loadUserCustomByUsername(username);

                        if (actorDetails == null) {
//                            Actor actor = new Actor(username, username);
                            Actor actor = new Actor(username, username, authLevel);
                            actorDetails = new ActorDetails(actor, null);
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
