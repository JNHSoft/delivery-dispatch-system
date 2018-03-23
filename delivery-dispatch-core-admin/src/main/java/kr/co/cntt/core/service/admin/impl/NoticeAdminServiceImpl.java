package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.NoticeMapper;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.admin.NoticeAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("noticeAdminService")
public class NoticeAdminServiceImpl implements NoticeAdminService {

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
     * @param noticeMapper Notice D A O
     */
    @Autowired
    public NoticeAdminServiceImpl(NoticeMapper noticeMapper) { this.noticeMapper = noticeMapper; }

    @Override
    public List<Notice> getNoticeList(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            notice.setRole("ROLE_ADMIN");
        }

        List<Notice> S_Notice = noticeMapper.getAdminNoticeList(notice);

        if (S_Notice.size() == 0) {
            return Collections.emptyList();
        }

        return S_Notice;
    }

    @Override
    public Map getNotice(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            notice.setRole("ROLE_ADMIN");
        }

        Notice S_Notice = noticeMapper.getAdminDetailNoticeList(notice);
        List<Notice> C_Notice = noticeMapper.selectNoticeConfirm(notice);

        Map<String, Object> map = new HashMap<>();
        map.put("S_Notice", S_Notice);
        map.put("C_Notice", C_Notice);

        return map;
    }

}
