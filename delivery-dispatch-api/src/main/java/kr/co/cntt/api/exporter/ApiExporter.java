package kr.co.cntt.api.exporter;

import static kr.co.cntt.api.exporter.Api.GET_TOKEN;
import static kr.co.cntt.api.exporter.Api.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.service.api.RiderService;
import kr.co.cntt.core.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import kr.co.cntt.api.security.Actor;
import kr.co.cntt.api.security.ActorDetails;
import kr.co.cntt.api.security.CustomAuthentificateService;
import kr.co.cntt.api.security.TokenManager;
import kr.co.cntt.api.service.ApiServiceRouter;
import kr.co.cntt.core.api.model.CommonBody;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.util.AgentUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping(Path)
public class ApiExporter extends ExporterSupportor implements Api {
    /**
     * 객체 주입
     */
    private RiderService riderService;
    private CustomAuthentificateService customAuthentificateService;
    private AuthenticationManager authenticationManager;
    private TokenManager tokenManager;

    public ApiExporter(){}

    @Autowired
    public ApiExporter(CustomAuthentificateService customAuthentificateService
            , AuthenticationManager authenticationManager
            , TokenManager tokenManager
            , RiderService riderService){
        this.customAuthentificateService = customAuthentificateService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.riderService = riderService;
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
    public ResponseEntity<?> createAuthenticate(HttpServletRequest request, @RequestParam String loginId, @RequestParam String loginPw, Device device) throws Exception {
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();

        try {
            Rider riderInfo = new Rider();
            riderInfo.setLoginId(loginId);
            riderInfo.setLoginPw(loginPw);
            String userLoginId = riderService.selectLoginRider(riderInfo);

            log.info("===> [createAuthenticate RequestParam][loginId : {}]", loginId);
            log.info("===> [createAuthenticate RequestParam][loginPw : {}]", loginPw);
            log.info("===> [createAuthenticate RequestParam][userLoginId : {}]", userLoginId);


            if (!loginId.equals(userLoginId)) {
                // 로그인 정보가 다르다.
                //throw new AppTrException(getMessage(ErrorCodeEnum.S0001), ErrorCodeEnum.S0001.name());
                throw new AppTrException("로그인정보가 다릅니다.", "LOERR");
            }

            /*
            if ("".equals(keyname)) {
                throw new AppTrException(getMessage(ErrorCodeEnum.A0002), ErrorCodeEnum.A0002.name());
            }
            */
            //Actor actor = customAuthentificateService.createActor(loginId, loginPw, AgentUtil.getIp(request), keyname);
            Actor actor = customAuthentificateService.createActor(loginId, loginPw);
            if (actor == null) {
                throw new AppTrException(getMessage(ErrorCodeEnum.A0003), ErrorCodeEnum.A0003.name());
            }
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
            result.put("result", CODE_SUCCESS);
            data.put("token", token);
            response.add(result);
            response.add(data);
            //response.add(new R_TR_A0002(CODE_SUCCESS, token));
            // Return the token

            // Token을 Insert
            riderInfo.setAccessToken(token);
            riderService.insertRiderSession(riderInfo);

            return ResponseEntity.ok(new Gson().toJson(response).toString());
        } catch(Exception e) {
            result.put("result", CODE_ERROR);
            data.put("token", "");
            data.put("msg", e.getLocalizedMessage());
            response.add(result);
            response.add(data);
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
            return ResponseEntity.ok(new CommonBody<String>(CODE_ERROR, e.getLocalizedMessage()));
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
    @PostMapping(value = "/{service}")
    public ResponseEntity<?> execute(HttpServletRequest request, @PathVariable String service, @RequestBody String jsonStr) throws AppTrException{
        try {
            return trServiceInvoker(ApiServiceRouter.service(service), jsonStr, request);
        } catch (Exception e) {
            return responseError(null, e);
        }
    }

}
