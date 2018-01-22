package kr.co.cntt.api;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

/**
* Spring application context
* : spring application bootstrap시 실행. spring-root-context와 spring-application-context scope에 따라 처리할 일이 있을 떄 처리가능
* : springActiveProfile : 현재 실행중인 profile 모드를 설정
* - 로컬 : local, 개발 : dev, 운영1 : oper1, 운영2 : oper2
* : profile은 was jvm argument로 전달 (ex. -Dspring.profiles.active=local)
* 
* @author KIM HYUN SU
* @history <pre>
* -----------------------------------------------------------------------
* [변경일자]		[작성자]	[SR번호]		[주요 변경 내용]
* 2016.06.27						최초작성
* </pre>
*
*/
public class SpringApplicationContext implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {
	
	/** spring context */
	private static ApplicationContext CONTEXT;
	/** spring active profile */
	private static List<String> springActiveProfile;
	/** system enironment */
	@Autowired
	private Environment environment;
	
	/**
	 * constructor
	 */
	public SpringApplicationContext() {
	}
	
	/**
	 * application context setter
	 */
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		CONTEXT = context;
	}
	
	/**
	 * spring contex에 load된 bean객체를 꺼낸다
	 * @param beanName : 빈이름
	 * @return
	 */
	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}
	
	/**
	 * spring contex에 load된 bean객체를 꺼낸다
	 * @param beanName : 빈이름
	 * @return
	 */
	public static Object getBean(String beanName, Object ... args) {
		return CONTEXT.getBean(beanName, args);
	}
	
	/**
	 * spring contex에 load된 T type의 bean객체를 꺼낸다
	 * @param beanName : 빈이름
	 * @param requiredType : class type
	 * @return
	 */
	public static <T> T getBean(String beanName, Class<T> requiredType) {
		return CONTEXT.getBean(beanName, requiredType);
	}

	/**
	 * 현재 실행중인 active profile을 가져온다.
	 * @return the springActiveProfile
	 */
	public static String getActiveProfile() {
		return springActiveProfile.size() == 1 ? springActiveProfile.get(BigInteger.ZERO.intValue()) : null;
	}
	
	/**
	 * 현재 실행중인 active profile에 해당하는지 여부를 조회한다.
	 * @param profile
	 * @return
	 */
	public static boolean containsProfile(final String profile) {
		return springActiveProfile.contains(profile);
	}
	/**
	 * 해당 목록이 현재 active profile에 속해있는지 조회한다.
	 * @param profiles
	 * @return
	 */
	public static boolean containsProfiles(final String... profiles) {
		for (final String profile : profiles) {
			if (containsProfile(profile)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * application listener
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// bootstraping 시 context Refreshed event가 두번 발생함. root와, dispatcher servlet 이를 구분해서 처리해야한다.
		if (event instanceof ContextRefreshedEvent) {
			if (((ContextRefreshedEvent) event).getApplicationContext().getParent() == null) {
				// root context를 저장한다.
				CONTEXT = ((ContextRefreshedEvent) event).getApplicationContext();
				// 현재 실행중인 profile mode를 저장한다.
				if (environment.getActiveProfiles().length > 0) {
					springActiveProfile = Arrays.asList(environment.getActiveProfiles());
				} else {
					throw new RuntimeException("Active Profile missing in JVM Arguments");
				}
			}
		}
	}
}

