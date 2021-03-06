package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
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
    public int updateRiderInfo(Rider rider){
        Rider S_Rider = riderMapper.getRiderInfo(rider);
        if(!S_Rider.getPhone().equals(rider.getPhone())){
            rider.setChangePhone("1");
        }
        return riderMapper.updateRiderInfo(rider);
    }

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

    // chatRoom 등록
    @Override
    public int insertChatRoom(Rider rider){return adminMapper.insertChatRoom(rider);}

    // chatUserChatRoomRel 등록
    @Override
    public int insertChatUserChatRoomRel(Rider rider){return adminMapper.insertChatUserChatRoomRel(rider);}

    // 기사 삭제
    @Override
    public int deleteRider(Rider rider){
        int result = adminMapper.deleteRider(rider);

        if (result > 0) {
            return adminMapper.deleteRiderToken(rider);
        } else {
            return result;
        }
    }

    // insert Admin token
    @Override
    public int insertAdminRiderSession(Rider rider){return riderMapper.insertAdminRiderSession(rider);}


    // 기사 아이디 중복 체크
    @Override
    public int selectRiderLoginIdCheck(Rider rider) {
        return adminMapper.selectRiderLoginIdCheck(rider);
    }

    // 기사 비밀번호 초기화
    @Override
    public int resetRiderPassword(Rider rider) {
        return riderMapper.resetRiderPassword(rider);
    }

}
