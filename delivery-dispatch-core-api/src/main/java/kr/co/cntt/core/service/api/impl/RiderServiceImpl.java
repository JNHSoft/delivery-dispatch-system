package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.model.rider.R_Rider;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.RiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("riderService")
public class RiderServiceImpl extends ServiceSupport implements RiderService {

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;

    /**
     * @param riderMapper USER D A O
     */
    @Autowired
    public RiderServiceImpl(RiderMapper riderMapper) {
        this.riderMapper = riderMapper;
    }

    @Override
    public String selectLoginRider(Rider rider) {
        return riderMapper.selectLoginRider(rider);
    }

    @Override
    public int selectRiderTokenCheck(Rider rider) {
        return riderMapper.selectRiderTokenCheck(rider);
    }

    @Override
    public int insertRiderSession(Rider rider) {
        return riderMapper.insertRiderSession(rider);
    }

    @Override
    public List<Rider> getRiderInfo(Rider rider) throws AppTrException{

        List<Rider> S_Rider = riderMapper.getRiderInfo(rider);

        if(S_Rider.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Rider;
    }

}
