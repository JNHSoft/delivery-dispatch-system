package kr.co.cntt.core.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>kr.co.cntt.core.util
 * <p>ReflectionUtil.java
 * <p>Reflection
 * <p>유틸
 * @author JIN
 */
public class ReflectionUtil {
	/**
	 * <p>convertObjectToMap
	 * <p>전달받은 Object에 Field를 Map에 담아 리턴
	 * @param targetObject 타겟 오브젝트
	 * @return 맵<필드키, 필드값>
	 * @throws Exception
	 * @author JIN
	 */
	public static Map<String, String> convertObjectToMap(Object targetObject) throws Exception {
		Field[] fields = targetObject.getClass().getDeclaredFields();

		Map<String, String> resultMap = new HashMap<String, String>();
		for (Field field : fields) {
			field.setAccessible(true);
			resultMap.put(field.getName(), field.get(targetObject).toString());
		}

		return resultMap;
	}
	/**
	 * <p>getObjectDeclaredFieldValue
	 * <p>전달받은 키 배열로 Object 필드들의 값을 꺼내 담아 리턴
	 * @param t Type
	 * @param targetKeyArray 타겟 키 배열
	 * @return 타겟 값 배열
	 * @throws Exception
	 * @author JIN
	 */
	public static <T> String[] getObjectDeclaredFieldValue(T t, String[] targetKeyArray) throws Exception {
		Field field = null;
		String[] targetValueArray = new String[targetKeyArray.length];
		for (int i = 0; i < targetKeyArray.length; ++i) {
			field = t.getClass().getDeclaredField(targetKeyArray[i]);
			field.setAccessible(true);
			targetValueArray[i] = field.get(t).toString();
		}
		return targetValueArray;
	}
	/**
	 * <p>setObjectDeclaredFieldValue
	 * <p>전달받은 키, 값으로 Object 필드 SET
	 * @param t Type
	 * @param parameterMap 파라미터 맵
	 * @throws Exception
	 * @author JIN
	 */
	public static <T> void setObjectDeclaredFieldValue(T t, Map<String, String> parameterMap) throws Exception {
		Class<?> targetC = t.getClass();
		for (String key : parameterMap.keySet()) {
			Field field = targetC.getDeclaredField(key);
			field.setAccessible(true);
			field.set(t, parameterMap.get(key));
		}
	}
	/**
	 * <p>setObjectTargetValue
	 * <p>전달받은 키, 값으로 Object 필드 SET
	 * <p>not null 일때 한건 씩
	 * @param t Type
	 * @param target set할 필드
	 * @param value set할 값
	 * @throws Exception 
	 * @author JIN
	 */
	public static <T> void setObjectTargetValue(T t, String target, String value) throws Exception {
		if (target != null) {
			Class<?> targetC = t.getClass();
			Field field = targetC.getDeclaredField(target);
			field.setAccessible(true);
			field.set(t, value);
		}
	}
}
