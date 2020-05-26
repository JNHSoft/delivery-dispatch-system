package kr.co.deliverydispatch.View;

import kr.co.cntt.core.model.statistic.ByDate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component("StoreStatisticsByDateExcelBuilderServiceImpl")
public class StoreStatisticsByDateExcelBuilderServiceImpl extends AbstractView {
    @Resource
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String nullCheck(String str){
        return str!=null?str:"";
    }

    private String changeType(Type type, String value){
        String strReturn = "";

        try{
            if (type.equals(Integer.class)){
                Integer intValue = 0;

                try{
                    intValue = Integer.parseInt(value);
                }catch (NumberFormatException e){
                    Double doubleValue = 0d;

                    doubleValue = Double.parseDouble(changeType(Double.class, value)) + 0.5d;       // 반올림 적용
                    intValue = doubleValue.intValue();

                }catch (NullPointerException e){
                    logger.debug("changeType Data NullPoint = " + value);
                }catch (Exception e){
                    e.printStackTrace();
                }

                strReturn = intValue.toString();
            }else if (type.equals(Float.class)){
                Float floatValue = 0f;

                try{
                    floatValue = Float.parseFloat(value);
                }catch (NullPointerException e){
                    logger.debug("changeType Data NullPoint = " + value);
                }catch (Exception e){
                    e.printStackTrace();
                }

                strReturn = floatValue.toString();
            }else if (type.equals(Double.class)){
                Double doubleValue = 0d;

                try{
                    doubleValue = Double.parseDouble(value);
                }catch (NullPointerException e){
                    logger.debug("changeType Data NullPoint = " + value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                strReturn = doubleValue.toString();
            }else if (type.equals(String.class)){
                if (value != null){
                    strReturn = value.toString();
                }
            }else if (type.equals(Long.class)){
                Long longValue = 0l;

                try{
                    longValue = Long.parseLong(value);
                }catch (NumberFormatException e){
                    Double doubleValue = 0d;

                    doubleValue = Double.parseDouble(changeType(Double.class, value)) + 0.5d;       // 반올림 적용

                    longValue = doubleValue.longValue();
                }catch (NullPointerException e){
                    logger.debug("changeType Data NullPoint = " + value);
                }catch (Exception e){
                    e.printStackTrace();
                }

                strReturn = longValue.toString();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return strReturn;
    }

    @Value("${spring.mvc.locale}")
    private Locale defaultLocale;

    private Locale locale;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        locale = LocaleContextHolder.getLocale();
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", locale );
        Date currentTime = new Date ( );
        String dTime = formatter.format ( currentTime );
        String fileName = "[" + dTime + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        if(request.getRequestURI().matches("/excelDownloadByDate")) {
            List<ByDate> storeStatisticsByDateList = (List<ByDate>) model.get("getStoreStatisticsByDateExcel");
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
            sheet.addMergedRegion(new CellRangeAddress(0,1,1,1));
            sheet.addMergedRegion(new CellRangeAddress(0,0,2,7));

            sheet.addMergedRegion(new CellRangeAddress(0,0,8,defaultLocale.toString().equals("zh_TW")?9:14));
            sheet.addMergedRegion(new CellRangeAddress(0,0,defaultLocale.toString().equals("zh_TW")?10:15,defaultLocale.toString().equals("zh_TW")?14:21));

            sheet.setColumnWidth(colNum, 15*256);
            Cell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.store",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.date",null, locale));
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

            if(!defaultLocale.toString().equals("zh_TW")) {
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

            if(!defaultLocale.toString().equals("zh_TW")) {
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
            colNum = 2;

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

            if(!defaultLocale.toString().equals("zh_TW")){
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

            if(!defaultLocale.toString().equals("zh_TW")) {
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
        int returnNullCnt = 0;
        int tpSpNullCnt = 0;
        int distanceNullCnt = 0;

        for(int i = 0, r = storeStatisticsByDateList.size(); i<r; i++) {

            int chkCnt = 0;
            int chkTpSpCnt = 0;
            int chkDistanceCnt = 0;

            //orderPickupTime += Long.parseLong(storeStatisticsByDateList.get(i).getOrderPickup().substring(0,storeStatisticsByDateList.get(i).getOrderPickup().length()-1).replace(".",""));
            //pickupCompleteTime += Long.parseLong(storeStatisticsByDateList.get(i).getPickupComplete().substring(0,storeStatisticsByDateList.get(i).getPickupComplete().length()-1).replace(".",""));
            //orderCompleteTime += Long.parseLong(storeStatisticsByDateList.get(i).getOrderComplete().substring(0,storeStatisticsByDateList.get(i).getOrderComplete().length()-1).replace(".",""));

            orderPickupTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderPickup())) * 1000;
            pickupCompleteTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getPickupComplete())) * 1000;
            orderCompleteTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getOrderComplete())) * 1000;

            // 빈값 가능
            if(storeStatisticsByDateList.get(i).getCompleteReturn() !=null){
                //completeReturnTime += Long.parseLong(storeStatisticsByDateList.get(i).getCompleteReturn().substring(0,storeStatisticsByDateList.get(i).getCompleteReturn().length()-1).replace(".",""));
                completeReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getCompleteReturn()))  * 1000;
            } else {
                chkCnt++;
            }

            if(storeStatisticsByDateList.get(i).getPickupReturn() !=null){
                //pickupReturnTime += Long.parseLong(storeStatisticsByDateList.get(i).getPickupReturn().substring(0,storeStatisticsByDateList.get(i).getPickupReturn().length()-1).replace(".",""));
                pickupReturnTime += Long.parseLong(changeType(Long.class, storeStatisticsByDateList.get(i).getPickupReturn()))  * 1000;
            } else {
                chkCnt++;
            }

            if(storeStatisticsByDateList.get(i).getOrderReturn() !=null){
                //orderReturnTime += Long.parseLong(storeStatisticsByDateList.get(i).getOrderReturn().substring(0,storeStatisticsByDateList.get(i).getOrderReturn().length()-1).replace(".",""));
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
            cell.setCellValue(storeStatisticsByDateList.get(i).getDayToDay());
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

            if(!defaultLocale.toString().equals("zh_TW")) {
                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(String.format("%.2f", Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMin30To40()))) + "%");
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin30To40()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(String.format("%.2f", Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMin40To50()))) + "%");
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin40To50()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(String.format("%.2f", Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMin50To60()))) + "%");
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin50To60()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(String.format("%.2f", Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMin60To90()))) + "%");
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin60To90()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(String.format("%.2f", Float.parseFloat(nullCheck(storeStatisticsByDateList.get(i).getMin90Under()))) + "%");
                cell.setCellValue(String.format("%.2f", Float.parseFloat(changeType(Float.class, storeStatisticsByDateList.get(i).getMin90Under()))) + "%");
                cell.setCellStyle(dataCellStyle);

                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(nullCheck(storeStatisticsByDateList.get(i).getTotalSales()));
                cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getTotalSales()));
                cell.setCellStyle(dataCellStyle);
            }

            cell = addListRow.createCell(colNum++);
            //cell.setCellValue(nullCheck(storeStatisticsByDateList.get(i).getTc()));
            cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getErrtc()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            //cell.setCellValue(nullCheck(storeStatisticsByDateList.get(i).getTc()));
            cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getTc()));
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            //cell.setCellValue(nullCheck(storeStatisticsByDateList.get(i).getTplh()));
            cell.setCellValue(changeType(Float.class, storeStatisticsByDateList.get(i).getTplh()));
            cell.setCellStyle(dataCellStyle);

            if(!defaultLocale.toString().equals("zh_TW")) {
                cell = addListRow.createCell(colNum++);
                //cell.setCellValue(nullCheck(storeStatisticsByDateList.get(i).getSpmh()));
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
                cell2.setCellValue("");
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderPickupTime > 0 && rowCnt > 0){
                    cell2.setCellValue(avgChkFilter(orderPickupTime/rowCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (pickupCompleteTime > 0 && rowCnt > 0){
                    cell2.setCellValue(avgChkFilter(pickupCompleteTime/rowCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderCompleteTime > 0 && rowCnt > 0){
                    cell2.setCellValue(avgChkFilter(orderCompleteTime/rowCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                // 빈값 가능
                cell2 = addListRow.createCell(colNum++);
                if (completeReturnTime > 0 && returnNullCnt > 0){
                    cell2.setCellValue(avgChkFilter(completeReturnTime/returnNullCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (pickupReturnTime > 0 && returnNullCnt > 0){
                    cell2.setCellValue(avgChkFilter(pickupReturnTime/returnNullCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (orderReturnTime > 0 && returnNullCnt > 0){
                    cell2.setCellValue(avgChkFilter(orderReturnTime/returnNullCnt));
                }else{
                    cell2.setCellValue("0");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (minD7Below > 0 && rowCnt > 0){
                    cell2.setCellValue(String.format("%.2f",minD7Below/rowCnt) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                cell2 = addListRow.createCell(colNum++);
                if (min30Below > 0 && rowCnt > 0){
                    cell2.setCellValue(String.format("%.2f",min30Below/rowCnt) +"%");
                }else{
                    cell2.setCellValue("0%");
                }
                cell2.setCellStyle(dataCellStyle);

                if(!defaultLocale.toString().equals("zh_TW")) {
                    cell2 = addListRow.createCell(colNum++);
                    if (min30To40 > 0 && rowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min30To40/rowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min40To50 > 0 && rowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min40To50/rowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min50To60 > 0 && rowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min50To60/rowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min60To90 > 0 && rowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min60To90/rowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (min90Under > 0 && rowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",min90Under/rowCnt) +"%");
                    }else{
                        cell2.setCellValue("0%");
                    }
                    cell2.setCellStyle(dataCellStyle);

                    cell2 = addListRow.createCell(colNum++);
                    if (totalSales > 0 && rowCnt > 0){
                        cell2.setCellValue(String.format("%.2f",totalSales/rowCnt));
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
                if (tc > 0 && rowCnt > 0){
                    cell2.setCellValue(String.format("%.2f",tc/rowCnt));
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

                if(!defaultLocale.toString().equals("zh_TW")) {
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
                    cell2.setCellValue(avgChkFilter((totalPickupReturnTime*1000)/returnNullCnt));
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

    // 일반 값
    public String minusChkFilter(String millsData){
        if(millsData !=null){
            long mills = Long.parseLong(Math.round(Double.parseDouble(millsData)*1000)+"");
            return mills>=0?DurationFormatUtils.formatDuration(mills,"HH:mm:ss"):"-"+DurationFormatUtils.formatDuration(Math.abs(mills),"HH:mm:ss");
        } else{
            return "-";
        }

    }
    // 평균 값 추가
    public String avgChkFilter(long mills){
        return mills>=0?DurationFormatUtils.formatDuration(mills,"HH:mm:ss"):"-" + DurationFormatUtils.formatDuration(Math.abs(mills),"HH:mm:ss");
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
        titleCellStyle.setWrapText(true);
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
