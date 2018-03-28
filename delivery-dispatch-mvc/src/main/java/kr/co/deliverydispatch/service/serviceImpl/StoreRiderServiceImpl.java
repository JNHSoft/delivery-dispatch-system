package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.fcm.FirebaseResponse;
import kr.co.cntt.core.mapper.ChatMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.notification.Notification;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.Misc;
import kr.co.deliverydispatch.service.StoreOrderService;
import kr.co.deliverydispatch.service.StoreRiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("storeRiderService")
public class StoreRiderServiceImpl extends ServiceSupport implements StoreRiderService{
    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;
    /**
     * Order DAO
     */
    private OrderMapper orderMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;

    /**
     * Chat DAO
     */
    private ChatMapper chatMapper;

    /**
     * @param orderMapper ORDER D A O
     * @param storeMapper STORE D A O
     * @param riderMapper Rider D A O
     */
    @Autowired
    public StoreRiderServiceImpl(OrderMapper orderMapper, StoreMapper storeMapper, RiderMapper riderMapper, ChatMapper chatMapper) {
        this.orderMapper = orderMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
        this.chatMapper = chatMapper;
    }

    @Override
    public Store getStoreInfo(Store store) {
        // 권한
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // store 가 조회
        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            // token 값 선언
            store.setAccessToken(store.getToken());
            store.setId("");
            store.setIsAdmin("");
            store.setRole("ROLE_STORE");
        }
        // 권한이 없는 다른 user 가 조회
        else if (authentication.getAuthorities().toString().matches(".*ROLE_USER.*")) {
            store.setAccessToken(null);
            store.setId("");
            store.setIsAdmin("");
        }

        // log 확인
        log.info(">>> token: " + store.getAccessToken());
        log.info(">>> token: " + store.getToken());

        // 리스트
        Store S_Store = storeMapper.selectStoreInfo(store);

        if (S_Store == null) {
            return null;
        }

        return S_Store;
    }

    @Override
    public List<Rider> getRiderNow(Common common){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            common.setRole("ROLE_STORE");
        }
        List<Rider> S_Rider = riderMapper.selectRiderNow(common);
        if (S_Rider.size() == 0) {
            return Collections.<Rider>emptyList();
        }

        return S_Rider;
    }

    @Override
    public List<Rider> getRiderFooter(Common common){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            common.setRole("ROLE_STORE");
        }
        List<Rider> S_Rider = riderMapper.selectRiderFooter(common);
        if (S_Rider.size() == 0) {
            return Collections.<Rider>emptyList();
        }

        return S_Rider;
    }

    @Override
    public int putRiderReturnTime(Rider rider){
        int nRet = riderMapper.updateRiderReturnTime(rider);

        Rider S_Rider = riderMapper.getRiderInfo(rider);

        if (nRet != 0) {
            if (S_Rider.getSubGroupStoreRel() != null) {
                redisService.setPublisher("rider_updated", "id:" + S_Rider.getId() + ", admin_id:" + S_Rider.getAdminId() + ", store_id:" + S_Rider.getSubGroupStoreRel().getStoreId());
            } else {
                redisService.setPublisher("rider_updated", "id:" + S_Rider.getId() + ", admin_id:" + S_Rider.getAdminId());
            }
        }
        return nRet;
    }

    @Override
    public List<Chat> getChat(Chat chat) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            chat.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().matches(".*ROLE_RIDER.*")) {
            chat.setRole("ROLE_RIDER");
        }
        Chat chatRoomChecked = chatMapper.selectChatUserChatRoomRel(chat);
        if (chatRoomChecked == null) {
            chatMapper.insertChatRoom(chat);
            chatMapper.insertChatRoomRelRecv(chat);
            chatMapper.insertChatRoomRelTran(chat);
        }
        List<Chat> S_Chat = chatMapper.selectStoreChat(chat);

        if (S_Chat.size() == 0) {
            return Collections.<Chat>emptyList();
        }

        return S_Chat;
    }

    @Override
    public int postChat(Chat chat){

        Store resultStore = new Store();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            chat.setRole("ROLE_STORE");

            Store store = new Store();
            store.setToken(chat.getToken());
            store.setAccessToken(chat.getToken());
            resultStore = storeMapper.selectStoreInfo(store);
        }

        Chat resultSelectChatRoomRel = chatMapper.selectChatUserChatRoomRel(chat);

        if (resultSelectChatRoomRel == null) {
            int resultInsertChatRoom = chatMapper.insertChatRoom(chat);
            if (resultInsertChatRoom != 0) {
                chatMapper.insertChatRoomRelRecv(chat);
                chatMapper.insertChatRoomRelTran(chat);
            }
        } else {
            chat.setChatRoomId(resultSelectChatRoomRel.getChatRoomId());
        }

        if (chat.getMessage().length() > 64) {
            chat.setLastMessage(chat.getMessage().substring(0, 64));
        } else {
            chat.setLastMessage(chat.getMessage());
        }

        int S_Chat = chatMapper.insertChat(chat);
        chatMapper.updateChatRoomLastMessage(chat);

        if (S_Chat == 0) {
            return  0;
        } else {
            if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
                redisService.setPublisher("chat_send", "admin_id:" + resultStore.getAdminId() + ", recv_chat_user_id:" + chat.getChatUserId());
            }
        }

        if(authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderTokenByChatUserId(chat);

            if(tokens.size() > 0){
                Notification noti = new Notification();
                noti.setType(Notification.NOTI.CHAT_SEND);
                noti.setTitle(resultStore.getStoreName());
                noti.setMessage(chat.getMessage());
                noti.setChat_user_id(resultStore.getChatUserId());
                CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                checkFcmResponse(pushNotification);
            }
        }

        return S_Chat;
    }
}
