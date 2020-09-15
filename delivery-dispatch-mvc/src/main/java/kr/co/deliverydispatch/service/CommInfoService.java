package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.store.Store;

public interface CommInfoService {

    /**
     * Chat 관련 정보
     * */
    // Chat User 신규등록
    int insertChatUser(Common common);

    /**
     * rider 등록 관련
     * */
    int insertRiderInfo(Rider rider);
    int insertSubGroupRiderRel(Rider rider);

    int updateRiderInfo(Rider rider);

    // 라이더 정보 삭제
    int deleteRiderInfo(Rider rider);

    String selectApprovalRiderPw(String id);
}