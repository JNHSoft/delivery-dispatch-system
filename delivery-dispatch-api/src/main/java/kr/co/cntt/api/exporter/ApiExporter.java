package kr.co.cntt.api.exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import kr.co.cntt.api.security.Actor;
import kr.co.cntt.api.security.ActorDetails;
import kr.co.cntt.api.security.CustomAuthentificateService;
import kr.co.cntt.api.security.TokenManager;
import kr.co.cntt.api.service.ApiServiceRouter;
import kr.co.cntt.core.api.model.CommonBody;
import kr.co.cntt.core.api.model.GenericResponse;
import kr.co.cntt.core.api.model.ResponseHeader;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.tracker.Tracker;
import kr.co.cntt.core.service.api.*;
import kr.co.cntt.core.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.config.TypeFilterParser;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static kr.co.cntt.api.exporter.Api.Path;


@Slf4j
@RestController
@RequestMapping(Path)
public class ApiExporter extends ExporterSupportor implements Api {

    /**
     * 객체 주입
     */
    private RiderService riderService;
    private StoreService storeService;
    private AdminService adminService;
    private TrackerService trackerService;
    private OrderService orderService;
    private CustomAuthentificateService customAuthentificateService;
    private AuthenticationManager authenticationManager;
    private TokenManager tokenManager;
    private FileUtil fileUtil;

    public ApiExporter(){}

    @Autowired
    public ApiExporter(CustomAuthentificateService customAuthentificateService
            , AuthenticationManager authenticationManager
            , TokenManager tokenManager
            , RiderService riderService
            , StoreService storeService
            , AdminService adminService
            , TrackerService trackerService
            , OrderService orderService
            , FileUtil fileUtil){
        this.customAuthentificateService = customAuthentificateService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.riderService = riderService;
        this.storeService = storeService;
        this.adminService = adminService;
        this.trackerService = trackerService;
        this.orderService = orderService;
        this.fileUtil = fileUtil;
    }

    /**
     * 토큰 생성 요청 전문
     * @param request
     * @param loginId
     * @param loginPw
     * @param device
     * @return
     * @throws Exception
     */
    //@GetMapping(value = GET_TOKEN)
    @RequestMapping(value = GET_TOKEN)
    public ResponseEntity<?> createAuthenticate(HttpServletRequest request, @RequestParam String level, @RequestParam String loginId, @RequestParam String loginPw, Device device) throws Exception {
//        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();

        Map<String, String> userSelectLoginMap = new HashMap<>();
        Rider riderInfo = new Rider();
        Store storeInfo = new Store();
        Admin adminInfo = new Admin();
        User trackerInfo = new User();

        try {
            if (level.equals("3")) {
                riderInfo.setLoginId(loginId);
                riderInfo.setLoginPw(loginPw);
                userSelectLoginMap = riderService.selectLoginRider(riderInfo);
            } else if (level.equals("2")) {
                storeInfo.setLoginId(loginId);
                storeInfo.setLoginPw(loginPw);
                userSelectLoginMap = storeService.selectLoginStore(storeInfo);
            } else if (level.equals("1")) {
                adminInfo.setLoginId(loginId);
                adminInfo.setLoginPw(loginPw);
                userSelectLoginMap = adminService.selectLoginAdmin(adminInfo);
            } else if (level.equals("4")) {
                trackerInfo.setLoginId(loginId);
                trackerInfo.setLoginPw(loginPw);
                userSelectLoginMap = trackerService.selectLoginTracker(trackerInfo);
            }

            log.info("===> [createAuthenticate RequestParam][loginId : {}]", loginId);
            log.info("===> [createAuthenticate RequestParam][loginPw : {}]", loginPw);
            log.info("===> [createAuthenticate RequestParam][userLoginId : {}]", userSelectLoginMap.get("loginId"));

            if (!loginId.equals(userSelectLoginMap.get("loginId"))) {
                // 로그인 정보가 다르다.
                //throw new AppTrException(getMessage(ErrorCodeEnum.S0001), ErrorCodeEnum.S0001.name());
                throw new AppTrException("로그인정보가 다릅니다.", "LOERR");
            }

//            Actor actor = customAuthentificateService.createActor(loginId, loginPw);
            Actor actor = customAuthentificateService.createActor(loginId, loginPw, level);

            if (actor == null) {
                throw new AppTrException(getMessage(ErrorCodeEnum.A0003), ErrorCodeEnum.A0003.name());
            }
            final Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(actor.getLoginId(), actor.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final ActorDetails actorDetails = customAuthentificateService.loadUserByUsername(actor.getLoginId());

            String token = null;
            if (userSelectLoginMap.get("accessToken") == null || userSelectLoginMap.get("accessToken").equals("")) {
                token = tokenManager.generateCustomToken(actorDetails, device);
            } else {
                token = userSelectLoginMap.get("accessToken");
            }

            log.info("[AppApiExporter][createAuthenticate][actor][loginId : {}]", actor.getLoginId());
            log.info("[AppApiExporter][createAuthenticate][actor][loginPw : {}]", actor.getLoginPw());
            log.info("[AppApiExporter][createAuthenticate][actor][uuid : {}]", actor.getUsername());
            //log.info("[AppApiExporter][createAuthenticate][actor][ip : {}]", actor.getIp());
            log.info("[AppApiExporter][createAuthenticate][actor][time : {}]", actor.getTime());
            log.info("[AppApiExporter][createAuthenticate][actor][token : {}]", token);

            /*
            final Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(actor.getUuid(), actor.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final ActorDetails actorDetails = customAuthentificateService.loadUserByUsername(actor.getUuid());
            final String token = tokenManager.generateToken(actorDetails, device);
            log.info("[AppApiExporter][createAuthenticate][actor][loginId : {}]", actor.getLoginId());
            log.info("[AppApiExporter][createAuthenticate][actor][loginPw : {}]", actor.getLoginPw());
            log.info("[AppApiExporter][createAuthenticate][actor][uuid : {}]", actor.getUsername());
            //log.info("[AppApiExporter][createAuthenticate][actor][ip : {}]", actor.getIp());
            log.info("[AppApiExporter][createAuthenticate][actor][time : {}]", actor.getTime());
            log.info("[AppApiExporter][createAuthenticate][actor][token : {}]", token);
            */
            result.put("result", CODE_SUCCESS);
            data.put("token", token);

            response.put("result", CODE_SUCCESS);
            response.put("token", token);

            // Token을 Insert
            if (level.equals("3")) {
                if (userSelectLoginMap.get("accessToken") == null || userSelectLoginMap.get("accessToken").equals("")) {
                    riderInfo.setAccessToken(token);
                    riderService.insertRiderSession(riderInfo);
                }
            } else if (level.equals("2")) {
                if (userSelectLoginMap.get("accessToken") == null || userSelectLoginMap.get("accessToken").equals("")) {
                    storeInfo.setAccessToken(token);
                    int getTokenResult = storeService.insertStoreSession(storeInfo);
                }
            } else if (level.equals("1")) {
                if (userSelectLoginMap.get("accessToken") == null || userSelectLoginMap.get("accessToken").equals("")) {
                    adminInfo.setAccessToken(token);
                    adminService.insertAdminSession(adminInfo);
                }
            } else if (level.equals("4")) {
                if (userSelectLoginMap.get("accessToken") == null || userSelectLoginMap.get("accessToken").equals("")) {
                    trackerInfo.setAccessToken(token);
                    trackerService.insertTrackerSession(trackerInfo);
                }
            }

            return ResponseEntity.ok(new Gson().toJson(response).toString());
        } catch(Exception e) {
            result.put("result", CODE_ERROR);
            data.put("token", "");
            data.put("msg", e.getLocalizedMessage());
            response.put("result", CODE_ERROR);
            response.put("token", "");
            response.put("msg", e.getLocalizedMessage());
            return ResponseEntity.ok(new Gson().toJson(response).toString());
        }
    }

    @PostMapping(value="/tokenValid")
    public ResponseEntity<?> tokenValid(@RequestBody  kr.co.cntt.core.api.model.GenericRequest<Void> request, Device device) throws Exception {
        // 여기 들어왔다는건 토큰이 valid 하다.
        try {
            // 서비스 로직 후 응답처리
            return ResponseEntity.ok(new CommonBody<Map<String, String>>(CODE_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonBody<String>(CODE_ERROR, e.getLocalizedMessage(), null));
        }

    }

    /** Token 만료일 설정은 관리자 및 스토어 페이지에서 가능 */
//    @RequestMapping(value = PUT_TOKEN)
//    public ResponseEntity<?> expiryToken(HttpServletRequest request, @RequestParam String level, @RequestParam String token) throws Exception {
////        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
//        Map<String, Object> response = new HashMap<String, Object>();
//        Map<String, Object> result = new HashMap<String, Object>();
//        Map<String, Object> data = new HashMap<String, Object>();
//
//        try {
//
//            if (level.equals("3")) {
//                riderService.updateRiderSession(token);
//            } else if (level.equals("2")) {
//                storeService.updateStoreSession(token);
//            } else if (level.equals("1")) {
//                adminService.updateAdminSession(token);
//            }
//
//            result.put("result", CODE_SUCCESS);
//            response.put("result", CODE_SUCCESS);
//
//            return ResponseEntity.ok(new Gson().toJson(response).toString());
//        } catch(Exception e) {
//            result.put("result", CODE_ERROR);
//            data.put("token", token);
//            data.put("msg", e.getLocalizedMessage());
//            response.put("result", CODE_ERROR);
//            response.put("token", token);
//            response.put("msg", e.getLocalizedMessage());
////            response.add(result);
////            response.add(data);
//            return ResponseEntity.ok(new Gson().toJson(response).toString());
//        }
//    }

    /**
     * 가입 페이지 기본 정보
     * getSignUpDefaultInfo.do
     * */
    @RequestMapping(value = SIGN_UP_DEFAULT_INFO)
    public ResponseEntity<?> getSignUpDefaultInfo(HttpServletRequest request) throws Exception{
        CommonBody<Map<String, List<Store>>> response = new CommonBody<>(CODE_SUCCESS);

        Map<String, List<Store>> data = new HashMap<>();

        try {
            List<Store> store = riderService.selectAllStore();
            List<Store> kfcStore = store.parallelStream()
                    .filter(x -> x.getBrandCode().equals("1"))
                    .collect(Collectors.toList());
            List<Store> pzhStore = store.parallelStream()
                    .filter(x -> x.getBrandCode().equals("0"))
                    .collect(Collectors.toList());

            data.clear();
            data.put("kfcStore", kfcStore);
            data.put("pzhStore", pzhStore);

            response.setCode(data);


            return ResponseEntity.ok(new Gson().toJson(response, response.getClass()).toString());
        } catch(Exception e) {
            Map<String, String> errDescription = new HashMap<>();


            errDescription.put("error_code", CODE_SYSTEM_ERROR);
            errDescription.put("error", e.getLocalizedMessage());

            response = new CommonBody<>(CODE_ERROR);
            response.setError_desc(errDescription);

            return ResponseEntity.ok(new Gson().toJson(response).toString());
        }
    }

    /**
     * Generic api controller 모든 request 를 서비스로 구분하여 generic하게 처리한다. 모든 response
     * type을 지원한다.
     *
     * @param service
     * @param request
     * @return
     */
    /*
     * @RequestMapping(value="/{service}", method = {RequestMethod.GET}) public
     * ResponseEntity<?> execute(@PathVariable String service,
     * HttpServletRequest request) { return
     * trServiceInvoker(PosServiceRouter.service(service), null); }
     */

    /**
     * Generic api controller 모든 request 를 서비스로 구분하여 generic하게 처리한다. 모든 response
     * type을 지원한다.
     * @param service
     * @param jsonStr
     * @return
     */
//    @PostMapping(value = {"/{service}", "/admin/{service}"})
    @PostMapping(value = "/{service}")
    public ResponseEntity<?> execute(HttpServletRequest request, @PathVariable String service, @RequestBody String jsonStr) throws AppTrException{

        System.out.println("execute Service = " + service);


        try {
            return trServiceInvoker(ApiServiceRouter.service(service), jsonStr, request);
        } catch (Exception e) {
            return responseError(null, e);
        }
    }


    @GetMapping(value = VERSION_CHECK)
    public ResponseEntity<?> versionCheck(HttpServletRequest request) throws Exception {
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> min = new HashMap<String, Object>();
        Map<String, Object> recommand = new HashMap<String, Object>();

        String userAgent = request.getHeader("user-agent");
        String[] os = {"android", "ios", "macintosh", "windows"};

        String device = null;
        for (int i = 0; i < os.length; i++) {
            if(userAgent.matches(".*"+os[i]+".*")){
                device = os[i];
                break;
            }
        }

        // 테스트용 - device android set
        if (device == null) {
            device = os[0];
        }

        if (device != null) {
            String version = riderService.getMobileVersion(device);
            response.put("result", CODE_SUCCESS);
            response.put("version", version);
        } else {
            response.put("result", CODE_ERROR);
        }


//        min.put("android", null);
//        min.put("ios", null);
//        min.put("pc", null);

//        recommand.put("android", null);
//        recommand.put("ios", null);
//        recommand.put("pc", null);

//        response.put("result", CODE_SUCCESS);
//        response.put("min", min);
//        response.put("recommanded", recommand);

        Gson gson = new GsonBuilder().serializeNulls().create();

        return ResponseEntity.ok(gson.toJson(response).toString());

    }

    @GetMapping(value = TRACKER_GET)
    public ResponseEntity<?> getTracker(@RequestParam String encParam) throws AppTrException {
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();

        try {
            Tracker trackerResult = trackerService.getTracker(encParam);

            result.put("result", CODE_SUCCESS);
            data.put("tracker", trackerResult);

            response.put("result", CODE_SUCCESS);
            response.put("tracker", trackerResult);

            Gson gson = new GsonBuilder().serializeNulls().create();

            return ResponseEntity.ok(gson.toJson(response).toString());
        } catch (Exception e) {
            return responseError(null, e);
        }
    }

}
