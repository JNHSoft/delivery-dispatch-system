package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.admin.FileUploadAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * @param adminMapper Admin D A O
     * @param storeMapper Store D A O
     */
    @Autowired
    public FileUploadAdminServiceImpl(AdminMapper adminMapper, StoreMapper storeMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
    }

    @Override
    public int alarmFileUpload(Alarm alarm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            alarm.setRole("ROLE_ADMIN");
        }

        return adminMapper.insertAlarm(alarm);
    }

    @Override
    public List<Alarm> getAlarmList(Alarm alarm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            alarm.setRole("ROLE_ADMIN");
        }

        Store store = new Store();
        store.setToken(alarm.getToken());
        store.setLevel("1");

        return storeMapper.selectAlarm(store);
    }

    @Override
    public int deleteAlarm(Alarm alarm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            alarm.setRole("ROLE_ADMIN");
        }

        return adminMapper.deleteAlarm(alarm);
    }

    @Override
    public int putDefaultSoundStatus(Admin admin) {
        return adminMapper.updateAdminInfo(admin);
    }

}
