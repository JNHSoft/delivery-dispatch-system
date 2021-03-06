package kr.co.cntt.core.model.admin;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import kr.co.cntt.core.model.AbstractPagination;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

/**
 * admin
 * FAQ,공지사항
 * TB_BOARD 모델
 * @author ok hyun
 *
 */
@Getter
@Setter
public class Board extends AbstractPagination implements Dto{
	
	private static final long serialVersionUID = -5731554221735110861L;
	
	@NotNull
	//TB_BOARD
	private String boardSeq;
	private String boardTitle;
	private String boardContent;
	private String imagePath;
	private int readCount;
	private String userId;
	private String regDate;
	private String regTime;
	private String updDate;
	private String updTime;
	private String boardTab;
	private String useFlag;
	
	//이전글,다음글,정렬번호
	private int rowNum;
	private String prevTitle;
	private String nextTitle;
	private int prevSeq;
	private int nextSeq;
	private int prevRowNum;
	private int nextRowNum;
	
	// MultipartFile
	private MultipartFile boardImage;
}
