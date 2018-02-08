package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.OrderService;
import kr.co.cntt.core.util.Geocoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @param orderMapper ORDER D A O
     * @param storeMapper STORE D A O
     */
    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, StoreMapper storeMapper) {
        this.orderMapper = orderMapper;
        this.storeMapper = storeMapper;
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
//             TODO. 자동 배정
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

}
