package kr.co.cntt.core.model.gis;

import kr.co.cntt.rest.interfaces.Dto;
import lombok.Getter;
import lombok.Setter;

/**
 * GIS API 모델
 * @author Tony
 */
@Getter
@Setter
public class Gis implements Dto {
	
	private static final long serialVersionUID = 3163641320013042711L;
	
	private String url;
	
	/** 
	 * [지번주소] 동 검색 
	 * /getAddrSearch_Dong_Name2_db.do
	 */
	private String name;
	private String bunhal;
	private String code;
	private String dong;
	private String fullname;
	private String gubun;
	private String gugun;
	private String guguncode;
	//private String name;
	private String rcode;
	private String ri;
	private String rname;
	private String sido;
	private String umname;
	
	/**
	 * [지번주소] 번지 검색
	 * /getAddrJibun3_db.do
	 */
	//private String code;
	//private String bunji;
	
	private String bcode;
	private String bunji;
	private int cx;
	private int cy;
	//private String dong;
	//private String gugun;
	private String ho;
	//private String ri;
	private String san;
	//private String sido;
	
	/**
	 * [지번주소] 건물명 검색 
	 * /poiSearch2.do
	 */
	private String dongcode;
	//private String sido;
	private String sigungu;
	//private String dong;
	//private String name;
	private String listCount = "9999";
	private int pageNum = 0;

	private String addr_bun;
	private String addr_ho;
	private String addr_ismountain;
	private String compareStr;
	private String d1name;
	private String d2name;
	private String d3name;
	//private String dong;
	//private String dongcode;
	private String place_id;
	private String place_name;
	private int point_x;
	private int point_y;
	private String resultStr;
	private String source;
	private String synonym;
	private int totalCount;
	private String viewStr;
	private String vname;

	/**
	 * [도로명주소] 도로명 검색
	 * /getRoadname2_db.do
	 */
	//private String name;
	
	private String addrcode;
	private String address;
	//private String dong;
	//private String gugun;
	//private String ri;
	private String roadaddr;
	private String roadcode;
	private String roadname;
	//private String sido;
	private String x;
	private String xylev;
	private String y;
	
	/**
	 * [도로명주소] 건물번호 검색
	 * /getRoadaddrSearchList2_db.do
	 */
	//private String code;
	private String roadnum;
	
	//private String bcode;
	private String bdcode;
	private String bddetail;
	private String bdname;
	private String bdongnm;
	//private String bunji;
	private String dongseq;
	//private String gugun;
	private String hcode;
	private String hdongnm;
	//private String ho;
	private String mn;
	//private int point_x;
	//private int point_y;
	private String postcd;
	private String postseq;
	//private String ri;
	//private String roadcode;
	//private String roadname;
	//private String san;
	//private String sido;
	private String sn;
	private String under;
	
	
	@Override
	public String toString() {
		return "Gis [url=" + url + ", name=" + name + ", bunhal=" + bunhal + ", code=" + code + ", dong=" + dong
				+ ", fullname=" + fullname + ", gubun=" + gubun + ", gugun=" + gugun + ", guguncode=" + guguncode
				+ ", rcode=" + rcode + ", ri=" + ri + ", rname=" + rname + ", sido=" + sido + ", umname=" + umname
				+ ", bcode=" + bcode + ", bunji=" + bunji + ", cx=" + cx + ", cy=" + cy + ", ho=" + ho + ", san=" + san
				+ ", dongcode=" + dongcode + ", sigungu=" + sigungu + ", listCount=" + listCount + ", pageNum="
				+ pageNum + ", addr_bun=" + addr_bun + ", addr_ho=" + addr_ho + ", addr_ismountain=" + addr_ismountain
				+ ", compareStr=" + compareStr + ", d1name=" + d1name + ", d2name=" + d2name + ", d3name=" + d3name
				+ ", place_id=" + place_id + ", place_name=" + place_name + ", point_x=" + point_x + ", point_y="
				+ point_y + ", resultStr=" + resultStr + ", source=" + source + ", synonym=" + synonym + ", totalCount="
				+ totalCount + ", viewStr=" + viewStr + ", vname=" + vname + ", addrcode=" + addrcode + ", address="
				+ address + ", roadaddr=" + roadaddr + ", roadcode=" + roadcode + ", roadname=" + roadname + ", x=" + x
				+ ", xylev=" + xylev + ", y=" + y + ", roadnum=" + roadnum + ", bdcode=" + bdcode + ", bddetail="
				+ bddetail + ", bdname=" + bdname + ", bdongnm=" + bdongnm + ", dongseq=" + dongseq + ", hcode=" + hcode
				+ ", hdongnm=" + hdongnm + ", mn=" + mn + ", postcd=" + postcd + ", postseq=" + postseq + ", sn=" + sn
				+ ", under=" + under + "]";
	}
	
	

}
