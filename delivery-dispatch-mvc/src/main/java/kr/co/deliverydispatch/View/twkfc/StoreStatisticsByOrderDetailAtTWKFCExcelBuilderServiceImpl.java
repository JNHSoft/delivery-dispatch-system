package kr.co.deliverydispatch.View.twkfc;

import kr.co.cntt.core.model.order.Order;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("StoreStatisticsByOrderDetailAtTWKFCExcelBuilderServiceImpl")
public class StoreStatisticsByOrderDetailAtTWKFCExcelBuilderServiceImpl extends CommExcel {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception{
        locale = LocaleContextHolder.getLocale();
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", locale );
        Date currentTime = new Date ( );
        String dTime = formatter.format ( currentTime );
        String fileName = "[" + dTime + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        if(request.getRequestURI().matches("/excelDownloadByOrderListAtTWKFC")) {
            List<Order> orderStatisticsByStoreList = (List<Order>) model.get("getStoreStatisticsByOrderListAtTWKFCExcel");
            setStoreStatisticsByOrderListExcel(workbook, orderStatisticsByStoreList);
            fileName += " OrderList_Report.xlsx";
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

    // ?????? ?????? ?????? ??????
    public void setStoreStatisticsByOrderListExcel(SXSSFWorkbook wb, List<Order> storeStatisticsByOrderList) {
        int rowNum = 0;
        int colNum = 0;
        storeStatisticsByOrderList.stream().map(a->{
            if(a.getReservationStatus().equals("1")){
                LocalDateTime reserveToCreated = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                a.setCreatedDatetime(reserveToCreated.minusMinutes(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")));
            }
            return true;
        }).collect(Collectors.toList());
        Sheet sheet = wb.createSheet("StoreStatisticsByOrderList");

        // Title Area Cell Style
        CellStyle titleCellStyle = settTitleCell(wb);
        Font titleCellFont = setTitleCellFont(wb);
        titleCellStyle.setFont(titleCellFont);

        // Data Area Cell Style
        CellStyle dataCellStyle = setDataCell(wb);
        Font dataCellFont = setDataCellFont(wb);
        dataCellStyle.setFont(dataCellFont);

        {
            // ?????? ??????
            Row titleRow = sheet.createRow(rowNum);

            // ?????? ID
            sheet.setColumnWidth(colNum, 15*256);
            Cell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.reg.order.id",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // ?????? ??????
            sheet.setColumnWidth(colNum, 10*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.status",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // ?????? ID
//            sheet.setColumnWidth(colNum, 17*256);
//            addTitle = titleRow.createCell(colNum++);
//            addTitle.setCellValue(messageSource.getMessage("order.id",null, locale));
//            addTitle.setCellStyle(titleCellStyle);

            // ?????? ?????? ??????
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.created",null, locale));
            addTitle.setCellStyle(titleCellStyle);

//            // ?????? ??????
//            sheet.setColumnWidth(colNum, 17*256);
//            addTitle = titleRow.createCell(colNum++);
//            addTitle.setCellValue(messageSource.getMessage("order.address",null, locale));
//            addTitle.setCellStyle(titleCellStyle);
//
//            // ?????????
//            sheet.setColumnWidth(colNum, 17*256);
//            addTitle = titleRow.createCell(colNum++);
//            addTitle.setCellValue(messageSource.getMessage("order.message",null, locale));
//            addTitle.setCellStyle(titleCellStyle);
//
//            // ?????? ????????????
//            sheet.setColumnWidth(colNum, 17*256);
//            addTitle = titleRow.createCell(colNum++);
//            addTitle.setCellValue(messageSource.getMessage("order.customer.phone",null, locale));
//            addTitle.setCellStyle(titleCellStyle);

            // ????????????
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.assigned",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // ?????? ??????
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.pickedup",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // ?????? ??????
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.arrived",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // ?????? ??????
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.completed",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // ?????? ??????
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("order.return",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            rowNum++;
        }

        // ?????? ??????
        for(int i = 0, r = storeStatisticsByOrderList.size(); i<r; i++) {
            colNum = 0;
            Row addListRow = sheet.createRow(rowNum);

            // Reg ?????? ID
            Cell cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getRegOrderId()));
            cell.setCellStyle(dataCellStyle);

            // ?????? ??????
            cell = addListRow.createCell(colNum++);
            switch (storeStatisticsByOrderList.get(i).getStatus()){
                case "3":
                    cell.setCellValue(messageSource.getMessage("status.completed",null, locale));
                    break;
                case "4":
                    cell.setCellValue(messageSource.getMessage("status.canceled",null, locale));
                    break;
                default:
                    cell.setCellValue("");
            }
            cell.setCellStyle(dataCellStyle);

            // ?????? ID
//            cell = addListRow.createCell(colNum++);
//            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getId()));
//            cell.setCellStyle(dataCellStyle);

            // ?????? ??????
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getCreatedDatetime()));
            cell.setCellStyle(dataCellStyle);

//            // ?????? ??????
//            cell = addListRow.createCell(colNum++);
//            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getAddress()));
//            cell.setCellStyle(dataCellStyle);
//
//            // ?????? ??????
//            cell = addListRow.createCell(colNum++);
//            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getMessage()));
//            cell.setCellStyle(dataCellStyle);
//
//            // ?????? ????????????
//            cell = addListRow.createCell(colNum++);
//            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getPhone()));
//            cell.setCellStyle(dataCellStyle);

            // ?????? ??????
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getAssignedDatetime()));
            cell.setCellStyle(dataCellStyle);

            // ?????? ??????
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getPickedUpDatetime()));
            cell.setCellStyle(dataCellStyle);

            // ?????? ??????
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getArrivedDatetime()));
            cell.setCellStyle(dataCellStyle);

            // ?????? ??????
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getCompletedDatetime()));
            cell.setCellStyle(dataCellStyle);

            // ?????? ??????
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(String.class, storeStatisticsByOrderList.get(i).getReturnDatetime()));
            cell.setCellStyle(dataCellStyle);

            rowNum ++;
       }
    }
}
