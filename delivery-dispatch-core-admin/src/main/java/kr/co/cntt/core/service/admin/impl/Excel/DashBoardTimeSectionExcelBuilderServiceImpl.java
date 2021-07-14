package kr.co.cntt.core.service.admin.impl.Excel;

import kr.co.cntt.core.model.dashboard.TimeSectionInfo;
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
import java.util.List;
import java.util.Map;

@Component("DashBoardTimeSectionExcelBuilderServiceImpl")
public class DashBoardTimeSectionExcelBuilderServiceImpl extends ExcelComm {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", locale);
        Date currentTime = new Date();
        String dTime = formatter.format(currentTime);

        String fileName = "[" + dTime + "]_";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        // 요청 하는 url 에 따라서 필요한 값을 넣어줌
        if (request.getRequestURI().matches("/downloadTimeSectionExcel")) {

            // 필요한 리스트 controller 에서 던져주는 List 					 		Nick
            List<TimeSectionInfo> dashboardDetailInfo = (List<TimeSectionInfo>) model.get("getTimeSectionInfo");
            setDashBoardTimeSectionforExcel(workbook, dashboardDetailInfo);
            // 파일 이름은 이렇게 한다. 											Nick
            fileName += "_TimeSectionInfo.xlsx";

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

    public void setDashBoardTimeSectionforExcel(SXSSFWorkbook wb, List<TimeSectionInfo> timeSectionList) {
        int rowNum = 0;
        int colNum = 0;

        Sheet sheet = wb.createSheet("TimeSectionInfos");

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

            // NAME 부문 합치기
            sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
            // 주문 전체 개수 합치기
            sheet.addMergedRegion(new CellRangeAddress(0,1,1,1));

            // 시간 구역별 개수
            sheet.addMergedRegion(new CellRangeAddress(0,0,2, 6));
            // 시간 구역별 퍼센트
            sheet.addMergedRegion(new CellRangeAddress(0,0,7, 11));

            // 명칭 (RC, AC, STORE 등)
            sheet.setColumnWidth(colNum, 15 * 256);
            Cell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("Name");
            addTitle.setCellStyle(titleCellStyle);

            // 주문 개수
            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("ORDER COUNT");
            addTitle.setCellStyle(titleCellStyle);

            // Order Count (시간 구역별 개수 Title)
            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("ORDER COUNT");
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            // Order Ratio (시간 구역별 퍼센트 Title)
            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("ORDER Ratio");
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellStyle(titleCellStyle);

            // 구역별 Title
            titleRow = sheet.createRow(rowNum++);
            colNum = 2;

            // 30 미만 개수
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("<= 30");
            addTitle.setCellStyle(titleCellStyle);

            // 31 ~ 40 개수
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("31 ~ 40");
            addTitle.setCellStyle(titleCellStyle);

            // 41 ~ 50 개수
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("41 ~ 50");
            addTitle.setCellStyle(titleCellStyle);

            // 51 ~ 60 개수
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("51 ~ 60");
            addTitle.setCellStyle(titleCellStyle);

            // >= 61
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(">= 61");
            addTitle.setCellStyle(titleCellStyle);


            // 퍼센트
            // 30 미만 퍼센트
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("<= 30");
            addTitle.setCellStyle(titleCellStyle);

            // 31 ~ 40 퍼센트
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("31 ~ 40");
            addTitle.setCellStyle(titleCellStyle);

            // 41 ~ 50 퍼센트
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("41 ~ 50");
            addTitle.setCellStyle(titleCellStyle);

            // 51 ~ 60 퍼센트
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("51 ~ 60");
            addTitle.setCellStyle(titleCellStyle);

            // >= 61 퍼센트
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(">= 61");
            addTitle.setCellStyle(titleCellStyle);

        }


        // 전체 개수에 대한 값을 구해야 한다.
//        int totalOrder = 0;
//        int totalD30 = 0;
//        int totalD40 = 0;
//        int totalD50 = 0;
//        int totalD60 = 0;
//        int totalOver = 0;

        for (int i = 0, r = timeSectionList.size(); i < r; i++) {

            Integer orderCount = timeSectionList.get(i).getOrderCount();

            colNum = 0;
            Row addListRow = sheet.createRow(rowNum);

            // 그룹에 대한 이름
            Cell cell = addListRow.createCell(colNum++);
            if (timeSectionList.get(i).getStoreName() != null){
                cell.setCellValue(timeSectionList.get(i).getStoreName());
            }else if (timeSectionList.get(i).getSubGroupName() != null){
                cell.setCellValue(timeSectionList.get(i).getSubGroupName());
            }else if (timeSectionList.get(i).getGroupName() != null){
                cell.setCellValue(timeSectionList.get(i).getGroupName());
            }else {
                cell.setCellValue("TOTAL");
            }
            cell.setCellStyle(dataCellStyle);
            
            // 주문 개수
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(orderCount);
            cell.setCellStyle(dataCellStyle);


            // <= 30
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(timeSectionList.get(i).getD30());
            cell.setCellStyle(dataCellStyle);

            // 31 ~ 40
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(timeSectionList.get(i).getD40());
            cell.setCellStyle(dataCellStyle);

            // 41 ~ 50
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(timeSectionList.get(i).getD50());
            cell.setCellStyle(dataCellStyle);

            // 51 ~ 60
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(timeSectionList.get(i).getD60());
            cell.setCellStyle(dataCellStyle);

            // > 60
            cell = addListRow.createCell(colNum++);
            cell.setCellValue(timeSectionList.get(i).getOverTime());
            cell.setCellStyle(dataCellStyle);

            // <= 30
            cell = addListRow.createCell(colNum++);
            if (timeSectionList.get(i).getD30() != null && timeSectionList.get(i).getD30() != 0){
                cell.setCellValue(String.format("%.2f", timeSectionList.get(i).getD30().floatValue() / orderCount.floatValue() * 100) + "%");
            }else {
                cell.setCellValue("0%");
            }
            cell.setCellStyle(dataCellStyle);

            // 31 ~ 40
            cell = addListRow.createCell(colNum++);
            if (timeSectionList.get(i).getD40() != null && timeSectionList.get(i).getD40() != 0){
                cell.setCellValue(String.format("%.2f", timeSectionList.get(i).getD40().floatValue() / orderCount.floatValue() * 100) + "%");
            }else {
                cell.setCellValue("0%");
            }
            cell.setCellStyle(dataCellStyle);

            // 41 ~ 50
            cell = addListRow.createCell(colNum++);
            if (timeSectionList.get(i).getD50() != null && timeSectionList.get(i).getD50() != 0){
                cell.setCellValue(String.format("%.2f", timeSectionList.get(i).getD50().floatValue() / orderCount.floatValue() * 100) + "%");
            }else {
                cell.setCellValue("0%");
            }
            cell.setCellStyle(dataCellStyle);

            // 51 ~ 60
            cell = addListRow.createCell(colNum++);
            if (timeSectionList.get(i).getD60() != null && timeSectionList.get(i).getD60() != 0){
                cell.setCellValue(String.format("%.2f", timeSectionList.get(i).getD60().floatValue() / orderCount.floatValue() * 100) + "%");
            }else {
                cell.setCellValue("0%");
            }
            cell.setCellStyle(dataCellStyle);

            // > 60
            cell = addListRow.createCell(colNum++);
            if (timeSectionList.get(i).getOverTime() != null && timeSectionList.get(i).getOverTime() != 0){
                cell.setCellValue(String.format("%.2f", timeSectionList.get(i).getOverTime().floatValue() / orderCount.floatValue() * 100) + "%");
            }else {
                cell.setCellValue("0%");
            }
            cell.setCellStyle(dataCellStyle);

            rowNum++;
        }
    }
}
