package kr.co.cntt.core.model.web;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>kr.co.cntt.core.model.web
 * <p>Banner.java
 * <p>배너
 * <p>MVC 메인 페이지 리스트 용도
 * @author JIN
 */
@Getter
@Setter
public class Banner implements Dto {
	private static final long serialVersionUID = -990466777779807324L;
	private int seq;
	private String bannerName;
	private String bannerLink;
	private String imageName;
	private String imagePath;
	private String imageNameBg;
	private String imagePathBg;
	private String imageNameMobile;
	private String imagePathMobile;
}
