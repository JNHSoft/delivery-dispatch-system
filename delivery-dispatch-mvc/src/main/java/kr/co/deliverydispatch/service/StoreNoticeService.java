package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StoreNoticeService {

    /**
     * <p> getStoreInfo
     *
     * @param store
     * @return Store
     */
    public Store getStoreInfo(Store store);

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
    public Notice getNotice(Notice notice);

    /**
     * <p> putNoticeConfirm
     *
     * @param notice
     * @return int
     */
    public int putNoticeConfirm(Notice notice);

}
