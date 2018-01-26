package kr.co.cntt.api.security;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.service.ServiceSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// TODO : service key property로 읽어서 list로 가지고 있자!!
public class CustomAuthentificateService extends ServiceSupport implements UserDetailsService {

	protected static ConcurrentHashMap<String, ActorDetails> userDataBase = new ConcurrentHashMap<String, ActorDetails>();


	public Actor createActor(String loginId, String loginPw, String level) throws Exception {
		/*
		if (!isExistKey(serviceKey)) {
			throw new AppTrException(getMessage(ErrorCodeEnum.A0001), ErrorCodeEnum.A0001.name());
		}
		*/
//		Actor actor = new Actor(loginId, loginPw);
		Actor actor = new Actor(loginId, loginPw, level);
		ActorDetails actorDetails = new ActorDetails(actor, null);
		actorDetails.setPassword(new BCryptPasswordEncoder().encode(actorDetails.getPassword()));
		//userDataBase.put(ip, actorDetails);
		userDataBase.put(loginId, actorDetails);
		log.info("[CustomAuthentificateService][createActor][userDataBase size] : {}", userDataBase.keySet().size());
		return actor;
	}

	@Override
	public ActorDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		log.debug("=========== loadUserByUsername param: "+loginId);
		ActorDetails actorDetails = null;
		for (String key : userDataBase.keySet()) {
			actorDetails = userDataBase.get(key);
			if (loginId.equals(actorDetails.getActor().getLoginId())) {
				return actorDetails;
			}
		}
		if (actorDetails == null) {
			throw new UsernameNotFoundException("No Rider found for username " + loginId);
		}
		return actorDetails;
	}

	public ActorDetails loadUserCustomByUsername(String loginId) throws UsernameNotFoundException {
		log.debug("=========== loadUserByUsername param: "+loginId);
		ActorDetails actorDetails = null;
		for (String key : userDataBase.keySet()) {
			actorDetails = userDataBase.get(key);
			if (loginId.equals(actorDetails.getActor().getLoginId())) {
				return actorDetails;
			}
		}
		return actorDetails;
	}

}
