package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;

import java.util.List;

public interface FileUploadAdminService {

    /**
     * <p> alarmFileUpload
     *
     * @param alarm
     * @return int
     */
    public int alarmFileUpload(Alarm alarm);

    /**
     * <p> getAlarmList
     *
     * @param alarm
     * @return Alarm
     */
    public List<Alarm> getAlarmList(Alarm alarm);

    /**
     * <p> deleteAlarm
     *
     * @param alarm
     * @return int
     */
    public int deleteAlarm(Alarm alarm);

    /**
     * <p> putDefaultSoundStatus
     *
     * @param admin
     * @return int
     */
    public int putDefaultSoundStatus(Admin admin);

}
