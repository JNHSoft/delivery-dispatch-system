package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StoreAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service("storeAdminService")
public class StoreAdminServiceImpl implements StoreAdminService {

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * @param adminMapper USER D A O
     * @author Nick
     */
    @Autowired
    public StoreAdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public List<Store> selectStoreList(Store store) {
        return adminMapper.selectStoreList(store);
    }
}
