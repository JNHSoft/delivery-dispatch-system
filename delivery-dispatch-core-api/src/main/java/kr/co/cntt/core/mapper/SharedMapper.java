package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.shared.SharedRiderInfo;
import kr.co.cntt.core.model.store.Store;

import java.util.List;
import java.util.Map;

@DeliveryDispatchMapper
public interface SharedMapper {
    List<User> selectSharedAdminInfo(int adminID);

    List<SharedRiderInfo> selectSharedInfoListForAdmin(int adminID);

    List<Group> selectSharedGroupListForAdmin(int adminID);

    List<SubGroup> selectSharedSubGroupListForAdmin(Map map);

    List<Store> selectSharedStoreListForAdmin(Map map);

    int updateSharedInfo(SharedRiderInfo sharedRiderInfo);

    int insertSharedInfo(SharedRiderInfo sharedRiderInfo);
}
