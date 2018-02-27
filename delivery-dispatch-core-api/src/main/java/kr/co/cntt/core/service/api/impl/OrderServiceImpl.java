package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.order.OrderCheckAssignment;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.OrderService;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.Misc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceSupport implements OrderService {

    private List<Map> orderReservationArrayList = new ArrayList();
    private List<Map> orderArrayList = new ArrayList();

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

    @Scheduled(fixedDelay = 1000 * 60)
    @Override
    public void autoAssignOrder() {
        String nowDate = String.format("%02d", LocalDateTime.now().getYear())
                + String.format("%02d", LocalDateTime.now().getMonthValue())
                + String.format("%02d", LocalDateTime.now().getDayOfMonth())
                + String.format("%02d", LocalDateTime.now().getHour())
                + String.format("%02d", LocalDateTime.now().getMinute())
                + "00";

        for (int i = 0; i < orderReservationArrayList.size(); i++) {
            Order order = new Order();
            order.setId(((Order) orderReservationArrayList.get(i).get("order")).getId());

            Store store = new Store();
            store.setId(((Store) orderReservationArrayList.get(i).get("store")).getId());
            store.setLatitude(((Store) orderReservationArrayList.get(i).get("store")).getLatitude());
            store.setLongitude(((Store) orderReservationArrayList.get(i).get("store")).getLongitude());
            store.setRadius(((Store) orderReservationArrayList.get(i).get("store")).getRadius());
            store.setStoreDistanceSort(((Store) orderReservationArrayList.get(i).get("store")).getStoreDistanceSort());
            store.setAssignmentLimit(((Store) orderReservationArrayList.get(i).get("store")).getAssignmentLimit());

            Map map = new HashMap();
            map.put("order", order);
            map.put("store", store);

            if (((Order) orderReservationArrayList.get(i).get("order")).getReservationDatetime().equals(nowDate)) {
                // TODO. 예약 배정
                log.info(">>> 예약 배정 " + ((Order) orderReservationArrayList.get(i).get("order")).getMenuName());
                int proc = this.autoAssignOrderProc(map);
                if (proc == 1) {
                    orderReservationArrayList.remove(i);
                    i -= 1;
                    log.info("============================================================================");
                }
            }
        }

        for (int i = 0; i < orderArrayList.size(); i++) {
            Order order = new Order();
            order.setId(((Order) orderArrayList.get(i).get("order")).getId());

            Store store = new Store();
            store.setId(((Store) orderArrayList.get(i).get("store")).getId());
            store.setLatitude(((Store) orderArrayList.get(i).get("store")).getLatitude());
            store.setLongitude(((Store) orderArrayList.get(i).get("store")).getLongitude());
            store.setRadius(((Store) orderArrayList.get(i).get("store")).getRadius());
            store.setStoreDistanceSort(((Store) orderArrayList.get(i).get("store")).getStoreDistanceSort());
            store.setAssignmentLimit(((Store) orderArrayList.get(i).get("store")).getAssignmentLimit());

            Map map = new HashMap();
            map.put("order", order);
            map.put("store", store);

            // TODO. 자동 배정
            log.info(">>> 자동 배정 " + ((Order) orderArrayList.get(i).get("order")).getMenuName());
            int proc = this.autoAssignOrderProc(map);
            if (proc == 1) {
                orderArrayList.remove(i);
                i -= 1;
                log.info("============================================================================");
            }
        }

    }

    public int autoAssignOrderProc(Map map) {
        // TODO. 자동 배정 proc
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ proc!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ order.getId() !!!!! " + ((Order) map.get("order")).getMenuName());

        return  1;
    }

    @Secured("ROLE_STORE")
    @Override
    public int postOrder(Order order) throws AppTrException {
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
        } else {
            int assignOrder = this.assignOrder(order);

            if (assignOrder == 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.E00012), ErrorCodeEnum.E00012.name());
            }
        }

        return postOrder;
    }

    /**
     * <p> assignOrder
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int assignOrder(Order order) throws AppTrException {
        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        storeDTO.setToken(order.getToken());

        List<Store> S_Store = storeMapper.getStoreInfo(storeDTO);

        if (S_Store.get(0).getAssignmentStatus().equals("1")) {
            log.info(">>> 자동배정");
            if (order.getReservationDatetime() != null && order.getReservationDatetime() != "") {
                Map map = new HashMap();
                map.put("order", order);
                map.put("store", S_Store.get(0));

                orderReservationArrayList.add(map);
            } else {
                Map map = new HashMap();
                map.put("order", order);
                map.put("store", S_Store.get(0));

                orderArrayList.add(map);
            }

            return 1;
        } else if (S_Store.get(0).getAssignmentStatus().equals("0")) {
            log.info(">>> 수동배정");

            return 1;
        } else {
            log.info(">>> error");

            return 0;
        }
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public List<Order> getOrders(Common common) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            common.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            common.setRole("ROLE_RIDER");
        }

        List<Order> S_Order = orderMapper.selectOrders(common);

        if (S_Order.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00015), ErrorCodeEnum.E00015.name());
        }

        return S_Order;
    }

    @Secured({"ROLE_STORE", "ROLE_RIDER"})
    @Override
    public List<Order> getOrderInfo(Common common) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            common.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            common.setRole("ROLE_RIDER");
        }

        List<Order> S_Order = orderMapper.selectOrderInfo(common);

        if (S_Order.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00016), ErrorCodeEnum.E00016.name());
        }

        Misc misc = new Misc();
        if (S_Order.get(0).getLatitude() != null && S_Order.get(0).getLongitude() != null) {
            Store storeInfo = storeMapper.selectStoreLocation(S_Order.get(0).getStoreId());

            try {
                S_Order.get(0).setDistance(Double.toString(misc.getHaversine(storeInfo.getLatitude(), storeInfo.getLongitude(), S_Order.get(0).getLatitude(), S_Order.get(0).getLongitude()) / (double) 1000));
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

            this.putOrder(combinedOrder);
        }

        return this.putOrder(order);
    }

    @Secured("ROLE_STORE")
    @Override
    public int putOrderAssigned(Order order) throws AppTrException {
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

            this.putOrder(combinedOrderAssigned);
        }

        return this.putOrder(orderAssigned);
    }

    @Secured("ROLE_RIDER")
    @Override
    public int putOrderPickedUp(Order order) throws AppTrException {
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

            this.putOrder(combinedOrderPickedUp);
        }

        return this.putOrder(orderPickedUp);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int putOrderCompleted(Order order) throws AppTrException {
        Order orderCompleted = new Order();

        orderCompleted.setToken(order.getToken());
        orderCompleted.setId(order.getId());
        orderCompleted.setStatus("3");
        orderCompleted.setCompletedDatetime(LocalDateTime.now().toString());

        return this.putOrder(orderCompleted);
    }


    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int putOrderCanceled(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0 || selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        Order orderCanceled = new Order();
        orderCanceled.setToken(order.getToken());
        orderCanceled.setId(order.getId());
        orderCanceled.setStatus("4");
        orderCanceled.setModifiedDatetime(LocalDateTime.now().toString());

        return this.putOrder(orderCanceled);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STORE", "ROLE_RIDER"})
    @Override
    public int putOrderAssignCanceled(Order order) throws AppTrException {
        int selectOrderIsApprovalCompleted = orderMapper.selectOrderIsApprovalCompleted(order);
        int selectOrderIsCompletedIsCanceled = orderMapper.selectOrderIsCompletedIsCanceled(order);

        if (selectOrderIsApprovalCompleted != 0 || selectOrderIsCompletedIsCanceled != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        Order orderAssignCanceled = new Order();
        orderAssignCanceled.setToken(order.getToken());
        orderAssignCanceled.setId(order.getId());
        orderAssignCanceled.setStatus("5");
        orderAssignCanceled.setRiderId(null);
        orderAssignCanceled.setModifiedDatetime(LocalDateTime.now().toString());

        return this.putOrder(orderAssignCanceled);
    }

    @Secured({"ROLE_RIDER"})
    @Override
    public int postOrderConfirm(Order order) throws AppTrException {

        Rider currentRider = new Rider();
        currentRider.setAccessToken(order.getToken());
        currentRider.setToken(order.getToken());

        List<Rider> S_Rider = riderMapper.getRiderInfo(currentRider);

        if (S_Rider.get(0).getSubGroupRiderRel() == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00009), ErrorCodeEnum.E00009.name());
        }

        List<OrderCheckAssignment> S_OrderConfirm = orderMapper.selectOrderConfirm(order);

        if (S_OrderConfirm.size() != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00013), ErrorCodeEnum.E00013.name());
        }

        return orderMapper.insertOrderConfirm(order);
    }

    @Secured({"ROLE_RIDER"})
    @Override
    public int postOrderDeny(Order order) throws AppTrException {

        Rider currentRider = new Rider();
        currentRider.setAccessToken(order.getToken());
        currentRider.setToken(order.getToken());

        List<Rider> S_Rider = riderMapper.getRiderInfo(currentRider);

        if (S_Rider.get(0).getSubGroupRiderRel() == null) {
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

        if (S_OrderDeny.size() != 0 && S_Rider.size() != 0) {
            for (OrderCheckAssignment orderDeny : S_OrderDeny) {
                if (orderDeny.getRiderId().equals(S_Rider.get(0).getId())) {
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00010), ErrorCodeEnum.E00010.name());
                }

            }
        }

        if (order.getOrderCheckAssignment() != null) {
            if (order.getOrderCheckAssignment().getStatus() == null || order.getOrderCheckAssignment().getStatus() == "") {
                order.getOrderCheckAssignment().setStatus("0");
            }

        }

        return orderMapper.insertOrderDeny(order);
    }

}
