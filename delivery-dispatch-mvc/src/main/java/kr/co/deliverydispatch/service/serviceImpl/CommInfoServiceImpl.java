package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.deliverydispatch.service.CommInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("commInfoService")
public class CommInfoServiceImpl extends ServiceSupport implements CommInfoService {

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;

    @Autowired
    public CommInfoServiceImpl(AdminMapper adminMapper, StoreMapper storeMapper, RiderMapper riderMapper){
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
    }


    @Override
    public int insertChatUser(Common common) {
        // Chat User 신규 등록
        return adminMapper.insertChatUser(common);

    }

    @Override
    public int insertRiderInfo(Rider rider) {
        return adminMapper.insertRider(rider);
    }

    @Override
    public int insertSubGroupRiderRel(Rider rider) {
        // 라이더 그룹 정보 등록
        return adminMapper.insertSubGroupRiderRel(rider);
    }

    @Override
    public int updateRiderInfo(Rider rider){
        return riderMapper.updateRiderInfo(rider);
    }

    @Override
    public int deleteRiderInfo(Rider rider) {
        int result = riderMapper.deleteRiderInfo(rider);

        if(result > 0){
            result = adminMapper.deleteRiderToken(rider);
        }

        return result;
    }

    @Override
    public String selectApprovalRiderPw(String id) {
        return riderMapper.selectApprovalRiderPw(id);
    }
}
