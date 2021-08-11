package kr.co.cntt.core.service.admin.impl.Excel;

import kr.co.cntt.core.model.statistic.AdminByDate;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component("StatisticsAdminByDateAtTWKFCBuilderServiceImpl")
public class StatisticsAdminByDateAtTWKFCBuilderServiceImpl extends ExcelComm {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", locale);
        Date currentTime = new Date();
        String dTime = formatter.format(currentTime);

        String fileName = "[" + dTime + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        // 요청 하는 url 에 따라서 필요한 값을 넣어줌
        if (request.getRequestURI().matches("/excelDownloadByDateAtTWKFC")) {

            // 필요한 리스트 controller 에서 던져주는 List 					 		Nick
            List<AdminByDate> dateStatisticsByAdminList = (List<AdminByDate>) model.get("selectStoreStatisticsByDateForAdminAtTWKFC");
            setStoreStatisticsByDateExcel(workbook, dateStatisticsByAdminList, (int)model.get("groupNumber"));
            // 파일 이름은 이렇게 한다. 											Nick
            fileName += " Date_Analysis_Report.xlsx";

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

    public void setStoreStatisticsByDateExcel(SXSSFWorkbook wb, List<AdminByDate> storeStatisticsByDateList, int groupNumber){
        int rowNum = 0;
        int colNum = 0;

        Sheet sheet = wb.createSheet("StoreStatisticsByDate");

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
            Row titleRow = sheet.createRow(rowNum++);

            sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
            sheet.addMergedRegion(new CellRangeAddress(0,0,1,6));

            sheet.addMergedRegion(new CellRangeAddress(0,0,7,locale.toString().equals("zh_TW")?8:13));
            sheet.addMergedRegion(new CellRangeAddress(0,0,locale.toString().equals("zh_TW")?9:14,locale.toString().equals("zh_TW")?14:21));


            sheet.setColumnWidth(colNum, 15*256);
            Cell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.store",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 6개
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.average.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.percent.completed",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellStyle(titleCellStyle);
                //
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellStyle(titleCellStyle);
                //
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellStyle(titleCellStyle);
                //
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellStyle(titleCellStyle);
                //
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellStyle(titleCellStyle);
            }

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.productivity",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellStyle(titleCellStyle);
                //
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellStyle(titleCellStyle);
            }

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);


            titleRow = sheet.createRow(rowNum++);
            colNum = 1;

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.in.store.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.delivery.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.completed.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.return.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.out.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.total.delivery.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("< D7 MINS %");
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("<=30 MINS %");
            addTitle.setCellStyle(titleCellStyle);

            if(!locale.toString().equals("zh_TW")){
                sheet.setColumnWidth(colNum, 17*256);
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellValue("<=40 MINS %");
                addTitle.setCellStyle(titleCellStyle);

                sheet.setColumnWidth(colNum, 17*256);
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellValue("<=50 MINS %");
                addTitle.setCellStyle(titleCellStyle);

                sheet.setColumnWidth(colNum, 17*256);
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellValue("<=60 MINS %");
                addTitle.setCellStyle(titleCellStyle);

                sheet.setColumnWidth(colNum, 17*256);
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellValue("<=90 MINS %");
                addTitle.setCellStyle(titleCellStyle);

                sheet.setColumnWidth(colNum, 17*256);
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellValue(">90 MINS %");
                addTitle.setCellStyle(titleCellStyle);

                sheet.setColumnWidth(colNum, 17*256);
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.sales",null, locale));
                addTitle.setCellStyle(titleCellStyle);

            }

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.errtc",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.thirdparty",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.tc",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.tplh",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                sheet.setColumnWidth(colNum, 17 * 256);
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.spmh", null, locale));
                addTitle.setCellStyle(titleCellStyle);
            }


            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.total.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.average.distance",null, locale));
            addTitle.setCellStyle(titleCellStyle);

        }


        long orderPickupTime = 0L;
        long pickupCompleteTime = 0L;
        long orderCompleteTime = 0L;
        long completeReturnTime = 0L;
        long pickupReturnTime = 0L;
        long orderReturnTime = 0L;

        float minD7Below = 0f;
        float min30Below = 0f;
        float min30To40 = 0f;
        float min40To50 = 0f;
        float min50To60 = 0f;
        float min60To90 = 0f;
        float min90Under = 0f;
        float totalSales = 0f;
        int errtc = 0;
        int thirdtc = 0;
        int tc = 0;
        float tplh = 0f;
        float spmh = 0f;
        long totalPickupReturnTime = 0L;
        float totalDistance = 0f;

        // 주문 개수
        int tcSum = storeStatisticsByDateList.stream().mapToInt(x -> Integer.parseInt(changeType(Integer.class, x.getTc()))).sum();
        // 정상 주문 개수
        long tcCount = storeStatisticsByDateList.stream().filter(x -> Integer.parseInt(changeType(Integer.class, x.getTc())) > 0).count();

        int onlyErrCnt = 0;
        int onlyThirdCnt = 0;

        for(int i = 0, r = storeStatisticsByDateList.size(); i<r; i++) {

            int currentTC = Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getTc()));

            int chkErrCnt = 0;
            int chkThirdCnt = 0;

            orderPickupTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderPickup()));
            pickupCompleteTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getPickupComplete()));
            orderCompleteTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderComplete()));

            // 빈값 가능
            if(storeStatisticsByDateList.get(i).getCompleteReturn() !=null){
                completeReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getCompleteReturn()));
            }

            if(storeStatisticsByDateList.get(i).getPickupReturn() !=null){
                pickupReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getPickupReturn()));
            }

            if(storeStatisticsByDateList.get(i).getOrderReturn() !=null){
                orderReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderReturn()));
            }

            minD7Below += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getD7Success()));
            min30Below += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin30Below()));
            min30To40 += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin30To40()));
            min40To50 += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin40To50()));
            min50To60 += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin50To60()));
            min60To90 += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin60To90()));
            min90Under += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin90Under()));

            totalSales += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getTotalSales()));

            if (storeStatisticsByDateList.get(i).getErrtc() != null){
                errtc += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getErrtc()));
                if (Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getErrtc())) > 0){
                    chkErrCnt++;
                }
            }

            if (storeStatisticsByDateList.get(i).getThirdtc() != null){
                thirdtc += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getThirdtc()));
                if (Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getThirdtc())) > 0){
                    chkThirdCnt++;
                }
            }

            tc += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getTc()));


            if(storeStatisticsByDateList.get(i).getHours() !=null){
                tplh += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getHours()));
                spmh += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getHours()));
            }

            if(storeStatisticsByDateList.get(i).getTotalPickupReturn() !=null){
                totalPickupReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getTotalPickupReturn()));
            }

            if(storeStatisticsByDateList.get(i).getAvgDistance() !=null){
                totalDistance += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getAvgDistance()));
            }

            colNum = 0;
            Row addListRow = sheet.createRow(rowNum);

            Cell cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByDateList.get(i).getStoreName());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getOrderPickup())) / currentTC * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getPickupComplete())) / currentTC * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getOrderComplete())) / currentTC * 1000));
            }else {
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getCompleteReturn())) / currentTC * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getPickupReturn())) / currentTC * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getOrderReturn())) / currentTC * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getD7Success())) / currentTC * 100) + "%");
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin30Below())) / currentTC * 100) + "%");
            }else {
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                cell = addListRow.createCell(colNum++);
                if (currentTC > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin30To40())) / currentTC * 100) + "%");
                }else {
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                if (currentTC > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin40To50())) / currentTC * 100) + "%");
                }else {
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                if (currentTC > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin50To60())) / currentTC * 100) + "%");
                }else {
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                if (currentTC > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin60To90())) / currentTC * 100) + "%");
                }else {
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                if (currentTC > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin90Under())) / currentTC * 100) + "%");
                }else {
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                if (currentTC > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getTotalSales())) / currentTC));
                }else {
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);
            }

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(Integer.class, storeStatisticsByDateList.get(i).getErrtc()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(Integer.class, storeStatisticsByDateList.get(i).getThirdtc()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(Integer.class, storeStatisticsByDateList.get(i).getTc()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0 && Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getHours())) > 0){
                cell.setCellValue(String.format("%.2f", currentTC / Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getHours()))));
            }else {
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                cell = addListRow.createCell(colNum++);
                if (Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getTotalSales())) > 0 && Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getHours())) > 0){
                    cell.setCellValue(String.format("%.2f", Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getTotalSales())) / Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getHours()))));
                }else {
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);
            }

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(storeStatisticsByDateList.get(i).getTotalPickupReturn()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (currentTC > 0){
                cell.setCellValue(String.format("%.1f",Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getAvgDistance())) / currentTC)+"km");
            }else {
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            rowNum ++;


            // 정상 개수가 없는 경우에만 err랑 제3자를 센다
            if (chkErrCnt > 0){
                onlyErrCnt++;
            }

            if (chkThirdCnt > 0){
                onlyThirdCnt++;
            }

            // 평균값
            if(i==storeStatisticsByDateList.size()-1){
                colNum = 0;
                addListRow = sheet.createRow(rowNum++);

                Cell cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("Average");
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderPickupTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(orderPickupTime/tcSum * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (pickupCompleteTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(pickupCompleteTime/tcSum * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderCompleteTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(orderCompleteTime/tcSum * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 빈값 가능
                cell2 = addListRow.createCell(colNum++);
                if (completeReturnTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(completeReturnTime/tcSum * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (pickupReturnTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(pickupReturnTime/tcSum * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderReturnTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(orderReturnTime/tcSum * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (minD7Below > 0 && tcSum > 0){
                    cell2.setCellValue(String.format("%.1f", (minD7Below/tcSum) * 100) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (min30Below > 0 && tcSum > 0){
                    cell2.setCellValue(String.format("%.1f", (min30Below/tcSum) * 100) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                if(!locale.toString().equals("zh_TW")) {
                    cell2 = addListRow.createCell(colNum++);
                    if (min30To40 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", (min30To40/tcSum) * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min40To50 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", (min40To50/tcSum) * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min50To60 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", (min50To60/tcSum) * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min60To90 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", (min60To90/tcSum) * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min90Under > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", (min90Under/tcSum) * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (totalSales > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", totalSales/tcSum));
                    }else{
                        cell2.setCellValue("0");
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                cell2 = addListRow.createCell(colNum++);
                if (errtc > 0 && onlyErrCnt > 0){
                    cell2.setCellValue(String.format("%.0f", Float.parseFloat(Integer.toString(errtc)) / onlyErrCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (thirdtc > 0 && onlyThirdCnt > 0){
                    cell2.setCellValue(String.format("%.0f", Float.parseFloat(Integer.toString(thirdtc)) /onlyThirdCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (tc > 0 && tcSum > 0){
                    cell2.setCellValue(String.format("%.0f", Float.parseFloat(Integer.toString(tc))/tcCount));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (Float.isNaN(tc/tplh) || Float.isInfinite(tc/tplh)){
                    cell2.setCellValue("0");
                }else{
                    cell2.setCellValue(String.format("%.2f", tc/tplh));
                }
                cell2.setCellStyle(dataCellStyle);

                if(!locale.toString().equals("zh_TW")) {
                    cell2 = addListRow.createCell(colNum++);
                    if (spmh > 0 && totalSales > 0){
                        if (Float.isNaN(totalSales / spmh) || Float.isInfinite(totalSales / spmh)){
                            cell2.setCellValue("0");
                        }else{
                            cell2.setCellValue(String.format("%.2f", totalSales / spmh));
                        }
                    }else{
                        cell2.setCellValue("0");
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                cell2 = addListRow.createCell(colNum++);
                if (totalPickupReturnTime > 0 && tcCount > 0){
                    cell2.setCellValue(avgChkFilter((totalPickupReturnTime*1000)/tcCount));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (totalDistance > 0 && tcSum > 0){
                    cell2.setCellValue(String.format("%.1f",totalDistance/tcSum)+"km");
                }else{
                    cell2.setCellValue("0km");
                }
                cell2.setCellStyle(dataCellStyle);

            }
        }

        // RC 또는 AC 간의 그룹핑 내용 적용
        Map<String, List<AdminByDate>> groupList = null;

        if (groupNumber == 1){
            // RC 그룹
            groupList = storeStatisticsByDateList.stream().collect(Collectors.groupingBy(AdminByDate::getGroupName));
        }else if (groupNumber == 2){
            // AC 그룹
            groupList = storeStatisticsByDateList.stream().sorted(Comparator.comparing(x -> x.getSubGroupName().split("-")[0])).collect(Collectors.groupingBy(x-> x.getSubGroupName().split("-")[0]));
        }

        if (groupList != null && !groupList.isEmpty()){
            groupList = groupList.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(e -> e.getKey(), e->e.getValue(), (e1, e2) -> e1, LinkedHashMap::new));

            for (Map.Entry<String, List<AdminByDate>> group: groupList.entrySet()) {
                long tOrderCount = group.getValue().stream().mapToInt(x -> Integer.parseInt(changeType(Integer.class, x.getTc()))).sum();
                long tErrCount = group.getValue().stream().mapToInt(x -> Integer.parseInt(changeType(Integer.class, x.getErrtc()))).sum();
                long tThirdCount = group.getValue().stream().mapToInt(x -> Integer.parseInt(changeType(Integer.class, x.getThirdtc()))).sum();

                colNum = 0;
                Row addListRow = sheet.createRow(rowNum++);

                Cell cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(group.getKey() + " Average");
                cell2.setCellStyle(dataCellStyle);

                double tPickup = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getOrderPickup()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tPickup > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tPickup/tOrderCount) + 0.5)))) * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tPickupComplete = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getPickupComplete()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tPickupComplete > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf((tPickupComplete/tOrderCount) + 0.5))) * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tOrderComplete = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getOrderComplete()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tOrderComplete > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf((tOrderComplete/tOrderCount) + 0.5))) * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 빈값 가능
                double tCompleteReturn = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getCompleteReturn()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tCompleteReturn > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf((tCompleteReturn/tOrderCount) + 0.5))) * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tPickupReturnTime = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getPickupReturn()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tPickupReturnTime > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf((tPickupReturnTime/tOrderCount) + 0.5))) * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tOrderReturnTime = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getOrderReturn()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tOrderReturnTime > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf((tOrderReturnTime/tOrderCount) + 0.5))) * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tMinD7Below = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getD7Success()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tMinD7Below > 0 && tOrderCount > 0){
                    cell2.setCellValue(String.format("%.1f", (tMinD7Below/tOrderCount * 100)) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                double tMin30Below = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin30Below()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tMin30Below > 0 && tOrderCount > 0){
                    cell2.setCellValue(String.format("%.1f", (tMin30Below/tOrderCount) * 100) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                double tTotalSales=0d;
                if(!locale.toString().equals("zh_TW")) {
                    double tMin30To40 = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin30To40()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin30To40 > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", (tMin30To40/tOrderCount) * 100) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    double tMin40To50 = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin40To50()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin40To50 > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", (tMin40To50/tOrderCount) * 100) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    double tMin50To60 = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin50To60()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin50To60 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", (tMin50To60/tOrderCount) * 100) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    double tMin60To90 = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin60To90()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin60To90 > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", (tMin60To90/tOrderCount) * 100) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    double tMin90Under = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin90Under()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin90Under > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", (tMin90Under/tOrderCount) * 100) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    tTotalSales = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getTotalSales()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tTotalSales > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", tTotalSales / tOrderCount));
                    }else{
                        cell2.setCellValue("0");
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                long errCnt = group.getValue().stream().filter(x -> Integer.parseInt(changeType(Integer.class, x.getErrtc())) > 0).count();
                cell2 = addListRow.createCell(colNum++);
                if (tErrCount > 0 && errCnt > 0){
                    cell2.setCellValue(changeType(Integer.class, String.valueOf(Float.parseFloat(String.valueOf(tErrCount)) / Float.parseFloat(String.valueOf(errCnt)) + 0.5)));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                long thirdCnt = group.getValue().stream().filter(x -> Integer.parseInt(changeType(Integer.class, x.getThirdtc())) > 0).count();
                cell2 = addListRow.createCell(colNum++);
                if (thirdCnt > 0 && tThirdCount > 0){
                    cell2.setCellValue(changeType(Integer.class, String.valueOf(Float.parseFloat(String.valueOf(tThirdCount)) / Float.parseFloat(String.valueOf(thirdCnt)) + 0.5)));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);


                long tcCnt = group.getValue().stream().filter(x -> Integer.parseInt(changeType(Integer.class, x.getTc())) > 0).count();
                cell2 = addListRow.createCell(colNum++);
                if (tOrderCount > 0 && tcCnt > 0){
                    cell2.setCellValue(changeType(Integer.class, String.valueOf(Float.parseFloat(String.valueOf(tOrderCount)) / Float.parseFloat(String.valueOf(tcCnt)) + 0.5)));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double hTime = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getHours()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (Double.isNaN(tOrderCount/hTime) || Double.isInfinite(tOrderCount/hTime)){
                    cell2.setCellValue("0");
                }else{
                    cell2.setCellValue(String.format("%.2f", tOrderCount/hTime));
                }
                cell2.setCellStyle(dataCellStyle);

                if(!locale.toString().equals("zh_TW")) {
                    cell2 = addListRow.createCell(colNum++);
                    if (hTime > 0 && tTotalSales > 0){
                        if (Double.isNaN(tTotalSales / hTime) || Double.isInfinite(tTotalSales / hTime)){
                            cell2.setCellValue("0");
                        }else{
                            cell2.setCellValue(String.format("%.2f", tTotalSales / hTime));
                        }
                    }else{
                        cell2.setCellValue("0");
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                double tTotalPickupReturnTime = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getTotalPickupReturn()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tTotalPickupReturnTime > 0 && tcCnt > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf((tTotalPickupReturnTime/tcCnt) + 0.5))) * 1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tTotalDistance = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getAvgDistance()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tTotalDistance > 0 && tOrderCount > 0){
                    cell2.setCellValue(String.format("%.1f", tTotalDistance/tOrderCount) + "km");
                }else{
                    cell2.setCellValue("0km");
                }
                cell2.setCellStyle(dataCellStyle);
            }
        }

    }

    // 평균 값 추가
    public String avgChkFilter(long mills){
        return mills>=0? DurationFormatUtils.formatDuration(mills,"HH:mm:ss"):"-"+DurationFormatUtils.formatDuration(Math.abs(mills),"HH:mm:ss");
    }
}
