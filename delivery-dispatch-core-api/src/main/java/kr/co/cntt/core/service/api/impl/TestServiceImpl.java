package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.config.api.TestMapper;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("testService")
public class TestServiceImpl extends ServiceSupport implements TestService {

    /**
     * TEST DAO
     */
    private TestMapper testMapper;


    public TestServiceImpl(){}

    /**
     * @param testMapper TEST D A O
     */
    @Autowired
    public TestServiceImpl (TestMapper testMapper) {
        this.testMapper = testMapper;
    }

    @Override
    public String selectTestQuery() {
        return testMapper.selectTestQuery();
    }
}
