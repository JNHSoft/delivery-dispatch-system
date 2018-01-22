package kr.co.cntt.core.excel;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

@Component
public class ExcelXlsxView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(final Map<String, Object> model, final Workbook workbook, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		new ExcelUtil(workbook, model, request, response).createExcel();
	}
}
