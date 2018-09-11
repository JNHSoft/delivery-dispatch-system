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
        /*int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        if (selectOrderIsApprovalCompleted != 0) {
            return throw new AppTrException(getMessage(ErrorCodeEnum.E00023), ErrorCodeEnum.E00023.name());
        }*/
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
        if (selectOrderIsCompletedIsCanceled != 0) {
            return 0;
        }

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());
        Store S_Store = storeMapper.selectStoreInfo(storeDTO);

        if (!S_Store.getAssignmentStatus().equals("0")) {
            return 0;
        }

        Rider tmpRider = new Rider();
        tmpRider.setIsAdmin("0");
        tmpRider.setToken(order.getToken());
        tmpRider.setAccessToken(order.getToken());
        tmpRider.setId(order.getRiderId());
        Rider S_Rider = riderMapper.getRiderInfo(tmpRider);

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
        String tmpOrderId = order.getId();
        int ret = this.putOrder(order);

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
//            Order combinedOrderAssigned = new Order();
            order.setId(order.getCombinedOrderId());
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

        Order combinedOrder = new Order();
        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
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

        String tmpRegOrderId = order.getId();

        int nRet = this.putOrder(order);

        String tmpOrderId = order.getId();

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
                redisService.setPublisher(Content.builder().type("order_updated").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            }else {
                redisService.setPublisher(Content.builder().type("order_updated").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
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

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
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

        String tmpOrderId = orderCanceled.getId();

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

        Order orderAssignCanceled = new Order();
        orderAssignCanceled.setId(order.getId());
        orderAssignCanceled.setStatus("5");
        orderAssignCanceled.setRiderId("-1");
        orderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
        orderAssignCanceled.setAssignedDatetime("-1");
        orderAssignCanceled.setPickedUpDatetime("-1");
        orderAssignCanceled.setToken(order.getToken());

        Order combinedOrderAssignCanceled = new Order();

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
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

        String tmpOrderId = orderAssignCanceled.getId();

        orderAssignCanceled.setId(order.getId());

        Order S_Order = orderMapper.selectOrderInfo(orderAssignCanceled);

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if (ret != 0) {
            if (S_Order.getSubGroup() != null){
                redisService.setPublisher(Content.builder().type("order_assign_canceled").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(S_Order.getSubGroup().getId()).build());
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
