package kr.co.cntt.core.service.admin;


import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.notice.Notice;

import java.util.List;
import java.util.Map;

public interface NoticeAdminService {

    /**
     * <p> getNoticeList
     *
     * @param notice
     * @return Notice
     */
    public List<Notice> getNoticeList(Notice notice);

    /**
     * <p> getNotice
     *
     * @param notice
     * @return Notice
     */
    public Map getNotice(Notice notice);

    /**
     * <p> getSubGroupList
     *
     * @param notice
     * @return SubGroup
     */
    public List<SubGroup> getSubGroupList(Notice notice);

    /**
     * <p> getSubGroupStoreRelList
     *
     * @param notice
     * @return SubGroupStoreRel
     */
    public List<SubGroupStoreRel> getSubGroupStoreRelList(Notice notice);

    /**
     * <p> putNotice
     *
     * @param notice
     * @return int
     */
    public int putNotice(Notice notice);

    /**
     * <p> deleteNotice
     *
     * @param notice
     * @return int
     */
    public int deleteNotice(Notice notice);

    /**
     * <p> getGroupList
     *
     * @param notice
     * @return Group
     */
    public List<Group> getGroupList(Notice notice);

    /**
     * <p> deleteNotice
     *
     * @param notice
     * @return int
     */
    public int postNotice(Notice notice);

}
