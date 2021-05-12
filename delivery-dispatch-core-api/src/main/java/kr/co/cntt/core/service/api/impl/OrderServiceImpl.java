package kr.co.cntt.core.service.api.impl;

import com.github.pagehelper.util.StringUtil;
import com.mysql.cj.util.StringUtils;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.fcm.FirebaseResponse;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.fcm.FcmBody;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
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
     * 라이더 자동 배정 순위 변경
     * 21-02-19
     * 라이더 쉐어 방식 변경
     *  #기존 => 하위 그룹 간 전체 라이더 공유
     *  #변경 => 하위 그룹 중 매장 or 관리자에서 공유를 허용한 라이더만 쉐어
     *  # 라이더 자동 배정 순위 변경은 다음과 같이 진행 될 예정
     *    --> ???
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
            // 추가되어 있는 인덱스를 활용하여 조회 속도 업
            denyOrderIdChkMap.put("adminId", order.getStore().getAdminId());
            List<Rider> riderList = riderMapper.selectForAssignRiders(denyOrderIdChkMap);

            // 20.05.29 주문 번호를 이용하여, 거리 측정 및 라이더 ID 가져오기.
            Map<String, String> searchMap = new HashMap<>();
            searchMap.put("id", order.getId());
            searchMap.put("distance", "300");        // 목적지 반경 거리 (단위 : 미터)

            List<Order> firstAssignedRider = orderMapper.selectNearOrderRider(searchMap);

            /// 20.05.29 반경 범위의 라이더가 존재하는 경우 작업
            if (firstAssignedRider.size() > 0){
                // 라이더 범위에서 제외가 되어야될 아이들을 추출한다.
                List<Rider> removeRider = riderList.stream().filter(x ->{
                    // 20.07.02 케인 요청으로 배달 제한 수는 제거 할 것
                    // 20.07.23 대만 요청으로 배달 제한 수 추가
                    if ((Integer.parseInt(x.getAssignCount()) >= Integer.parseInt(order.getStore().getAssignmentLimit()) || x.getMinOrderStatus() == null)){
                        System.out.println("################## true => " + x.getId() + " # " + x.getAssignCount());
                        return true;
                    }else{
                        switch (x.getMinOrderStatus()){
                            case "0":           /// 신규주문
                            case "2":           //// 픽업 완료
                            case "3":           //// 복귀 완료
                            case "4":           //// 주문 취소
                            case "5":           //// 신규주문
                            case "6":           //// 도착
                                return true;
                            case "1":           //// 배정 완료
                            default:
                                // 특정 구역 범위 내에 이미 배정이 된 라이더가 존재하는지 확인
                                // 21.04.26 소속된 라이더의 스토어와 주문의 스토어가 같은지 확인하는 절차가 필요로 한다. subGroupRiderRel_store_id
                                return firstAssignedRider.stream().filter(y -> y.getRiderId().equals(x.getId()) && y.getStoreId().equals(x.getSubGroupRiderRel().getStoreId())).count() <= 0;
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

            // 21.02.21 NULL 오류 발생으로 프로세스 변경
            List<Rider> deleteRiderList = new ArrayList<>();

            for (Rider r : riderList) { //iterator를 써야 for문 안에서 리스트 제거가능, map 과 fillter로 이동 고려
                if (r.getLatitude() != null && r.getLongitude() != null) {
                    try {
                        r.setDistance(misc.getHaversine(order.getStore().getLatitude(), order.getStore().getLongitude(), r.getLatitude(), r.getLongitude()));
                        //r.setDistance(r.getDistance() - r.getDistance() % 100); // 0~100M => 0, 101M ~ 201M => 1
                        r.setDistance(r.getDistance() / 100); // 100M 범위로 변경
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                } else {
                    deleteRiderList.add(r);
                }
            }

            // 21.02.21 삭제해야될 라이더가 있는 경우, List에서 제외하기
            if (deleteRiderList.size() > 0){
                riderList.removeAll(deleteRiderList);
            }

            log.debug(">>> autoAssignGetRider_Iterator_RiderList:::: Iterator_riderList: " + riderList);

            riderList = riderList.stream()
                    .filter(a -> Integer.parseInt(a.getAssignCount()) < Integer.parseInt(order.getStore().getAssignmentLimit()))//해당 주문의 상점 기준 최대 오더 개수 안넘는 라이더만 ***** 해당 라이더 상점의 최대 주문개수가 아님(바꿔야하나)
//                    .filter(a->a.getDistance() <= Integer.parseInt(order.getStore().getRadius())*1000)// 해당 주문의 상점기준 1키로 반경 내 라이더만
                    .filter(a -> a.getSubGroupRiderRel() != null && a.getSubGroupRiderRel().getStoreId() != null)      // 소속된 매장 정보가 없는 경우 제외한다.
                    .filter(a -> {
                        if (a.getSubGroupRiderRel().getSubGroupId() == null) {//해당 라이더의 서브그룹이 존재x -> getSubGroupRiderRel()은 storeId를 가지고 있기 때문에 항상존재, 해당 주문의 스토어에 해당하는 라이더
                                log.debug(">>> autoAssignRider_Stream First:::: Stream Boolean: " + a.getSubGroupRiderRel().getSubGroupId());
                            return a.getSubGroupRiderRel().getStoreId().equals(order.getStoreId());
                        } else if (order.getSubGroupStoreRel() != null && a.getReturnTime() == null) {//해당 라이더의 서브그룹이 존재, 해당주문의 상점 서브그룹 존재 -> 해당 주문의 상점 서브그룹과 같을 때, 라이더 재배치 상태가 아닐 때
                                log.debug(">>> autoAssignRider_Stream Second_1:::: Stream Boolean: " + order.getSubGroupStoreRel());
                                log.debug(">>> autoAssignRider_Stream Second_2:::: Stream Boolean: " + a.getReturnTime());
                            return a.getSubGroupRiderRel().getSubGroupId().equals(order.getSubGroupStoreRel().getSubGroupId());
                        } else {
                            log.debug(">>> autoAssignRider_Stream False:::: Stream False:::: ");
                            return false;
                        }
                    })
                    .filter(a -> !order.getId().equals((a.getOrderCheckAssignment() == null) ? "" : a.getOrderCheckAssignment().getOrderId()))//5분 이내에 거절한 오더인지 확인
                    .filter(a -> {
                        if (a.getMinOrderStatus() == null){
                            return true;
                        }

                        switch (a.getMinOrderStatus()){
                            case "2":       // 픽업
                            case "6":       // 도착
                                return false;
                            default:
                                return true;
                        }
                    })                  // 주문 상태 값이 픽업 또는 배달 중인 경우 삭제
                    .sorted(Comparator.comparing(Rider::getSharedStatus, Comparator.nullsLast(Comparator.naturalOrder()))       // 1순위 쉐어 내용에 따른 정렬
                            .thenComparing(Rider::getMinOrderStatus, Comparator.nullsFirst(Comparator.naturalOrder()))          // 2순위 상태값 정렬 (현재는 배정과 미배정만 나온다)
                            .thenComparing(Rider::getDistance)                                                                  // 3순위 거리 순
                            .thenComparing(Rider::getAssignCount)                                                               // 4순위 주문을 가지고 있는 순서
                            .thenComparing(Rider::getSubGroupRiderRel, (o1, o2) -> {
                                int sameStore1 = o1.getStoreId() == order.getStoreId() ? 1 : 2;
                                int sameStore2 = o2.getStoreId() == order.getStoreId() ? 1 : 2;

                                return sameStore1 > sameStore2 ? 2 : (sameStore1 == sameStore2 ? 1 : 0);
                            })                                                                                                  // 5순위 주문이 들어간 매장의 라이더가 먼저 배정 될 수 있도록 적용
//                            .thenComparing(Rider->Rider, (o1, o2) -> {
//                                int iResult;
//
//                                // Rider의 값을 전체적으로 비교하여 정렬한다. 기본 주문 상태
//                                int minStatus1;
//                                switch (o1.getMinOrderStatus())
//                                {
//                                    case "2":           // 픽업 상태
//                                        minStatus1 = 3;
//                                        break;
//                                    case "6":           //  도착 상태
//                                        minStatus1 = 4;
//                                        break;
//                                    default:            // 위 3가지 상황 이외의 상태
//                                        minStatus1 = 1;
//                                        break;
//                                }
//
//                                int minStatus2;
//                                switch (o2.getMinOrderStatus()){
//                                    case "2":           // 픽업 상태
//                                        minStatus2 = 3;
//                                        break;
//                                    case "6":           //  도착 상태
//                                        minStatus2 = 4;
//                                        break;
//                                    default:            // 위 3가지 상황 이외의 상태
//                                        minStatus2 = 1;
//                                        break;
//                                }
//
//                                // iResult 값 정하기
//                                if (minStatus1 == minStatus2 && minStatus1 == 1){
//                                    //
//                                }
//
//                                return minStatus1 < minStatus2 ? 0 : (minStatus1 == minStatus2 ? 1 : 2);
//
//
//                            })
                    )

//                    .sorted(Comparator.comparing(Rider::getSharedStatus, Comparator.reverseOrder()) // 1순위 주문된 매장의 라이더가 아닌 경우 1위}) // 1순위 주문된 매장의 라이더가 아닌 경우 1위
//                            .thenComparing(Rider::getDistance)                                      // 4순위 거리순(10미터 단위)             // 20.07.02 라이더 오더가 적은 순 부터 적용을 한다
//                            .thenComparing(Rider::getAssignCount)                                   //3순위 라이더의 오더 개수....
//                            .thenComparing(Rider::getMinOrderStatus, Comparator.nullsFirst(Comparator.naturalOrder()))
//                            ) //5순위 라이더가 들고있는 주문(배정,픽업) 중 가장빠른 주문의 상태


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
            }
        }
    }

    public int autoAssignOrderProc(Map map) throws AppTrException {
        if (map.get("rider") == null) {
            log.debug(">>> autoAssignOrderProc_GetRiderList:::: riderListMap: " + map.get("rider"));
            return -1;
        } else {
            Rider rider = (Rider) map.get("rider");
            Order order = (Order) map.get("order");

            order.setRole("ROLE_SYSTEM");
            order.setId(order.getRegOrderId());
            order.setRiderId(rider.getId());
            order.setStatus("1");

            order.setAssignedDatetime("-2");
            if (rider.getLatitude() != null && !rider.getLatitude().equals("")) {
                order.setAssignXy(rider.getLatitude() + "|" + rider.getLongitude());
            } else {
                order.setAssignXy("none");
            }

            ArrayList<Map> tokens = (ArrayList) riderMapper.selectRiderToken(order);

            // 21.04.26 주문을 다시 확인하여 배정이 되었는지 체크한다.
            Order checkOrder = orderMapper.selectOrderInfo(order);

            if (checkOrder != null){
                log.info("자동 배정 중 라이더가 이미 배정이 되어 종료 되었습니다. # Order Reg Order ID = " + checkOrder.getRegOrderId() + " # Order ID = " + checkOrder.getId() + " # 이미 배정된 라이더 = " + checkOrder.getRiderId() + " # 배정 될 라이더 = " + order.getRiderId());
                return 0;
            }

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

                    // PUSH 객체로 변환 후 전달
                    FcmBody fcmBody = new FcmBody();

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("obj", noti);

                    fcmBody.setData(obj);
                    fcmBody.setPriority("high");

                    fcmBody.getNotification().setTitle(getMessage("ASSIGN.ORDER"));
                    fcmBody.getNotification().setBody(getMessage("ASSIGN.ORDER"));

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
                            log.error(e.getMessage());
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
                            ArrayList<String> androidTokenValue = new ArrayList<>();

                            android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(androidTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                            checkFcmResponse(androidPushNotification);
                        }catch (Exception e){
                            log.error(e.getMessage());
                        }
                    }


                    oldMap.addAll(tokens.stream().filter(x->{
                        if (x.getOrDefault("appType", "").toString().equals("")){
                            return true;
                        }

                        return false;
                    }).collect(Collectors.toList()));

                    // old android push
                    if (iosMap.size() > 0){
                        try {
                            ArrayList<String> oldTokenValue = new ArrayList<>();

                            oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(oldTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                            checkFcmResponse(oldPushNotification);
                        }catch (Exception e){
                            log.error(e.getMessage());
                        }
                    }
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
        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        String address = "";

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

        // 20.02.03 좌표값 변수 값 사용이 달라, 공통적인 변수 값으로 변경
        if (order.getItem_XA11() != null && order.getItem_XA12() != null
                && !(order.getItem_XA12().trim().equals("") && order.getItem_XA11().trim().equals(""))){
            order.setLatitude(order.getItem_XA12());
            order.setLongitude(order.getItem_XA11());
        }


        // 20.02.03 주문 등록 시, 좌표가 있는 경우 요청한 좌표 사용, 없는 경우 입력된 주소 값으로 좌표 사용
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
//                e.printStackTrace();
                log.error(e.getMessage());
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

        // 예약시간 설정
        if (order.getReservationDatetime() != null && !order.getReservationDatetime().equals("")) {
            order.setReservationDatetime(order.getReservationDatetime().substring(0, 12) + "00");
            order.setReservationStatus("1");
        } else {
            order.setReservationStatus("0");
            LocalDateTime ldt = LocalDateTime.now();
            // 예약시간은 5분 단위
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

        // regOrder 중복 체크
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
//                e.printStackTrace();
                log.error(e.getMessage());
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

        try {
            float menuPrice = Float.parseFloat(S_Order.getMenuPrice());
            float deliveryPrice = Float.parseFloat(S_Order.getDeliveryPrice());
            float totalPrice = Float.parseFloat(S_Order.getTotalPrice());

            DecimalFormat numberFormat = new DecimalFormat("#0.##");

            S_Order.setMenuPrice(numberFormat.format(menuPrice));
            S_Order.setDeliveryPrice(numberFormat.format(deliveryPrice));
            S_Order.setTotalPrice(numberFormat.format(totalPrice));

        }catch (Exception e){
//            e.printStackTrace();
            log.error(e.getMessage());
        }


        if (S_Order == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00016), ErrorCodeEnum.E00016.name());
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
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);
        if (selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00022), ErrorCodeEnum.E00022.name());
        }

        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        storeDTO = storeMapper.selectStoreInfo(storeDTO);

        String address = "";

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

        order.setRole("ROLE_STORE");
        // 20.07.01 주문 예약 시간 변동 시, 라이더 배정 취소 프로세스 추가
        Order orgOrd = getOrderInfo(order);     // 변경 전 주문 정보 추출
        try {
            // 예약 시간이 입력이 되었는지 유무가 기준이 된다.
            if (!StringUtils.isNullOrEmpty(order.getReservationDatetime()) && !StringUtils.isNullOrEmpty(orgOrd.getRiderId())){
                Date changeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(order.getReservationDatetime());             // 변경될 예약 시간의 형식 변경
                Date orgDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(orgOrd.getReservationDatetime());          // 기존에 적용된 예약 시간

                //Locale regionLocale = LocaleContextHolder.getLocale();

                //int iTimer = regionLocale.toString().equals("zh_TW") ? 31 : 51;     // 국가별 배정 타임이 다르므로

                // 변경된 예약 시간이 현재 시간보다 30분을 초과하는 경우 (넉넉하게 31분 기준)
                //if ((changeDate.getTime() - new Date().getTime()) / 1000 > (iTimer * 60)){
                // 20.08.19 배정 시간이 변동된 경우 및 주문 상태가 신규 또는 배정인 경우에 한하여 취소
                if (!(changeDate.getTime() == orgDate.getTime()) && (orgOrd.getStatus().equals("0") || orgOrd.getStatus().equals("1") || orgOrd.getStatus().equals("5"))){
                    // 변경되어야 될 내용 적용 ex. 배정취소
                    // 20.08.03 배정 취소 사라지게 하기
                    // 20.08.28 배정 시간을 임의로 조정
                    /**
                     * 배정시간 조건
                     * 예약 시간 마이너스 QT 타임을 비교하여, QT 타임보다 낮은 경우에 한하여, 예약시간 - QT 타임 시간을 적용한다.
                     * */
                    int orgQT = 0;

                    try {
                        orgQT = Integer.parseInt(orgOrd.getCookingTime());
                    }catch (Exception e){
//                        e.printStackTrace();
                        log.error(e.getMessage());
                    }finally {
                        if (orgQT == 0){
                            orgQT = 30;
                        }
                    }

                    Date qtDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(changeDate)).minusMinutes(orgQT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

                    // 시간 비교
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
                    log.info("changeDate [변경 요청 시간] = " + changeDate);
                    log.info("orgDate [오리지널 시간] = " + orgDate);
                    log.info("qtDate [QT 적용 후 배정 세팅 시간] = " + qtDate);
                    log.info("order.getAssignedDatetime [배정 적용 시간] = " + order.getAssignedDatetime());
                    log.info("########### Order Updated # Order Cancel Completed #############");
                }
            }
        }catch (Exception e){
//            e.printStackTrace();
            log.error(e.getMessage());
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
                // 해당 라이더한테만 푸쉬
                ArrayList<Map> tokens = (ArrayList) riderMapper.selectRiderTokenByOrderId(order);
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_CHANGE);

                    // PUSH 객체로 변환 후 전달
                    FcmBody fcmBody = new FcmBody();

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("obj", noti);

                    fcmBody.setData(obj);
                    fcmBody.setPriority("high");

                    fcmBody.getNotification().setTitle(getMessage("CHANGE.ORDER"));
                    fcmBody.getNotification().setBody(getMessage("CHANGE.ORDER"));

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
                            log.error(e.getMessage());
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
                            ArrayList<String> androidTokenValue = new ArrayList<>();

                            android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(androidTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                            checkFcmResponse(androidPushNotification);
                        }catch (Exception e){
                            log.error(e.getMessage());
                        }
                    }


                    oldMap.addAll(tokens.stream().filter(x->{
                        if (x.getOrDefault("appType", "").toString().equals("")){
                            return true;
                        }

                        return false;
                    }).collect(Collectors.toList()));

                    // old android push
                    if (iosMap.size() > 0){
                        try {
                            ArrayList<String> oldTokenValue = new ArrayList<>();

                            oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(oldTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                            checkFcmResponse(oldPushNotification);
                        }catch (Exception e){
                            log.error(e.getMessage());
                        }
                    }
                }
            } else {
                // 상점 관련 라이더한테 푸쉬
                if (storeDTO.getAssignmentStatus().equals("2")) {
                    ArrayList<Map> tokens = (ArrayList) orderMapper.selectPushToken(storeDTO.getSubGroup());
                    if (tokens.size() > 0) {
                        Notification noti = new Notification();
                        noti.setType(Notification.NOTI.ORDER_CHANGE);

                        // PUSH 객체로 변환 후 전달
                        FcmBody fcmBody = new FcmBody();

                        Map<String, Object> obj = new HashMap<>();
                        obj.put("obj", noti);

                        fcmBody.setData(obj);
                        fcmBody.setPriority("high");

                        fcmBody.getNotification().setTitle(getMessage("CHANGE.ORDER"));
                        fcmBody.getNotification().setBody(getMessage("CHANGE.ORDER"));

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
//                                e.printStackTrace();
                                log.error(e.getMessage());
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
                                ArrayList<String> androidTokenValue = new ArrayList<>();

                                android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                                fcmBody.setRegistration_ids(androidTokenValue);

                                // noti 전문 삭제
                                fcmBody.setNotification(null);

                                CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                                checkFcmResponse(androidPushNotification);
                            }catch (Exception e){
                                log.error(e.getMessage());
                            }
                        }


                        oldMap.addAll(tokens.stream().filter(x->{
                            if (x.getOrDefault("appType", "").toString().equals("")){
                                return true;
                            }

                            return false;
                        }).collect(Collectors.toList()));

                        // old android push
                        if (iosMap.size() > 0){
                            try {
                                ArrayList<String> oldTokenValue = new ArrayList<>();

                                oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                                fcmBody.setRegistration_ids(oldTokenValue);

                                // noti 전문 삭제
                                fcmBody.setNotification(null);

                                CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                                checkFcmResponse(oldPushNotification);
                            }catch (Exception e){
                                log.error(e.getMessage());
                            }
                        }
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
        orderAssigned.setAssignedDatetime("-2");

        if (S_Rider != null && S_Rider.getLatitude() != null && !S_Rider.getLatitude().equals("")) {
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

            if (S_Rider != null && S_Rider.getLatitude() != null && !S_Rider.getLatitude().equals("")) {
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
                ArrayList<Map> tokens = (ArrayList) orderMapper.selectPushToken(S_Store.getSubGroup());
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_ASSIGN);
                    noti.setRider_id(Integer.parseInt(orderAssigned.getRiderId()));

                    // PUSH 객체로 변환 후 전달
                    FcmBody fcmBody = new FcmBody();

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("obj", noti);

                    fcmBody.setData(obj);
                    fcmBody.setPriority("high");

                    fcmBody.getNotification().setTitle(getMessage("ASSIGN.ORDER"));
                    fcmBody.getNotification().setBody(getMessage("ASSIGN.ORDER"));

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
//                            e.printStackTrace();
                            log.error(e.getMessage());
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
                            ArrayList<String> androidTokenValue = new ArrayList<>();

                            android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(androidTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                            checkFcmResponse(androidPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }


                    oldMap.addAll(tokens.stream().filter(x->{
                        if (x.getOrDefault("appType", "").toString().equals("")){
                            return true;
                        }

                        return false;
                    }).collect(Collectors.toList()));

                    // old android push
                    if (iosMap.size() > 0){
                        try {
                            ArrayList<String> oldTokenValue = new ArrayList<>();

                            oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(oldTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                            checkFcmResponse(oldPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }

                }
            } else {
                ArrayList<Map> tokens = (ArrayList) orderMapper.selectPushToken(S_Store.getSubGroup());
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_ASSIGN);

                    // PUSH 객체로 변환 후 전달
                    FcmBody fcmBody = new FcmBody();

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("obj", noti);

                    fcmBody.setData(obj);
                    fcmBody.setPriority("high");

                    fcmBody.getNotification().setTitle(getMessage("ASSIGN.ORDER"));
                    fcmBody.getNotification().setBody(getMessage("ASSIGN.ORDER"));
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
//                            e.printStackTrace();
                            log.error(e.getMessage());
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
                            ArrayList<String> androidTokenValue = new ArrayList<>();

                            android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(androidTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                            checkFcmResponse(androidPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }


                    oldMap.addAll(tokens.stream().filter(x->{
                        if (x.getOrDefault("appType", "").toString().equals("")){
                            return true;
                        }

                        return false;
                    }).collect(Collectors.toList()));

                    // old android push
                    if (iosMap.size() > 0){
                        try {
                            ArrayList<String> oldTokenValue = new ArrayList<>();

                            oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(oldTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                            checkFcmResponse(oldPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }

                }
            }
        }

        return ret;
    }

    @Secured("ROLE_RIDER")
    @Override
    public int putOrderPickedUp(Order order) throws AppTrException {
        order.setRole("ROLE_RIDER");
        Order orderInfo = orderMapper.selectOrderInfo(order);


        if (orderInfo.getStatus().toString().equals("0")){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00025), ErrorCodeEnum.E00025.name());
        }else if (orderInfo.getStatus().toString().equals("3") || orderInfo.getStatus().toString().equals("4")){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00026), ErrorCodeEnum.E00026.name());
        }else if (!(orderInfo.getStatus().toString().equals("1"))){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00056), ErrorCodeEnum.E00056.name());
        }

        // 라이더 위치 업데이트
        try {
            Rider rider = new Rider();
            rider.setAccessToken(order.getToken());
            rider.setLatitude(order.getLatitude());
            rider.setLongitude(order.getLongitude());

            riderMapper.updateRiderLocation(rider);
            
            log.info("픽업 버튼 클릭으로 라이더 위치 업데이트 완료 token = " +  rider.getAccessToken());

        }catch (Exception e){
            log.error("픽업 버튼 클릭으로 라이더 위치 업데이트 중 오류 발생", e);
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
        order.setRole("ROLE_RIDER");
        Order orderInfo = orderMapper.selectOrderInfo(order);

        if (orderInfo.getStatus().toString().equals("0")){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00025), ErrorCodeEnum.E00025.name());
        }else if (orderInfo.getStatus().toString().equals("3") || orderInfo.getStatus().toString().equals("4")){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00026), ErrorCodeEnum.E00026.name());
        }else if (!(orderInfo.getStatus().toString().equals("2"))){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00057), ErrorCodeEnum.E00057.name());
        }

        // 라이더 위치 업데이트
        try {
            Rider rider = new Rider();
            rider.setAccessToken(order.getToken());
            rider.setLatitude(order.getLatitude());
            rider.setLongitude(order.getLongitude());

            riderMapper.updateRiderLocation(rider);

            log.info("도착 버튼 클릭으로 라이더 위치 업데이트 완료 token = " +  rider.getAccessToken());

        }catch (Exception e){
            log.error("도착 버튼 클릭으로 라이더 위치 업데이트 중 오류 발생", e);
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

            log.info("rier AppType = [" + S_Order.getAppType() + "]");

            if (S_Order.getAppType() != null && S_Order.getAppType().equals("1") && S_Order.getStore() != null && S_Order.getStore().getBrandCode().equals("1")){
                try{
                    log.info("newRider Put Arrived Button goto Completed");
                    int iResult = putOrderCompleted(order);
                    log.info("newRider Put Arrived Button gotoCompleted result = [" + iResult + "]");
                }catch (Exception e){
//                    e.printStackTrace();
                    log.info("newRider Put Error");
                    log.info(e.getMessage());
                }

            }
        }

        return result;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int putOrderCompleted(Order order) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            order.setRole("ROLE_STORE");
        } else {
            order.setRole("ROLE_RIDER");
        }
        Order orderInfo = orderMapper.selectOrderInfo(order);

        if (orderInfo.getStatus().toString().equals("0")){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00025), ErrorCodeEnum.E00025.name());
        }else if (orderInfo.getStatus().toString().equals("4")){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00026), ErrorCodeEnum.E00026.name());
        }else if (!(orderInfo.getStatus().toString().equals("6"))){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00058), ErrorCodeEnum.E00058.name());
        }

        boolean bUpdateAndroid = false;

        log.info("rier AppType = [" + orderInfo.getAppType() + "]");

        if (orderInfo.getAppType() != null && orderInfo.getAppType().equals("1") && orderInfo.getStore() != null && orderInfo.getStore().getBrandCode().equals("1")
                && orderInfo.getPlatform() != null && orderInfo.getPlatform().equals("android")){
            try{
                log.info("new Rider Android Check OK");
                bUpdateAndroid = true;
            }catch (Exception e){
//                e.printStackTrace();
                log.info("newRider Android Check Error");
                log.info(e.getMessage());
            }

        }

        // 라이더 위치 업데이트
        try {
            Rider rider = new Rider();
            rider.setAccessToken(order.getToken());
            rider.setLatitude(order.getLatitude());
            rider.setLongitude(order.getLongitude());

            riderMapper.updateRiderLocation(rider);

            log.info("완료 버튼 클릭으로 라이더 위치 업데이트 완료 token = " +  rider.getAccessToken());

        }catch (Exception e){
            log.error("완료 버튼 클릭으로 라이더 위치 업데이트 중 오류 발생", e);
        }


        Order orderCompleted = new Order();

        orderCompleted.setToken(order.getToken());
        orderCompleted.setId(order.getId());
        if (bUpdateAndroid){
            orderCompleted.setStatus("6");
        }else {
            orderCompleted.setStatus("3");
        }

        orderCompleted.setCompletedDatetime(LocalDateTime.now().toString());
        orderCompleted.setCompleteXy(order.getLatitude() + "|" + order.getLongitude());

        Order combinedOrderCompleted = new Order();

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            combinedOrderCompleted.setId(order.getCombinedOrderId());
            if (bUpdateAndroid){
                combinedOrderCompleted.setStatus("6");
            }else{
                combinedOrderCompleted.setStatus("3");
            }

            combinedOrderCompleted.setCompletedDatetime(LocalDateTime.now().toString());
            combinedOrderCompleted.setToken(order.getToken());
            combinedOrderCompleted.setCompleteXy(order.getLatitude() + "|" + order.getLongitude());

            this.putOrder(combinedOrderCompleted);
        }

        int result = this.putOrder(orderCompleted);

        String tmpOrderId = orderCompleted.getId();

        orderCompleted.setId(order.getId());

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
         * 주문 상태를 매장에 전송하기 위하여 추출
         * */
        Store S_Store = storeMapper.selectStoreInfo(storeDTO);

        if (result != 0) {
            if (S_Store.getSubGroup() != null) {
                redisService.setPublisher(Content.builder().type("order_completed").id(tmpOrderId).orderId(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Order.getStoreId()).subGroupId(S_Store.getSubGroup().getId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("order_completed").id(tmpOrderId).orderId(order.getId()).adminId(S_Store.getAdminId()).storeId(S_Order.getStoreId()).build());
            }

            if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
                ArrayList<Map> tokens = (ArrayList) riderMapper.selectRiderToken(S_Order);
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_COMPLET);

                    // PUSH 객체로 변환 후 전달
                    FcmBody fcmBody = new FcmBody();

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("obj", noti);

                    fcmBody.setData(obj);
                    fcmBody.setPriority("high");

                    fcmBody.getNotification().setTitle(getMessage("COMPLETED.ORDER"));
                    fcmBody.getNotification().setBody(getMessage("COMPLETED.ORDER"));

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
//                            e.printStackTrace();
                            log.error(e.getMessage());
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
                            ArrayList<String> androidTokenValue = new ArrayList<>();

                            android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(androidTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                            checkFcmResponse(androidPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }


                    oldMap.addAll(tokens.stream().filter(x->{
                        if (x.getOrDefault("appType", "").toString().equals("")){
                            return true;
                        }

                        return false;
                    }).collect(Collectors.toList()));

                    // old android push
                    if (iosMap.size() > 0){
                        try {
                            ArrayList<String> oldTokenValue = new ArrayList<>();

                            oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(oldTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                            checkFcmResponse(oldPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }
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
                // 해당 라이더한테만 푸쉬
                ArrayList<Map> tokens = (ArrayList) riderMapper.selectRiderTokenByOrderId(order);
                if (tokens.size() > 0) {
                    Notification noti = new Notification();
                    noti.setType(Notification.NOTI.ORDER_CANCEL);

                    // PUSH 객체로 변환 후 전달
                    FcmBody fcmBody = new FcmBody();

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("obj", noti);

                    fcmBody.setData(obj);
                    fcmBody.setPriority("high");

                    fcmBody.getNotification().setTitle(getMessage("CANCEL.ORDER"));
                    fcmBody.getNotification().setBody(getMessage("CANCEL.ORDER"));

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
//                            e.printStackTrace();
                            log.error(e.getMessage());
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
                            ArrayList<String> androidTokenValue = new ArrayList<>();

                            android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(androidTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                            checkFcmResponse(androidPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }


                    oldMap.addAll(tokens.stream().filter(x->{
                        if (x.getOrDefault("appType", "").toString().equals("")){
                            return true;
                        }

                        return false;
                    }).collect(Collectors.toList()));

                    // old android push
                    if (iosMap.size() > 0){
                        try {
                            ArrayList<String> oldTokenValue = new ArrayList<>();

                            oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(oldTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                            checkFcmResponse(oldPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }
                }
            } else {
                // 상점 관련 라이더한테 푸쉬
                if (storeDTO.getAssignmentStatus().equals("2")) {
                    ArrayList<Map> tokens = (ArrayList) orderMapper.selectPushToken(storeDTO.getSubGroup());
                    if (tokens.size() > 0) {
                        Notification noti = new Notification();
                        noti.setType(Notification.NOTI.ORDER_CANCEL);

                        // PUSH 객체로 변환 후 전달
                        FcmBody fcmBody = new FcmBody();

                        Map<String, Object> obj = new HashMap<>();
                        obj.put("obj", noti);

                        fcmBody.setData(obj);
                        fcmBody.setPriority("high");

                        fcmBody.getNotification().setTitle(getMessage("CANCEL.ORDER"));
                        fcmBody.getNotification().setBody(getMessage("CANCEL.ORDER"));

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
//                                e.printStackTrace();
                                log.error(e.getMessage());
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
                                ArrayList<String> androidTokenValue = new ArrayList<>();

                                android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                                fcmBody.setRegistration_ids(androidTokenValue);

                                // noti 전문 삭제
                                fcmBody.setNotification(null);

                                CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                                checkFcmResponse(androidPushNotification);
                            }catch (Exception e){
//                                e.printStackTrace();
                                log.error(e.getMessage());
                            }
                        }


                        oldMap.addAll(tokens.stream().filter(x->{
                            if (x.getOrDefault("appType", "").toString().equals("")){
                                return true;
                            }

                            return false;
                        }).collect(Collectors.toList()));

                        // old android push
                        if (iosMap.size() > 0){
                            try {
                                ArrayList<String> oldTokenValue = new ArrayList<>();

                                oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                                fcmBody.setRegistration_ids(oldTokenValue);

                                // noti 전문 삭제
                                fcmBody.setNotification(null);

                                CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                                checkFcmResponse(oldPushNotification);
                            }catch (Exception e){
//                                e.printStackTrace();
                                log.error(e.getMessage());
                            }
                        }
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
        ArrayList<Map> tokens = (ArrayList) riderMapper.selectRiderTokenByOrderId(order);      // 위치변경 20.07.16 주문이 취소되기 전에 구해놓은다.

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

        // 20.08.03 원본에서 취소 시, assign 값도 취소될 수 있도록 설정
        if(order.getAssignedDatetime() != null && order.getAssignedDatetime().equals("-1")){
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
            // 20.08.03 원본에서 취소 시, assign 값도 취소될 수 있도록 설정
            if (order.getAssignedDatetime() != null && order.getAssignedDatetime().equals("-1")){
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

                    // PUSH 객체로 변환 후 전달
                    FcmBody fcmBody = new FcmBody();

                    Map<String, Object> obj = new HashMap<>();
                    obj.put("obj", noti);

                    fcmBody.setData(obj);
                    fcmBody.setPriority("high");

                    fcmBody.getNotification().setTitle(getMessage("ASSIGN.CANCEL.ORDER"));
                    fcmBody.getNotification().setBody(getMessage("ASSIGN.CANCEL.ORDER"));

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
                            log.error(e.getMessage());
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
                            ArrayList<String> androidTokenValue = new ArrayList<>();

                            android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(androidTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                            checkFcmResponse(androidPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }


                    oldMap.addAll(tokens.stream().filter(x->{
                        if (x.getOrDefault("appType", "").toString().equals("")){
                            return true;
                        }

                        return false;
                    }).collect(Collectors.toList()));

                    // old android push
                    if (iosMap.size() > 0){
                        try {
                            ArrayList<String> oldTokenValue = new ArrayList<>();

                            oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                            fcmBody.setRegistration_ids(oldTokenValue);

                            // noti 전문 삭제
                            fcmBody.setNotification(null);

                            CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                            checkFcmResponse(oldPushNotification);
                        }catch (Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                    }
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

        //List<OrderCheckAssignment> S_OrderConfirm = orderMapper.selectOrderConfirm(needOrderId);

        int nRet = orderMapper.insertOrderConfirm(needOrderId);
        if (nRet != 0) {
            riderMapper.updateRiderOrderStandbyStatus(order);
        }
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

        //List<OrderCheckAssignment> S_OrderConfirm = orderMapper.selectOrderConfirm(needOrderId);

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
        // 홍콩도 대만처럼 강제 배정 되도록 주석 처리 적용
//        if (!locale.toString().equals("zh_TW")) {
        int orderDenyCount = orderMapper.selectOrderDenyCount(currentRider);
        if (orderDenyCount > 1) {
            currentRider.setWorking("2");
            riderMapper.updateWorkingRider(currentRider);

            // 해당 라이더한테만 푸쉬
            Order pushOrder = new Order();
            pushOrder.setRiderId(S_Rider.getId());
            ArrayList<Map> tokens = (ArrayList) riderMapper.selectRiderToken(pushOrder);
            if (tokens.size() > 0) {
                Notification noti = new Notification();
                noti.setType(Notification.NOTI.RIDER_WORKING_OFF);

                // PUSH 객체로 변환 후 전달
                FcmBody fcmBody = new FcmBody();

                Map<String, Object> obj = new HashMap<>();
                obj.put("obj", noti);

                fcmBody.setData(obj);
                fcmBody.setPriority("high");

                fcmBody.getNotification().setTitle(getMessage("RIDER.WORKING.OFF"));
                fcmBody.getNotification().setBody(getMessage("RIDER.WORKING.OFF"));

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
//                        e.printStackTrace();
                        log.error(e.getMessage());
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
                        ArrayList<String> androidTokenValue = new ArrayList<>();

                        android.forEach(x -> androidTokenValue.add(x.get("push_token").toString()));
                        fcmBody.setRegistration_ids(androidTokenValue);

                        // noti 전문 삭제
                        fcmBody.setNotification(null);

                        CompletableFuture<FirebaseResponse> androidPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "android");
                        checkFcmResponse(androidPushNotification);
                    }catch (Exception e){
//                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                }


                oldMap.addAll(tokens.stream().filter(x->{
                    if (x.getOrDefault("appType", "").toString().equals("")){
                        return true;
                    }

                    return false;
                }).collect(Collectors.toList()));

                // old android push
                if (iosMap.size() > 0){
                    try {
                        ArrayList<String> oldTokenValue = new ArrayList<>();

                        oldMap.forEach(x -> oldTokenValue.add(x.get("push_token").toString()));
                        fcmBody.setRegistration_ids(oldTokenValue);

                        // noti 전문 삭제
                        fcmBody.setNotification(null);

                        CompletableFuture<FirebaseResponse> oldPushNotification = androidPushNotificationsService.sendGroup(fcmBody, "old");
                        checkFcmResponse(oldPushNotification);
                    }catch (Exception e){
//                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                }
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
    public List<Reason> getOrderFirstAssignmentReason(Common common) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        switch (authentication.getAuthorities().toString()) {
            case "[ROLE_STORE]":
                common.setRole("ROLE_STORE");
                break;
            case "[ROLE_RIDER]":
                common.setRole("ROLE_RIDER");
                break;
            case "[ROLE_ADMIN]":
                common.setRole("ROLE_ADMIN");
                break;
        }

        return orderMapper.selectOrderFirstAssignmentReason(common);
    }

    @Secured("ROLE_RIDER")
    @Override
    public int putOrderReturn(Order order) throws AppTrException {
        order.setRole("ROLE_RIDER");
        Order orderInfo = orderMapper.selectOrderInfo(order);

        if (orderInfo.getStatus().toString().equals("0")){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00025), ErrorCodeEnum.E00025.name());
        }else if (orderInfo.getStatus().toString().equals("4")){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00026), ErrorCodeEnum.E00026.name());
        }else if (StringUtil.isEmpty(orderInfo.getPickedUpDatetime()) || StringUtil.isEmpty(orderInfo.getArrivedDatetime()) || StringUtil.isEmpty(orderInfo.getCompletedDatetime())){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00059), ErrorCodeEnum.E00059.name());
        }

        // 라이더 위치 업데이트
        try {
            Rider rider = new Rider();
            rider.setAccessToken(order.getToken());
            rider.setLatitude(order.getLatitude());
            rider.setLongitude(order.getLongitude());

            riderMapper.updateRiderLocation(rider);

            log.info("복귀 버튼 클릭으로 라이더 위치 업데이트 완료 token = " +  rider.getAccessToken());

        }catch (Exception e){
            log.error("복귀 버튼 클릭으로 라이더 위치 업데이트 중 오류 발생", e);
        }

        order.setRole("ROLE_RIDER");
        Order needOrderId = orderMapper.selectOrderInfo(order);

        if (order.getCombinedOrderId() != null && !order.getCombinedOrderId().equals("")) {
            Order combinedOrder = new Order();

            combinedOrder.setReturnDatetime(LocalDateTime.now().toString());

            // 완료 처리로 변경 되지 않은 경우 변경한다
            combinedOrder.setStatus("3");

            combinedOrder.setToken(order.getToken());
            combinedOrder.setId(order.getCombinedOrderId());

            this.putOrder(combinedOrder);
        }

        order.setReturnDatetime(LocalDateTime.now().toString());
        order.setStatus("3");

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
