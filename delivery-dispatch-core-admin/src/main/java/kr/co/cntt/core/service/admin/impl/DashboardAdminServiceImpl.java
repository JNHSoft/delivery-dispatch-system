package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.model.dashboard.DashboardInfo;
import kr.co.cntt.core.model.dashboard.SearchInfo;
import kr.co.cntt.core.service.admin.DashboardAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("DashboardAdminService")
public class DashboardAdminServiceImpl implements DashboardAdminService {
    @Override
    public DashboardInfo selectD30Detail(SearchInfo search) {
        return null;
    }

    @Override
    public DashboardInfo selectTPLHDetail(SearchInfo search) {
        return null;
    }

    @Override
    public DashboardInfo selectQTDetail(SearchInfo search) {
        return null;
    }

    @Override
    public DashboardInfo selectTCDetail(SearchInfo search) {
        return null;
    }

    @Override
    public DashboardInfo selectOrderStackRateDetail(SearchInfo search) {
        return null;
    }

    @Override
    public DashboardInfo selectD7Detail(SearchInfo search) {
        return null;
    }
}
