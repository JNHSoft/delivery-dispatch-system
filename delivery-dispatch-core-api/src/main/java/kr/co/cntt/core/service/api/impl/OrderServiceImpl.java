package kr.co.cntt.core.service.api.impl;

import com.mysql.cj.util.StringUtils;
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
import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.OrderService;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.Misc;
import kr.co.cntt.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceSupport implements OrderService {
    @Value("${spring.mvc.locale}")
    private Locale locale;

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

    /**********************************************
     * 19-10-28
     * ????????? ?????? ?????? ?????? ??????
     **********************************************/
    @Override
    public void autoAssignOrder() throws AppTrException {
        Map<String, String> localeMap = new HashMap<>();
        localeMap.put("locale", locale.toString());
        List<Order> orderList = orderMapper.selectForAssignOrders(localeMap);

        log.debug(">>> autoAssign_GetOrderList:::: orderList: " + orderList);

        for (Order order : orderList) {
            Map map = new HashMap();
            map.put("order", order);
            Map denyOrderIdChkMap = new HashMap();
            denyOrderIdChkMap.put("orderId", order.getId());
            denyOrderIdChkMap.put("storeId", order.getStore().getId());
            // ???????????? ?????? ???????????? ???????????? ?????? ?????? ???
            denyOrderIdChkMap.put("adminId", order.getStore().getAdminId());
            List<Rider> riderList = riderMapper.selectForAssignRiders(denyOrderIdChkMap);
            // 19.12.23 ??? 3??? ???????????? ????????? ?????????
            List<Rider> astRiderList = riderMapper.selectForAssignRidersAssistant(denyOrderIdChkMap);
            // ??? 3??? ???????????? ????????? ??? ?????? ?????? ??????
            List<Rider> allowRiderList = astRiderList.stream()
                                                  .filter(x -> x.getShared_flag() == 1).collect(Collectors.toList());

            // ??? 3??? ???????????? ????????? ??? ????????? ??? ??????
            List<Rider> rejectRiderList = astRiderList.stream()
                    .filter(x -> x.getShared_flag() == 0).collect(Collectors.toList());
            List<Rider> duplicationRider = new ArrayList<>();

            // 20.05.29 ?????? ????????? ????????????, ?????? ?????? ??? ????????? ID ????????????.
            Map<String, String> searchMap = new HashMap<>();
            searchMap.put("id", order.getId());
            searchMap.put("distance", "300");        // ????????? ?????? ?????? (?????? : ??????)


            List<Order> firstAssignedRider = orderMapper.selectNearOrderRider(searchMap);

            allowRiderList.forEach(x -> {
                rejectRiderList.forEach(y->{
                    if (y.getId().equals(x.getId()) && y.getShared_sort() > x.getShared_sort()){
                        astRiderList.remove(x);
                    }
                });
            });

            astRiderList.removeAll(rejectRiderList);

            for (Iterator<Rider> riderX = astRiderList.iterator(); riderX.hasNext();
                 ) {
                Rider r = riderX.next();

                for (Iterator<Rider> riderY = astRiderList.iterator(); riderY.hasNext();){
                    Rider y = riderY.next();

                    if (r.getId().equals(y.getId()) && r.getShared_sort() > y.getShared_sort()){
//                        astRiderList.remove(y);
                        duplicationRider.add(y);
                    }
                }
            }

            astRiderList.removeAll(duplicationRider);
            riderList.addAll(astRiderList);

            /// 20.05.29 ?????? ????????? ???????????? ???????????? ?????? ??????
            if (firstAssignedRider.size() > 0){
                // ????????? ???????????? ????????? ???????????? ???????????? ????????????.
                List<Rider> removeRider = riderList.stream().filter(x ->{
                    // 20.07.02 ?????? ???????????? ?????? ?????? ?????? ?????? ??? ???
                    // 20.07.23 ?????? ???????????? ?????? ?????? ??? ??????
                    if ((Integer.parseInt(x.getAssignCount()) >= Integer.parseInt(order.getStore().getAssignmentLimit()) || x.getMinOrderStatus() == null)){
                    //if (x.getMinOrderStatus() == null){
                        return true;
                    }else{
                        switch (x.getMinOrderStatus()){
                            case "0":           /// ????????????
                            case "2":           //// ?????? ??????
                            case "3":           //// ?????? ??????
                            case "4":           //// ?????? ??????
                            case "5":           //// ????????????
                            case "6":           //// ??????
                                return true;
                            case "1":           //// ?????? ??????
                            default:

                                // ?????? ?????? ?????? ?????? ?????? ????????? ??? ???????????? ??????????????? ??????
                                if (firstAssignedRider.stream().filter(y -> {
                                    if (y.getRiderId().equals(x.getId())){
                                        return true;
                                    }else{
                                        return false;
                                    }
                                }).count() > 0){
                                    return false;
                                }else{
                                    return true;
                                }
                        }
                    }
                })
                .collect(Collectors.toList());

                for (Rider rmR:removeRider
                     ) {
                    firstAssignedRider.removeIf(x ->x.getRiderId().equals(rmR.getId()));
                }

                if (firstAssignedRider.size() > 0){
                    for (Rider rmR:removeRider
                    ) {
                        riderList.removeIf(x ->x.getId().equals(rmR.getId()));
                    }
                }
            }

            log.debug(">>> autoAssign_GetRiderList:::: riderList: " + riderList);
            log.debug(">>> autoAssign_GetOrderId:::: orderId: " + order.getId());
            log.debug(">>> autoAssign_GetStoreId:::: storeId: " + order.getStore().getId());
            Misc misc = new Misc();

            for (Iterator<Rider> rider = riderList.iterator(); rider.hasNext(); ) { //iterator??? ?????? for??? ????????? ????????? ????????????, map ??? fillter??? ?????? ??????
                Rider r = rider.next();
                if (r.getLatitude() != null) {
                    try {
                        r.setDistance(misc.getHaversine(order.getStore().getLatitude(), order.getStore().getLongitude(), r.getLatitude(), r.getLongitude()));
                        r.setDistance(r.getDistance() - r.getDistance() % 10);//?????? 10?????? ??????
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    riderList.remove(r);//??????????????? ?????? ????????? ??????
                }
            }
            log.debug(">>> autoAssignGetRider_Iterator_RiderList:::: Iterator_riderList: " + riderList);

            riderList = riderList.stream()
                    .filter(a -> Integer.parseInt(a.getAssignCount()) < Integer.parseInt(order.getStore().getAssignmentLimit()))//?????? ????????? ?????? ?????? ?????? ?????? ?????? ????????? ???????????? ***** ?????? ????????? ????????? ?????? ??????????????? ??????(???????????????)
//                    .filter(a->a.getDistance() <= Integer.parseInt(order.getStore().getRadius())*1000)// ?????? ????????? ???????????? 1?????? ?????? ??? ????????????
                    .filter(a -> a.getSubGroupRiderRel() != null && a.getSubGroupRiderRel().getStoreId() != null)      // ????????? ?????? ????????? ?????? ?????? ????????????.
                    .filter(a -> {
                        if (a.getSubGroupRiderRel().getSubGroupId() == null) {//?????? ???????????? ??????????????? ??????x -> getSubGroupRiderRel()??? storeId??? ????????? ?????? ????????? ????????????, ?????? ????????? ???????????? ???????????? ?????????
                                log.debug(">>> autoAssignRider_Stream First:::: Stream Boolean: " + a.getSubGroupRiderRel().getSubGroupId());
                            return a.getSubGroupRiderRel().getStoreId().equals(order.getStoreId());
                        } else if (order.getSubGroupStoreRel() != null && a.getReturnTime() == null) {//?????? ???????????? ??????????????? ??????, ??????????????? ?????? ???????????? ?????? -> ?????? ????????? ?????? ??????????????? ?????? ???, ????????? ????????? ????????? ?????? ???
                                log.debug(">>> autoAssignRider_Stream Second_1:::: Stream Boolean: " + order.getSubGroupStoreRel());
                                log.debug(">>> autoAssignRider_Stream Second_2:::: Stream Boolean: " + a.getReturnTime());
                            return a.getSubGroupRiderRel().getSubGroupId().equals(order.getSubGroupStoreRel().getSubGroupId());
                        } else {
                            log.debug(">>> autoAssignRider_Stream False:::: Stream False:::: ");
                            return false;
                        }
                    })
                    .filter(a -> !order.getId().equals((a.getOrderCheckAssignment() == null) ? "" : a.getOrderCheckAssignment().getOrderId()))//5??? ????????? ????????? ???????????? ??????
                    .sorted(Comparator.comparing(Rider::getMinPickedUpDatetime, Comparator.nullsFirst(Comparator.naturalOrder()))// 1?????? ???????????? ???????????? ??????(??????,??????) ??? ???????????? ????????? ????????????
                            .thenComparing(Rider::getAssignCount)//2?????? ???????????? ?????? ??????....
                            .thenComparing(Rider::getDistance)// 3?????? ?????????(10?????? ??????)           // 20.07.02 ????????? ????????? ?????? ??? ?????? ????????? ??????
                            .thenComparing(Rider::getMinOrderStatus, Comparator.nullsFirst(Comparator.naturalOrder()))) //4?????? ???????????? ???????????? ??????(??????,??????) ??? ???????????? ????????? ??????
                    .collect(Collectors.toList());


            if (!riderList.isEmpty()) {//riderList.size()!=0
                map.put("rider", riderList.get(0));

                log.debug(">>> autoAssign_GetRiderList::::: riderListMap: " + riderList.get(0));
                log.debug(">>> autoAssign_GetRiderList:::: riderListMap: " + riderList);
                log.debug(">>> autoAssign_GetRiderList_OrderId:::: riderListMap_OrderId: " + order.getId());
                this.autoAssignOrderProc(map);
            }
            else {
                log.debug(">>> autoAssign_GetRiderList Else:::: riderList_Else: " + order.getId());
//                throw new AppTrException(getMessage(ErrorCodeEnum.E00029, locale), ErrorCodeEnum.E00029.name());//?????? ????????? ???????????? ????????? ????????????.
            }
        }
    }

    public int autoAssignOrderProc(Map map) throws AppTrException {
        if (map.get("rider") == null) {
            log.debug(">>> autoAssignOrderProc_GetRiderList:::: riderListMap: " + map.get("rider"));
//            throw new AppTrException(getMessage(ErrorCodeEnum.E00029), ErrorCodeEnum.E00029.name());
            return -1;
        } else {
            Rider rider = (Rider) map.get("rider");
            Order order = (Order) map.get("order");

            order.setRole("ROLE_SYSTEM");
            order.setId(order.getRegOrderId());
            order.setRiderId(rider.getId());
            order.setStatus("1");
//            order.setAssignedDatetime(LocalDateTime.now().toString());
            order.setAssignedDatetime("-2");
            if (rider.getLatitude() != null && !rider.getLatitude().equals("")) {
                order.setAssignXy(rider.getLatitude() + "|" + rider.getLongitude());
            } else {
                order.setAssignXy("none");
            }

            ArrayList<String> tokens = (ArrayList) riderMapper.selectRiderToken(order);
            int result = orderMapper.updateOrder(order);

            Store storeDTO = new Store();
            storeDTO.setRole("ROLE_SYSTEM");
            storeDTO.setId(order.getStoreId());

            storeDTO = storeMapper.selectStoreInfo(storeDTO);


            log.debug(">>> autoAssignOrderProc:::: storeId: " + order.getStoreId() + ", orderId: " + order.getId() + ", regOrderId: " + order.getRegOrderId() + ", riderId: " + rider.getId());
            log.debug(">>> autoAssignOrderProc_Result ::::: Result: " + result);

            if (result != 0) {
                riderMapper.updateRiderOrderStandbyDateTime(rider);
                riderMapper.updateRiderOrderStandbyStatus(rider);
                if (storeDTO.getSubGroup() != null) {
                    redisService.setPublisher(Content.builder().type("order_assigned").id(order.getRegOrderId()).adminId(storeDTO.getAdminId()).storeId(order.getStoreId()).subGroupId(storeDTO.getSubGroup().getId()).build());
                } else {
                    redisService.setPublisher(Content.builder().type("order_assigned").id(order.getRegOrderId()).adminId(storeDTO.getAdminId()).storeId(order.getStoreId()).build());
                }
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_ASSIGN_AUTO);
                    noti.setId(order.getRegOrderId());
                    noti.setStoreName(order.getStore().getAdminId());
                    noti.setStoreName(order.getStore().getStoreName());
                    noti.setAddr(order.getAddress());

                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);

                    checkFcmResponse(pushNotification);
                }
            }

            return result;
        }

    }

    @Override
    public void reservationOrders() throws AppTrException {
        List<Order> reservationOrders = orderMapper.selectReservationOrders();
        if (reservationOrders.size() > 0) {
            for (Order order : reservationOrders) {
                if (order.getSubGroup() != null) {
                    redisService.setPublisher(Content.builder().type("order_new").id(order.getId()).adminId(order.getAdminId()).storeId(order.getStoreId()).subGroupId(order.getSubGroup().getId()).build());
                } else {
                    redisService.setPublisher(Content.builder().type("order_new").id(order.getId()).adminId(order.getAdminId()).storeId(order.getStoreId()).build());
                }
            }
        }

    }

    @Secured("ROLE_STORE")
    @Override
    public int postOrder(Order order) throws AppTrException {
        // orderList = null;

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        String address = "";

        /*if((locale.toString()).equals("zh_TW")){
            address += storeDTO.getDetailAddress();
        }*/

        if (order.getAreaAddress() != null && !order.getAreaAddress().equals("")) {
            address += order.getAreaAddress();
        }

        if (order.getDistrictAddress() != null && !order.getDistrictAddress().equals("")) {
            address += " " + order.getDistrictAddress();
        }

        if (order.getStreetAddress() != null && !order.getStreetAddress().equals("")) {
            address += " " + order.getStreetAddress();
        }

        if (order.getEstateAddress() != null && !order.getEstateAddress().equals("")) {
            address += " " + order.getEstateAddress();
        }

        if (order.getBuildingAddress() != null && !order.getBuildingAddress().equals("")) {
            address += " " + order.getBuildingAddress();
        }

        order.setAddress(address);

        // 20.02.03 ????????? ?????? ??? ????????? ??????, ???????????? ?????? ????????? ??????
        if (order.getItem_XA11() != null && order.getItem_XA12() != null
                && !(order.getItem_XA12().trim().equals("") && order.getItem_XA11().trim().equals(""))){
            order.setLatitude(order.getItem_XA12());
            order.setLongitude(order.getItem_XA11());
        }


        // 20.02.03 ?????? ?????? ???, ????????? ?????? ?????? ????????? ?????? ??????, ?????? ?????? ????????? ?????? ????????? ?????? ??????
        if (order.getLongitude() == null || order.getLatitude() == null || order.getLongitude().trim().equals("") || order.getLatitude().trim().equals("") ) {
            Geocoder geocoder = new Geocoder();

            try {
                Map<String, String> geo = geocoder.getLatLng(order.getAddress());
                if (geo.get("lat") != null && geo.get("lng") != null) {
                    order.setLatitude(geo.get("lat"));
                    order.setLongitude(geo.get("lng"));
                } else {
                    order.setLatitude("0");
                    order.setLongitude("0");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String orderLatitude = order.getLatitude();
        String orderLongitude = order.getLongitude();

        if (order.getDeliveryPrice() == null || order.getDeliveryPrice().equals("")) {
            order.setDeliveryPrice("0");
        }

        order.setTotalPrice(String.valueOf(Double.parseDouble(order.getMenuPrice()) + Double.parseDouble(order.getDeliveryPrice())));

        if (order.getCookingTime() == null || order.getCookingTime().equals("")) {
            order.setCookingTime("30");
        }

        if (order.getPaid() == null || order.getPaid().equals("")) {
            order.setPaid("0");
        } else if (!order.getPaid().equals("0") && !order.getPaid().equals("1") && !order.getPaid().equals("2") && !order.getPaid().equals("3")) {
            throw new AppTrException(getMessage(ErrorCodeEnum.S0002), ErrorCodeEnum.S0002.name());
        }

        order.setStatus("0");

        // ???????????? ??????
        if (order.getReservationDatetime() != null && !order.getReservationDatetime().equals("")) {
            order.setReservationDatetime(order.getReservationDatetime().substring(0, 12) + "00");
            order.setReservationStatus("1");
        } else {
            order.setReservationStatus("0");
            LocalDateTime ldt = LocalDateTime.now();
            // ??????????????? 5??? ??????
            int chkMinutes = 0;
            if (ldt.getMinute() % 5 != 0) {
                chkMinutes = 5 - ldt.getMinute() % 5;
            }

            LocalDateTime reserveLDT = LocalDateTime.now().plusMinutes(Integer.parseInt(order.getCookingTime()) + chkMinutes);
            String nowDate = String.format("%02d", reserveLDT.getYear())
                    + String.format("%02d", reserveLDT.getMonthValue())
                    + String.format("%02d", reserveLDT.getDayOfMonth())
                    + String.format("%02d", reserveLDT.getHour())
                    + String.format("%02d", reserveLDT.getMinute())
                    + "00";
            order.setReservationDatetime(nowDate);
        }

        // regOrder ?????? ??????
        int hasRegOrder = orderMapper.selectRegOrderIdCheck(order);

        if (hasRegOrder > 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00039), ErrorCodeEnum.E00039.name());
        }

        if (order.getWebOrderId() == null || order.getWebOrderId().equals("")) {
            order.setWebOrderId(order.getRegOrderId());
        }

        Misc misc = new Misc();

        if (orderLatitude != null && orderLongitude != null) {
            try {
                order.setDistance(Double.toString(misc.getHaversine(storeDTO.getLatitude(), storeDTO.getLongitude(), orderLatitude, orderLongitude) / (double) 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int postOrder = orderMapper.insertOrder(order);

        if (postOrder == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00011), ErrorCodeEnum.E00011.name());
        }

        if (postOrder != 0) {
            if (storeDTO.getAssignmentStatus().equals("1")) {
                this.autoAssignOrder();
            }
            if (storeDTO.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_new").orderId(order.getId()).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_new").orderId(order.getId()).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
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
            rider.setAccessToken(order.getToken());
            rider.setToken(order.getToken());

            Rider S_Rider = riderMapper.getRiderInfo(rider);

            List<Order> R_Order = new ArrayList<>();
            char[] allArray = {'0', '1', '2', '3', '4', '5', '6'};
            if (order.getStatus().equals("")) {
                statusArray = allArray;
            }
            if (statusArray != null) {
                for (char s : statusArray) {
                    if (s != '0' && s != '5') {
                        for (Order o : S_Order) {
                            if (o.getRiderId() != null) {
                                if (o.getRiderId().equals(S_Rider.getId())) {
                                    if (!R_Order.contains(o)) {
                                        R_Order.add(o);
                                    }
                                }
                            }
                        }
                    } else {
                        for (Order o : S_Order) {
                            if (!R_Order.contains(o)) {
                                R_Order.add(o);
                            }
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
        /*int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        if (selectOrderIsApprovalCompleted != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00021), ErrorCodeEnum.E00021.name());
        }*/
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00022), ErrorCodeEnum.E00022.name());
        }

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        String address = "";

        /*if((locale.toString()).equals("zh_TW")){
            address += storeDTO.getDetailAddress();
        }*/

        if (order.getAreaAddress() != null && !order.getAreaAddress().equals("")) {
            address += order.getAreaAddress();
        }

        if (order.getDistrictAddress() != null && !order.getDistrictAddress().equals("")) {
            address += " " + order.getDistrictAddress();
        }

        if (order.getStreetAddress() != null && !order.getStreetAddress().equals("")) {
            address += " " + order.getStreetAddress();
        }

        if (order.getEstateAddress() != null && !order.getEstateAddress().equals("")) {
            address += " " + order.getEstateAddress();
        }

        if (order.getBuildingAddress() != null && !order.getBuildingAddress().equals("")) {
            address += " " + order.getBuildingAddress();
        }

        order.setAddress(address);

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

        order.setRole("ROLE_STORE");
        // 20.07.01 ?????? ?????? ?????? ?????? ???, ????????? ?????? ?????? ???????????? ??????
        Order orgOrd = getOrderInfo(order);     // ?????? ??? ?????? ?????? ??????
        try {
            // ?????? ????????? ????????? ???????????? ????????? ????????? ??????.
            if (!StringUtils.isNullOrEmpty(order.getReservationDatetime()) && !StringUtils.isNullOrEmpty(orgOrd.getRiderId())){
                Date changeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(order.getReservationDatetime());             // ????????? ?????? ????????? ?????? ??????
                Date orgDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(orgOrd.getReservationDatetime());          // ????????? ????????? ?????? ??????

                Locale regionLocale = LocaleContextHolder.getLocale();

                //int iTimer = regionLocale.toString().equals("zh_TW") ? 31 : 51;     // ????????? ?????? ????????? ????????????

                // ????????? ?????? ????????? ?????? ???????????? 30?????? ???????????? ?????? (???????????? 31??? ??????)
                //if ((changeDate.getTime() - new Date().getTime()) / 1000 > (iTimer * 60)){
                // 20.08.19 ?????? ????????? ????????? ?????? ??? ?????? ????????? ?????? ?????? ????????? ????????? ????????? ??????
                if (!(changeDate.getTime() == orgDate.getTime()) && (orgOrd.getStatus().equals("0") || orgOrd.getStatus().equals("1") || orgOrd.getStatus().equals("5"))){
                    // ??????????????? ??? ?????? ?????? ex. ????????????
                    // 20.08.03 ?????? ?????? ???????????? ??????
                    // 20.08.28 ?????? ????????? ????????? ??????
                    /**
                     * ???????????? ??????
                     * ?????? ?????? ???????????? QT ????????? ????????????, QT ???????????? ?????? ????????? ?????????, ???????????? - QT ?????? ????????? ????????????.
                     * */
                    int orgQT = 0;

                    try {
                        orgQT = Integer.parseInt(orgOrd.getCookingTime());
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if (orgQT == 0){
                            orgQT = 30;
                        }
                    }

                    Date qtDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(changeDate)).minusMinutes(orgQT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

                    // ?????? ??????
                    if (new Date().getTime() < qtDate.getTime()){
                        order.setAssignedDatetime("-1");
                    }else{
                        order.setAssignedDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(qtDate));
                    }

                    putOrderAssignCanceled(order);
                    order.setAssignedDatetime(null);
                    log.info("########### Order Updated # Order Cancel Completed #############");
                    log.info(order.getId());
                    log.info(changeDate.toString());
                    log.info(orgOrd.getReservationDatetime());
                    log.info(orgOrd.getRiderId());
                    log.info("changeDate [?????? ?????? ??????] = " + changeDate);
                    log.info("orgDate [???????????? ??????] = " + orgDate);
                    log.info("qtDate [QT ?????? ??? ?????? ?????? ??????] = " + qtDate);
                    log.info("order.getAssignedDatetime [?????? ?????? ??????] = " + order.getAssignedDatetime());
                    log.info("########### Order Updated # Order Cancel Completed #############");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

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
        order.setArrivedDatetime(null);
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
                throw new AppTrException(getMessage(ErrorCodeEnum.E00021), ErrorCodeEnum.E00021.name());
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00022), ErrorCodeEnum.E00022.name());
            }

            this.putOrder(combinedOrder);
        }

        String tmpRegOrderId = order.getId();

        int nRet = this.putOrder(order);
        String tmpOrderId = order.getId();

        order.setId(tmpRegOrderId);
        if (nRet == 1) {
            Order curOrder = getOrderInfo(order);
            if (curOrder.getRiderId() != null && !curOrder.getRiderId().equals("")) {
                // ?????? ?????????????????? ??????
                ArrayList<String> tokens = (ArrayList) riderMapper.selectRiderTokenByOrderId(order);
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_CHANGE);
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            } else {
                // ?????? ?????? ??????????????? ??????
                if (storeDTO.getAssignmentStatus().equals("2")) {
                    ArrayList<String> tokens = (ArrayList) orderMapper.selectPushToken(storeDTO.getSubGroup());
                    if (tokens.size() > 0) {
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
            if (storeDTO.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_updated").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_updated").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
            }
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

            storeDTO.setIsAdmin("0");
            storeDTO.setId(subGroupRiderRel.getStoreId());
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

        Rider S_Rider = null;
        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            Rider tmpRider = new Rider();
            tmpRider.setToken(order.getToken());
            tmpRider.setAccessToken(order.getToken());
            S_Rider = riderMapper.getRiderInfo(tmpRider);
        } else if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            Rider tmpRider = new Rider();
            tmpRider.setIsAdmin("0");
            tmpRider.setToken(order.getToken());
            tmpRider.setAccessToken(order.getToken());
            tmpRider.setId(order.getRiderId());
            S_Rider = riderMapper.getRiderInfo(tmpRider);
        }

        Order orderAssigned = new Order();

        orderAssigned.setToken(order.getToken());
        orderAssigned.setId(order.getId());
        orderAssigned.setRiderId(order.getRiderId());
        orderAssigned.setStatus("1");
//        orderAssigned.setAssignedDatetime(LocalDateTime.now().toString());
        orderAssigned.setAssignedDatetime("-2");
//        orderAssigned.setCombinedOrderId(order.getCombinedOrderId());
        if (S_Rider.getLatitude() != null && !S_Rider.getLatitude().equals("")) {
            orderAssigned.setAssignXy(S_Rider.getLatitude() + "|" + S_Rider.getLongitude());
        } else {
            orderAssigned.setAssignXy("none");
        }

        Order combinedOrderAssigned = new Order();

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            combinedOrderAssigned.setId(order.getCombinedOrderId());
            combinedOrderAssigned.setRiderId(order.getRiderId());
            combinedOrderAssigned.setStatus("1");
            combinedOrderAssigned.setAssignedDatetime(LocalDateTime.now().toString());
            combinedOrderAssigned.setToken(order.getToken());
//            combinedOrderAssigned.setCombinedOrderId(order.getId());
            if (S_Rider.getLatitude() != null && !S_Rider.getLatitude().equals("")) {
                combinedOrderAssigned.setAssignXy(S_Rider.getLatitude() + "|" + S_Rider.getLongitude());
            } else {
                combinedOrderAssigned.setAssignXy("none");
            }

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

        String tmpOrderId = orderAssigned.getId();

        if (ret != 0) {
            if (S_Store.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_assigned").id(tmpOrderId).adminId(S_Store.getAdminId()).storeId(S_Store.getId()).subGroupId(S_Store.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_assigned").id(tmpOrderId).adminId(S_Store.getAdminId()).storeId(S_Store.getId()).build());
            }

            if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
                ArrayList<String> tokens = (ArrayList) orderMapper.selectPushToken(S_Store.getSubGroup());
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_ASSIGN);
                    noti.setRider_id(Integer.valueOf(orderAssigned.getRiderId()));
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            } else {
                ArrayList<String> tokens = (ArrayList) orderMapper.selectPushToken(S_Store.getSubGroup());
                if (tokens.size() > 0) {
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
        orderPickedUp.setPickupXy(order.getLatitude() + "|" + order.getLongitude());

        Order combinedOrderPickedUp = new Order();

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            combinedOrderPickedUp.setId(order.getCombinedOrderId());
            combinedOrderPickedUp.setStatus("2");
            combinedOrderPickedUp.setPickedUpDatetime(LocalDateTime.now().toString());
            combinedOrderPickedUp.setToken(order.getToken());
//            combinedOrderPickedUp.setPickupXy(order.getPickupXy());
            combinedOrderPickedUp.setPickupXy(order.getLatitude() + "|" + order.getLongitude());

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

        String tmpOrderId = orderPickedUp.getId();

        orderPickedUp.setId(order.getId());
        orderPickedUp.setRole("ROLE_RIDER");

        Order S_Order = orderMapper.selectOrderInfo(orderPickedUp);

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());
        storeDTO.setIsAdmin("0");
        storeDTO.setId(S_Order.getStoreId());
        Store S_Store = storeMapper.selectStoreInfo(storeDTO);

        if (result != 0) {
            if (S_Store.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_picked_up").id(tmpOrderId).orderId(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Order.getStoreId()).subGroupId(S_Store.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_picked_up").id(tmpOrderId).orderId(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Order.getStoreId()).build());
            }
        }

        return result;
    }

    @Secured("ROLE_RIDER")
    @Override
    public int putOrderArrived(Order order) throws AppTrException{
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00025), ErrorCodeEnum.E00025.name());
        }

        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00026), ErrorCodeEnum.E00026.name());
        }

        Order orderArrived = new Order();

        orderArrived.setToken(order.getToken());
        orderArrived.setId(order.getId());
        orderArrived.setStatus("6");
        orderArrived.setArrivedDatetime(LocalDateTime.now().toString());

        Order combinedOrderArrived = new Order();

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            combinedOrderArrived.setId(order.getCombinedOrderId());
            combinedOrderArrived.setStatus("6");
            combinedOrderArrived.setArrivedDatetime(LocalDateTime.now().toString());
            combinedOrderArrived.setToken(order.getToken());

            int selectCombinedOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
            int selectCombinedOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

            if (selectCombinedOrderIsApprovalCompleted != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00025), ErrorCodeEnum.E00025.name());
            }

            if (selectCombinedOrderIsCompletedIsCanceled != 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00026), ErrorCodeEnum.E00026.name());
            }

            this.putOrder(combinedOrderArrived);
        }

        int result = this.putOrder(orderArrived);

        String tmpOrderId = orderArrived.getId();

        orderArrived.setId(order.getId());
        orderArrived.setRole("ROLE_RIDER");

        Order S_Order = orderMapper.selectOrderInfo(orderArrived);

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());
        storeDTO.setIsAdmin("0");
        storeDTO.setId(S_Order.getStoreId());
        Store S_Store = storeMapper.selectStoreInfo(storeDTO);

        if (result != 0) {
            if (S_Store.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_arrived").id(tmpOrderId).orderId(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Order.getStoreId()).subGroupId(S_Store.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_arrived").id(tmpOrderId).orderId(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Order.getStoreId()).build());
            }
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
        orderCompleted.setCompleteXy(order.getLatitude() + "|" + order.getLongitude());

        Order combinedOrderCompleted = new Order();

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            combinedOrderCompleted.setId(order.getCombinedOrderId());
            combinedOrderCompleted.setStatus("3");
            combinedOrderCompleted.setCompletedDatetime(LocalDateTime.now().toString());
            combinedOrderCompleted.setToken(order.getToken());
//            combinedOrderCompleted.setCompleteXy(order.getCompleteXy());
            combinedOrderCompleted.setCompleteXy(order.getLatitude() + "|" + order.getLongitude());

            this.putOrder(combinedOrderCompleted);
        }

        int result = this.putOrder(orderCompleted);

        String tmpOrderId = orderCompleted.getId();

        orderCompleted.setId(order.getId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            orderCompleted.setRole("ROLE_STORE");
        } else {
            orderCompleted.setRole("ROLE_RIDER");
        }

        Order S_Order = orderMapper.selectOrderInfo(orderCompleted);

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            storeDTO.setIsAdmin("0");
            storeDTO.setId(S_Order.getStoreId());
        }

        /**
         * ?????? ????????? ????????? ???????????? ????????? ??????
         * */
        Store S_Store = storeMapper.selectStoreInfo(storeDTO);

        System.out.println("####################################");
        System.out.println("storeDTO =[" + storeDTO + "]");
        System.out.println("####################################");

        if (result != 0) {
            if (S_Store.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_completed").id(tmpOrderId).orderId(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Order.getStoreId()).subGroupId(S_Store.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_completed").id(tmpOrderId).orderId(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Order.getStoreId()).build());
            }

            if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
                ArrayList<String> tokens = (ArrayList) riderMapper.selectRiderToken(S_Order);
                if (tokens.size() > 0) {
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

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
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

        String tmpOrderId = orderCanceled.getId();

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if (nRet == 1) {
            Order curOrder = getOrderInfo(order);
            if (curOrder.getRiderId() != null && !curOrder.getRiderId().equals("")) {
                // ?????? ?????????????????? ??????
                ArrayList<String> tokens = (ArrayList) riderMapper.selectRiderTokenByOrderId(order);
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_CANCEL);
                    CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                    checkFcmResponse(pushNotification);
                }
            } else {
                // ?????? ?????? ??????????????? ??????
                if (storeDTO.getAssignmentStatus().equals("2")) {
                    ArrayList<String> tokens = (ArrayList) orderMapper.selectPushToken(storeDTO.getSubGroup());
                    if (tokens.size() > 0) {
                        Notification noti = new Notification();
                        noti.setType(Notification.NOTI.ORDER_CANCEL);
                        CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                        checkFcmResponse(pushNotification);
                    }
                }
            }
        }

        if (nRet != 0) {
            if (storeDTO.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_canceled").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_canceled").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
            }
        }

        return nRet;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int putOrderAssignCanceled(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
        ArrayList<String> tokens = (ArrayList) riderMapper.selectRiderTokenByOrderId(order);      // ???????????? 20.07.16 ????????? ???????????? ?????? ???????????????.

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

        // 20.08.03 ???????????? ?????? ???, assign ?????? ????????? ??? ????????? ??????
        if(order.getAssignedDatetime() != null && order.getAssignedDatetime() == "-1"){
            orderAssignCanceled.setAssignedDatetime("-1");
        }else{
            orderAssignCanceled.setAssignedDatetime(order.getAssignedDatetime());
        }


        orderAssignCanceled.setPickedUpDatetime("-1");
        orderAssignCanceled.setArrivedDatetime("-1");
        orderAssignCanceled.setToken(order.getToken());

        Order combinedOrderAssignCanceled = new Order();

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            combinedOrderAssignCanceled.setId(order.getCombinedOrderId());
            combinedOrderAssignCanceled.setStatus("5");
            combinedOrderAssignCanceled.setRiderId("-1");
            combinedOrderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
            // 20.08.03 ???????????? ?????? ???, assign ?????? ????????? ??? ????????? ??????
            if (order.getAssignedDatetime() != null && order.getAssignedDatetime() == "-1"){
                combinedOrderAssignCanceled.setAssignedDatetime("-1");
            }else{
                combinedOrderAssignCanceled.setAssignedDatetime(order.getAssignedDatetime());
            }

            combinedOrderAssignCanceled.setPickedUpDatetime("-1");
            combinedOrderAssignCanceled.setArrivedDatetime("-1");
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

        String tmpRegOrderId = order.getId();

        int ret = this.putOrder(orderAssignCanceled);

        String tmpOrderId = orderAssignCanceled.getId();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        orderAssignCanceled.setId(order.getId());

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            orderAssignCanceled.setRole("ROLE_STORE");
        } else {
            orderAssignCanceled.setRole("ROLE_RIDER");
        }
        Order S_Order = orderMapper.selectOrderInfo(orderAssignCanceled);


        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            storeDTO.setIsAdmin("0");
            storeDTO.setId(S_Order.getStoreId());
        }

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if (ret != 0) {
            if (S_Order.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_assign_canceled").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_assign_canceled").id(tmpOrderId).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
            }
            if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
                if (tokens.size() > 0) {
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
        order.setRole("ROLE_RIDER");

        Order needOrderId = orderMapper.selectOrderInfo(order);
        needOrderId.setToken(order.getToken());

        Rider currentRider = new Rider();
        currentRider.setAccessToken(order.getToken());
        currentRider.setToken(order.getToken());

        Rider S_Rider = riderMapper.getRiderInfo(currentRider);

        if (S_Rider.getSubGroupRiderRel() == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00009), ErrorCodeEnum.E00009.name());
        }

        List<OrderCheckAssignment> S_OrderConfirm = orderMapper.selectOrderConfirm(needOrderId);

//        ????????????????????? ?????? ?????? ??????.. DB ???????????? ????????? ????????? ????????? ?????? ??? ?????????
//        ?????? ?????? ??? ?????? ??????????????? ?????? ?????? ????????????
//        if (S_OrderConfirm.size() != 0) {
//            log.info("555555555555555555555555555555555555555555555555555555555555");
//            throw new AppTrException(getMessage(ErrorCodeEnum.E00013), ErrorCodeEnum.E00013.name());
//        }

        int nRet = orderMapper.insertOrderConfirm(needOrderId);
        if (nRet != 0) {
            riderMapper.updateRiderOrderStandbyStatus(order);
        }
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
        order.setRole("ROLE_RIDER");

        Order needOrderId = orderMapper.selectOrderInfo(order);
        needOrderId.setToken(order.getToken());
        needOrderId.setOrderCheckAssignment(order.getOrderCheckAssignment());

        Rider currentRider = new Rider();
        currentRider.setAccessToken(order.getToken());
        currentRider.setToken(order.getToken());

        Rider S_Rider = riderMapper.getRiderInfo(currentRider);

        if (S_Rider.getSubGroupRiderRel() == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00009), ErrorCodeEnum.E00009.name());
        }

/*
        int orderDenyCount = orderMapper.selectOrderDenyCount(currentRider);
        if (orderDenyCount > 1) {
            currentRider.setWorking("2");
            riderMapper.updateWorkingRider(currentRider);
        }
*/

        List<OrderCheckAssignment> S_OrderConfirm = orderMapper.selectOrderConfirm(needOrderId);

//        if (S_OrderConfirm.size() != 0) {
//            throw new AppTrException(getMessage(ErrorCodeEnum.E00014), ErrorCodeEnum.E00014.name());
//        }

        List<OrderCheckAssignment> S_OrderDeny = orderMapper.selectOrderDeny(needOrderId);

        if (S_OrderDeny.size() != 0 && S_Rider != null) {
            for (OrderCheckAssignment orderDeny : S_OrderDeny) {
                if (orderDeny.getRiderId().equals(S_Rider.getId())) {
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00010), ErrorCodeEnum.E00010.name());
                }

            }
        }

        if (order.getOrderCheckAssignment() != null) {
            if (order.getOrderCheckAssignment().getStatus() == null || order.getOrderCheckAssignment().getStatus().equals("")) {
                order.getOrderCheckAssignment().setStatus("0");
            }

        }

        int ret = 0;

        if (orderMapper.insertOrderDeny(needOrderId) != 0) {
            Order orderAssignCanceled = new Order();
            orderAssignCanceled.setRole("ROLE_RIDER");
            orderAssignCanceled.setId(order.getId());
            orderAssignCanceled.setStatus("0");
            orderAssignCanceled.setRiderId("-1");
            orderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
            orderAssignCanceled.setAssignedDatetime("-1");
            orderAssignCanceled.setPickedUpDatetime("-1");
            orderAssignCanceled.setArrivedDatetime("-1");
            orderAssignCanceled.setToken(order.getToken());

            if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
                Order combinedOrderAssignCanceled = new Order();
                orderAssignCanceled.setRole("ROLE_RIDER");
                combinedOrderAssignCanceled.setId(order.getCombinedOrderId());
                combinedOrderAssignCanceled.setStatus("0");
                combinedOrderAssignCanceled.setRiderId("-1");
                combinedOrderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());
                combinedOrderAssignCanceled.setAssignedDatetime("-1");
                combinedOrderAssignCanceled.setPickedUpDatetime("-1");
                combinedOrderAssignCanceled.setArrivedDatetime("-1");
                combinedOrderAssignCanceled.setToken(order.getToken());

                this.putOrder(combinedOrderAssignCanceled);
            }

            ret = this.putOrder(orderAssignCanceled);
        }
        // ????????? ???????????? ?????? ?????? ????????? ?????? ?????? ??????
//        if (!locale.toString().equals("zh_TW")) {
        int orderDenyCount = orderMapper.selectOrderDenyCount(currentRider);
        if (orderDenyCount > 1) {
            currentRider.setWorking("2");
            riderMapper.updateWorkingRider(currentRider);

            // ?????? ?????????????????? ??????
            Order pushOrder = new Order();
            pushOrder.setRiderId(S_Rider.getId());
            ArrayList<String> tokens = (ArrayList) riderMapper.selectRiderToken(pushOrder);
            if (tokens.size() > 0) {
                Notification noti = new Notification();
                noti.setType(Notification.NOTI.RIDER_WORKING_OFF);
                CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.sendGroup(tokens, noti);
                checkFcmResponse(pushNotification);
            }
        }
//        }

        Store storeDTO = new Store();
        storeDTO.setId(needOrderId.getStoreId());
        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if (ret != 0) {
            riderMapper.updateRiderOrderStandbyStatus(order);
            if (storeDTO.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_denied").id(needOrderId.getId()).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_denied").id(needOrderId.getId()).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
            }
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

    @Secured({"ROLE_ADMIN", "ROLE_STORE", "ROLE_RIDER"})
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

    @Secured("ROLE_RIDER")
    @Override
    public int putOrderReturn(Order order) throws AppTrException {
        order.setRole("ROLE_RIDER");
        Order needOrderId = orderMapper.selectOrderInfo(order);

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            Order combinedOrder = new Order();

            combinedOrder.setReturnDatetime(LocalDateTime.now().toString());
            combinedOrder.setToken(order.getToken());
            combinedOrder.setId(order.getCombinedOrderId());

            this.putOrder(combinedOrder);
        }

        order.setReturnDatetime(LocalDateTime.now().toString());

        int ret = this.putOrder(order);

        Store storeDTO = new Store();
        storeDTO.setId(needOrderId.getStoreId());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        if (ret != 0) {
            if (storeDTO.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_denied").id(needOrderId.getId()).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).subGroupId(storeDTO.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_denied").id(needOrderId.getId()).adminId(storeDTO.getAdminId()).storeId(storeDTO.getId()).build());
            }
        }

        return ret;
    }

}
