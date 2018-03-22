package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StoreRiderService {
    /**
     * <p> getStoreInfo
     *
     * @param store
     * @return
     */
    public Store getStoreInfo(Store store);

    /**
     * <p> getRiderNow
     *
     * @param common
     * @return List<Rider>
     */
    public List<Rider> getRiderNow(Common common);

    /**
     * <p> putRiderReturnTime
     *
     * @param rider
     * @return int
     */
    public int putRiderReturnTime(Rider rider);

    /**
     * <p> getChat
     *
     * @param chat
     * @return List<Chat>
     */
    public List<Chat> getChat(Chat chat);

    /**
     * <p> postChat
     *
     * @param chat
     * @return int
     */
    public int postChat(Chat chat);
}
