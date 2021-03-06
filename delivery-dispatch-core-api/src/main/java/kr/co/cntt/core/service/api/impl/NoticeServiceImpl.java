package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.fcm.FirebaseResponse;
import kr.co.cntt.core.mapper.NoticeMapper;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.notification.Notification;
import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("noticeService")
public class NoticeServiceImpl extends ServiceSupport implements NoticeService {
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
    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

//   Role 확인
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    log.info(">>> role: " + authentication.getAuthorities());

    // 공지 사항 등록
    @Secured({"ROLE_ADMIN"})
    @Override
    public int postNotice(Notice notice) {
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {

            Notice res = noticeMapper.selectAdminId(notice);

            notice.setAdminId(res.getAdminId());
            // 파일 업로드
            if (notice.getOriFileName() != null) {
                DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmSS");
                String[] tmp = notice.getOriFileName().split("\\.");

                notice.setFileName(RandomStringUtils.randomAlphanumeric(16) + "_" + LocalDateTime.now().format(dateformatter) + "." + tmp[1]);

            }

        }

        int result = noticeMapper.insertNotice(notice);

        if (result != 0) {
            redisService.setPublisher(Content.builder().type("notice_updated").adminId(notice.getAdminId()).build());


            ArrayList<String> tokens = (ArrayList)noticeMapper.selectAllToken();

            if(tokens.size() > 0){
                Notification push = new Notification();
                push.setType(Notification.NOTI.NOTICE_MASTER);
                push.setTitle(notice.getTitle());
                push.setMessage(notice.getContent());
                CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, push);
                checkFcmResponse(pushNotification);
            }
        }

        return result;
    }

    // 공지 사항 수정
    @Secured({"ROLE_ADMIN"})
    @Override
    public int updateNotice(Notice notice) throws AppTrException{
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")){

            Notice res = noticeMapper.selectAdminId(notice);

            notice.setAdminId(res.getAdminId());
        }
        // Error
        int res = noticeMapper.updateNotice(notice);

        if (res == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00035), ErrorCodeEnum.E00035.name());
        }

        if (res != 0) {
            redisService.setPublisher(Content.builder().type("notice_updated").adminId(notice.getAdminId()).build());
        }

        return res;
    }

    // 공지 사항 삭제
    @Secured({"ROLE_ADMIN"})
    @Override
    public int deleteNotice(Notice notice) throws AppTrException {
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")){

            Notice res = noticeMapper.selectAdminId(notice);

            notice.setAdminId(res.getAdminId());

        }
        // Error
        int res = noticeMapper.deleteNotice(notice);

        if (res == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00035), ErrorCodeEnum.E00035.name());
        }

        if (res != 0) {
            redisService.setPublisher(Content.builder().type("notice_updated").adminId(notice.getAdminId()).build());
        }

        return res;
    }



    // 공지사항 상세 보기
    @Secured({"ROLE_ADMIN","ROLE_STORE","ROLE_RIDER"})
    @Override
    public Map detailNotice(Notice notice) throws AppTrException {
        // list 선언
//        List<Notice> A_Notice = new ArrayList<>();
//        List<Notice> S_Notice = new ArrayList<>();
//        List<Notice> R_Notice = new ArrayList<>();

        Notice A_Notice = new Notice();
        Notice S_Notice = new Notice();
        Notice R_Notice = new Notice();

        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 상점
        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            // token 값 선언
            notice.setAccessToken(notice.getToken());

            S_Notice = noticeMapper.getStoreDetailNoticeList(notice);

        }
        // 라이더
        else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            // token 값 선언
            notice.setAccessToken(notice.getToken());

            R_Notice = noticeMapper.getRiderDetailNoticeList(notice);

        }
        // 관리자
        else if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            // token 값 선언
            notice.setAccessToken(notice.getToken());
            // admin
            A_Notice = noticeMapper.getAdminDetailNoticeList(notice);


        }
        // map 으로 넘겨준다
//        Map<String, List<Notice>> map = new HashMap<>();
        Map<String, Notice> map = new HashMap<>();
        map.put("adminNotice", A_Notice);
        map.put("storeNotice", S_Notice);
        map.put("riderNotice", R_Notice);
        return map;
    }

    // 공지 사항 List map 으로 값을 받아온다.
    @Secured({"ROLE_ADMIN","ROLE_STORE","ROLE_RIDER"})
    @Override
    public Map getNoticeList(Notice notice) throws AppTrException{
        // list 선언
        List<Notice> A_Notice = new ArrayList<>();
        List<Notice> S_Notice = new ArrayList<>();
        List<Notice> R_Notice = new ArrayList<>();
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            // token 값 선언
            notice.setAccessToken(notice.getToken());

            S_Notice = noticeMapper.getStoreNoticeList(notice);

        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            notice.setAccessToken(notice.getToken());

            // Store
            R_Notice = noticeMapper.getRiderNoticeList(notice);

        } else if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {

            notice.setAccessToken(notice.getToken());

            A_Notice = noticeMapper.getAdminNoticeList(notice);
        }
        // map 으로 넘겨준다
        Map<String, List<Notice>> map = new HashMap<>();
        map.put("adminNotice", A_Notice);
        map.put("storeNotice", S_Notice);
        map.put("riderNotice", R_Notice);
        return map;
    }

    /**
     * <p> 상점 - 공지사항 확인
     * @param notice
     * @return
     * @throws AppTrException
     */
    @Secured("ROLE_STORE")
    @Override
    public int putNoticeConfirm(Notice notice) throws AppTrException {
        int ret = 0;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {

            List<Notice> C_Notice = noticeMapper.selectNoticeConfirm(notice);

            if (C_Notice.size() == 0) {
                ret = noticeMapper.insertNoticeConfirm(notice);
            } else {
                ret = noticeMapper.updateNoticeConfirm(notice);
            }
        }

        return ret;
    }
}
