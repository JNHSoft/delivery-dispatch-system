package kr.co.cntt.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import kr.co.cntt.core.exception.CnttBizException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class EhCacheService {

	private CacheManager cacheManager;

	public EhCacheService (CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	/**
	 * 전체 캐시 목록 조회
	 * @return
	 */
	public String[] getAllCacheList() throws CnttBizException{
		return cacheManager.getCacheNames();
	}
	
	/**
	 * 캐시 조회
	 * @param id : 캐시 id
	 * @return
	 * @throws CnttBizException
	 */
	public Map<Object, String> getCache(String id) throws CnttBizException{
		Cache cache = cacheManager.getCache(id);
		@SuppressWarnings("unchecked")
		List<Object> keys = cache.getKeys();
		Map<Object, String> result = new HashMap<Object, String>();
		for (Object key : keys) {
			result.put(key, new Gson().toJson(cache.get(key)));
		}
		return result;
	}
	
	/**
	 * 캐시삭제
	 * @param id
	 * @return
	 * @throws CnttBizException
	 */
	public boolean clearCache(String id) throws CnttBizException{
		Cache cache = cacheManager.getCache(id);
		if (cache != null) {
			cacheManager.getCache(id).removeAll();
		}
		return true;
	}
	
	public boolean clearAll() {
		cacheManager.clearAll();
		return true;
	}

	public static class EhCacheKey {
		public static final String MENU_BASE_CACHE = "menuBaseCache";
		public static final String MENU_CLASS_CACHE = "menuClassCache";
		public static final String MENU_PRODUCT_CACHE = "menuProductCache";
		public static final String MENU_CONDIMENT_CACHE = "menuCondimentCache";
		public static final String STORE_CACHE = "storeCache";
		public static final String SET_MENU_CACHE = "setMenuCache";
		public static final String MAIN_BANNER_CACHE = "mainBannerCache";
	}

}
