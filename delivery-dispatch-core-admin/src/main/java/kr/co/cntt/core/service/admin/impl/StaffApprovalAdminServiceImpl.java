package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StaffApprovalAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("StaffApprovalAdminService")
public class StaffApprovalAdminServiceImpl implements StaffApprovalAdminService {
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

    /** 라이더 승인 목록 가져오기 */
    @Autowired
    public StaffApprovalAdminServiceImpl(AdminMapper adminMapper , StoreMapper storeMapper, RiderMapper riderMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
    }

    @Override
    public List<RiderApprovalInfo> getRiderApprovalList(Common common) {
        List<RiderApprovalInfo> approvalRider = riderMapper.selectApprovalRiderList(common);
        if (approvalRider.size() == 0) {
            return Collections.<RiderApprovalInfo>emptyList();
        }

        return approvalRider;
    }

    @Override
    public RiderApprovalInfo getRiderApprovalInfo(Common common) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            common.setRole("ROLE_ADMIN");
        }
        RiderApprovalInfo info = riderMapper.selectApprovalRiderInfo(common);

        return info;
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
    public int setRiderInfo(RiderApprovalInfo riderInfo) {
        return riderMapper.updateApprovalRiderInfo(riderInfo);
    }

    @Override
    public int updateRiderSession(RiderSession session) {
        return riderMapper.updateRiderSession(session);
    }

    @Override
    public String selectApprovalRiderPw(String id) {
        return riderMapper.selectApprovalRiderPw(id);
    }

    @Override
    public Store selectStoreInfo(Store store) {
        return storeMapper.selectStoreInfo(store);
    }

    // chatUserId 등록
    @Override
    public int insertChatUser(Rider rider){return adminMapper.insertChatUser(rider);}

    // 기사 등록
    @Override
    public int insertRider(Rider rider){return adminMapper.insertRider(rider);}

    // 기사 상점 및 그룹 추가
    @Override
    public int insertSubGroupRiderRel(Rider rider){return adminMapper.insertSubGroupRiderRel(rider);}

    // insert Admin token
    @Override
    public int insertAdminRiderSession(Rider rider){return riderMapper.insertAdminRiderSession(rider);}

    // 기사 정보 수정
    @Override
    public int updateRiderInfo(Rider rider){
        Rider S_Rider = riderMapper.getRiderInfo(rider);
        if(!S_Rider.getPhone().equals(rider.getPhone())){
            rider.setChangePhone("1");
        }
        return riderMapper.updateRiderInfo(rider);
    }

    // 라이더 Row Data 삭제
    @Override
    public int deleteApprovalRiderRowData(RiderApprovalInfo riderInfo) {
        return riderMapper.deleteApprovalRiderRowData(riderInfo);
    }

    // 기사 비밀번호 초기화
    @Override
    public int resetRiderPassword(Rider rider) {
        return riderMapper.resetRiderPassword(rider);
    }

    // 라이더 정보 가져오기
    @Override
    public Rider getRiderInfo(Common common){
        return riderMapper.getRiderInfo(common);
    }

    // 라이더를 빌려줄 매장의 정보 가져오기
    @Override
    public List<Store> getSharedStoreList(Rider rider){
        return storeMapper.selectSharedStoreList(rider);
    }

    /**
     * 21.05.21 라이더가 속해질 타 매장의 정보 저장
     * */
    @Override
    public int regSharedStoreInfo(Rider rider){
        return riderMapper.insertSharedStoreInfo(rider);
    }

    /**
     * 21.05.21 라이더가 소속되어 있던 타 매장 정보 삭제
     * */
    @Override
    public int deleteSharedStoreInfo(Rider rider){
        return riderMapper.deleteSharedStoreInfo(rider);
    }

}
