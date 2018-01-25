package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.OrderService;
import kr.co.cntt.core.util.Geocoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceSupport implements OrderService {

    /**
     * Order DAO
     */
    private OrderMapper orderMapper;

    /**
     * @param orderMapper ORDER D A O
     */
    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public int postOrder(Order order) {
        Geocoder geocoder = new Geocoder();

        try {
            Map<String, String> geo = geocoder.getLatLng(order.getAddress());
            order.setLatitude(geo.get("lat"));
            order.setLongitude(geo.get("lng"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderMapper.insertOrder(order);
    }

}
