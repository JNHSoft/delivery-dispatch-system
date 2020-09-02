package kr.co.deliverydispatch.View.twkfc;

import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.statistic.Interval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("StoreStatisticsByDateAtTWKFCExcelBuilderServiceImpl")
public class StoreStatisticsByDateAtTWKFCExcelBuilderServiceImpl extends CommExcel {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //locale = LocaleContextHolder.getLocale();
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", locale );
        Date currentTime = new Date ( );
        String dTime = formatter.format ( currentTime );
        String fileName = "[" + dTime + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        if(request.getRequestURI().matches("/excelDownloadByDateAtTWKFC")) {
            java.util.List<ByDate> storeStatisticsByDateList = (java.util.List<ByDate>) model.get("getStoreStatisticsByDateAtTWKFCExcel");
            setStoreStatisticsByDateExcel(workbook, storeStatisticsByDateList);
            fileName += " Date_Report.xlsx";
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
    public void setStoreStatisticsByDateExcel(SXSSFWorkbook wb, List<ByDate> storeStatisticsByDateList) {
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
            sheet.addMergedRegion(new CellRangeAddress(0,0,locale.toString().equals("zh_TW")?9:14,locale.toString().equals("zh_TW")?13:20));

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
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);
            //
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);
            //
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
        float errtc = 0f;
        float tc = 0f;
        float tplh = 0f;
        float spmh = 0f;
        long totalPickupReturnTime = 0L;
        float totalDistance = 0f;


        int rowCnt = storeStatisticsByDateList.size();
        int tcRowCnt = storeStatisticsByDateList.stream()
                .filter(x -> {
                    if (Integer.parseInt(changeType(Integer.class, x.getTc())) > 0){
                        return true;
                    }else{
                        return false;
                    }
                }).collect(Collectors.toList()).size();
        int returnNullCnt = 0;
        int tpSpNullCnt = 0;
        int distanceNullCnt = 0;

        for(int i = 0, r = storeStatisticsByDateList.size(); i<r; i++) {

            int chkCnt = 0;
            int chkTpSpCnt = 0;
            int chkDistanceCnt = 0;

            orderPickupTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderPickup())) * 1000;
            pickupCompleteTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getPickupComplete())) * 1000;
            orderCompleteTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderComplete())) * 1000;

            // 빈값 가능
            if(storeStatisticsByDateList.get(i).getCompleteReturn() !=null){
                completeReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getCompleteReturn()))  * 1000;
            } else {
                chkCnt++;
            }

            if(storeStatisticsByDateList.get(i).getPickupReturn() !=null){
                pickupReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getPickupReturn()))  * 1000;
            } else {
                chkCnt++;
            }

            if(storeStatisticsByDateList.get(i).getOrderReturn() !=null){
                orderReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderReturn()))  * 1000;
            } else {
                chkCnt++;
            }

            minD7Below += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMinD7Below()));
            min30Below += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin30Below()));
            min30To40 += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin30To40()));
            min40To50 += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin40To50()));
            min50To60 += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin50To60()));
            min60To90 += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin60To90()));
            min90Under += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin90Under()));
            totalSales += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getTotalSales()));

            errtc += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getErrtc()));
            tc += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getTc()));

            if(storeStatisticsByDateList.get(i).getTplh() !=null){
                tplh += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getTplh()));
            } else {
                chkTpSpCnt++;
            }

            if(storeStatisticsByDateList.get(i).getSpmh() !=null){
                spmh += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getSpmh()));
            } else {
                chkTpSpCnt++;
            }

            if(storeStatisticsByDateList.get(i).getTotalPickupReturn() !=null){
                totalPickupReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getTotalPickupReturn()));
            } else {
                chkCnt++;
            }

            if(storeStatisticsByDateList.get(i).getAvgDistance() !=null){
                totalDistance += Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getAvgDistance()));
            } else {
                chkDistanceCnt++;
            }

            colNum = 0;
            Row addListRow = sheet.createRow(rowNum);

            Cell cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByDateList.get(i).getStoreName());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(storeStatisticsByDateList.get(i).getOrderPickup()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(storeStatisticsByDateList.get(i).getPickupComplete()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(storeStatisticsByDateList.get(i).getOrderComplete()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(storeStatisticsByDateList.get(i).getCompleteReturn()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(storeStatisticsByDateList.get(i).getPickupReturn()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(storeStatisticsByDateList.get(i).getOrderReturn()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            //cell.setCellValue(String.format("%.2f",Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMinD7Below())))+"%");
            cell.setCellValue(String.format("%.2f",Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMinD7Below())))+"%");
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            //cell.setCellValue(String.format("%.2f",Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMin30Below())))+"%");
            cell.setCellValue(String.format("%.2f",Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin30Below())))+"%");
            cell.setCellStyle(dataCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(String.format("%.2f", Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMin30To40()))) + "%");
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin30To40()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(String.format("%.2f", Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMin40To50()))) + "%");
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin40To50()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin50To60()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin60To90()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin90Under()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getTotalSales()));
                cell.setCellStyle(dataCellStyle);
            }

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getErrtc()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getTc()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(Math.floor(Double.parseDouble(changeType(Float.class, storeStatisticsByDateList.get(i).getTplh())) * 100) / 100);
            cell.setCellStyle(dataCellStyle);

            if(!locale.toString().equals("zh_TW")) {
                cell = addListRow.createCell(colNum++);
                cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getSpmh()));
                cell.setCellStyle(dataCellStyle);
            }

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(minusChkFilter(storeStatisticsByDateList.get(i).getTotalPickupReturn()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(String.format("%.2f",Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getAvgDistance())))+"km");
            cell.setCellStyle(dataCellStyle);


            rowNum ++;

            if(chkCnt == 0){
                returnNullCnt++;
            }

            if(chkTpSpCnt == 0){
                tpSpNullCnt++;
            }

            if(chkDistanceCnt == 0){
                distanceNullCnt++;
            }

            // 평균값
            if(i==storeStatisticsByDateList.size()-1){
                colNum = 0;
                addListRow = sheet.createRow(rowNum++);

                Cell cell2 = addListRow.createCell(colNum++);
                cell2.setCellValue("Average");
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderPickupTime > 0 && tcRowCnt > 0){
                    cell2.setCellValue(minusChkFilter(orderPickupTime/tcRowCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (pickupCompleteTime > 0 && tcRowCnt > 0){
                    cell2.setCellValue(minusChkFilter(pickupCompleteTime/tcRowCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderCompleteTime > 0 && tcRowCnt > 0){
                    cell2.setCellValue(minusChkFilter(orderCompleteTime/tcRowCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 빈값 가능
                cell2 = addListRow.createCell(colNum++);
                if (completeReturnTime > 0 && returnNullCnt > 0){
                    cell2.setCellValue(minusChkFilter(completeReturnTime/returnNullCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (pickupReturnTime > 0 && returnNullCnt > 0){
                    cell2.setCellValue(minusChkFilter(pickupReturnTime/returnNullCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderReturnTime > 0 && returnNullCnt > 0){
                    cell2.setCellValue(minusChkFilter(orderReturnTime/returnNullCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (minD7Below > 0 && tcRowCnt > 0){
                    cell2.setCellValue(String.format("%.2f",minD7Below/tcRowCnt) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (min30Below > 0 && tcRowCnt > 0){
                    cell2.setCellValue(String.format("%.2f",min30Below/tcRowCnt) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                if(!locale.toString().equals("zh_TW")) {
                    cell2 = addListRow.createCell(colNum++);
                    if (min30To40 > 0 && tcRowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min30To40/tcRowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min40To50 > 0 && tcRowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min40To50/tcRowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min50To60 > 0 && tcRowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min50To60/tcRowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min60To90 > 0 && tcRowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min60To90/tcRowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min90Under > 0 && tcRowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min90Under/tcRowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (totalSales > 0 && tcRowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",totalSales/tcRowCnt));
                    }else{
                        cell2.setCellValue("0");
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                cell2 = addListRow.createCell(colNum++);
                if (errtc > 0 && rowCnt > 0){
                    cell2.setCellValue(String.format("%.2f",errtc/rowCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (tc > 0 && tcRowCnt > 0){
                    cell2.setCellValue(String.format("%.2f",tc/tcRowCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (Float.isNaN(tplh/tpSpNullCnt) || Float.isInfinite(tplh/tpSpNullCnt)){
                    cell2.setCellValue("0");
                }else{
                    cell2.setCellValue(String.format("%.2f", tplh/tpSpNullCnt));
                }
                cell2.setCellStyle(dataCellStyle);

                if(!locale.toString().equals("zh_TW")) {
                    cell2 = addListRow.createCell(colNum++);
                    if (Float.isNaN(spmh / tpSpNullCnt) || Float.isInfinite(spmh / tpSpNullCnt)){
                        cell2.setCellValue("0");
                    }else{
                        cell2.setCellValue(String.format("%.2f", spmh / tpSpNullCnt));
                    }
                    cell2.setCellStyle(dataCellStyle);
                }

                cell2 = addListRow.createCell(colNum++);
                if (totalPickupReturnTime > 0 && returnNullCnt > 0){
                    cell2.setCellValue(minusChkFilter((totalPickupReturnTime*1000)/returnNullCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (totalDistance > 0 && distanceNullCnt > 0){
                    cell2.setCellValue(String.format("%.2f",totalDistance/distanceNullCnt)+"km");
                }else{
                    cell2.setCellValue("0km");
                }
                cell2.setCellStyle(dataCellStyle);

            }
        }
    }
}
