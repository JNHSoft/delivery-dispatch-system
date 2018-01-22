package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.mapper.TestMapper;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("testService")
public class TestServiceImpl extends ServiceSupport implements TestService {

    /**
     * TEST DAO
     */
    private TestMapper testMapper;

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
