package kr.co.cntt.core.model.web;

import javax.validation.constraints.NotNull;

import kr.co.cntt.core.model.AbstractPagination;
import lombok.Getter;
import lombok.Setter;

/**
 * FAQ,공지사항
 * TB_BOARD 모델
 * @author ok hyun
 *
 */
@Getter
@Setter
public class Board extends AbstractPagination{
	
	@NotNull
	//TB_BOARD
	private int boardSeq;
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
}
