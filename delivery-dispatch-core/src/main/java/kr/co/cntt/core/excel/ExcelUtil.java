package kr.co.cntt.core.excel;

import static org.springframework.util.StringUtils.isEmpty;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ui.Model;

import kr.co.cntt.core.model.excel.ExcelCell;
import kr.co.cntt.core.model.excel.ExcelCellStyle;
import kr.co.cntt.core.model.excel.ExcelRow;
import kr.co.cntt.core.model.excel.ExcelSheet;
import lombok.NonNull;

/**
 * 엑셀 생성 툴
 */
public class ExcelUtil {

	/** xlsx 형식 엑셀 출력 뷰 이름(스트리밍) */
	public static final String EXCEL_XLSX_STREAMING_VIEW = "excelXlsxStreamingView";
	/** xlsx 형식 엑셀 출력 뷰 이름 */
	public static final String EXCEL_XLSX_VIEW = "excelXlsxView";
	/** xls 형식 엑셀 출력 뷰 이름 */
	public static final String EXCEL_XLS_VIEW = "excelXlsView";

	private static final String FILE_NAME = "poiExcelFileName";
	private static final String DATA = "poiExcelData";

	@NonNull
	private final Workbook workbook;
	@NonNull
	private final CreationHelper createHelper;
	@NonNull
	private final Map<String, Object> model;
	@NonNull
	private final HttpServletRequest request;
	@NonNull
	private final HttpServletResponse response;

	/**
	 * 엑셀 작성하면서 생성된 CellStyle모음. 동일한 스타일 중복생성 방지용. 직접참조 대신 {@link #getCellStyle(String, boolean, boolean, BorderStyle)}
	 * 활용할 것
	 */
	private final Map<ExcelCellStyle, CellStyle> cellStyleMap = new HashMap<>();

	/**
	 * 입력된 조건에 맞는 스타일을 Map에서 검색하여 return. 없으면 새로 생성하여 맵에 저장 및 return.
	 *
	 * @param bold 굵은글씨 여부
	 * @return
	 * @author brad
	 */
	private CellStyle getCellStyle(final ExcelCellStyle style) {

		//Map 확인
		CellStyle cellStyle = cellStyleMap.get(style);
		if (cellStyle != null) {
			return cellStyle;
		}

		//새 스타일 생성
		cellStyle = workbook.createCellStyle();

		if (style != null) {
			if (style.getFormat() != null) {
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(style.getFormat().getFormat()));
			}

			if (style.isBold()) {
				//폰트
				final Font font = workbook.createFont();
				font.setBold(true);
				font.setFontName("맑은 고딕");

				cellStyle.setFont(font);
			}

			if (style.getBorderBottom() != null) {
				cellStyle.setBorderBottom(style.getBorderBottom());
			}
			if (style.getBorderTop() != null) {
				cellStyle.setBorderTop(style.getBorderTop());
			}
			if (style.getBorderRight() != null) {
				cellStyle.setBorderRight(style.getBorderRight());
			}
			if (style.getBorderLeft() != null) {
				cellStyle.setBorderLeft(style.getBorderLeft());
			}

			cellStyle.setWrapText(style.isWrapText());
		}

		cellStyleMap.put(style, cellStyle);

		return cellStyle;
	}

	@SuppressWarnings("javadoc")
	public ExcelUtil(final Workbook workbook, final Map<String, Object> model, final HttpServletRequest request,
			final HttpServletResponse response) {
		this.workbook = workbook;
		createHelper = workbook.getCreationHelper();
		this.model = model;
		this.request = request;
		this.response = response;
	}

	/**
	 * 맵에 주어진 정보를 이용하여 엑셀 작성
	 */
	public void createExcel() {
		setFileName(request, response, mapToFileName());

		for (final ExcelSheet excelSheet : getExcelData()) {
			//시트
			final Sheet sheet = isEmpty(excelSheet.getName()) ? workbook.createSheet()
					: workbook.createSheet(excelSheet.getName());

			int colSize = 0;
			for (final Entry<Integer, ExcelRow> entry : excelSheet.getRowMap().entrySet()) {
				//행
				createRow(sheet, entry.getValue(), entry.getKey());

				colSize = Math.max(colSize, entry.getValue().getCellMap().size());
			}
		}
	}

	/**
	 * 0부터 시작하는 인덱스 좌표값을 A1형식 주소로 변환 (예: (3,2) -> "C4")
	 *
	 * @param row
	 * @param col
	 *
	 * @return
	 */
	public static String addrCellToA1(final int row, final int col) {
		return new CellReference(row, col).formatAsString();
	}

	/**
	 * 0부터 시작하는 범위 좌표값을 A1형식 주소로 변환 (예: (3,2,4,3) -> "C4:D5")
	 *
	 * @param fromRow 범위 첫 행index
	 * @param fromCol 범위 첫 열index
	 * @param toRow 범위 마지막 행index
	 * @param toCol 범위 마지막 열index
	 *
	 * @return
	 */
	public static String addrRangeToA1(final int fromRow, final int fromCol, final int toRow, final int toCol) {
		return addrCellToA1(fromRow, fromCol) + ":" + addrCellToA1(toRow, toCol);
	}

	private String mapToFileName() {
		return (String) model.get(FILE_NAME);
	}

	@SuppressWarnings("unchecked")
	private List<ExcelSheet> getExcelData() {
		return (List<ExcelSheet>) model.get(DATA);
	}

	/**
	 * 엑셀 뷰가 참조 가능하도록 시트를 모델에 저장
	 *
	 * @param model 엑셀정보가 입력된 모델
	 * @param filename 확장자를 제외한 파일명
	 * @param data
	 */
	public static void setExcelData(final Model model, final String filename, final ExcelSheet data) {
		model.addAttribute(FILE_NAME, filename);
		model.addAttribute(DATA, data != null ? Arrays.asList(data) : new ArrayList<ExcelSheet>());
	}

	/**
	 * 엑셀 뷰가 참조 가능하도록 시트를 모델에 저장
	 *
	 * @param model 엑셀정보가 입력된 모델
	 * @param filename 확장자를 제외한 파일명
	 * @param data
	 */
	public static void setExcelData(final Model model, final String filename, final List<ExcelSheet> data) {
		model.addAttribute(FILE_NAME, filename);
		model.addAttribute(DATA, data != null ? data : new ArrayList<ExcelSheet>());
	}

	/**
	 * rowIndex를 key로 하는 Map형태의 엑셀정보를 모델에 저장
	 *
	 * @param model 엑셀정보가 입력된 모델
	 * @param filename 확장자를 제외한 파일명
	 * @param data
	 */
	public static void setExcelData(final Map<String, Object> model, final String filename, final ExcelSheet data) {
		model.put(FILE_NAME, filename);
		model.put(DATA, data != null ? Arrays.asList(data) : new ArrayList<ExcelSheet>());
	}

	/**
	 * rowIndex를 key로 하는 Map형태의 엑셀정보를 모델에 저장
	 *
	 * @param model 엑셀정보가 입력된 모델
	 * @param filename 확장자를 제외한 파일명
	 * @param data
	 */
	public static void setExcelData(final Map<String, Object> model, final String filename,
			final List<ExcelSheet> data) {
		model.put(FILE_NAME, filename);
		model.put(DATA, data != null ? data : new ArrayList<ExcelSheet>());
	}

	private void setFileName(final HttpServletRequest request, final HttpServletResponse response,
			final String fileName) {
		String fileNameWithExt = appendExtension(fileName);

		final String userAgent = request.getHeader("User-Agent");
		try {
			if (userAgent != null && (userAgent.contains("MSIE") || userAgent.contains("Trident"))) { //IE8~11
				fileNameWithExt = URLEncoder.encode(fileNameWithExt, "UTF-8").replaceAll("\\+", "%20");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileNameWithExt + ";");

			} else {
				fileNameWithExt = URLDecoder.decode(URLEncoder.encode(fileNameWithExt, "UTF-8"), "ISO8859_1");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameWithExt + "\"");
			}
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String appendExtension(final String fileName) {
		String fileNameWithExt = fileName;
		if (workbook instanceof XSSFWorkbook) {
			fileNameWithExt += ".xlsx";
		}
		if (workbook instanceof SXSSFWorkbook) {
			fileNameWithExt += ".xlsx";
		}
		if (workbook instanceof HSSFWorkbook) {
			fileNameWithExt += ".xls";
		}

		return fileNameWithExt;
	}

	private void createRow(final Sheet sheet, final ExcelRow excelRow, final int rowNum) {
		if (excelRow == null) {
			return;
		}

		final Row row = sheet.createRow(rowNum);
		row.setRowStyle(getCellStyle(excelRow.getStyle())); //행 스타일 설정

		final Set<Entry<Integer, ExcelCell>> entrySet = excelRow.getCellMap().entrySet();
		for (final Entry<Integer, ExcelCell> entry : entrySet) {

			final Cell cell = row.createCell(entry.getKey());

			final ExcelCell cellValue = entry.getValue();

			//값
			setCellValue(cell, cellValue);

			//형식
			setCellStyle(cell, cellValue);
		}
	}

	private void setCellStyle(final Cell cell, final ExcelCell cellValue) {
		if (cellValue.getStyle() != null) {
			//셀 형식이 있다면 적용
			cell.setCellStyle(getCellStyle(cellValue.getStyle()));
		}
	}

	private static void setCellValue(final Cell cell, final ExcelCell cellValue) {
		if (cellValue.isFunction()) {
			cell.setCellType(CellType.FORMULA);
			cell.setCellFormula((String) cellValue.getValue());

		} else {
			if (cellValue.getValue() != null) {
				if (cellValue.getStyle() != null && cellValue.getStyle().getFormat() != null
						&& cellValue.getStyle().getFormat().isNumeric()) {
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue((double) cellValue.getValue());

				} else {
					cell.setCellValue((String) cellValue.getValue());
				}
			}
		}
	}
}
