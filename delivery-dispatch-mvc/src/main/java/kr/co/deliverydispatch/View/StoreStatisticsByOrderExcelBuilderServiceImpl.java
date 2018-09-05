package kr.co.deliverydispatch.View;

import kr.co.cntt.core.model.order.Order;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component("StoreStatisticsByOrderExcelBuilderServiceImpl")
public class StoreStatisticsByOrderExcelBuilderServiceImpl extends AbstractView {
    @Resource
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String nullCheck(String str){
        return str!=null?str:"";
    }

    @Value("${spring.mvc.locale}")
    private Locale locale;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", locale );
        Date currentTime = new Date ( );
        String dTime = formatter.format ( currentTime );
        String fileName = "[" + dTime + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        if(request.getRequestURI().matches("/excelDownloadByOrder")) {
            List<Order> orderStatisticsByStoreList = (List<Order>) model.get("getStoreStatisticsByOrderExcel");
            setStoreStatisticsByOrderExcel(workbook, orderStatisticsByStoreList);
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
        workbook.dispose();
    }
    // 내용 셋팅 하는 부분
    public void setStoreStatisticsByOrderExcel(SXSSFWorkbook wb, List<Order> storeStatisticsByOrderList) {
        int rowNum = 0;
        int colNum = 0;
        Sheet sheet = wb.createSheet("StoreStatisticsByOrder");

        // Title Area Cell Style
        CellStyle titleCellStyle = settTitleCell(wb);
        Font titleCellFont = setTitleCellFont(wb);
        titleCellStyle.setFont(titleCellFont);

        // Data Area Cell Style
        CellStyle dataCellStyle = setDataCell(wb);
        Font dataCellFont = setDataCellFont(wb);
        dataCellStyle.setFont(dataCellFont);

        {
            // 제목 부분
            Row titleRow = sheet.createRow(rowNum);

            sheet.setColumnWidth(colNum, 15*256);
            Cell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("Order 號碼");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("訂單日期");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("留店時間");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("外送時間");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("外送達成時間");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("回店所需時間");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("外出時間");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("完成整張外送時間");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("距離");
            addTitle.setCellStyle(titleCellStyle);

            rowNum++;
        }
        // 내용 부분
        Long orderPickupTime = 0l;
        Long pickupCompleteTime = 0l;
        Long orderCompleteTime = 0l;
        Long completeReturnTime = 0l;
        Long pickupReturnTime = 0l;
        Long orderReturnTime = 0l;

        Double totalDistance = 0d;

        for(int i = 0, r = storeStatisticsByOrderList.size(); i<r; i++) {
            LocalDateTime orderTime = LocalDateTime.parse((storeStatisticsByOrderList.get(i).getCreatedDatetime()).replace(" ", "T"));
            LocalDateTime pickupTime = LocalDateTime.parse((storeStatisticsByOrderList.get(i).getPickedUpDatetime()).replace(" ", "T"));
            LocalDateTime completeTime = LocalDateTime.parse((storeStatisticsByOrderList.get(i).getCompletedDatetime()).replace(" ", "T"));
            LocalDateTime returnTime = LocalDateTime.parse((storeStatisticsByOrderList.get(i).getReturnDatetime()).replace(" ", "T"));

            Long orderPickup = orderTime.until(pickupTime, ChronoUnit.MILLIS);
            Long pickupComplete = pickupTime.until(completeTime, ChronoUnit.MILLIS);
            Long orderComplete = orderTime.until(completeTime, ChronoUnit.MILLIS);
            Long completeReturn =completeTime.until(returnTime, ChronoUnit.MILLIS);
            Long pickupReturn =pickupTime.until(returnTime, ChronoUnit.MILLIS);
            Long orderReturn = orderTime.until(returnTime, ChronoUnit.MILLIS);

            orderPickupTime += orderPickup;
            System.out.println("orderPickup : " + orderPickup+"orderPickupTime : "+orderPickupTime);
            pickupCompleteTime += pickupComplete;
            orderCompleteTime += orderComplete;
            completeReturnTime += completeReturn;
            pickupReturnTime += pickupReturn;
            orderReturnTime += orderReturn;

            totalDistance += Double.parseDouble(storeStatisticsByOrderList.get(i).getDistance());

            colNum = 0;
            Row addListRow = sheet.createRow(rowNum);

            Cell cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByOrderList.get(i).getRegOrderId());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByOrderList.get(i).getCreatedDatetime());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(DurationFormatUtils.formatDuration(orderPickup,"HH:mm:ss"));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(DurationFormatUtils.formatDuration(pickupComplete,"HH:mm:ss"));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(DurationFormatUtils.formatDuration(orderComplete,"HH:mm:ss"));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(DurationFormatUtils.formatDuration(completeReturn,"HH:mm:ss"));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(DurationFormatUtils.formatDuration(pickupReturn,"HH:mm:ss"));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(DurationFormatUtils.formatDuration(orderReturn,"HH:mm:ss"));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(nullCheck(storeStatisticsByOrderList.get(i).getDistance()));
            cell.setCellStyle(dataCellStyle);

            rowNum ++;

            if(i==storeStatisticsByOrderList.size()-1){
                colNum = 0;
                addListRow = sheet.createRow(rowNum);

                Cell cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("TOTAL");
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("");
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(DurationFormatUtils.formatDuration(orderPickupTime,"HH:mm:ss"));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(DurationFormatUtils.formatDuration(pickupCompleteTime,"HH:mm:ss"));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(DurationFormatUtils.formatDuration(orderCompleteTime,"HH:mm:ss"));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(DurationFormatUtils.formatDuration(completeReturnTime,"HH:mm:ss"));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(DurationFormatUtils.formatDuration(pickupReturnTime,"HH:mm:ss"));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(DurationFormatUtils.formatDuration(orderReturnTime,"HH:mm:ss"));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(totalDistance);
                cell2.setCellStyle(dataCellStyle);

                rowNum ++;
            }
        }

    }





    public Font setTotalCellFont(SXSSFWorkbook wb) {
        Font dataCellFont = wb.createFont();
        dataCellFont.setFontHeightInPoints((short) 10);
        dataCellFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        dataCellFont.setFontName("맑은 고딕");

        return dataCellFont;
    }

    public CellStyle setTotalCell(SXSSFWorkbook wb){
        // 합계 셀 스타일
        CellStyle TotalRowStyle = wb.createCellStyle();
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


    public Font setDataCellFont(SXSSFWorkbook wb) {
        Font dataCellFont = wb.createFont();
        dataCellFont.setFontHeightInPoints((short) 10);
        dataCellFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        dataCellFont.setFontName("맑은 고딕");

        return dataCellFont;
    }

    public CellStyle setDataCell(SXSSFWorkbook wb) {

        CellStyle dataCellStyle = wb.createCellStyle();
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

    public CellStyle settTitleCell(SXSSFWorkbook wb) {

        CellStyle titleCellStyle = wb.createCellStyle();
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

    public Font setTitleCellFont(SXSSFWorkbook wb) {

        Font titleCellFont = wb.createFont();
        titleCellFont.setFontHeightInPoints((short) 10);
        titleCellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleCellFont.setFontName("맑은 고딕");

        return titleCellFont;
    }
}
