package kr.co.cntt.core.model.gis;

import kr.co.cntt.core.model.Dto;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주문
 * 배달지역 모델
 * @author tony
 *
 */
@Data
@NoArgsConstructor
public class DeliveryZone implements Dto {
	
	private static final long serialVersionUID = -1274033975818274637L;
	
	public DeliveryZone(String cx, String cy){
		this.cx = cx;
		this.cy = cy;
	}
	
	private String idx;
	private String title;
	private String sido;
	private String sigungu;
	private String dong;
	private String addr;
	private String cx;
	private String cy;
	private String url;
	private int zoneidx;
	private String zoneurl;
	private String zonetitle;
	private String time;
	private String useYn;
	
}
