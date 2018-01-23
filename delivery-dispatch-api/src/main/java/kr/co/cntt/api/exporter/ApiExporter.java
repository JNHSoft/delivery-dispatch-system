package kr.co.cntt.api.exporter;

import static kr.co.cntt.api.exporter.Api.GET_TOKEN;
import static kr.co.cntt.api.exporter.Api.Path;
import static kr.co.cntt.api.exporter.Api.SET_SERVICE_KEY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
    private CustomAuthentificateService customAuthentificateService;
    private AuthenticationManager authenticationManager;
    private TokenManager tokenManager;

    public ApiExporter(){}

    @Autowired
    public ApiExporter(CustomAuthentificateService customAuthentificateService
            , AuthenticationManager authenticationManager
            , TokenManager tokenManager) {
        this.customAuthentificateService = customAuthentificateService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
    }

    /**
     * 서비스 키등록
     * 오류시 body 전문이 다른 전문과 상이하게 규약되어있음(기회가 되면 맞추시길...). 맞춰줘야함.
     * @param reqeust : serviceKey 토큰에 생성 시 사용할 서비스키
     * @param device
     * @return List<R_TR_A0001> json string
     * @throws Exception
     */
    //@GetMapping(value = SET_SERVICE_KEY)
    @RequestMapping(value = SET_SERVICE_KEY)
    public ResponseEntity<String> setServiceKey(HttpServletRequest reqeust, @RequestParam String keyname, Device device) throws Exception {
        //List<R_TR_A0001> response = new ArrayList<R_TR_A0001>();
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            if (keyname == null) {
                throw new AppTrException(getMessage(ErrorCodeEnum.A0002), ErrorCodeEnum.A0002.name());
            }
            result.put("result", CODE_SUCCESS);
            // proerty에 등록된 서비스 키만 허용한다. 이 api는 의미없다.
            //data.put("servicekey", ipBasedAuthentificateService.registerServiceKey(keyname));
            response.add(result);
            response.add(data);
            //response.add(new R_TR_A0001(CODE_SUCCESS, ipBasedAuthentificateService.registerServiceKey(keyname)));
            // Return the token
            return ResponseEntity.ok(new Gson().toJson(response).toString());
        } catch (Exception e) {
            result.put("result", CODE_ERROR);
            data.put("servicekey", keyname);
            data.put("msg", e.getLocalizedMessage());
            response.add(result);
            response.add(data);
            //response.add(new R_TR_A0001(CODE_ERROR, keyname, e.getLocalizedMessage()));
            return ResponseEntity.ok(new Gson().toJson(response).toString());
        }
    }

    /**
     * 토큰 생성 요청 전문
     * @param request : serviceKey 토큰에 생성 시 사용할 서비스키
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
    public ResponseEntity<?> execute(@PathVariable String service, @RequestBody String jsonStr) throws AppTrException{
        try {
            return trServiceInvoker(ApiServiceRouter.service(service), jsonStr);
        } catch (Exception e) {
            return responseError(null, e);
        }
    }

}
