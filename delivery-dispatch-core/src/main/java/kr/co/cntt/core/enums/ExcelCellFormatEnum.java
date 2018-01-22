package kr.co.cntt.core.enums;

import com.google.gson.Gson;

import lombok.Getter;

/**
 * 엑셀 셀 표시 형식
 *
 * @author brad
 */
@SuppressWarnings("javadoc")
@Getter
public enum ExcelCellFormatEnum {
	INT("0", true),
	CURRENCY("#,##0", true),
	DECIMAL("#,##0.00", true),
	DATE("yyyy-mm-dd", true),
	TIME("h:mm:ss", true);

	/** 엑셀 표시 형식 */
	private String format;
	/** 숫자여부 */
	private boolean numeric;

	private ExcelCellFormatEnum(final String format, final boolean numeric) {
		this.format = format;
		this.numeric = numeric;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
