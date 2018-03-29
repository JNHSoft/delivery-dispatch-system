package kr.co.deliverydispatch.service;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.tracker.Tracker;

public interface TrackerService {

    /**
     * <p> getTracker
     *
     * @param encParam
     * @return
     */
    public Tracker getTracker(String encParam) throws AppTrException;

}
