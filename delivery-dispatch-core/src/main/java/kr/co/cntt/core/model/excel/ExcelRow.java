package kr.co.cntt.core.model.excel;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import kr.co.cntt.core.exception.CnttBizException;
import kr.co.cntt.core.model.Dto;
import lombok.Data;
import lombok.NonNull;

/**
 * 엑셀 행 정보
 *
 * @author brad
 */
@SuppressWarnings("javadoc")
@Data
public class ExcelRow implements Dto {
	private static final long serialVersionUID = -7706890370637642578L;

	/** 셀 정보 */
	private TreeMap<Integer, ExcelCell> cellMap = new TreeMap<>();
	/** 행의 기본 셀 스타일 */
	private ExcelCellStyle style = null;

	public ExcelRow(final TreeMap<Integer, ExcelCell> excelCells, final ExcelCellStyle style) {
		setCellMap(excelCells);
		setStyle(style);
	}

	public ExcelRow(final List<ExcelCell> excelCells, final ExcelCellStyle style) {
		setCellMap(excelCells);
		setStyle(style);
	}

	/**
	 * 셀 목록을 열 index 0부터 순차적으로 추가
	 *
	 * @param cells 셀 목록
	 */
	public void setCellMap(@NonNull final List<ExcelCell> cells) {
		final TreeMap<Integer, ExcelCell> map = new TreeMap<Integer, ExcelCell>();

		for (int i = 0; i < cells.size(); i++) {
			if (cells.get(i) == null) {
				continue;
			}
			map.put(i, cells.get(i));
		}

		cellMap = map;
	}

	/**
	 * 키를 행 index로 하는 셀 Map을 추가
	 *
	 * @param excelCells 셀 목록. 모든 키는 0이상이어야 함.
	 */
	public void setCellMap(@NonNull final TreeMap<Integer, ExcelCell> excelCells) {
		checkIndexes(excelCells.keySet());

		cellMap = excelCells;
	}

	/**
	 * key가 엑셀 rowIndex로 사용가능한지 확인
	 *
	 * @param indexes
	 */
	private static void checkIndexes(@NonNull final Set<Integer> indexes) {
		for (final Integer rowIndex : indexes) {
			if (rowIndex < 0) {
				throw new CnttBizException("Map keys(row indexes) cannot be negative values: " + rowIndex, null);
			}
		}
	}

}
