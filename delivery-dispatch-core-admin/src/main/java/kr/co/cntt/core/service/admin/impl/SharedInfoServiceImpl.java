package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.SharedMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.shared.SharedRiderInfo;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.SharedInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service("sharedInfoService")
public class SharedInfoServiceImpl implements SharedInfoService {
    /**
     * Shared DAO
     * */
    private SharedMapper sharedMapper;

    @Autowired
    public SharedInfoServiceImpl(SharedMapper sharedMapper){this.sharedMapper = sharedMapper;}

    /**
     * 공유가 허용된 관리자 정보 조회
     * */
    public List<User> getAllowAdminInfo(int adminID){
        List<User> sharedAdmins = sharedMapper.selectSharedAdminInfo(adminID);

        return sharedAdmins;
    }

    /**
     * 공유된 데이터 Row
     * */
    public List<SharedRiderInfo> getSharedInfoListForAdmin(int adminID){
        List<SharedRiderInfo> riderInfos = sharedMapper.selectSharedInfoListForAdmin(adminID);

        return riderInfos;
    }

    /**
     * 공유가 허용된 관리자의 그룹 리스트 추출
     * */
    public List<Group> getSharedGroupListForAdmin(int adminID){
        List<Group> groupList = sharedMapper.selectSharedGroupListForAdmin(adminID);

        return groupList;
    }

    /**
     * 공유가 허용된 관리자의 서브 그룹 리스트 추출
     * */
    public List<SubGroup> getSharedSubGroupListForAdmin(Map map){
        List<SubGroup> groupList = sharedMapper.selectSharedSubGroupListForAdmin(map);

        return groupList;
    }

    /**
     * 공유가 허용된 관리자의 매장 리스트 추출
     * */
    public List<Store> getSharedStoreListForAdmin(Map map){
        List<Store> groupList = sharedMapper.selectSharedStoreListForAdmin(map);

        return groupList;
    }

    /**
     * 쉐어링 정보 변경
     * */
    public int setSharedInfoUpdate(SharedRiderInfo sharedRiderInfo){

        if (sharedRiderInfo.getSeq() == 0){
            sharedMapper.insertSharedInfo(sharedRiderInfo);
        }else{
            sharedMapper.updateSharedInfo(sharedRiderInfo);
        }
        return 0;
    }
}
