package kr.co.cntt.core.service.admin;


import kr.co.cntt.core.model.admin.Admin;

public interface AccountAdminService {

    /**
     * <p> getAdminAccount
     *
     * @param admin
     * @return Admin
     */
    public Admin getAdminAccount(Admin admin);


    /**
     * <p> getAdminAccount
     *
     * @param admin
     * @return int
     */
    public int putAdminAccount(Admin admin);

}
