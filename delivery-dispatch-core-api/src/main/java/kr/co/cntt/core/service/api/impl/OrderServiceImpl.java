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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceSupport implements OrderService {

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
    public int postOrder(Order order) throws AppTrException {
        Geocoder geocoder = new Geocoder();

        try {
            Map<String, String> geo = geocoder.getLatLng(order.getAddress());
            order.setLatitude(geo.get("lat"));
            order.setLongitude(geo.get("lng"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int postOrder = orderMapper.insertOrder(order);

        if (postOrder == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        } else {
            int assignOrder = this.autoAssignOrder(order);

            if (assignOrder == 0) {
                throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
            }
        }

        return postOrder;
    }

    /**
     * <p> autoAssignOrder
     *
     * @param order
     * @return
     * @throws AppTrException
     */
    public int autoAssignOrder(Order order) throws AppTrException {
        Store storeDTO = new Store();
        storeDTO.setAccessToken(order.getToken());
        List<Store> S_Store = storeMapper.getStoreInfo(storeDTO);

        if (S_Store.get(0).getAssignmentStatus().equals("1")) {
            // TODO. 자동 배정
            log.info(">>> 자동배정");

            return 1;
        } else if (S_Store.get(0).getAssignmentStatus().equals("0")) {
            log.info(">>> 수동배정");

            return 1;
        } else {
            log.info(">>> error");

            return 0;
        }
    }

    @Override
    public List<Order> getOrders(Common common) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            common.setRole("ROLE_ADMIN");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            common.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            common.setRole("ROLE_RIDER");
        } else {
            common.setRole("ROLE_USER");
        }

        List<Order> S_Order = orderMapper.selectOrders(common);

        if (S_Order.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Order;
    }

    @Override
    public List<Order> getOrderInfo(Common common) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            common.setRole("ROLE_ADMIN");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            common.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            common.setRole("ROLE_RIDER");
        } else {
            common.setRole("ROLE_USER");
        }

        List<Order> S_Order = orderMapper.selectOrderInfo(common);

        if (S_Order.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Order;
    }

    /**
     * <p> putOrder
     * @param order
     * @return
     * @throws AppTrException
     */
    public int putOrder(Order order) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            order.setRole("ROLE_ADMIN");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            order.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            order.setRole("ROLE_RIDER");
        } else {
            order.setRole("ROLE_USER");
        }

        int S_Order = orderMapper.updateOrder(order);

        if (S_Order == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Order;
    }

    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int putOrderInfo(Order order) throws AppTrException {
        Geocoder geocoder = new Geocoder();

        try {
            Map<String, String> geo = geocoder.getLatLng(order.getAddress());
            order.setLatitude(geo.get("lat"));
            order.setLongitude(geo.get("lng"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        order.setStatus(null);
        order.setRiderId(null);
        order.setAssignedDatetime(null);
        order.setPickedUpDatetime(null);
        order.setCompletedDatetime(null);

        return this.putOrder(order);
    }

    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int putOrderAssigned(Order order) throws AppTrException {
        Order orderAssigned = new Order();

        orderAssigned.setToken(order.getToken());
        orderAssigned.setId(order.getId());
        orderAssigned.setRiderId(order.getRiderId());
        orderAssigned.setStatus("1");
        orderAssigned.setAssignedDatetime(LocalDateTime.now().toString());

        return this.putOrder(orderAssigned);
    }

    @Secured({"ROLE_RIDER"})
    @Override
    public int putOrderPickedUp(Order order) throws AppTrException {
        Order orderPickedUp = new Order();

        orderPickedUp.setToken(order.getToken());
        orderPickedUp.setId(order.getId());
        orderPickedUp.setStatus("2");
        orderPickedUp.setPickedUpDatetime(LocalDateTime.now().toString());

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

    @Override
    public int putOrderCanceled(Order order) throws AppTrException {
        // TODO. 주문 취소
        return 0;
    }

    @Secured({"ROLE_RIDER"})
    @Override
    public int postOrderConfirm(Order order) throws AppTrException {
        List<OrderCheckAssignment> S_OrderConfirm = orderMapper.selectOrderConfirm(order);

        if (S_OrderConfirm.size() != 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return orderMapper.insertOrderConfirm(order);
    }

    @Secured({"ROLE_RIDER"})
    @Override
    public int postOrderDeny(Order order) throws AppTrException {

        Rider currentRider = new Rider();
        currentRider.setAccessToken(order.getToken());

        List<Rider> S_Rider = riderMapper.getRiderInfo(currentRider);

        List<OrderCheckAssignment> S_OrderDeny = orderMapper.selectOrderDeny(order);

        if (S_OrderDeny.size() != 0 && S_Rider.size() != 0) {
            for (OrderCheckAssignment orderDeny : S_OrderDeny) {
                if (orderDeny.getRiderId().equals(S_Rider.get(0).getId())) {
                    throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
                }
            }
        }

        return orderMapper.insertOrderDeny(order);
    }

}
