package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
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
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")){

            Notice res = noticeMapper.selectAdminId(notice);

            notice.setAdminId(res.getAdminId());
            notice.setWriterId(res.getAdminId());
            notice.setWriterType("1");

        } else {
            Notice res = noticeMapper.selectStoreAdminId(notice);

            notice.setAdminId(res.getAdminId());
            notice.setWriterId(res.getAdminId());
            notice.setWriterType("2");
        }

        return noticeMapper.updateNotice(notice);
    }

    // 공지 사항 삭제
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int deleteNotice(Notice notice) throws AppTrException {
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")){

            Notice res = noticeMapper.selectStoreAdminId(notice);

            notice.setAdminId(res.getAdminId());
            notice.setWriterId(res.getAdminId());
            notice.setWriterType("2");
        } else {
            Notice res = noticeMapper.selectAdminId(notice);

            notice.setAdminId(res.getAdminId());
            notice.setWriterType("1");
        }
        // Error
        int res = noticeMapper.deleteNotice(notice);

        if (res == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return res;
    }

    // 공지사항 상세 보기 진행중...
    @Override
    public Notice detailNotice(Notice notice) throws AppTrException {
        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Notice nt = null;
        // 상점
        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {

            nt = noticeMapper.detailNotice(notice);
            // 해당 스토어 아이디로 작성한 목록

        }
        // 라이더
        else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            nt = noticeMapper.detailNotice(notice);
            // 해당 라이더 아이디로 작성한 목록


        }
        // 관리자
        else if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            notice.setToRider("1");
            notice.setToStore("1");
            nt = noticeMapper.detailNotice(notice);

        }

        return nt;
    }
}
