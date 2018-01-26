package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.admin.Admin;

import java.util.List;

public interface AdminService {
    /**
     * <p> selectLoginAdmin
     *
     * @return
     */
    public String selectLoginAdmin(Admin admin);

    /**
     * <p> selectAdminTokenCheck
     *
     * @return
     */
    public int selectAdminTokenCheck(Admin admin);

    /**
     * <p> insertAdminSession
     *
     * @return
     */
    public int insertAdminSession(Admin admin);

    /**
     * <p> selectAdminInfo
     *
     * @return
     */
    public List<Admin> getAdminInfo(Admin admin) throws AppTrException;
}
