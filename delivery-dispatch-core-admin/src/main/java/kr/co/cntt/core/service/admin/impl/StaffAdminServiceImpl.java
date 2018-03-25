package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StaffAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("StaffAdminService")
public class StaffAdminServiceImpl implements StaffAdminService {

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
    public StaffAdminServiceImpl(AdminMapper adminMapper , StoreMapper storeMapper, RiderMapper riderMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
    }

    // 기사 리스트
    @Override
    public List<Rider> selectRiderList(Rider rider) {
        return adminMapper.selectRiders(rider);
    }

    // 기사 상세보기
    @Override
    public Rider getRiderInfo(Rider rider) {
        return riderMapper.getRiderInfo(rider);
    }


    // 상점 리스트
    @Override
    public List<Store> selectStoreList(Store store) {
        return adminMapper.selectStores(store);
    }

    // 기사 정보 수정
    @Override
    public int updateRiderInfo(Rider rider){return riderMapper.updateRiderInfo(rider);}

    // 기사 상점만 수정
    @Override
    public int updateRiderStore(Rider rider){
        return adminMapper.updateRiderStore(rider);
    }


    // 기사 등록
    @Override
    public int insertRider(Rider rider){return adminMapper.insertRider(rider);}

    // 기사 상점 및 그룹 추가
    @Override
    public int insertSubGroupRiderRel(Rider rider){return adminMapper.insertSubGroupRiderRel(rider);}


    // 상점 정보 가져오기
    @Override
    public Store selectStoreInfo(Store store) {
        return storeMapper.selectStoreInfo(store);
    }

    // chatUserId 등록
    @Override
    public int insertChatUser(Rider rider){return adminMapper.insertChatUser(rider);}

    // 기사 삭제
    @Override
    public int deleteRider(Rider rider){return adminMapper.deleteRider(rider);}


}
