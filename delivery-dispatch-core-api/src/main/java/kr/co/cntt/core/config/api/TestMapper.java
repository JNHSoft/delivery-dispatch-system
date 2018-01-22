package kr.co.cntt.core.config.api;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;

/**
 * <p> kr.co.cntt.core.config </p>
 * <p> TestMapper.java </p>
 * <p> TEST Mapper </p>
 * @see DeliveryDispatchMapper
 * @author Merlin
 */
@DeliveryDispatchMapper
public interface TestMapper {
    /**
     * <p> TEST
     *
     * @return TEST String
     */
    public String selectTestQuery();
}
