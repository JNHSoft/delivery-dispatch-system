package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.fcm.FirebaseResponse;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.notification.Notification;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.deliverydispatch.service.StoreOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("storeOrderService")
public class StoreOrderServiceImpl extends ServiceSupport implements StoreOrderService{
    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

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
     * @param orderMapper ORDER D A O
     * @param storeMapper STORE D A O
     * @param riderMapper Rider D A O
     */
    @Autowired
    public StoreOrderServiceImpl(OrderMapper orderMapper, StoreMapper storeMapper, RiderMapper riderMapper) {
        this.orderMapper = orderMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
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
    public List<Order> getFooterOrders(Order order){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            order.setRole("ROLE_STORE");
        }
        List<Order> S_Order = orderMapper.selectFooterOrders(order);

        if (S_Order.size() == 0) {
            return Collections.<Order>emptyList();
        }

        return S_Order;
    }

    @Override
    public int getCountOderAdmit(Order order) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            order.setRole("ROLE_STORE");
        }
        return orderMapper.selectCountOderAdmit(order);
    }

    @Override
    public List<Order> getOrders(Order order){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            order.setRole("ROLE_STORE");
        }

        /*if (order.getStatus() != null) {
            String tmpString = order.getStatus().replaceAll("[\\D]", "");
            char[] statusArray = tmpString.toCharArray();

            order.setStatusArray(statusArray);
        }*/

        List<Order> S_Order = orderMapper.selectOrders(order);

        if (S_Order.size() == 0) {
            return Collections.<Order>emptyList();
        }

        return S_Order;
    }

    @Override
    public Order getOrderInfo(Common common) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            common.setRole("ROLE_STORE");
        }

        Order S_Order = orderMapper.selectOrderInfo(common);

        if (S_Order == null) {
            return null;
        }

        /*Misc misc = new Misc();
        if (S_Order.getLatitude() != null && S_Order.getLongitude() != null) {
            Store storeInfo = storeMapper.selectStoreLocation(S_Order.getStoreId());

            try {
                S_Order.setDistance(Double.toString(misc.getHaversine(storeInfo.getLatitude(), storeInfo.getLongitude(), S_Order.getLatitude(), S_Order.getLongitude()) / (double) 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        return S_Order;
    }


    /**
     * <p> putThirdParty
     *
     * @param order
     * @return
     * @throws
     */
    public int putOrderThirdParty(Order order){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            order.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().matches(".*ROLE_RIDER.*")) {
            order.setRole("ROLE_RIDER");
        }
        String tmpRegOrderId = order.getId();
        order.setId(null);
        Store S_Store = storeMapper.selectStoreInfo(order);
        order.setId(tmpRegOrderId);

        // 오더상태값 가져오는 쿼리
        int selectThirdPartyStatus = orderMapper.selectOrderIsThirdPartyStatus(order);
        if (selectThirdPartyStatus != 0){
            return 0;
        }

        int orderStatusThirdParty = orderMapper.updateOrderThirdParty(order);
        if (orderStatusThirdParty == 0 ){
            return 0;
        }

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            order.setId(order.getCombinedOrderId());
            int selectCombinedThirdPartyStatus = orderMapper.selectOrderIsThirdPartyStatus(order);
            if (selectCombinedThirdPartyStatus != 0){
                return 0;
            }
            int combinedOrderStatusThirdParty = orderMapper.updateOrderThirdParty(order);
            if (combinedOrderStatusThirdParty == 0){
                return 0;
            }
        }
        order.setId(tmpRegOrderId);
        if (S_Store.getSubGroup() != null){
            redisService.setPublisher(Content.builder().type("order_thirdparty").id(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Store.getId()).subGroupId(S_Store.getSubGroup().getId()).build());
        }else {
            redisService.setPublisher(Content.builder().type("order_thirdparty").id(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Store.getId()).build());
        }
        return orderStatusThirdParty;
    }



    @Override
    public int putOrderAssignedFirst(Order order) {
        /*int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }*/
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }
        order.setAssignedFirst("True");
        return this.putOrder(order);
    }

    /**
     * <p> putOrder
     *
     * @param order
     * @return
     * @throws
     */
    public int putOrder(Order order){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            order.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().matches(".*ROLE_RIDER.*")) {
            order.setRole("ROLE_RIDER");
        }

        int S_Order = orderMapper.updateOrder(order);
        if (S_Order == 0) {
            return 0;
        }

        return S_Order;
    }

    @Override
    public int putOrderAssigned(Order order) {
        /*int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        if (selectOrderIsApprovalCompleted != 0) {
            return throw new AppTrException(getMessage(ErrorCodeEnum.E00023), ErrorCodeEnum.E00023.name());
        }*/
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }
        String tmpOrderId = order.getId();
        order.setId(null);
        /*Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());
        Store S_Store = storeMapper.selectStoreInfo(storeDTO);*/
        Store S_Store = storeMapper.selectStoreInfo(order);

        //자동배정시 강제배정이 안되도록 설정하는 부분
        /*if (!S_Store.getAssignmentStatus().equals("0")&&!regionLocale.toString().equals("zh_TW")) {
            return 0;
        }*/

        /*Rider tmpRider = new Rider();
        tmpRider.setIsAdmin("0");
        tmpRider.setToken(order.getToken());
        tmpRider.setAccessToken(order.getToken());
        tmpRider.setId(order.getRiderId());*/
        order.setId(order.getRiderId());
        Rider S_Rider = riderMapper.getRiderInfo(order);

//        Order orderAssigned = new Order();

        /*orderAssigned.setToken(order.getToken());
        orderAssigned.setId(order.getId());
        orderAssigned.setRiderId(order.getRiderId());
        orderAssigned.setStatus("1");
        orderAssigned.setAssignedDatetime(LocalDateTime.now().toString());
        if (S_Rider.getLatitude() != null && !S_Rider.getLatitude().equals("")) {
            orderAssigned.setAssignXy(S_Rider.getLatitude()+"|"+S_Rider.getLongitude());
        } else {
            orderAssigned.setAssignXy("none");
        }*/
        order.setStatus("1");
        order.setAssignedDatetime(LocalDateTime.now().toString());
        if (S_Rider.getLatitude() != null && !S_Rider.getLatitude().equals("")) {
            order.setAssignXy(S_Rider.getLatitude()+"|"+S_Rider.getLongitude());
        } else {
            order.setAssignXy("none");
        }
        order.setId(tmpOrderId);
        int ret = this.putOrder(order);

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
//            Order combinedOrderAssigned = new Order();
            order.setId(order.getCombinedOrderId());
            order.setCombinedOrderId(tmpOrderId);
            /*combinedOrderAssigned.setRiderId(order.getRiderId());
            combinedOrderAssigned.setStatus("1");
            combinedOrderAssigned.setAssignedDatetime(LocalDateTime.now().toString());
            combinedOrderAssigned.setToken(order.getToken());
            if (S_Rider.getLatitude() != null && !S_Rider.getLatitude().equals("")) {
                combinedOrderAssigned.setAssignXy(S_Rider.getLatitude() + "|" + S_Rider.getLongitude());
            } else {
                combinedOrderAssigned.setAssignXy("none");
            }*/

            /*int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
            if (selectCombinedOrderIsApprovalCompleted != 0) {
                return 0;
            }*/
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                return 0;
            }
            this.putOrder(order);
        }
        order.setId(tmpOrderId);

//        int ret = this.putOrder(orderAssigned);

        if (ret != 0) {
            if (S_Store.getSubGroup() != null){
                redisService.setPublisher(Content.builder().type("order_assigned").id(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Store.getId()).subGroupId(S_Store.getSubGroup().getId()).build());
            }else {
                redisService.setPublisher(Content.builder().type("order_assigned").id(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Store.getId()).build());
            }
//            ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderToken(orderAssigned);
            ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderToken(order);
            if(tokens.size() > 0){
                Notification noti = new Notification();
                noti.setType(Notification.NOTI.ORDER_ASSIGN);
                noti.setRider_id(Integer.valueOf(order.getRiderId()));
                CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                checkFcmResponse(pushNotification);
            }
        }
        return ret;
    }

    @Override
    public List<Rider> getSubgroupRiderRels(Common common){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            common.setRole("ROLE_STORE");
        }
        List<Rider> S_Rider = riderMapper.selectSubgroupRiderRels(common);
        if (S_Rider.size() == 0) {
            return Collections.<Rider>emptyList();
        }

        return S_Rider;
    }

    @Override
    public int putOrderInfo(Order order){
        /*int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }*/
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }

//        String address = "";
//
//        if (order.getAreaAddress() != null && order.getAreaAddress() != "") {
//            address += order.getAreaAddress();
//        }
//
//        if (order.getDistrictAddress() != null && order.getDistrictAddress() != "") {
//            address += " " + order.getDistrictAddress();
//        }
//
//        if (order.getStreetAddress() != null && order.getStreetAddress() != "") {
//            address += " " + order.getStreetAddress();
//        }
//
//        if (order.getEstateAddress() != null && order.getEstateAddress() != "") {
//            address += " " + order.getEstateAddress();
//        }
//
//        if (order.getBuildingAddress() != null && order.getBuildingAddress() != "") {
//            address += " " + order.getBuildingAddress();
//        }
//
//        order.setAddress(address);

        /*Geocoder geocoder = new Geocoder();

        if (order.getAddress() != null && order.getAddress() != "") {
            try {
                Map<String, String> geo = geocoder.getLatLng(order.getAddress());
                order.setLatitude(geo.get("lat"));
                order.setLongitude(geo.get("lng"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        if (order.getMenuPrice() == null || order.getMenuPrice().equals("")) {
            order.setMenuPrice("0");
        }

        if (order.getDeliveryPrice() == null || order.getDeliveryPrice().equals("")) {
            order.setDeliveryPrice("0");
        }

        order.setTotalPrice(String.valueOf(Double.parseDouble(order.getMenuPrice()) + Double.parseDouble(order.getDeliveryPrice())));

        order.setStatus(null);
        order.setRiderId(null);
        order.setAssignedDatetime(null);
        order.setPickedUpDatetime(null);
        order.setCompletedDatetime(null);

        String tmpRegOrderId = order.getId();
        String tmpCombinedOrderId = order.getCombinedOrderId();

        if (!order.getIsCombined()) {
            order.setCombinedOrderId("-1");
        }

        int nRet = this.putOrder(order);

        if (order.getIsCombined() != null && !order.getCombinedOrderId().equals("")) {
            order.setId(tmpCombinedOrderId);
            if (order.getIsCombined()) {
                order.setCombinedOrderId(tmpRegOrderId);
            } else {
                order.setCombinedOrderId("-1");
            }

            /*int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
            if (selectCombinedOrderIsApprovalCompleted != 0) {
                return 0;
            }*/
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                return 0;
            }

            this.putOrder(order);
        }

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if(nRet == 1){
            order.setId(tmpRegOrderId);
            Order curOrder = getOrderInfo(order);
            if(curOrder.getRiderId() != null && !curOrder.getRiderId().equals("")){
                // 해당 라이더한테만 푸쉬
                ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderTokenByOrderId(order);
                if(tokens.size() > 0){
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_CHANGE);
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            }else{
                // 상점 관련 라이더한테 푸쉬
                if(storeDTO.getAssignmentStatus().equals("2")){
                    ArrayList<String> tokens = (ArrayList)orderMapper.selectPushToken(storeDTO.getSubGroup());
                    if(tokens.size() > 0){
                        Notification noti = new Notification();
                        noti.setType(Notification.NOTI.ORDER_CHANGE);
                    /*noti.setId(291);
                    noti.setStoreName(order.getMenuName());
                    noti.setAddr(order.getAreaAddress());*/
                        CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                        checkFcmResponse(pushNotification);
                    }
                }
            }
        }

        if (nRet != 0) {
            if (storeDTO.getSubGroup() != null){
                redisService.setPublisher(Content.builder().type("order_updated").id(tmpRegOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            }else {
                redisService.setPublisher(Content.builder().type("order_updated").id(tmpRegOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
            }
        }

        return nRet;
    }

    @Override
    public int putOrderCanceled(Order order) {
        /*int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }*/
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }

        order.setStatus("4");
        order.setModifiedDatetime(LocalDateTime.now().toString());
        String tmpOrderId = order.getId();
        String tmpCombinedOrderId = order.getCombinedOrderId();

        if (!order.getIsCombined()) {
            order.setCombinedOrderId("-1");
        }

        int nRet = this.putOrder(order);

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            order.setId(tmpCombinedOrderId);
            if (order.getIsCombined()) {
                order.setCombinedOrderId(tmpOrderId);
            } else {
                order.setCombinedOrderId("-1");
                order.setStatus(null);
            }
            /*int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(combinedOrderCanceled);
            if (selectCombinedOrderIsApprovalCompleted != 0) {
                return 0;
            }*/
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                return 0;
            }
            this.putOrder(order);
        }

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if(nRet == 1){
            order.setId(tmpOrderId);
            Order curOrder = getOrderInfo(order);
            if(curOrder.getRiderId() != null && !curOrder.getRiderId().equals("")){
                // 해당 라이더한테만 푸쉬
                ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderTokenByOrderId(order);
                if(tokens.size() > 0){
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_CANCEL);
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            }else{
                // 상점 관련 라이더한테 푸쉬
                if(storeDTO.getAssignmentStatus().equals("2")){
                    ArrayList<String> tokens = (ArrayList)orderMapper.selectPushToken(storeDTO.getSubGroup());
                    if(tokens.size() > 0){
                        Notification noti = new Notification();
                        noti.setType(Notification.NOTI.ORDER_CANCEL);
                        CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                        checkFcmResponse(pushNotification);
                    }
                }
            }
        }

        if (nRet != 0) {
            if (storeDTO.getSubGroup() != null){
                redisService.setPublisher(Content.builder().id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            }else {
                redisService.setPublisher(Content.builder().id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
            }
        }

        return nRet;
    }

    @Override
    public int putOrderAssignCanceled(Order order){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderTokenByOrderId(order);
        /*int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }*/
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }

        /*Order orderAssignCanceled = new Order();
        orderAssignCanceled.setId(order.getId());
        orderAssignCanceled.setStatus("5");
        orderAssignCanceled.setRiderId("-1");
        orderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
        orderAssignCanceled.setAssignedDatetime("-1");
        orderAssignCanceled.setPickedUpDatetime("-1");
        orderAssignCanceled.setToken(order.getToken());*/
//        Order orderAssignCanceled = new Order();
        order.setStatus("5");
        order.setRiderId("-1");
        order.setModifiedDatetime(LocalDateTime.now().toString());
        order.setAssignedDatetime("-1");
        order.setPickedUpDatetime("-1");

        String tmpOrderId = order.getId();

        int ret = this.putOrder(order);
        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
//            Order combinedOrderAssignCanceled = new Order();
            order.setId(order.getCombinedOrderId());
            order.setCombinedOrderId(tmpOrderId);

            /*int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(combinedOrderAssignCanceled);
            if (selectCombinedOrderIsApprovalCompleted != 0) {
                return 0;
            }*/

            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                return 0;
            }

            this.putOrder(order);
        }

        order.setId(tmpOrderId);
        Order S_Order = orderMapper.selectOrderInfo(order);

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);
        if (ret != 0) {
            if (S_Order.getSubGroup() != null){
                redisService.setPublisher(Content.builder().type("order_assign_canceled").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            }else {
                redisService.setPublisher(Content.builder().type("order_assign_canceled").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
            }
            if(authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
                if(tokens.size() > 0){
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_ASSIGN_CANCEL);
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            }
        }

        return ret;
    }
}
