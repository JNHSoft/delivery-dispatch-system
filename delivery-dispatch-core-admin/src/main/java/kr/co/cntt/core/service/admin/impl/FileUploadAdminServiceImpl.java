package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.admin.FileUploadAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service("fileUploadAdminService")
public class FileUploadAdminServiceImpl implements FileUploadAdminService {

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * @param adminMapper Admin D A O
     */
    @Autowired
    public FileUploadAdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public int alarmFileUpload(Alarm alarm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            alarm.setRole("ROLE_ADMIN");
        }

        return adminMapper.insertAlarm(alarm);
    }

}
