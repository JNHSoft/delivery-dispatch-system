package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.NoticeMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.redis.Content;
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
     * Admin DAO
     */
    private AdminMapper adminMapper;


    /**
     * @param noticeMapper Notice D A O
     */
    @Autowired
    public NoticeAdminServiceImpl(NoticeMapper noticeMapper, AdminMapper adminMapper) {
        this.noticeMapper = noticeMapper;
        this.adminMapper = adminMapper;
    }

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

        Common common = new Common();
        common.setRole("ROLE_ADMIN");
        common.setToken(notice.getToken());

        List<Group> S_Group = adminMapper.selectGroups(common);

        Map<String, Object> map = new HashMap<>();
        map.put("S_Notice", S_Notice);
        map.put("C_Notice", C_Notice);
        map.put("S_Group", S_Group);

        return map;
    }

    @Override
    public List<SubGroup> getSubGroupList(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            notice.setRole("ROLE_ADMIN");
        }

        Common common = new Common();
        common.setRole("ROLE_ADMIN");
        common.setToken(notice.getToken());
        common.setId(notice.getToGroupId());

        List<SubGroup> S_SubGroup = adminMapper.selectSubGroups(common);

        return S_SubGroup;
    }

    @Override
    public List<SubGroupStoreRel> getSubGroupStoreRelList(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            notice.setRole("ROLE_ADMIN");
        }

        SubGroupStoreRel subGroupStoreRel = new SubGroupStoreRel();
        subGroupStoreRel.setRole("ROLE_ADMIN");
        subGroupStoreRel.setToken(notice.getToken());
        subGroupStoreRel.setGroupId(notice.getToGroupId());
        subGroupStoreRel.setSubGroupId(notice.getToSubGroupId());

        List<SubGroupStoreRel> S_SubGroup = adminMapper.selectSubgroupStoreRels(subGroupStoreRel);

        return S_SubGroup;
    }

    @Override
    public int putNotice(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            notice.setRole("ROLE_ADMIN");
        }

        return noticeMapper.updateNotice(notice);
    }

    @Override
    public int deleteNotice(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            notice.setRole("ROLE_ADMIN");
        }

        return noticeMapper.deleteNotice(notice);
    }

    @Override
    public List<Group> getGroupList(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            notice.setRole("ROLE_ADMIN");
        }

        Common common = new Common();
        common.setRole("ROLE_ADMIN");
        common.setToken(notice.getToken());

        List<Group> S_Group = adminMapper.selectGroups(common);

        return S_Group;
    }

    @Override
    public int postNotice(Notice notice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            notice.setRole("ROLE_ADMIN");
        }
        int result = noticeMapper.insertNotice(notice);
        if(result !=0){
            redisService.setPublisher(Content.builder().type("notice_updated").adminId(notice.getAdminId()).build());
        }
        return result;
    }

}
