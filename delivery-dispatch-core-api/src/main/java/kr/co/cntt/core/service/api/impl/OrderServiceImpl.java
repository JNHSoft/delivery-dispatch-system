package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.fcm.FirebaseResponse;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.notification.Notification;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.order.OrderCheckAssignment;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.OrderService;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.Misc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceSupport implements OrderService {
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
    public OrderServiceImpl(OrderMapper orderMapper, StoreMapper storeMapper, RiderMapper riderMapper) {
        this.orderMapper = orderMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
    }

    @Override
    public void autoAssignOrder() throws AppTrException {
        String nowDate = String.format("%02d", LocalDateTime.now().getYear())
                + String.format("%02d", LocalDateTime.now().getMonthValue())
                + String.format("%02d", LocalDateTime.now().getDayOfMonth())
                + String.format("%02d", LocalDateTime.now().getHour())
                + String.format("%02d", LocalDateTime.now().getMinute())
                + "00";

        Map map = new HashMap();

        List<Order> orderList = orderMapper.selectForAssignOrders();
        if (orderList != null) {
            Loop1 : for (int i = 0; i < orderList.size(); i++) {
                Order order = new Order();
                order = orderList.get(i);
                map.put("order", order);

//                log.info(">>> 배정 대기 오더 getCreatedDatetime(): " + orderList.get(i).getCreatedDatetime());
//                log.info(">>> 배정 대기 오더 getReservationDatetime(): " + orderList.get(i).getReservationDatetime());
//                log.info(">>> 배정 대기 오더 getId(): " + orderList.get(i).getId());
//                log.info(">>> 배정 대기 오더 getLongitude(): " + orderList.get(i).getLongitude());
//                log.info(">>> 배정 대기 오더 getLongitude(): " + orderList.get(i).getLongitude());
//
//                log.info(">>> store_id: " + orderList.get(i).getStore().getId());
//                log.info(">>> s_lon: " + orderList.get(i).getStore().getLongitude());
//                log.info(">>> s_sort: " + orderList.get(i).getStore().getStoreDistanceSort());
//                log.info("=====================================================");

                List<Rider> riderList = riderMapper.selectForAssignRiders(orderList.get(i).getStore().getId());

                Misc misc = new Misc();
                Map<String, Integer> riderHaversineMap = new HashMap<>();

                if (riderList != null) {
                    for (Rider r : riderList) {
                        if (r.getLatitude() != null) {
                            try {
                                riderHaversineMap.put(r.getId(), misc.getHaversine(orderList.get(i).getStore().getLatitude(), orderList.get(i).getStore().getLongitude(), r.getLatitude(), r.getLongitude()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }


//                        log.info(">>> rider_id" + r.getId());
//                        log.info(">>> rider_status   " + r.getStatus());
//                        log.info(">>> rider_working   " + r.getWorking());
//                        log.info(">>> rider_workingHours   " + r.getWorkingHours());
//                        log.info(">>> rider_restHours   " + r.getRestHours());
//                        log.info("-----------------------------------------------------");
                    }
                }

                String riderSort = null;
                String[] riderSortArray = null;
                if (!riderHaversineMap.isEmpty()) {
                    riderSort = StringUtils.arrayToDelimitedString(misc.sortByValue(riderHaversineMap).toArray(), "|");
                }

                if (riderSort != null) {
                    riderSortArray = riderSort.split("\\|");
                }

                Boolean flag = Boolean.FALSE;

                if (riderSortArray != null) {
                    Loop2:
                    for (int j = 0; j < riderSortArray.length; j++) {
                        Loop3:
                        for (Rider r1 : riderList) {
                            if (r1.getId().equals(riderSortArray[j])) {
                                if (riderHaversineMap.get(riderSortArray[j]) <= Integer.parseInt(order.getStore().getRadius()) * 1000) {
                                    if (r1.getSubGroupRiderRel().getStoreId().equals(orderList.get(i).getStoreId()) && r1.getAssignCount().equals("0")) {
                                        log.info(">>> auto assign step1");
                                        if (r1.getReturnTime() == null) {
                                            map.put("rider", r1);
                                            flag = Boolean.TRUE;
                                            break Loop2;
                                        } else {
                                            if (r1.getSubGroupRiderRel().getStoreId().equals(order.getStoreId())) {
                                                map.put("rider", r1);
                                                flag = Boolean.TRUE;
                                                break Loop2;
                                            } else {
                                                log.info(">>> 라이더 재배치: 해당 스토어 주문 아님");
                                                flag = Boolean.FALSE;
                                            }
                                        }

                                    } else {
                                        flag = Boolean.FALSE;
                                    }
                                } else if (riderHaversineMap.get(riderSortArray[j]) <= Integer.parseInt(order.getStore().getRadius()) * 1000) {
                                    if (r1.getSubGroupRiderRel().getStoreId().equals(orderList.get(i).getStoreId()) && Integer.parseInt(r1.getAssignCount()) <= Integer.parseInt(order.getStore().getAssignmentLimit())) {
                                        log.info(">>> auto assign step2");
                                        if (r1.getReturnTime() == null) {
                                            map.put("rider", r1);
                                            flag = Boolean.TRUE;
                                            break Loop2;
                                        } else {
                                            if (r1.getSubGroupRiderRel().getStoreId().equals(order.getStoreId())) {
                                                map.put("rider", r1);
                                                flag = Boolean.TRUE;
                                                break Loop2;
                                            } else {
                                                log.info(">>> 라이더 재배치: 해당 스토어 주문 아님");
                                                flag = Boolean.FALSE;
                                            }
                                        }
                                    } else {
                                        flag = Boolean.FALSE;
                                    }
                                } else {
                                    if (riderHaversineMap.get(riderSortArray[j]) <= Integer.parseInt(order.getStore().getRadius()) * 2000) {
                                        if (r1.getAssignCount().equals("0")) {
                                            log.info(">>> auto assign step3");
                                            if (r1.getReturnTime() == null) {
                                                map.put("rider", r1);
                                                flag = Boolean.TRUE;
                                                break Loop2;
                                            } else {
                                                if (r1.getSubGroupRiderRel().getStoreId().equals(order.getStoreId())) {
                                                    map.put("rider", r1);
                                                    flag = Boolean.TRUE;
                                                    break Loop2;
                                                } else {
                                                    flag = Boolean.FALSE;
                                                    log.info(">>> 라이더 재배치: 해당 스토어 주문 아님");
                                                }
                                            }
                                        } else {
                                            flag = Boolean.FALSE;
                                        }
                                    } else {
                                        flag = Boolean.FALSE;
                                    }
                                }

                            } else {
                                flag = Boolean.FALSE;
                            }

                        }

                    }
                }

                if (flag == Boolean.TRUE) {

                    if (orderList.get(i).getReservationDatetime() != null && orderList.get(i).getReservationDatetime() != "") {
                        if (orderList.get(i).getReservationDatetime().equals(nowDate)) {
                            // 예약 배정
                            log.info(">>> 예약 배정 " + orderList.get(i).getId());
                            int proc = this.autoAssignOrderProc(map);
                            if (proc == 1) {
                                orderList.remove(i);
                                i -= 1;
                                log.info("============================================================================");
                            }
                        } else {
                            log.info(">>> 예약 시간 안됨 pass " + orderList.get(i).getId());
                            log.info("============================================================================");
                        }

                    } else {
                        // 자동 배정
                        log.info(">>> 자동 배정 " + orderList.get(i).getId());
                        int proc = this.autoAssignOrderProc(map);

                        if (proc == 1) {
                            orderList.remove(i);
                            i -= 1;
                            log.info("============================================================================");
                        }
                    }
                }
            }

        }

    }

    public int autoAssignOrderProc(Map map) throws AppTrException {
        log.info(">>> proc!!!");
        log.info(">>> order.getId() " + ((Order) map.get("order")).getId());
        log.info(">>> order.getStore().getId() " + ((Order) map.get("order")).getStore().getId());
        log.info(">>> order.getStore().getAdminId() " + ((Order) map.get("order")).getStore().getAdminId());

        if (map.get("rider") == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00029), ErrorCodeEnum.E00029.name());
        } else {
            log.info(">>> rider.getId() " + ((Rider) map.get("rider")).getId());
            Order order = new Order();
            order.setRole("ROLE_SYSTEM");
            order.setId(((Order) map.get("order")).getId());
            order.setRiderId(((Rider) map.get("rider")).getId());
            order.setStatus("1");

            ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderToken(order);
            Order notiOrder = (Order) map.get("order");

            int result = orderMapper.updateOrder(order);

            if (result != 0) {
                redisService.setPublisher("order_assigned", "id:"+notiOrder.getId() + ", admin_id:"+notiOrder.getAdminId() + ", store_id:"+notiOrder.getStoreId());
                if(tokens.size() > 0){
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_ASSIGN_AUTO);
                    noti.setId(Integer.valueOf(notiOrder.getId()));
                    noti.setStoreName(notiOrder.getStore().getAdminId());
                    noti.setStoreName(notiOrder.getStore().getStoreName());
                    noti.setAddr(notiOrder.getAddress());
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);

                    checkFcmResponse(pushNotification);
                }
            }

            return result;

        }

    }

    @Secured("ROLE_STORE")
    @Override
    public int postOrder(Order order) throws AppTrException {
        // orderList = null;

        String address = "";

        if (order.getAreaAddress() != null && order.getAreaAddress() != "") {
            address += order.getAreaAddress();
        }

        if (order.getDistrictAddress() != null && order.getDistrictAddress() != "") {
            address += " " + order.getDistrictAddress();
        }

        if (order.getStreetAddress() != null && order.getStreetAddress() != "") {
            address += " " + order.getStreetAddress();
        }

        if (order.getEstateAddress() != null && order.getEstateAddress() != "") {
            address += " " + order.getEstateAddress();
        }

        if (order.getBuildingAddress() != null && order.getBuildingAddress() != "") {
            address += " " + order.getBuildingAddress();
        }

        order.setAddress(address);

        Geocoder geocoder = new Geocoder();

        try {
            Map<String, String> geo = geocoder.getLatLng(order.getAddress());
            order.setLatitude(geo.get("lat"));
            order.setLongitude(geo.get("lng"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (order.getReservationDatetime() != null && order.getReservationDatetime() != "") {
            order.setReservationDatetime(order.getReservationDatetime().substring(0, 12) +"00");
        } else {
            order.setReservationDatetime(null);
        }

        if (order.getDeliveryPrice() == null || order.getDeliveryPrice() == "") {
            order.setDeliveryPrice("0");
        }

        order.setTotalPrice(order.getMenuPrice() + order.getDeliveryPrice());

        if (order.getCookingTime() == null || order.getCookingTime() == "") {
            order.setCookingTime("0");
        }

        order.setStatus("0");

        int postOrder = orderMapper.insertOrder(order);

        if (postOrder == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00011), ErrorCodeEnum.E00011.name());
        }

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if (postOrder != 0) {
            redisService.setPublisher("order_new", "id:"+order.getId() + ", admin_id:" + storeDTO.getAdminId() + ", store_id:"+storeDTO.getId());

            if(storeDTO.getAssignmentStatus().equals("2")){

                ArrayList<String> tokens = (ArrayList)orderMapper.selectPushToken(storeDTO.getSubGroup());
                if(tokens.size() > 0){
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_NEW);
                /*noti.setId(291);
                noti.setStoreName(order.getMenuName());
                noti.setAddr(order.getAreaAddress());*/
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            }
        }

        return postOrder;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public List<Order> getOrders(Order order) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            order.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            order.setRole("ROLE_RIDER");
        }

        char[] statusArray = null;
        if (order.getStatus() != null) {
            String tmpString = order.getStatus().replaceAll("[\\D]", "");
            statusArray = tmpString.toCharArray();

            order.setStatusArray(statusArray);
        }

        List<Order> S_Order = orderMapper.selectOrders(order);

        if (order.getRole().equals("ROLE_RIDER")) {
            Rider rider = new Rider();
            List<Order> R_Order = new ArrayList<>();
            if (statusArray != null) {
                for (char s : statusArray) {
                    if (s != '0' && s != '5') {
                        rider.setAccessToken(order.getToken());
                        rider.setToken(order.getToken());
                        Rider S_Rider = riderMapper.getRiderInfo(rider);

                        for (Order o : S_Order) {
                            if (o.getRiderId() != null) {
                                if (o.getRiderId().equals(S_Rider.getId())) {
                                    R_Order.add(o);
                                }
                            }
                        }
                    } else {
                        for (Order o : S_Order) {
                            R_Order.add(o);
                        }
                    }
                }

                if (R_Order.size() == 0) {
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00015), ErrorCodeEnum.E00015.name());
                }

                return R_Order;

            } else {
                if (S_Order.size() == 0) {
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00015), ErrorCodeEnum.E00015.name());
                }

                return S_Order;
            }
        } else {
            if (S_Order.size() == 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00015), ErrorCodeEnum.E00015.name());
            }

            return S_Order;
        }

    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public Order getOrderInfo(Common common) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            common.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            common.setRole("ROLE_RIDER");
        }

        Order S_Order = orderMapper.selectOrderInfo(common);

        if (S_Order == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00016), ErrorCodeEnum.E00016.name());
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

    /**
     * <p> putOrder
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrder(Order order) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            order.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            order.setRole("ROLE_RIDER");
        }

        int S_Order = orderMapper.updateOrder(order);

        if (S_Order == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00016), ErrorCodeEnum.E00016.name());
        }

        return S_Order;
    }

    @Secured("ROLE_STORE")
    @Override
    public int putOrderInfo(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00021), ErrorCodeEnum.E00021.name());
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00022), ErrorCodeEnum.E00022.name());
        }

        String address = "";

        if (order.getAreaAddress() != null && order.getAreaAddress() != "") {
            address += order.getAreaAddress();
        }

        if (order.getDistrictAddress() != null && order.getDistrictAddress() != "") {
            address += " " + order.getDistrictAddress();
        }

        if (order.getStreetAddress() != null && order.getStreetAddress() != "") {
            address += " " + order.getStreetAddress();
        }

        if (order.getEstateAddress() != null && order.getEstateAddress() != "") {
            address += " " + order.getEstateAddress();
        }

        if (order.getBuildingAddress() != null && order.getBuildingAddress() != "") {
            address += " " + order.getBuildingAddress();
        }

        order.setAddress(address);

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
                throw new AppTrException(getMessage(ErrorCodeEnum.E00021), ErrorCodeEnum.E00021.name());
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00022), ErrorCodeEnum.E00022.name());
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
            redisService.setPublisher("order_updated", "id:"+order.getId()+", admin_id:"+storeDTO.getAdminId()+", store_id:"+storeDTO.getId());
        }

        return nRet;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int putOrderAssigned(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00023), ErrorCodeEnum.E00023.name());
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00024), ErrorCodeEnum.E00024.name());
        }

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            Rider rider = new Rider();
            rider.setToken(order.getToken());
            rider.setRole("ROLE_RIDER");
            SubGroupRiderRel subGroupRiderRel = riderMapper.selectMySubgroupRiderRels(rider);

            storeDTO.setId(subGroupRiderRel.getStoreId());
            storeDTO.setIsAdmin("0");
        }

        Store S_Store = storeMapper.selectStoreInfo(storeDTO);

        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            Rider rider = new Rider();
            rider.setToken(order.getToken());
            String assignmentStatus = riderMapper.selectRiderAssignmentStatus(rider);
            if (!assignmentStatus.equals("2")) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00027), ErrorCodeEnum.E00027.name());
            }
        } else if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            if (!S_Store.getAssignmentStatus().equals("0")) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00028), ErrorCodeEnum.E00028.name());
            }
        }

        Order orderAssigned = new Order();

        orderAssigned.setToken(order.getToken());
        orderAssigned.setId(order.getId());
        orderAssigned.setRiderId(order.getRiderId());
        orderAssigned.setStatus("1");
        orderAssigned.setAssignedDatetime(LocalDateTime.now().toString());

        Order combinedOrderAssigned = new Order();

        if (order.getCombinedOrderId() != null && order.getCombinedOrderId() != "") {
            combinedOrderAssigned.setId(order.getCombinedOrderId());
            combinedOrderAssigned.setRiderId(order.getRiderId());
            combinedOrderAssigned.setStatus("1");
            combinedOrderAssigned.setAssignedDatetime(LocalDateTime.now().toString());
            combinedOrderAssigned.setToken(order.getToken());

            int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

            if (selectCombinedOrderIsApprovalCompleted != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00023), ErrorCodeEnum.E00023.name());
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00024), ErrorCodeEnum.E00024.name());
            }

            this.putOrder(combinedOrderAssigned);
        }

        int ret = this.putOrder(orderAssigned);

        if (ret != 0) {
            redisService.setPublisher("order_assigned", "id:"+order.getId()+", admin_id:"+S_Store.getAdminId()+", store_id:"+S_Store.getId());

            if(authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
                ArrayList<String> tokens = (ArrayList)orderMapper.selectPushToken(S_Store.getSubGroup());
                if(tokens.size() > 0){
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_ASSIGN);
                    noti.setRider_id(Integer.valueOf(orderAssigned.getRiderId()));
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            }else{
                ArrayList<String> tokens = (ArrayList)orderMapper.selectPushToken(S_Store.getSubGroup());
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

    @Secured("ROLE_RIDER")
    @Override
    public int putOrderPickedUp(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00025), ErrorCodeEnum.E00025.name());
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00026), ErrorCodeEnum.E00026.name());
        }

        Order orderPickedUp = new Order();

        orderPickedUp.setToken(order.getToken());
        orderPickedUp.setId(order.getId());
        orderPickedUp.setStatus("2");
        orderPickedUp.setPickedUpDatetime(LocalDateTime.now().toString());

        Order combinedOrderPickedUp = new Order();

        if (order.getCombinedOrderId() != null && order.getCombinedOrderId() != "") {
            combinedOrderPickedUp.setId(order.getCombinedOrderId());
            combinedOrderPickedUp.setStatus("2");
            combinedOrderPickedUp.setPickedUpDatetime(LocalDateTime.now().toString());
            combinedOrderPickedUp.setToken(order.getToken());

            int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

            if (selectCombinedOrderIsApprovalCompleted != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00025), ErrorCodeEnum.E00025.name());
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00026), ErrorCodeEnum.E00026.name());
            }

            this.putOrder(combinedOrderPickedUp);
        }

        int result = this.putOrder(orderPickedUp);

        orderPickedUp.setId(order.getId());

        Order S_Order = orderMapper.selectOrderInfo(orderPickedUp);

        if (result != 0) {
            redisService.setPublisher("order_picked_up", "id:"+order.getId()+", admin_id:"+S_Order.getAdminId()+", store_id:"+S_Order.getStoreId());
        }

        return result;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int putOrderCompleted(Order order) throws AppTrException {
        Order orderCompleted = new Order();

        orderCompleted.setToken(order.getToken());
        orderCompleted.setId(order.getId());
        orderCompleted.setStatus("3");
        orderCompleted.setCompletedDatetime(LocalDateTime.now().toString());

        Order combinedOrderCompleted = new Order();

        if (order.getCombinedOrderId() != null && order.getCombinedOrderId() != "") {
            combinedOrderCompleted.setId(order.getCombinedOrderId());
            combinedOrderCompleted.setStatus("3");
            combinedOrderCompleted.setCompletedDatetime(LocalDateTime.now().toString());
            combinedOrderCompleted.setToken(order.getToken());

            this.putOrder(combinedOrderCompleted);
        }

        int result = this.putOrder(orderCompleted);

        orderCompleted.setId(order.getId());

        Order S_Order = orderMapper.selectOrderInfo(orderCompleted);

        if (result != 0) {
            redisService.setPublisher("order_completed", "id:"+order.getId()+", admin_id:"+S_Order.getAdminId()+", store_id:"+S_Order.getStoreId());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
                ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderToken(S_Order);
                if(tokens.size() > 0){
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_COMPLET);
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            }
        }

        return result;
    }


    @Secured("ROLE_STORE")
    @Override
    public int putOrderCanceled(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00019), ErrorCodeEnum.E00019.name());
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00020), ErrorCodeEnum.E00020.name());
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
                throw new AppTrException(getMessage(ErrorCodeEnum.E00019), ErrorCodeEnum.E00019.name());
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00020), ErrorCodeEnum.E00020.name());
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
            redisService.setPublisher("order_canceled", "id:"+order.getId()+", admin_id:"+storeDTO.getAdminId()+", store_id:"+storeDTO.getId());
        }

        return nRet;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int putOrderAssignCanceled(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00017), ErrorCodeEnum.E00017.name());
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00018), ErrorCodeEnum.E00018.name());
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
                throw new AppTrException(getMessage(ErrorCodeEnum.E00017), ErrorCodeEnum.E00017.name());
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00018), ErrorCodeEnum.E00018.name());
            }

            this.putOrder(combinedOrderAssignCanceled);
        }

        int ret = this.putOrder(orderAssignCanceled);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        orderAssignCanceled.setId(order.getId());

        Order S_Order = orderMapper.selectOrderInfo(orderAssignCanceled);

        if (ret != 0) {
            redisService.setPublisher("order_canceled", "id:"+order.getId()+", admin_id:"+S_Order.getAdminId()+", store_id:"+S_Order.getId());
            if(authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
                ArrayList<String> tokens = (ArrayList)riderMapper.selectRiderTokenByOrderId(orderAssignCanceled);
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

    @Secured({"ROLE_RIDER"})
    @Override
    public int postOrderConfirm(Order order) throws AppTrException {

        Rider currentRider = new Rider();
        currentRider.setAccessToken(order.getToken());
        currentRider.setToken(order.getToken());

        Rider S_Rider = riderMapper.getRiderInfo(currentRider);

        if (S_Rider.getSubGroupRiderRel() == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00009), ErrorCodeEnum.E00009.name());
        }

        List<OrderCheckAssignment> S_OrderConfirm = orderMapper.selectOrderConfirm(order);

        if (S_OrderConfirm.size() != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00013), ErrorCodeEnum.E00013.name());
        }

        int nRet = orderMapper.insertOrderConfirm(order);
        /*if (nRet != 0) {
            ArrayList<String> tokens = (ArrayList) riderMapper.selectRiderToken(order);
            if(tokens.size() > 0){
                Notification noti = new Notification();
                noti.setType(Notification.NOTI.ORDER_ASSIGN);
                CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                checkFcmResponse(pushNotification);
            }


        }*/
        return nRet;
    }

    @Secured({"ROLE_RIDER"})
    @Override
    public int postOrderDeny(Order order) throws AppTrException {

        Rider currentRider = new Rider();
        currentRider.setAccessToken(order.getToken());
        currentRider.setToken(order.getToken());

        Rider S_Rider = riderMapper.getRiderInfo(currentRider);

        if (S_Rider.getSubGroupRiderRel() == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00009), ErrorCodeEnum.E00009.name());
        }

        int orderDenyCount = orderMapper.selectOrderDenyCount(currentRider);
        if (orderDenyCount > 1) {
            currentRider.setWorking("2");
            riderMapper.updateWorkingRider(currentRider);
        }

        List<OrderCheckAssignment> S_OrderConfirm = orderMapper.selectOrderConfirm(order);
        if (S_OrderConfirm.size() != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00014), ErrorCodeEnum.E00014.name());
        }

        List<OrderCheckAssignment> S_OrderDeny = orderMapper.selectOrderDeny(order);

        if (S_OrderDeny.size() != 0 && S_Rider != null) {
            for (OrderCheckAssignment orderDeny : S_OrderDeny) {
                if (orderDeny.getRiderId().equals(S_Rider.getId())) {
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00010), ErrorCodeEnum.E00010.name());
                }

            }
        }

        if (order.getOrderCheckAssignment() != null) {
            if (order.getOrderCheckAssignment().getStatus() == null || order.getOrderCheckAssignment().getStatus() == "") {
                order.getOrderCheckAssignment().setStatus("0");
            }

        }

        int ret = 0;

        if (orderMapper.insertOrderDeny(order) != 0) {
            Order orderAssignCanceled = new Order();
            orderAssignCanceled.setRole("ROLE_RIDER");
            orderAssignCanceled.setId(order.getId());
            orderAssignCanceled.setStatus("0");
            orderAssignCanceled.setRiderId("-1");
            orderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
            orderAssignCanceled.setAssignedDatetime("-1");
            orderAssignCanceled.setPickedUpDatetime("-1");
            orderAssignCanceled.setToken(order.getToken());

            if (order.getCombinedOrderId() != null && order.getCombinedOrderId() != "") {
                Order combinedOrderAssignCanceled = new Order();
                orderAssignCanceled.setRole("ROLE_RIDER");
                combinedOrderAssignCanceled.setId(order.getCombinedOrderId());
                combinedOrderAssignCanceled.setStatus("0");
                combinedOrderAssignCanceled.setRiderId("-1");
                combinedOrderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
                combinedOrderAssignCanceled.setAssignedDatetime("-1");
                combinedOrderAssignCanceled.setPickedUpDatetime("-1");
                combinedOrderAssignCanceled.setToken(order.getToken());

                this.putOrder(combinedOrderAssignCanceled);
            }

            ret = this.putOrder(orderAssignCanceled);
        }
        return ret;
    }

    @Secured("ROLE_STORE")
    @Override
    public int putOrderAssignedFirst(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00023), ErrorCodeEnum.E00023.name());
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00024), ErrorCodeEnum.E00024.name());
        }

        order.setAssignedFirst("True");

        return this.putOrder(order);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STORE" , "ROLE_RIDER"})
    @Override
    public List<Reason> getOrderFirstAssignmentReason(Common common) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            common.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            common.setRole("ROLE_RIDER");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            common.setRole("ROLE_ADMIN");
        }

        List<Reason> reasonList = orderMapper.selectOrderFirstAssignmentReason(common);

        return reasonList;
    }



}
