package kr.co.cntt.core.model.excel;

import java.util.NoSuchElementException;
import java.util.TreeMap;

import kr.co.cntt.core.model.Dto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

/**
 * 엑셀 시트 정보
 *
 * @author brad
 */
@Data
public class ExcelSheet implements Dto {
	private static final long serialVersionUID = -7706890370637642578L;

	/** 시트 명. null일 경우 자동입력 */
	private String name;
	/** rowMap 행 정보. not null. */
	@Setter(AccessLevel.NONE)
	@NonNull
	private final TreeMap<Integer, ExcelRow> rowMap = new TreeMap<>();

	/**
	 * 빈 시트를 생성
	 */
	public ExcelSheet() {
	}

	/**
	 * 특정 이름의 빈 시트 생성
	 *
	 * @param name 시트명
	 */
	public ExcelSheet(final String name) {
		setName(name);
	}

	/**
	 * 셀 정보 읽기
	 *
	 * @param rowNum
	 * @param colNum
	 * @return
	 */
	public ExcelCell getCell(final int rowNum, final int colNum) {
		final ExcelRow excelRow = getRowMap().get(rowNum);
		if (excelRow == null) {
			return null;
		}

		final ExcelCell cell = excelRow.getCellMap().get(colNum);
		if (cell == null) {
			return null;
		}

		return cell;
	}

	/**
	 * 셀 정보 입력
	 *
	 * @param rowNum
	 * @param colNum
	 * @param cell null일 경우 {@link #removeCell(int, int)}과 동일한 기능 수행
	 */
	public void setCell(final int rowNum, final int colNum, final ExcelCell cell) {
		if (cell == null) {
			removeCell(rowNum, colNum);
			return;
		}

		final ExcelRow excelRow = getRowMap().get(rowNum);
		if (excelRow == null) {
			final TreeMap<Integer, ExcelCell> newRow = new TreeMap<>();
			newRow.put(colNum, cell);

			getRowMap().put(rowNum, new ExcelRow(newRow, null));
			return;
		}

		excelRow.getCellMap().put(colNum, cell);
	}

	/**
	 * 셀 정보 제거
	 *
	 * @param rowNum
	 * @param colNum
	 */
	public void removeCell(final int rowNum, final int colNum) {
		final ExcelRow excelRow = getRowMap().get(rowNum);
		if (excelRow == null) {
			return;
		}

		excelRow.getCellMap().remove(colNum);
		if (excelRow.getCellMap().size() == 0) {
			getRowMap().remove(rowNum);
		}
	}

	/**
	 * 행 정보 읽기
	 *
	 * @param rowNum
	 * @return
	 */
	public ExcelRow getRow(final int rowNum) {
		return getRowMap().get(rowNum);
	}

	/**
	 * 행 정보 제거
	 *
	 * @param rowNum
	 */
	private void removeRow(final int rowNum) {
		getRowMap().remove(rowNum);
	}

	/**
	 * 입력된 셀 정보에서 마지막 행 번호 출력
	 *
	 * @return 마지막 행 번호. 셀 정보가 없는 경우 null.
	 */
	public Integer getLastRowNum() {
		try {
			return getRowMap().lastKey();
		} catch (final NoSuchElementException e) {
			return null;
		}
	}

}
