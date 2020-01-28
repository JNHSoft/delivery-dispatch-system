package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.shared.SharedRiderInfo;
import kr.co.cntt.core.model.store.Store;

import java.util.List;
import java.util.Map;

public interface SharedInfoService {
    /**
     * 허용된 관리자 ID 리스트 추출
     * */
    List<User> getAllowAdminInfo(int adminID);

    /**
     * 공유된 데이터 Row
     * */
    List<SharedRiderInfo> getSharedInfoListForAdmin(int adminID);

    /**
     * 공유가 허용된 관리자의 그룹 리스트 추출
     * */
    List<Group> getSharedGroupListForAdmin(int adminID);

    /**
     * 공유가 허용된 관리자의 하위 리스트 추출
     * */
    List<SubGroup> getSharedSubGroupListForAdmin(Map map);

    /**
     * 공유가 허용된 관리자의 매장 리스트 추출
     * */
    List<Store> getSharedStoreListForAdmin(Map map);

    /**
     * 쉐어링 정보 저장
     * */
    int setSharedInfoUpdate(SharedRiderInfo sharedRiderInfo);

}
