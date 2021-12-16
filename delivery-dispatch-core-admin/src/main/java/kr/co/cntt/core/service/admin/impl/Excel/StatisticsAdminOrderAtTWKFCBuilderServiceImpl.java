package kr.co.cntt.core.service.admin.impl.Excel;

import kr.co.cntt.core.model.order.Order;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("StatisticsAdminOrderAtTWKFCBuilderServiceImpl")
public class StatisticsAdminOrderAtTWKFCBuilderServiceImpl extends ExcelComm {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", locale);
        Date currentTime = new Date();
        String dTime = formatter.format(currentTime);

        String fileName = "[" + dTime + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        // 요청 하는 url 에 따라서 필요한 값을 넣어줌
        if (request.getRequestURI().matches("/excelDownloadByOrderAtTWKFC")) {

            // 필요한 리스트 controller 에서 던져주는 List 					 		Nick
            List<Order> orderStatisticsByAdminList = (List<Order>) model.get("selectStoreStatisticsByOrderForAdminAtTWKFC");
            setOrderStatisticsByOrderExcel(workbook, orderStatisticsByAdminList, (int)model.get("groupNumber"));
            // 파일 이름은 이렇게 한다. 											Nick
            fileName += " Order_Time_Analysis_Report.xlsx";

        }

        String encordedFilename = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encordedFilename + ";filename*= UTF-8''" + encordedFilename);
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        OutputStream fs = response.getOutputStream();
        workbook.write(fs);
        if (fs != null) fs.close();
        workbook.dispose();
    }

    public void setOrderStatisticsByOrderExcel(SXSSFWorkbook wb, List<Order> orderList, int groupNumber) {
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

            sheet.setColumnWidth(colNum, 15 * 256);
            Cell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.number", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("store.name", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.date", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 배정시간
            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.assign", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // QT
            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.QT", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.in.store.time", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.delivery.time", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.completed.time", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.return.time", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.out.time", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.total.time", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 15 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.1st.label.order.distance", null, locale));
            addTitle.setCellStyle(titleCellStyle);

            rowNum++;
        }
        // 내용 부분
        long orderPickupTime = 0L;
        long pickupCompleteTime = 0L;
        long orderCompleteTime = 0L;
        long completeReturnTime = 0L;
        long pickupReturnTime = 0L;
        long orderReturnTime = 0L;

        long qtTotalTime = 0L;

        double totalDistance = 0d;

        int returnNullCnt = 0;
        int distanceNullCnt = 0;

        for (int i = 0, r = orderList.size(); i < r; i++) {

            LocalDateTime pickupTime = LocalDateTime.MIN;
            if (orderList.get(i).getPickedUpDatetime() != null){
                pickupTime = LocalDateTime.parse((orderList.get(i).getPickedUpDatetime()).replace(" ", "T"));
            }

            // Assign Time
            LocalDateTime assignTime = LocalDateTime.MIN;
            if (orderList.get(i).getAssignedDatetime() != null){
                assignTime = LocalDateTime.parse((orderList.get(i).getAssignedDatetime()).replace(" ", "T"));
            }

            LocalDateTime returnTime = LocalDateTime.MIN;
            if (orderList.get(i).getReturnDatetime() != null) {
                returnTime = LocalDateTime.parse((orderList.get(i).getReturnDatetime()).replace(" ", "T"));
            } else {
                returnNullCnt++;
            }

            LocalDateTime arrivedTime = LocalDateTime.MIN;
            if (orderList.get(i).getArrivedDatetime()!=null){
                arrivedTime = LocalDateTime.parse((orderList.get(i).getArrivedDatetime()).replace(" ", "T"));;
            }


            long orderPickup = pickupTime != LocalDateTime.MIN ? assignTime.until(pickupTime, ChronoUnit.MILLIS) : 0l;
            long pickupComplete = arrivedTime != LocalDateTime.MIN ? pickupTime.until(arrivedTime, ChronoUnit.MILLIS) : 0l;
            long orderComplete = arrivedTime != LocalDateTime.MIN ? assignTime.until(arrivedTime, ChronoUnit.MILLIS) : 0l;
            long completeReturn = returnTime != LocalDateTime.MIN && arrivedTime != LocalDateTime.MIN ? arrivedTime.until(returnTime, ChronoUnit.MILLIS) : 0l;
            long pickupReturn = returnTime != LocalDateTime.MIN ? pickupTime.until(returnTime, ChronoUnit.MILLIS) : 0l;
            long orderReturn = returnTime != LocalDateTime.MIN ? assignTime.until(returnTime, ChronoUnit.MILLIS) : 0l;

            orderPickupTime += orderPickup;
            pickupCompleteTime += pickupComplete;
            orderCompleteTime += orderComplete;
            completeReturnTime += completeReturn;
            pickupReturnTime += pickupReturn;
            orderReturnTime += orderReturn;

            qtTotalTime += Long.parseLong(orderList.get(i).getCookingTime());

            if (orderList.get(i).getDistance() != null) {
                totalDistance += Double.parseDouble(orderList.get(i).getDistance());
            } else {
                distanceNullCnt++;
            }

            colNum = 0;
            Row addListRow = sheet.createRow(rowNum);

            Cell cell = addListRow.createCell(colNum++);
            cell.setCellValue(orderList.get(i).getRegOrderId());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(orderList.get(i).getStore().getStoreName());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(orderList.get(i).getCreatedDatetime());
            cell.setCellStyle(dataCellStyle);

            // 배정시간
            cell = addListRow.createCell(colNum++);
            if (orderList.get(i).getAssignedDatetime() != null) {
                cell.setCellValue(orderList.get(i).getAssignedDatetime());
            } else {
                cell.setCellValue("-");
            }

            cell.setCellStyle(dataCellStyle);

            // QT
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(orderList.get(i).getCookingTime());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(orderPickup));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(pickupComplete));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(orderComplete));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(completeReturn));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(pickupReturn));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(orderReturn));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            //cell.setCellValue(String.format("%.2f", Float.parseFloat(nullCheck(orderList.get(i).getDistance()))));
            cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, orderList.get(i).getDistance()))) + " km");
            cell.setCellStyle(dataCellStyle);

            rowNum++;

            if (i == orderList.size() - 1) {
                colNum = 0;
                addListRow = sheet.createRow(rowNum++);

                Cell cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("TOTAL");
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("");
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("");
                cell2.setCellStyle(dataCellStyle);

                // 배정시간
                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("");
                cell2.setCellStyle(dataCellStyle);

                // QT
                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(qtTotalTime);
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(minusChkFilter(orderPickupTime));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(minusChkFilter(pickupCompleteTime));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(minusChkFilter(orderCompleteTime));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(minusChkFilter(completeReturnTime));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(minusChkFilter(pickupReturnTime));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(minusChkFilter(orderReturnTime));
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(totalDistance);
                cell2.setCellStyle(dataCellStyle);

                int totalCnt = orderList.size();

                colNum = 0;
                addListRow = sheet.createRow(rowNum++);
                Cell cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue("AVERAGE");
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue("");
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue("");
                cell3.setCellStyle(dataCellStyle);

                // 배정시간
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue("");
                cell3.setCellStyle(dataCellStyle);

                // QT
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue( String.format("%.2f", Float.parseFloat(String.valueOf(qtTotalTime)) / Float.parseFloat(String.valueOf(totalCnt))));
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(orderPickupTime / totalCnt));
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(pickupCompleteTime / totalCnt));
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(orderCompleteTime / totalCnt));
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(completeReturnTime / (totalCnt - returnNullCnt)));
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(pickupReturnTime / (totalCnt - returnNullCnt)));
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(orderReturnTime / (totalCnt - returnNullCnt)));
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(String.format("%.2f", (totalDistance / (totalCnt - distanceNullCnt))) + "km");
                cell3.setCellStyle(dataCellStyle);
            }
        }

        // RC 또는 AC 평균을 추가한다.
        Map<String, List<Order>> groupList = null;

        if (groupNumber == 1){
            groupList = orderList.stream().collect(Collectors.groupingBy(x -> x.getGroup().getName()));
        }else if (groupNumber == 2){
            groupList = orderList.stream().collect(Collectors.groupingBy(x -> x.getSubGroup().getName().split("-")[0]));
        }

        if (groupList != null && !groupList.isEmpty()){
            for (Map.Entry<String, List<Order>> group:groupList.entrySet()) {

                colNum = 0;
                Row addListRow = sheet.createRow(rowNum++);
                Cell cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(group.getKey() + " AVERAGE");
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue("");
                cell3.setCellStyle(dataCellStyle);

                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue("");
                cell3.setCellStyle(dataCellStyle);

                // 배정시간
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue("");
                cell3.setCellStyle(dataCellStyle);

                // QT
                double dQtTotalTime = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getCookingTime()))).sum();
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(String.format("%.2f", (dQtTotalTime / group.getValue().size())));
                //cell3.setCellValue(qtTotalTime / totalCnt);
                cell3.setCellStyle(dataCellStyle);

                double dOrderPickupTime = dOrderPickupTime = group.getValue().stream().mapToDouble(x -> {

                    if (x.getAssignedDatetime() != null && x.getPickedUpDatetime() != null) {
                        return LocalDateTime.parse((x.getAssignedDatetime().replace(" ", "T"))).until(LocalDateTime.parse(x.getPickedUpDatetime().replace(" ", "T")), ChronoUnit.MILLIS);
                    }else{
                        return 0d;
                    }
                }).sum();
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(dOrderPickupTime / group.getValue().size())))));
                //cell3.setCellValue(minusChkFilter(orderPickupTime / totalCnt));
                cell3.setCellStyle(dataCellStyle);

                double dPickupCompleteTime = group.getValue().stream().mapToDouble(x -> {
                    if (x.getPickedUpDatetime() != null && x.getArrivedDatetime() != null){
                        return LocalDateTime.parse((x.getPickedUpDatetime().replace(" ", "T"))).until(LocalDateTime.parse(x.getArrivedDatetime().replace(" ", "T")), ChronoUnit.MILLIS);
                    } else {
                        return 0d;
                    }
                }).sum();
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(dPickupCompleteTime / group.getValue().size())))));
                //cell3.setCellValue(minusChkFilter(pickupCompleteTime / totalCnt));
                cell3.setCellStyle(dataCellStyle);

                double dOrderCompleteTime = group.getValue().stream().mapToDouble(x -> {
                    if (x.getAssignedDatetime() != null && x.getArrivedDatetime() != null){
                        return LocalDateTime.parse((x.getAssignedDatetime().replace(" ", "T"))).until(LocalDateTime.parse(x.getArrivedDatetime().replace(" ", "T")), ChronoUnit.MILLIS);
                    }else {
                        return 0d;
                    }
                }).sum();
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(dOrderCompleteTime / group.getValue().size())))));
                //cell3.setCellValue(minusChkFilter(orderCompleteTime / totalCnt));
                cell3.setCellStyle(dataCellStyle);

                double dCompleteReturnTime = group.getValue().stream().mapToDouble(x -> {
                    if (x.getArrivedDatetime() != null && x.getReturnDatetime() != null){
                        return LocalDateTime.parse((x.getArrivedDatetime().replace(" ", "T"))).until(LocalDateTime.parse(x.getReturnDatetime().replace(" ", "T")), ChronoUnit.MILLIS);
                    } else {
                        return 0d;
                    }
                }).sum();
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(dCompleteReturnTime / group.getValue().size())))));
                //cell3.setCellValue(minusChkFilter(completeReturnTime / (totalCnt - returnNullCnt)));
                cell3.setCellStyle(dataCellStyle);

                double dPickupReturnTime = group.getValue().stream().mapToDouble(x -> {
                    if (x.getPickedUpDatetime() != null && x.getReturnDatetime() != null){
                        return LocalDateTime.parse((x.getPickedUpDatetime().replace(" ", "T"))).until(LocalDateTime.parse(x.getReturnDatetime().replace(" ", "T")), ChronoUnit.MILLIS);
                    } else {
                        return 0d;
                    }
                }).sum();
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(dPickupReturnTime / group.getValue().size())))));
                //cell3.setCellValue(minusChkFilter(pickupReturnTime / (totalCnt - returnNullCnt)));
                cell3.setCellStyle(dataCellStyle);

                double dOrderReturnTime = group.getValue().stream().mapToDouble(x -> {
                    if (x.getAssignedDatetime() != null && x.getReturnDatetime() != null){
                        return LocalDateTime.parse((x.getAssignedDatetime().replace(" ", "T"))).until(LocalDateTime.parse(x.getReturnDatetime().replace(" ", "T")), ChronoUnit.MILLIS);
                    }else {
                        return 0d;
                    }
                }).sum();
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(minusChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(dOrderReturnTime / group.getValue().size())))));
                //cell3.setCellValue(minusChkFilter(orderReturnTime / (totalCnt - returnNullCnt)));
                cell3.setCellStyle(dataCellStyle);

                double dTotalDistance = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getDistance()))).sum();
                cell3 = addListRow.createCell(colNum++);
                cell3.setCellValue(String.format("%.2f", (dTotalDistance / group.getValue().size())) + "km");
                //cell3.setCellValue(String.format("%.2f", (totalDistance / (totalCnt - distanceNullCnt))));
                cell3.setCellStyle(dataCellStyle);
            }
        }

    }
}
