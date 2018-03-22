package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.NoticeMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.deliverydispatch.service.StoreNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service("storeNoticeService")
public class StoreNoticeServiceImpl extends ServiceSupport implements StoreNoticeService {
    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;

    /**
     * Notice DAO
     */
    private NoticeMapper noticeMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * @param noticeMapper Notice D A O
     * @param storeMapper STORE D A O
     */
    @Autowired
    public StoreNoticeServiceImpl(NoticeMapper noticeMapper, StoreMapper storeMapper) {
        this.noticeMapper = noticeMapper;
        this.storeMapper = storeMapper;
    }

    @Override
    public Store getStoreInfo(Store store) {
        // 권한
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // store 가 조회
        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            // token 값 선언
            store.setAccessToken(store.getToken());
            store.setId("");
            store.setIsAdmin("");
            store.setRole("ROLE_STORE");
        }
        // 권한이 없는 다른 user 가 조회
        else if (authentication.getAuthorities().toString().matches(".*ROLE_USER.*")) {
            store.setAccessToken(null);
            store.setId("");
            store.setIsAdmin("");
        }

        // log 확인
        log.info(">>> token: " + store.getAccessToken());
        log.info(">>> token: " + store.getToken());

        // 리스트
        Store S_Store = storeMapper.selectStoreInfo(store);

        if (S_Store == null) {
            return null;
        }

        return S_Store;
    }

    @Override
    public List<Notice> getNoticeList(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            notice.setRole("ROLE_STORE");
        }

        List<Notice> S_Notice = noticeMapper.getStoreNoticeList(notice);

        if (S_Notice.size() == 0) {
            return Collections.emptyList();
        }

        return S_Notice;
    }

    @Override
    public Notice getNotice(Notice notice) {
        return null;
    }

    @Override
    public int putNoticeConfirm(Notice notice) {
        return 0;
    }

}
