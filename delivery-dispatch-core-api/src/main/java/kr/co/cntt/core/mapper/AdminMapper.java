package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.admin.Admin;

import java.util.List;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> AdminMapper.java </p>
 * <p> Admin 관련 </p>
 *
 * @author Aiden
 * @see DeliveryDispatchMapper
 */
@DeliveryDispatchMapper
public interface AdminMapper {

    /**
     * <p> selectLoginAdmin
     *
     * @return loginId String
     */
    public Admin selectLoginAdmin(Admin admin);

    /**
     * <p> selectAdminTokenCheck
     *
     * @return Count Int
     */
    public int selectAdminTokenCheck(Admin admin);

    /**
     * <p> Admin Session Insert
     *
     * @return Insert 결과값
     */
    public int insertAdminSession(Admin admin);


    /**
     * <p> Admin 정보 조회
     *
     * @return Admin Info 조회 결과값
     */
    public List<Admin> getAdminInfo(Admin admin);


}
