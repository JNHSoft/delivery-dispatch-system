package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.mapper.NoticeMapper;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("noticeService")
public class NoticeServiceImpl extends ServiceSupport implements NoticeService {

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
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int postNotice(Notice notice) {
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {


            Notice res = noticeMapper.selectAdminId(notice);

            notice.setAdminId(res.getAdminId());

            notice.setWriterId(res.getAdminId());
            notice.setWriterType("1");


        } else {
            Notice res = noticeMapper.selectStoreAdminId(notice);

            notice.setAdminId(res.getAdminId());
            notice.setWriterId(res.getWriterId());
            notice.setWriterType("2");

        }
        return noticeMapper.insertNotice(notice);
    }

    // 공지 사항 수정
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int updateNotice(Notice notice){
//        // Role 확인
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication.getAuthorities().equals("[ROLE_ADMIN]")){
//            Notice result = noticeMapper.selectAdminId(notice);
//
//            notice.setAdminId(result.getAdminId());
//            notice.setWriterId(result.getWriterId());
//
//            String writerId = not
//
//
//        } else {
//            String adminId = noticeMapper.selectStoreAdminId(notice);
//            notice.setAdminId(adminId);
//        }
//
//        return noticeMapper.updateNotice(notice);
        return 0;
    }










}
