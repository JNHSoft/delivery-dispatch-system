package kr.co.cntt.core.model.store;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;


/*
    store session                                                                           2018. 01. 26        Nick
 */
@Setter
@Getter
public class StoreSession implements Dto {
    // 찾아봐야함
    private static final long serialVersionUID = 6564668381107129224L;

    private String expiryDatetime;
    private String os;
    private String pushToken;
    private String device;
    private String store_id;

}
