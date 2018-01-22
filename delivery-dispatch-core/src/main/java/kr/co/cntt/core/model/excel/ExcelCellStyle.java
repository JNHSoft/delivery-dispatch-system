package kr.co.cntt.core.model.excel;

import org.apache.poi.ss.usermodel.BorderStyle;

import kr.co.cntt.core.enums.ExcelCellFormatEnum;
import lombok.Data;

/**
 * 셀 스타일 (표시 형식, 테두리, 폰트 등)
 *
 * @author brad
 */
@Data
public class ExcelCellStyle {

	/** 텍스트 줄 바꿈 */
	private boolean wrapText = false;
	/** 굵은글씨 */
	private boolean bold = false;
	/** 왼족 테두리 */
	private BorderStyle borderLeft = null;
	/** 오른쪽 테두리 */
	private BorderStyle borderRight = null;
	/** 위 테두리 */
	private BorderStyle borderTop = null;
	/** 아래 테두리 */
	private BorderStyle borderBottom = null;
	/** 셀 값 표시 형식. null일 경우 텍스트 셀 */
	private ExcelCellFormatEnum format = null;

	@SuppressWarnings("javadoc")
	public ExcelCellStyle() {
	}

	/**
	 * @param wrapText 텍스트 줄 바굼
	 * @param bold 굵게
	 * @param borderAll 모든 테두리(null OR {@link BorderStyle#NONE}: 테두리 없음)
	 * @param format 셀 값 표현 형식(null: 일반)
	 */
	public ExcelCellStyle(final boolean wrapText, final boolean bold, final BorderStyle borderAll,
			final ExcelCellFormatEnum format) {
		this.wrapText = wrapText;
		this.bold = bold;
		borderLeft = borderAll;
		borderRight = borderAll;
		borderTop = borderAll;
		borderBottom = borderAll;
		this.format = format;
	}

}