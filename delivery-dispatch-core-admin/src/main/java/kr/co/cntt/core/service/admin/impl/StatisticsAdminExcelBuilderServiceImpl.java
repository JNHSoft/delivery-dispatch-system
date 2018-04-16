package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.model.order.Order;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component("StatisticsAdminExcelBuilderServiceImpl")
public class StatisticsAdminExcelBuilderServiceImpl extends AbstractView {
    @Resource
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Value("${spring.mvc.locale}")
    private Locale locale;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Get now date & time
        // 파일 명 날짜가져온다.
//        SimpleDateFormat formatter = new SimpleDateFormat ( "yy-mm-dd", Locale.KOREA );
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", locale );
        Date currentTime = new Date ( );
        String dTime = formatter.format ( currentTime );

        String fileName = "[" + dTime + "]";
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 요청 하는 url 에 따라서 필요한 값을 넣어줌
        if(request.getRequestURI().matches("/excelDownload")) {

            // 필요한 리스트 controller 에서 던져주는 List 					 		Nick
            List<Order> orderStatisticsByAdminList = (List<Order>) model.get("selectAdminStatisticsExcel");


            setOrderStatisticsByAdminExcel(workbook, orderStatisticsByAdminList);

            // 파일 이름은 이렇게 한다. 											Nick
            fileName += " Order_Report.xlsx";

        }

        String encordedFilename = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encordedFilename + ";filename*= UTF-8''" + encordedFilename);
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        OutputStream fs = response.getOutputStream();
        workbook.write(fs);
        if(fs != null) fs.close();

    }
    // 내용 셋팅 하는 부분
    public void setOrderStatisticsByAdminExcel(XSSFWorkbook wb, List<Order> orderStatisticsByAdminList) {
        int rowNum = 0;
        int colNum = 0;
        XSSFSheet sheet = wb.createSheet("OrderStatisticsByAdmin");

        // Title Area Cell Style
        XSSFCellStyle titleCellStyle = settTitleCell(wb);
        Font titleCellFont = setTitleCellFont(wb);
        titleCellStyle.setFont(titleCellFont);

        // Data Area Cell Style
        XSSFCellStyle dataCellStyle = setDataCell(wb);
        Font dataCellFont = setDataCellFont(wb);
        dataCellStyle.setFont(dataCellFont);

        {
            // 제목 부분
            XSSFRow titleRow = sheet.createRow(rowNum);

            sheet.setColumnWidth(colNum, 15*256);
            XSSFCell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.reg.order.id",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 20*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("group.name",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 20*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("subgroup.name",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 25*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("store.name",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 10*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.status",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.created",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.reserved",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.assigned",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.pickedup",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.completed",null, locale));
            addTitle.setCellStyle(titleCellStyle);

           /* sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("MenuName");
            addTitle.setCellStyle(titleCellStyle);*/

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.cooking",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.payment",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.delivery.price",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.menu.price",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.total.price",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.combined",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.name",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.phone",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.message",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.customer.phone",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 60*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.customer.address",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.customer.address.detail",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.distance",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            rowNum++;
        }
        // 내용 부분
        for(int i = 0, r = orderStatisticsByAdminList.size(); i<r; i++) {

            colNum = 0;
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@!@#!@#!@#!@#!@#"+orderStatisticsByAdminList);
            XSSFRow addListRow = sheet.createRow(rowNum);

            XSSFCell cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getRegOrderId());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getGroup().getName());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getSubGroup().getName());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getStore().getStoreName());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if(orderStatisticsByAdminList.get(i).getStatus().equals("3")){
                cell.setCellValue(messageSource.getMessage("status.completed",null, locale));
            }else if(orderStatisticsByAdminList.get(i).getStatus().equals("4")){
                cell.setCellValue(messageSource.getMessage("status.canceled",null, locale));
            }
            cell.setCellStyle(dataCellStyle);

//            cell = addListRow.createCell(colNum++);
//            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getId());
//            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getCreatedDatetime());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getReservationDatetime());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getAssignedDatetime());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getPickedUpDatetime());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getCompletedDatetime());
            cell.setCellStyle(dataCellStyle);

            /*cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getMenuName());
            cell.setCellStyle(dataCellStyle);*/

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getCookingTime());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if(orderStatisticsByAdminList.get(i).getPaid().equals("0")){
                cell.setCellValue(messageSource.getMessage("order.payment.cash",null, locale));
            }else if(orderStatisticsByAdminList.get(i).getPaid().equals("1")){
                cell.setCellValue(messageSource.getMessage("order.payment.card",null, locale));
            }else if(orderStatisticsByAdminList.get(i).getPaid().equals("2")){
                cell.setCellValue(messageSource.getMessage("order.payment.prepayment",null, locale));
            }else if(orderStatisticsByAdminList.get(i).getPaid().equals("3")){
                cell.setCellValue(messageSource.getMessage("order.payment.service",null, locale));
            }
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getDeliveryPrice());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getMenuPrice());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getTotalPrice());
            cell.setCellStyle(dataCellStyle);

//            cell = addListRow.createCell(colNum++);
//            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getPayment().getType());
//            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getCombinedOrderId());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getRider().getName());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getRider().getPhone());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getMessage());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getPhone());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getAddress());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getDetailAddress());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue((String) orderStatisticsByAdminList.get(i).getDistance());
            cell.setCellStyle(dataCellStyle);

            rowNum ++;
        }
    }

    public Font setTotalCellFont(XSSFWorkbook wb) {
        Font dataCellFont = wb.createFont();
        dataCellFont.setFontHeightInPoints((short) 10);
        dataCellFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        dataCellFont.setFontName("맑은 고딕");

        return dataCellFont;
    }

    public XSSFCellStyle setTotalCell(XSSFWorkbook wb){
        // 합계 셀 스타일
        XSSFCellStyle TotalRowStyle = wb.createCellStyle();
        TotalRowStyle.setAlignment(CellStyle.ALIGN_CENTER);
        TotalRowStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        TotalRowStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        TotalRowStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        TotalRowStyle.setBorderBottom(CellStyle.BORDER_THIN);
        TotalRowStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        TotalRowStyle.setBorderLeft(CellStyle.BORDER_THIN);
        TotalRowStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        TotalRowStyle.setBorderRight(CellStyle.BORDER_THIN);
        TotalRowStyle.setRightBorderColor(IndexedColors.BLACK.index);
        TotalRowStyle.setBorderTop(CellStyle.BORDER_THIN);
        TotalRowStyle.setTopBorderColor(IndexedColors.BLACK.index);
        TotalRowStyle.setWrapText(true);
        Font TotalRowFont = wb.createFont();
        TotalRowFont.setFontHeightInPoints((short) 10);
        TotalRowFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        TotalRowFont.setFontName("맑은 고딕");
        TotalRowStyle.setFont(TotalRowFont);

        return TotalRowStyle;
    }


    public Font setDataCellFont(XSSFWorkbook wb) {
        Font dataCellFont = wb.createFont();
        dataCellFont.setFontHeightInPoints((short) 10);
        dataCellFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        dataCellFont.setFontName("맑은 고딕");

        return dataCellFont;
    }

    public XSSFCellStyle setDataCell(XSSFWorkbook wb) {

        XSSFCellStyle dataCellStyle = wb.createCellStyle();
        dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        dataCellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
        dataCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        dataCellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        dataCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        dataCellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        dataCellStyle.setBorderRight(CellStyle.BORDER_THIN);
        dataCellStyle.setRightBorderColor(IndexedColors.BLACK.index);
        dataCellStyle.setBorderTop(CellStyle.BORDER_THIN);
        dataCellStyle.setTopBorderColor(IndexedColors.BLACK.index);

        return dataCellStyle;
    }

    public XSSFCellStyle settTitleCell(XSSFWorkbook wb) {

        XSSFCellStyle titleCellStyle = wb.createCellStyle();
        titleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        titleCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        titleCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        titleCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        titleCellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        titleCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        titleCellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        titleCellStyle.setBorderRight(CellStyle.BORDER_THIN);
        titleCellStyle.setRightBorderColor(IndexedColors.BLACK.index);
        titleCellStyle.setBorderTop(CellStyle.BORDER_THIN);
        titleCellStyle.setTopBorderColor(IndexedColors.BLACK.index);

        return titleCellStyle;
    }

    public Font setTitleCellFont(XSSFWorkbook wb) {

        Font titleCellFont = wb.createFont();
        titleCellFont.setFontHeightInPoints((short) 10);
        titleCellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleCellFont.setFontName("맑은 고딕");

        return titleCellFont;
    }
}
