package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.fcm.FirebaseResponse;
import kr.co.cntt.core.mapper.ChatMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.fcm.FcmBody;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.notification.Notification;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.rider.RiderSession;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            store.setAccessToken(store.getToken());
            store.setRole("ROLE_STORE");
        }

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
                redisService.setPublisher(Content.builder().id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getSubGroupStoreRel().getStoreId()).subGroupId(S_Rider.getSubGroupStoreRel().getSubGroupId()).build());
            } else {
                redisService.setPublisher(Content.builder().id(S_Rider.getId()).adminId(S_Rider.getAdminId()).build());
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
                redisService.setPublisher(Content.builder().type("chat_send").adminId(resultStore.getAdminId()).recvChatUserId(chat.getChatUserId()).build());
            }
        }

        if(authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            ArrayList<Map> tokens = (ArrayList)riderMapper.selectRiderTokenByChatUserId(chat);

            if(tokens.size() > 0){
                Notification noti = new Notification();
                noti.setType(Notification.NOTI.CHAT_SEND);
                noti.setTitle(resultStore.getStoreName());
                noti.setMessage(chat.getMessage());
                noti.setChat_user_id(resultStore.getChatUserId());
                // 19.09.18 안드로이드 푸쉬 전송 시 채팅방번호 전달
                noti.setChat_room_id(chat.getChatRoomId());

                // PUSH 객체로 변환 후 전달
                FcmBody fcmBody = new FcmBody();

                Map<String, Object> obj = new HashMap<>();
                obj.put("obj", noti);

                fcmBody.setData(obj);
                fcmBody.setPriority("high");

                fcmBody.getNotification().setTitle(noti.getTitle());
                fcmBody.getNotification().setBody(noti.getMessage());

                // APP 유형 및 신규 APP 사용 유무에 다른 분기처리
                ArrayList<Map> iosMap = new ArrayList<>();      // 신규 iOS
                ArrayList<Map> android = new ArrayList<>();     // 신규 android
                ArrayList<Map> oldMap = new ArrayList<>();      // 구버전 (단, iOS 버전 없음)

                iosMap.addAll(tokens.stream().filter(x -> {
                    if (x.getOrDefault("appType", "").toString().equals("1") && x.getOrDefault("platform", "").toString().equals("")){
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList()));

                // iOS push
                if (iosMap.size() > 0){
                    try {
                        ArrayList<String> iosTokenValue = new ArrayList<>();

                        iosMap.forEach(x -> iosTokenValue.add(x.get("push_token").toString()));
                        fcmBody.setRegistration_ids(iosTokenValue);

                        CompletableFuture<FirebaseResponse> iosPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "ios");
                        checkFcmResponse(iosPushNotification);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }


                android.addAll(tokens.stream().filter(x -> {
                    if (x.getOrDefault("appType", "").toString().equals("1") && x.getOrDefault("platform", "").toString().equals("android")){
                        return true;
                    }

                    return false;
                }).collect(Collectors.toList()));

                // new android push
                if (android.size() > 0){
                    try {
                        log.info("############# 안드로이드 푸쉬");
                        ArrayList<String> androidTokenValue = new ArrayList<>();

                        android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                        fcmBody.setRegistration_ids(androidTokenValue);

                        // noti 전문 삭제
                        fcmBody.setNotification(null);

                        CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                        checkFcmResponse(androidPushNotification);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }


                oldMap.addAll(tokens.stream().filter(x->{
                    if (x.getOrDefault("appType", "").toString().equals("")){
                        return true;
                    }

                    return false;
                }).collect(Collectors.toList()));

                // old android push
                if (oldMap.size() > 0){
                    try {
                        log.info("############# 구버젼 푸쉬");
                        ArrayList<String> oldTokenValue = new ArrayList<>();

                        oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                        fcmBody.setRegistration_ids(oldTokenValue);

                        // noti 전문 삭제
                        fcmBody.setNotification(null);

                        CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                        checkFcmResponse(oldPushNotification);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        return S_Chat;
    }

    /**
     * 20.08.07
     * 라이더 승인 리스트
     * */
    @Override
    public List<RiderApprovalInfo> getRiderApprovalList(Common common){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
//            common.setRole("ROLE_STORE");
//        }

        List<RiderApprovalInfo> approvalRider = riderMapper.selectApprovalRiderList(common);
        if (approvalRider.size() == 0) {
            return Collections.<RiderApprovalInfo>emptyList();
        }

        return approvalRider;


        //common.setRole("ROLE_STORE");
    }

    /**
     * 21.03.10 라이더 승인 내용
     * */
    @Override
    public List<Rider> getRegistRiderInfoList(User user){
        List<Rider> regRiderList = riderMapper.selectRegistRiderInfoList(user);
        if (regRiderList.size() == 0) {
            return Collections.<Rider>emptyList();
        }

        return regRiderList;
    }

    /**
     * 라이더 승인 개별 정보
     * */
    @Override
    public RiderApprovalInfo getRiderApprovalInfo(Common common){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            common.setRole("ROLE_STORE");
        }
        RiderApprovalInfo info = riderMapper.selectApprovalRiderInfo(common);
        return info;
    }

    /**
     * 라이더 정보 변경
     * */
    @Override
    public int setRiderInfo(RiderApprovalInfo riderInfo){
        return riderMapper.updateApprovalRiderInfo(riderInfo);
    }

    /**
     * 라이더 세션 변경
     * */
    @Override
    public int updateRiderSession(RiderSession session){
        return riderMapper.updateRiderSession(session);
    }

    /**
     * deleteApprovalRiderRowData
     * */
    @Override
    public int deleteApprovalRiderRowData(RiderApprovalInfo riderInfo){ return riderMapper.deleteApprovalRiderRowData(riderInfo); }

    /**
     * 라이더 패스워드 초기화
     * */
    @Override
    public int resetRiderPassword(Rider rider) {
        riderMapper.resetRiderPasswordforStore(rider);
        return 0;
    }

    /**
     * 21.05.20 소속된 관리자가 관리하는 스토어 현황 가져오기
     * */
    @Override
    public List<Store> getSharedStoreList(Common comm){
        return storeMapper.selectSharedStoreList(comm);
    }
}
