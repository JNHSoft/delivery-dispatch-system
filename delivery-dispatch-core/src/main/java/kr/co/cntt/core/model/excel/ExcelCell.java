package kr.co.cntt.core.model.excel;

import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.poi.ss.usermodel.DateUtil;

import kr.co.cntt.core.enums.ExcelCellFormatEnum;
import kr.co.cntt.core.model.Dto;
import lombok.Data;
import lombok.ToString;

/**
 * 엑셀 작성시 사용하는 셀 정보
 *
 * @author brad
 */
@Data
public class ExcelCell implements Dto {
	private static final long serialVersionUID = -5475395295548062237L;

	/** 셀의 값. 함수일 경우, 가장 앞의 "="을 제외(예: "SUM(A1:A10)") */
	private Object value;
	/** value값의 함수 여부 */
	private boolean function = false;
	/** 셀 스타일 */
	private ExcelCellStyle style = null;

	@SuppressWarnings("javadoc")
	public ExcelCell() {
	}

	/**
	 * 일반 셀
	 *
	 * @param value 셀의 값
	 */
	public ExcelCell(final String value) {
		setValue(value);
	}

	/**
	 * 일반 OR 함수 셀
	 *
	 * @param value 셀의 값. 함수일 경우, 가장 앞의 "="을 제외한 텍스트(예: "SUM(A1:A10)")
	 * @param style 셀 스타일. 행 스타일 OR 시트 스타일이 있다면 override한다.
	 * @param function 함수 여부
	 */
	public ExcelCell(final String value, final ExcelCellStyle style, final boolean function) {
		setValue(value);
		setStyle(style);
		setFunction(function);
	}

	/**
	 * 숫자 셀
	 *
	 * @param value
	 * @param style 셀 스타일. 행 스타일 OR 시트 스타일이 있다면 override한다.
	 */
	public ExcelCell(final Number value, final ExcelCellStyle style) {
		setValue(value);
		setStyle(style);
	}

	/**
	 * 날짜 셀
	 *
	 * @param value
	 * @param style 셀 스타일. 행 스타일 OR 시트 스타일이 있다면 override한다.
	 */
	public ExcelCell(final LocalDate value, final ExcelCellStyle style) {
		setValue(value);
		setStyle(style == null ? new ExcelCellStyle() : style);
		getStyle().setFormat(ExcelCellFormatEnum.DATE);
	}

	/**
	 * 시각 셀
	 *
	 * @param value
	 * @param style 셀 스타일. 행 스타일 OR 시트 스타일이 있다면 override한다.
	 */
	public ExcelCell(final LocalTime value, final ExcelCellStyle style) {
		setValue(value);
		setStyle(style == null ? new ExcelCellStyle() : style);
		getStyle().setFormat(ExcelCellFormatEnum.TIME);
	}

	@SuppressWarnings("javadoc")
	public void setValue(final String value) {
		this.value = value;
	}

	@SuppressWarnings("javadoc")
	public void setValue(final Number value) {
		this.value = value == null ? null : value.doubleValue();
	}

	@SuppressWarnings("javadoc")
	public void setValue(final LocalDate value) {
		this.value = value == null ? null : DateUtil.getExcelDate(java.sql.Date.valueOf(value));
	}

	@SuppressWarnings("javadoc")
	public void setValue(final LocalTime value) {
		this.value = value == null ? null
				: DateUtil.convertTime(kr.co.cntt.core.util.DateUtil.localTimeToStr(value, "HH:mm:ss"));
	}

}
