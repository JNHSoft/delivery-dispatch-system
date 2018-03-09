package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.deliverydispatch.service.StoreOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service("storeOrderService")
public class StoreOrderServiceImpl implements StoreOrderService{
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
    public List<Order> getOrders(Order order){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            order.setRole("ROLE_STORE");
        }

        if (order.getStatus() != null) {
            String tmpString = order.getStatus().replaceAll("[\\D]", "");
            char[] statusArray = tmpString.toCharArray();

            order.setStatusArray(statusArray);
        }

        List<Order> S_Order = orderMapper.selectOrders(order);

        if (S_Order.size() == 0) {
            System.out.println("메롱");
            return Collections.<Order>emptyList();
        }

        return S_Order;
    }
}
