package kr.co.cntt.core.service.admin.impl;


import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.GroupAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("groupAdminService")
public class GroupAdminServiceImpl implements GroupAdminService {

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
    public GroupAdminServiceImpl(AdminMapper adminMapper , StoreMapper storeMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
    }

    // 그룹 리스트
    @Override
    public List<Group> selectGroupsList(Store store){
        return adminMapper.selectGroups(store);
    }

    // 그룹 수정
    @Override
    public int updateGroup(Group group){
        return adminMapper.updateGroup(group);
    }

    // 그룹 등록
    @Override
    public int insertGroup(Group group){
        return adminMapper.insertGroup(group);
    }

    // 그룹 삭제
    @Override
    public int deleteGroup(Group group){
        return adminMapper.deleteGroup(group);
    }

    // 서브 그룹 수정
    @Override
    public int updateSubGroup(SubGroup subGroup){
        return adminMapper.updateSubGroup(subGroup);
    }

    // 서브그룹 리스트
    @Override
    public List<SubGroup> selectSubGroupsList(Admin admin){
        return adminMapper.selectSubGroups(admin);
    }

    // 서브그룹 삭제
    @Override
    public int deleteSubGroup(SubGroup subGroup){
        return adminMapper.deleteSubGroup(subGroup);
    }


    // 상점 그룹 목록 리스트
    @Override
    public List<SubGroupStoreRel> selectSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel){
        return adminMapper.selectSubgroupStoreRels(subGroupStoreRel);
    }

    // 상점 서브 그룹만 수정
    @Override
    public int updateStoreSubGroup(Store store){
        return adminMapper.updateStoreSubGroup(store);
    }


    // 서브 그룹 등록
    @Override
    public int insertSubGroup(SubGroup subGroup){
        return adminMapper.insertSubGroup(subGroup);
    }


    // 상점 미지정 그룹 목록 리스트
    @Override
    public List<SubGroupStoreRel> selectNoneSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel){
        return adminMapper.selectNoneSubgroupStoreRels(subGroupStoreRel);
    }

    // 상점 서브 그룹 삭제
    @Override
    public int deleteSubGroupStoreRel(SubGroupStoreRel subGroupStoreRel){
        return adminMapper.deleteSubGroupStoreRel(subGroupStoreRel);
    }

    // 상점 그룹 서브 그룹 등록
    @Override
    public int insertSubGroupStoreRel(Store store){
        return adminMapper.insertSubGroupStoreRel(store);
    }


}
