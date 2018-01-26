package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("adminService")
public class AdminServiceImpl extends ServiceSupport implements AdminService {

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * @param adminMapper USER D A O
     */
    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public String selectLoginAdmin(Admin admin) {
        return adminMapper.selectLoginAdmin(admin);
    }

    @Override
    public int selectAdminTokenCheck(Admin admin) {
        return adminMapper.selectAdminTokenCheck(admin);
    }

    @Override
    public int insertAdminSession(Admin admin) {
        return adminMapper.insertAdminSession(admin);
    }

    @Override
    public List<Admin> getAdminInfo(Admin admin) throws AppTrException {

        List<Admin> S_Admin = adminMapper.getAdminInfo(admin);

        if (S_Admin.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Admin;
    }

}
