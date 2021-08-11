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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("StatisticsAdminByDateBuilderServiceImpl")
public class StatisticsAdminByDateBuilderServiceImpl extends ExcelComm {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", locale);
        Date currentTime = new Date();
        String dTime = formatter.format(currentTime);

        String fileName = "[" + dTime + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        // 요청 하는 url 에 따라서 필요한 값을 넣어줌
        if (request.getRequestURI().matches("/excelDownloadByDate")) {

            // 필요한 리스트 controller 에서 던져주는 List 					 		Nick
            List<AdminByDate> dateStatisticsByAdminList = (List<AdminByDate>) model.get("selectStoreStatisticsByDateForAdmin");
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

            // 상점
            sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));

            // RC
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));

            // AC
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));

            // 평균 시간
            sheet.addMergedRegion(new CellRangeAddress(0,0,3,9));

            sheet.addMergedRegion(new CellRangeAddress(0,0,10,locale.toString().equals("zh_TW")?10:15));
            sheet.addMergedRegion(new CellRangeAddress(0,0,locale.toString().equals("zh_TW")?11:16,locale.toString().equals("zh_TW")?16:23));


            // 0 상점
            sheet.setColumnWidth(colNum, 15*256);
            Cell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.store",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 1 AC
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.store",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 2 RC
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.store",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 3 - 평균시간 7개
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
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            // 10 - 배달완료율
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

            // 11 or 16 - 성능 및 생산성
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



            titleRow = sheet.createRow(rowNum++);
            colNum = 3;

            // 주문~픽업시간
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.in.store.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 픽업~완료시간
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.delivery.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 주문~완료시간
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.completed.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 20.07.15 Stay Time 도착~완료시간
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.stay.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 완료~복귀시간
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.return.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 픽업~복귀시간
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.out.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 주문~복귀시간
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.total.delivery.time",null, locale));
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

                // 총 소요시간
                sheet.setColumnWidth(colNum, 17*256);
                addTitle = titleRow.createCell(colNum++);
                addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.sales",null, locale));
                addTitle.setCellStyle(titleCellStyle);

            }

            // 오류TC
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

            // 총소요시간
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.total.time",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            //
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.excel.label.average.distance",null, locale));
            addTitle.setCellStyle(titleCellStyle);

        }


        long orderPickupTime = 0L;
        long pickupCompleteTime = 0L;
        long riderStayTimeSum = 0L;
        long orderCompleteTime = 0L;
        long completeReturnTime = 0L;
        long pickupReturnTime = 0L;
        long orderReturnTime = 0L;

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


        int tcSum = storeStatisticsByDateList.stream().mapToInt(x -> Integer.parseInt(changeType(Integer.class, x.getTc()))).sum();
        int rowCnt = storeStatisticsByDateList.size();
        int onlyErrCnt = 0;
        int onlyThirdCnt = 0;

        for(int i = 0, r = storeStatisticsByDateList.size(); i<r; i++) {

            int chkTCCnt = 0;
            int chkErrCnt = 0;
            int chkThirdCnt = 0;
            int currentRow = Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getTc()));

            pickupCompleteTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getPickupComplete()));
            orderPickupTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderPickup()));
            riderStayTimeSum += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getStayTime()));
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

            min30Below += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin30Below()));
            min30To40 += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin30To40()));
            min40To50 += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin40To50()));
            min50To60 += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin50To60()));
            min60To90 += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin60To90()));
            min90Under += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin90Under()));

            totalSales += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getTotalSales()));

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

            if (storeStatisticsByDateList.get(i).getTc() != null){
                tc += Integer.parseInt(changeType(Integer.class, storeStatisticsByDateList.get(i).getTc()));
            }else{
                chkTCCnt++;
            }


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

            // 21.04.14 RC
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByDateList.get(i).getGroupName());
            cell.setCellStyle(dataCellStyle);

            // 21.04.14 AC
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByDateList.get(i).getSubGroupName());
            cell.setCellStyle(dataCellStyle);


            // 주문~픽업시간
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getOrderPickup())) / currentRow * 1000));
            }else {
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            // 픽업~완료시간
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getPickupComplete())) / currentRow * 1000));
            }else {
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            // 주문~완료시간
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getOrderComplete())) / currentRow * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            // 20.07.15 도착~완료시간
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getStayTime())) / currentRow * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            // 완료~복귀시간
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getCompleteReturn())) / currentRow * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            // 픽업~복귀시간
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getPickupReturn())) / currentRow * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            // 주문~복귀시간
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Integer.class, storeStatisticsByDateList.get(i).getOrderReturn())) / currentRow * 1000));
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            // 30Min
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin30Below())) / currentRow * 100) + "%");
            }else{
                cell.setCellValue("-");
            }
            cell.setCellStyle(dataCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                // 30 to 40
                cell = addListRow.createCell(colNum++);
                if (currentRow > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin30To40())) / currentRow * 100) + "%");
                }else{
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                // 40 to 50
                cell = addListRow.createCell(colNum++);
                if (currentRow > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin40To50())) / currentRow * 100) + "%");
                }else{
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                // 50 to 60
                cell = addListRow.createCell(colNum++);
                if (currentRow > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin50To60())) / currentRow * 100) + "%");
                }else{
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                // 60 to 90
                cell = addListRow.createCell(colNum++);
                if (currentRow > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin60To90())) / currentRow * 100) + "%");
                }else{
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                // over 90
                cell = addListRow.createCell(colNum++);
                if (currentRow > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Integer.class, storeStatisticsByDateList.get(i).getMin90Under())) / currentRow * 100) + "%");
                }else{
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);

                // Sales
                cell = addListRow.createCell(colNum++);
                if (currentRow > 0){
                    cell.setCellValue(String.format("%.1f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getTotalSales())) / currentRow));
                }else{
                    cell.setCellValue("-");
                }
                cell.setCellStyle(dataCellStyle);
            }

            // 오류TC
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getErrtc()));
            cell.setCellStyle(dataCellStyle);

            // 제3자배송수량
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getThirdtc()));
            cell.setCellStyle(dataCellStyle);

            // TC
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getTc()));
            cell.setCellStyle(dataCellStyle);

            // TPLH
            cell = addListRow.createCell(colNum++);
            float tplhf = Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getHours()));
            cell.setCellValue(String.format("%.2f", currentRow / tplhf));
            cell.setCellStyle(dataCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                // SPMH
                cell = addListRow.createCell(colNum++);
                float spmhf = Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getHours()));
                cell.setCellValue(String.format("%.2f", Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getTotalSales())) / spmhf));
                cell.setCellStyle(dataCellStyle);
            }

            // 총소요시간
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getTotalPickupReturn())) * 1000));
            cell.setCellStyle(dataCellStyle);

            // 평균배달거리
            cell = addListRow.createCell(colNum++);
            if (currentRow > 0){
                cell.setCellValue(String.format("%.1f",Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getAvgDistance())) / currentRow)+"km");
            }else{
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

            if (chkTCCnt != 0){
               rowCnt--;
            }

            // 평균값
            if(i==storeStatisticsByDateList.size()-1){
                colNum = 0;
                addListRow = sheet.createRow(rowNum++);

                sheet.addMergedRegion(new CellRangeAddress(addListRow.getRowNum(), addListRow.getRowNum(), 0, 2));

                Cell cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("Average");
                cell2.setCellStyle(titleCellStyle);

                //
                cell2 = addListRow.createCell(colNum++);
                cell2.setCellStyle(titleCellStyle);

                //
                cell2 = addListRow.createCell(colNum++);
                cell2.setCellStyle(titleCellStyle);


                // 주문~픽업시간
                cell2 = addListRow.createCell(colNum++);
                if (orderPickupTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(orderPickupTime/tcSum*1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 픽업~완료시간
                cell2 = addListRow.createCell(colNum++);
                if (pickupCompleteTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(pickupCompleteTime/tcSum*1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 주문~완료시간
                cell2 = addListRow.createCell(colNum++);
                if (orderCompleteTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(orderCompleteTime/tcSum*1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 20.07.15 도착 도착~완료시간
                cell2 = addListRow.createCell(colNum++);
                if (riderStayTimeSum > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(riderStayTimeSum/tcSum*1000));
                }else {
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 완료~복귀시간
                cell2 = addListRow.createCell(colNum++);
                if (completeReturnTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(completeReturnTime/tcSum*1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 픽업~복귀시간
                cell2 = addListRow.createCell(colNum++);
                if (pickupReturnTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(pickupReturnTime/tcSum*1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 주문~복귀시간
                cell2 = addListRow.createCell(colNum++);
                if (orderReturnTime > 0 && tcSum > 0){
                    cell2.setCellValue(avgChkFilter(orderReturnTime/tcSum*1000));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 30Min
                cell2 = addListRow.createCell(colNum++);
                if (min30Below > 0 && tcSum > 0){
                    cell2.setCellValue(String.format("%.1f", Float.parseFloat(String.valueOf(min30Below/tcSum * 100))) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                if(!locale.toString().equals("zh_TW")) {
                    // 30 to 40
                    cell2 = addListRow.createCell(colNum++);
                    if (min30To40 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", Float.parseFloat(String.valueOf(min30To40 / tcSum * 100))) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    // 40 to 50
                    cell2 = addListRow.createCell(colNum++);
                    if (min40To50 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", Float.parseFloat(String.valueOf(min40To50 / tcSum * 100))) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    // 50 to 60
                    cell2 = addListRow.createCell(colNum++);
                    if (min50To60 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", Float.parseFloat(String.valueOf(min50To60 / tcSum * 100))) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    // 60 to 90
                    cell2 = addListRow.createCell(colNum++);
                    if (min60To90 > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", Float.parseFloat(String.valueOf(min60To90 / tcSum * 100))) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    // over 90
                    cell2 = addListRow.createCell(colNum++);
                    if (min90Under > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", Float.parseFloat(String.valueOf(min90Under / tcSum * 100))) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    // Sales
                    cell2 = addListRow.createCell(colNum++);
                    if (totalSales > 0 && tcSum > 0){
                        cell2.setCellValue(String.format("%.1f", Float.parseFloat(String.valueOf(totalSales / tcSum))));
                    }else{
                        cell2.setCellValue("0");
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                // 오류TC
                cell2 = addListRow.createCell(colNum++);
                if (errtc > 0 && onlyErrCnt > 0){
                    cell2.setCellValue(changeType(Integer.class, String.valueOf(Float.parseFloat(String.valueOf(errtc)) / Float.parseFloat(String.valueOf(onlyErrCnt)) + 0.5)));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 제3자배송수량
                cell2 = addListRow.createCell(colNum++);
                if (thirdtc > 0 && onlyThirdCnt > 0){
                    cell2.setCellValue(changeType(Integer.class, String.valueOf(Float.parseFloat(String.valueOf(thirdtc)) / Float.parseFloat(String.valueOf(onlyThirdCnt)) + 0.5)));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // TC
                cell2 = addListRow.createCell(colNum++);
                if (tc > 0 && rowCnt > 0){
                    cell2.setCellValue(changeType(Integer.class, String.valueOf(Float.parseFloat(String.valueOf(tc)) / Float.parseFloat(String.valueOf(storeStatisticsByDateList.stream().filter(x -> Integer.parseInt(changeType(Integer.class, x.getTc())) > 0).count())) + 0.5)));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // tplh
                cell2 = addListRow.createCell(colNum++);
                if (Float.isNaN(tcSum/tplh) || Float.isInfinite(tcSum/tplh)){
                    cell2.setCellValue("0");
                }else{
                    cell2.setCellValue(String.format("%.2f", tcSum/tplh));
                }
                cell2.setCellStyle(dataCellStyle);

                if(!locale.toString().equals("zh_TW")) {
                    // SPMH
                    cell2 = addListRow.createCell(colNum++);
                    if (spmh > 0 && totalSales > 0){
                        if (Float.isNaN(totalSales / spmh) || Float.isInfinite(totalSales/spmh)){
                            cell2.setCellValue("0");
                        }else{
                            cell2.setCellValue(String.format("%.2f", totalSales/spmh));
                        }
                    }else{
                        cell2.setCellValue("0");
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                // 총소요시간
                cell2 = addListRow.createCell(colNum++);
                if (totalPickupReturnTime > 0){
                    cell2.setCellValue(avgChkFilter((totalPickupReturnTime*1000)/ storeStatisticsByDateList.stream().filter(x -> Integer.parseInt(changeType(Integer.class, x.getTc())) > 0).count()));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 평균배달거리
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

            groupList.entrySet().forEach(x -> {
                System.out.println("key => " + x.getKey());
                System.out.println("value size => " + x.getValue().size());
            });


        }else if (groupNumber == 2){
            // AC 그룹
            groupList = storeStatisticsByDateList.stream().collect(Collectors.groupingBy(x-> x.getSubGroupName().split("-")[0]));

            groupList.entrySet().forEach(x -> {
                System.out.println("key => " + x.getKey());
                System.out.println("value size => " + x.getValue().size());
            });
        }

        if (groupList != null && !groupList.isEmpty()){
            groupList = groupList.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(e -> e.getKey(), e->e.getValue(), (e1, e2) -> e1, LinkedHashMap::new));

            for (Map.Entry<String, List<AdminByDate>> group: groupList.entrySet()) {
                long tOrderCount = group.getValue().stream().mapToInt(x -> Integer.parseInt(changeType(Integer.class, x.getTc()))).sum();
                long tErrCount = group.getValue().stream().mapToInt(x -> Integer.parseInt(changeType(Integer.class, x.getErrtc()))).sum();
                long tThirdCount = group.getValue().stream().mapToInt(x -> Integer.parseInt(changeType(Integer.class, x.getThirdtc()))).sum();

                colNum = 0;
                Row addListRow = sheet.createRow(rowNum++);

                sheet.addMergedRegion(new CellRangeAddress(addListRow.getRowNum(), addListRow.getRowNum(), 0, 2));

                Cell cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue(group.getKey() + " Average");
                cell2.setCellStyle(titleCellStyle);

                //
                cell2 = addListRow.createCell(colNum++);
                cell2.setCellStyle(titleCellStyle);

                //
                cell2 = addListRow.createCell(colNum++);
                cell2.setCellStyle(titleCellStyle);


                // orderPickupTime 주문 ~ 픽업
                double tPickup = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getOrderPickup()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tPickup > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tPickup/tOrderCount) + 0.5)*1000)))));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);


                double tPickupComplete = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getPickupComplete()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tPickupComplete > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tPickupComplete/tOrderCount) + 0.5)*1000)))));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tOrderComplete = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getOrderComplete()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tOrderComplete > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tOrderComplete/tOrderCount) + 0.5)*1000)))));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 20.07.15 도착
                double tRiderStayTime = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getStayTime()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tRiderStayTime > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tRiderStayTime/tOrderCount) + 0.5)*1000)))));
                }else {
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 빈값 가능
                double tCompleteReturn = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getCompleteReturn()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tCompleteReturn > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tCompleteReturn/tOrderCount) + 0.5)*1000)))));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tPIckupReturn = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getPickupReturn()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tPIckupReturn > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tPIckupReturn/tOrderCount) + 0.5)*1000)))));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tOrderReturn = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getOrderReturn()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tOrderReturn > 0 && tOrderCount > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tOrderReturn/tOrderCount) + 0.5)*1000)))));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tMin30Below = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin30Below()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tMin30Below > 0 && tOrderCount > 0){
                    cell2.setCellValue(String.format("%.1f", tMin30Below/tOrderCount * 100) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                double tTotalSales=0d;
                if(!locale.toString().equals("zh_TW")) {
                    double tMin30to40 = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin30To40()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin30to40 > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", tMin30to40 / tOrderCount * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    double tMin40to50 = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin40To50()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin40to50 > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", tMin40to50 / tOrderCount * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    double tMin50to60 = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin50To60()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin50to60 > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", tMin50to60 / tOrderCount * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    double tMin60to90 = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin60To90()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin60to90 > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", tMin60to90 / tOrderCount * 100) + "%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    double tMin90Under = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getMin90Under()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (tMin90Under > 0 && tOrderCount > 0){
                        cell2.setCellValue(String.format("%.1f", tMin90Under / tOrderCount * 100) + "%");
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
                if (tThirdCount > 0 && thirdCnt > 0){
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

                double dTplh = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getHours()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (Double.isNaN(tOrderCount/dTplh) || Double.isInfinite(tOrderCount/dTplh)){
                    cell2.setCellValue("0");
                }else{
                    cell2.setCellValue(String.format("%.2f", tOrderCount/dTplh));
                }
                cell2.setCellStyle(dataCellStyle);

                if(!locale.toString().equals("zh_TW")) {
                    double dSpmh = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getHours()))).sum();
                    cell2 = addListRow.createCell(colNum++);
                    if (spmh > 0 && tTotalSales > 0){
                        if (Double.isNaN(tTotalSales / dSpmh) || Double.isInfinite(tTotalSales/dSpmh)){
                            cell2.setCellValue("0");
                        }else{
                            cell2.setCellValue(String.format("%.2f", tTotalSales/dSpmh));
                        }
                    }else{
                        cell2.setCellValue("0");
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                double tTotalPickupReturnTime = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getTotalPickupReturn()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tTotalPickupReturnTime > 0 && tcCnt > 0){
                    cell2.setCellValue(avgChkFilter(Long.parseLong(changeType(Long.class, String.valueOf(((tTotalPickupReturnTime/tcCnt) + 0.5)*1000)))));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                double tTotalDistance = group.getValue().stream().mapToDouble(x -> Double.parseDouble(changeType(Double.class, x.getAvgDistance()))).sum();
                cell2 = addListRow.createCell(colNum++);
                if (tTotalDistance > 0 && tOrderCount > 0){
                    cell2.setCellValue(String.format("%.1f", (tTotalDistance/tOrderCount)) + "km");
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
