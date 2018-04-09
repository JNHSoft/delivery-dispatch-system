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
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.Misc;
import kr.co.deliverydispatch.service.StoreOrderService;
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
@Service("storeOrderService")
public class StoreOrderServiceImpl extends ServiceSupport implements StoreOrderService{
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

        Misc misc = new Misc();
        if (S_Order.getLatitude() != null && S_Order.getLongitude() != null) {
            Store storeInfo = storeMapper.selectStoreLocation(S_Order.getStoreId());

            try {
                S_Order.setDistance(Double.toString(misc.getHaversine(storeInfo.getLatitude(), storeInfo.getLongitude(), S_Order.getLatitude(), S_Order.getLongitude()) / (double) 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return S_Order;
    }

    @Override
    public int putOrderAssignedFirst(Order order) {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }
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
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        Store S_Store = storeMapper.selectStoreInfo(storeDTO);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_RIDER.*")) {
            Rider rider = new Rider();
            rider.setToken(order.getToken());
            String assignmentStatus = riderMapper.selectRiderAssignmentStatus(rider);
            if (!assignmentStatus.equals("2")) {
                return 0;
            }
        } else if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            if (!S_Store.getAssignmentStatus().equals("0")) {
                return 0;
            }
        }

        Rider tmpRider = new Rider();
        tmpRider.setIsAdmin("0");
        tmpRider.setToken(order.getToken());
        tmpRider.setAccessToken(order.getToken());
        tmpRider.setId(order.getRiderId());
        Rider S_Rider = riderMapper.getRiderInfo(tmpRider);

        Order orderAssigned = new Order();

        orderAssigned.setToken(order.getToken());
        orderAssigned.setId(order.getId());
        orderAssigned.setRiderId(order.getRiderId());
        orderAssigned.setStatus("1");
        orderAssigned.setAssignedDatetime(LocalDateTime.now().toString());
        if (S_Rider.getLatitude() != null && S_Rider.getLatitude() != "") {
            orderAssigned.setAssignXy(S_Rider.getLatitude()+"|"+S_Rider.getLongitude());
        } else {
            orderAssigned.setAssignXy("none");
        }

        Order combinedOrderAssigned = new Order();

        if (order.getCombinedOrderId() != null && order.getCombinedOrderId() != "") {
            combinedOrderAssigned.setId(order.getCombinedOrderId());
            combinedOrderAssigned.setRiderId(order.getRiderId());
            combinedOrderAssigned.setStatus("1");
            combinedOrderAssigned.setAssignedDatetime(LocalDateTime.now().toString());
            combinedOrderAssigned.setToken(order.getToken());
            if (S_Rider.getLatitude() != null && S_Rider.getLatitude() != "") {
                combinedOrderAssigned.setAssignXy(S_Rider.getLatitude() + "|" + S_Rider.getLongitude());
            } else {
                combinedOrderAssigned.setAssignXy("none");
            }

            int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

            if (selectCombinedOrderIsApprovalCompleted != 0) {
                return 0;
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                return 0;
            }

            this.putOrder(combinedOrderAssigned);
        }

        int ret = this.putOrder(orderAssigned);

        if (ret != 0) {
            if (S_Store.getSubGroup() != null){
                redisService.setPublisher("order_assigned", "id:"+order.getId()+", store_id:"+S_Store.getAdminId()+", store_id:"+S_Store.getId()+", subgroup_id:"+S_Store.getSubGroup().getId());
            }else {
                redisService.setPublisher("order_assigned", "id:"+order.getId()+", store_id:"+S_Store.getAdminId()+", store_id:"+S_Store.getId());
            }

            if(authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
                ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderToken(orderAssigned);
                if(tokens.size() > 0){
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_ASSIGN);
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
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
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }

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

        Geocoder geocoder = new Geocoder();

        if (order.getAddress() != null && order.getAddress() != "") {
            try {
                Map<String, String> geo = geocoder.getLatLng(order.getAddress());
                order.setLatitude(geo.get("lat"));
                order.setLongitude(geo.get("lng"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (order.getMenuPrice() == null || order.getMenuPrice() == "") {
            order.setMenuPrice("0");
        }

        if (order.getDeliveryPrice() == null || order.getDeliveryPrice() == "") {
            order.setDeliveryPrice("0");
        }

        order.setTotalPrice(order.getMenuPrice() + order.getDeliveryPrice());

        order.setStatus(null);
        order.setRiderId(null);
        order.setAssignedDatetime(null);
        order.setPickedUpDatetime(null);
        order.setCompletedDatetime(null);

        Order combinedOrder = new Order();
        if (order.getCombinedOrderId() != null && order.getCombinedOrderId() != "") {
            combinedOrder.setId(order.getCombinedOrderId());
            combinedOrder.setCombinedOrderId(order.getId());
            combinedOrder.setRiderId(order.getRiderId());
            combinedOrder.setToken(order.getToken());

            int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

            if (selectCombinedOrderIsApprovalCompleted != 0) {
                return 0;
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                return 0;
            }

            this.putOrder(combinedOrder);
        }

        int nRet = this.putOrder(order);

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if(nRet == 1){
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
                redisService.setPublisher("order_updated", "id:"+order.getId()+", admin_id:"+storeDTO.getAdminId()+", store_id:"+storeDTO.getId()+", subgroup_id:"+storeDTO.getSubGroup().getId());
            }else {
                redisService.setPublisher("order_updated", "id:"+order.getId()+", admin_id:"+storeDTO.getAdminId()+", store_id:"+storeDTO.getId());
            }
        }

        return nRet;
    }

    @Override
    public int putOrderCanceled(Order order) {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }

        Order orderCanceled = new Order();
        orderCanceled.setToken(order.getToken());
        orderCanceled.setId(order.getId());
        orderCanceled.setStatus("4");
        orderCanceled.setModifiedDatetime(LocalDateTime.now().toString());

        Order combinedOrderCanceled = new Order();

        if (order.getCombinedOrderId() != null && order.getCombinedOrderId() != "") {
            combinedOrderCanceled.setId(order.getCombinedOrderId());
            combinedOrderCanceled.setStatus("4");
            combinedOrderCanceled.setModifiedDatetime(LocalDateTime.now().toString());
            combinedOrderCanceled.setToken(order.getToken());

            int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(combinedOrderCanceled);
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(combinedOrderCanceled);

            if (selectCombinedOrderIsApprovalCompleted != 0) {
                return 0;
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                return 0;
            }

            this.putOrder(combinedOrderCanceled);
        }

        int nRet = this.putOrder(orderCanceled);

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if(nRet == 1){
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
                redisService.setPublisher("order_canceled", "id:"+order.getId()+", admin_id:"+storeDTO.getAdminId()+", store_id:"+storeDTO.getId()+", subgroup_id:"+storeDTO.getSubGroup().getId());
            }else {
                redisService.setPublisher("order_canceled", "id:"+order.getId()+", admin_id:"+storeDTO.getAdminId()+", store_id:"+storeDTO.getId());
            }
        }

        return nRet;
    }

    @Override
    public int putOrderAssignCanceled(Order order){
        ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderTokenByOrderId(order);
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            return 0;
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }

        Order orderAssignCanceled = new Order();
        orderAssignCanceled.setId(order.getId());
        orderAssignCanceled.setStatus("5");
        orderAssignCanceled.setRiderId("-1");
        orderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
        orderAssignCanceled.setAssignedDatetime("-1");
        orderAssignCanceled.setPickedUpDatetime("-1");
        orderAssignCanceled.setToken(order.getToken());

        Order combinedOrderAssignCanceled = new Order();

        if (order.getCombinedOrderId() != null && order.getCombinedOrderId() != "") {
            combinedOrderAssignCanceled.setId(order.getCombinedOrderId());
            combinedOrderAssignCanceled.setStatus("5");
            combinedOrderAssignCanceled.setRiderId("-1");
            combinedOrderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
            combinedOrderAssignCanceled.setAssignedDatetime("-1");
            combinedOrderAssignCanceled.setPickedUpDatetime("-1");
            combinedOrderAssignCanceled.setToken(order.getToken());

            int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(combinedOrderAssignCanceled);
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(combinedOrderAssignCanceled);

            if (selectCombinedOrderIsApprovalCompleted != 0) {
                return 0;
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                return 0;
            }

            this.putOrder(combinedOrderAssignCanceled);
        }

        int ret = this.putOrder(orderAssignCanceled);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        orderAssignCanceled.setId(order.getId());

        Order S_Order = orderMapper.selectOrderInfo(orderAssignCanceled);

        if (ret != 0) {
            if (S_Order.getSubGroup() != null){
                redisService.setPublisher("order_assign_canceled", "id:"+order.getId()+", admin_id:"+S_Order.getAdminId()+", store_id:"+S_Order.getId()+", subgroup_id:"+S_Order.getSubGroup().getId());
            }else {
                redisService.setPublisher("order_assign_canceled", "id:"+order.getId()+", admin_id:"+S_Order.getAdminId()+", store_id:"+S_Order.getId());
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
