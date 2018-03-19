package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.fcm.FirebaseResponse;
import kr.co.cntt.core.mapper.ChatMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.notification.Notification;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("chatService")
public class ChatServiceImpl extends ServiceSupport implements ChatService {
    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @Autowired
    private RedisService redisService;

    /**
     * Chat DAO
     */
    private ChatMapper chatMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;

    /**
     * @param chatMapper CHAT D A O
     * @param storeMapper STORE D A O
     * @param riderMapper RIDER D A O

     */
    @Autowired
    public ChatServiceImpl(ChatMapper chatMapper, StoreMapper storeMapper, RiderMapper riderMapper) {
        this.chatMapper = chatMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int postChat(Chat chat) throws AppTrException {

        Store resultStore = new Store();
        Rider resultRider = new Rider();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            chat.setRole("ROLE_STORE");

            Store store = new Store();
            store.setToken(chat.getToken());
            store.setAccessToken(chat.getToken());

            resultStore = storeMapper.selectStoreInfo(store);

        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            chat.setRole("ROLE_RIDER");

            Rider rider = new Rider();
            rider.setToken(chat.getToken());
            rider.setAccessToken(chat.getToken());

            resultRider = riderMapper.getRiderInfo(rider);
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

        chatMapper.updateChatRoomLastMessage(chat);
        int S_Chat = chatMapper.insertChat(chat);

        if (S_Chat == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00030), ErrorCodeEnum.E00030.name());
        } else {
            if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
                redisService.setPublisher("chat_send", "admin_id:" + resultStore.getAdminId() + ", recv_chat_user_id:" + chat.getChatUserId());
            } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
                redisService.setPublisher("chat_send", "admin_id:" + resultRider.getAdminId() + ", recv_chat_user_id:" + chat.getChatUserId());
            }
        }

        if(authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
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

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public List<Chat> getChat(Chat chat) throws AppTrException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            chat.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            chat.setRole("ROLE_RIDER");
        }

        List<Chat> S_Chat = chatMapper.selectChat(chat);

        if (S_Chat.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00031), ErrorCodeEnum.E00031.name());
        }

        return S_Chat;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public List<Chat> getChatRoom(Chat chat) throws AppTrException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            chat.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            chat.setRole("ROLE_RIDER");
        }

        List<Chat> S_Chatroom = chatMapper.selectChatRoom(chat);

        if (S_Chatroom.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00032), ErrorCodeEnum.E00032.name());
        }

        return S_Chatroom;
    }


}