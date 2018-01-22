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
public class IPBasedAuthentificateService extends ServiceSupport implements UserDetailsService {

	protected static ConcurrentHashMap<String, ActorDetails> userDataBase = new ConcurrentHashMap<String, ActorDetails>();
	@Value("${app.servicekeys.key}")
	private String[] serviceKeyArr;

	@PostConstruct
	public void serviceKeys() {
		for (int i=0; i < serviceKeyArr.length;i++) {
			log.debug("service key array {} : {}", i, serviceKeyArr[i]);
		}
	}
	public Actor createActor(String ip, String serviceKey) throws Exception {
		if (!isExistKey(serviceKey)) {
			throw new AppTrException(getMessage(ErrorCodeEnum.A0001), ErrorCodeEnum.A0001.name());
		}
		Actor actor = new Actor(ip, serviceKey);
		ActorDetails actorDetails = new ActorDetails(actor, null);
		actorDetails.setPassword(new BCryptPasswordEncoder().encode(actorDetails.getPassword()));
		userDataBase.put(ip, actorDetails);
		log.info("[IPBasedAuthentificateService][createActor][userDataBase size] : {}", userDataBase.keySet().size());
		return actor;
	}

	@Override
	public ActorDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
		ActorDetails actorDetails = null;
		for (String key : userDataBase.keySet()) {
			actorDetails = userDataBase.get(key);
			if (uuid.equals(actorDetails.getActor().getUuid())) {
				return actorDetails;
			}
		}
		if (actorDetails == null) {
			throw new UsernameNotFoundException("No user found for username " + uuid);
		}
		return actorDetails;
	}
	
	public boolean isExistKey(final String serviceKey) {
		boolean isExist = false;
		for (int i=0; i < serviceKeyArr.length;i++) {
			log.debug("service key array {} : {}", i, serviceKeyArr[i]);
			if (serviceKey.equals(serviceKeyArr[i])) {
				isExist = true;
				break;
			}
		}
		log.debug("is valid service key : {}", isExist);
		return isExist;
	}
}
