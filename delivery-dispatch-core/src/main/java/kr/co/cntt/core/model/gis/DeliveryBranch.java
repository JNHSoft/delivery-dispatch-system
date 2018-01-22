package kr.co.cntt.core.model.gis;

import kr.co.cntt.core.model.AbstractPagination;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>kr.co.cntt.core.model.gis
 * <p>DeliveryBranch.java
 * <p>소구역 AbstractPagination 상속
 * @author JIN
 */
@Getter
@Setter
public class DeliveryBranch extends AbstractPagination {
	private static final long serialVersionUID = -7677043935139385175L;
	private String no;
	private String branchCode;
	private String idx;
	private String title;
	private String sido;
	private String sigungu;
	private String dong;
	private String useYn;
	private String updId; 
	private String updDate;
	private String aBranchName;
	
	// 검색 관련
	private String searchdeliveryUseYn;
	private String searchBranchId;
}
