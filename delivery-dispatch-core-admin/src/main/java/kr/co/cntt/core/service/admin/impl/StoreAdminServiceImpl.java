package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StoreAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("storeAdminService")
public class StoreAdminServiceImpl implements StoreAdminService {

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;


    /**
     * @param adminMapper USER D A O
     * @author Nick
     */
    @Autowired
    public StoreAdminServiceImpl(AdminMapper adminMapper , StoreMapper storeMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
    }
    // 상점 리스트
    @Override
    public List<Store> selectStoreList(Store store) {
        return adminMapper.selectStores(store);
    }
    // 상점 상세보기
    @Override
    public Store selectStoreInfo(Store store) {
        return storeMapper.selectStoreInfo(store);
    }

    // 그룹 리스트
    @Override
    public List<Group> selectGroupsList(Store store){
        return adminMapper.selectGroups(store);
    }
    // 서브 그룹 리스트
    @Override
    public List<SubGroup> selectSubGroupsList(Store store){
        return adminMapper.selectSubGroups(store);
    }

    // 상점 정보 수정
    @Override
    public int updateStoreInfo(Store store){return storeMapper.updateStoreInfo(store);}

    // 상점 서브그룹 정보 수정
    @Override
    public int updateSubGroupStoreRel(Store store){
        return adminMapper.updateSubGroupStoreRel(store);
    }

    // 상점 배정 정보 수정
    @Override
    public int updateStoreAssignmentStatus(Store store){return storeMapper.updateStoreAssignmentStatus(store);}

    // 상점 subGroupRel 등록
    @Override
    public int insertSubGroupStoreRel(Store store){return adminMapper.insertSubGroupStoreRel(store);}

    // 상점 subGroupRel 리스트
    @Override
    public List<SubGroupStoreRel> selectSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel){return adminMapper.selectSubgroupStoreRels(subGroupStoreRel);}

    // 상점 등록
    @Override
    public int insertStore(Store store){return adminMapper.insertStore(store);}

    // chatUserId 등록
    @Override
    public int insertChatUser(Store store){return adminMapper.insertChatUser(store);}

    // chatRoom 등록
    @Override
    public int insertChatRoom(Store store){return adminMapper.insertChatRoom(store);}

    // chatUserChatRoomRel 등록
    @Override
    public int insertChatUserChatRoomRel(Store store){return adminMapper.insertChatUserChatRoomRel(store);}

    // 기사 삭제
    @Override
    public int deleteStore(Store store){
        // 2020.04.21 Store 삭제 시 소속된 라이더 존재 시 삭제 미 진행
        if (adminMapper.selectRiderCountForStore(store) > 0){
            log.info("deleteStore => selectRiderCountForStore  StoreInfo : {}", store.getId());
            return 0;
        }

        return adminMapper.deleteStore(store);
    }

    // insert Admin token
    @Override
    public int insertAdminStoreSession(Store store){return storeMapper.insertAdminStoreSession(store);}

    // 상점 아이디 중복 체크
    @Override
    public int selectStoreLoginIdCheck(Store store) {
        return adminMapper.selectStoreLoginIdCheck(store);
    }

    // 기사 서브그룹 수정 by store_id
    @Override
    public int putSubGroupRiderRelByStoreId(Store store){ return adminMapper.updateSubGroupRiderRelByStoreId(store);}

    // 상점 비밀번호 초기화
    @Override
    public int resetStorePassword(Store store) {
        return storeMapper.resetStorePassword(store);
    }

}
