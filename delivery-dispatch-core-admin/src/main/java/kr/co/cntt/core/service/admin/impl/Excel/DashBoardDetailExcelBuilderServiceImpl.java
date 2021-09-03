package kr.co.cntt.core.service.admin.impl.Excel;

import kr.co.cntt.core.model.dashboard.RankInfo;
import org.apache.poi.ss.usermodel.*;
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

@Component("DashBoardDetailExcelBuilderServiceImpl")
public class DashBoardDetailExcelBuilderServiceImpl extends ExcelComm {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", locale);
        Date currentTime = new Date();
        String dTime = formatter.format(currentTime);

        String fileName = "[" + dTime + "]_";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        // 요청 하는 url 에 따라서 필요한 값을 넣어줌
        if (request.getRequestURI().matches("/dashboardDetailExcel")) {

            // 필요한 리스트 controller 에서 던져주는 List 					 		Nick
            List<RankInfo> dashboardDetailInfo = (List<RankInfo>) model.get("getDashboardDetailInfo");
            String headerName = (String) model.get("typeName");
            setDashBoardDetailforExcel(workbook, dashboardDetailInfo, headerName);
            // 파일 이름은 이렇게 한다. 											Nick
            fileName += headerName;
            fileName += "_DashBoardData.xlsx";

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

    public void setDashBoardDetailforExcel(SXSSFWorkbook wb, List<RankInfo> dashboardList, String headerName) {
        int rowNum = 0;
        int colNum = 0;

        Sheet sheet = wb.createSheet(headerName + "_DashBoard");

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

            sheet.setColumnWidth(colNum, 15 * 256);
            Cell addTitle = titleRow.createCell(colNum++);
            //addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.store",null, locale));
            addTitle.setCellValue("Store");
            addTitle.setCellStyle(titleCellStyle);

            // 6개
            sheet.setColumnWidth(colNum, 17 * 256);
            addTitle = titleRow.createCell(colNum++);
            //addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.average.time",null, locale));
            addTitle.setCellValue(headerName);
            addTitle.setCellStyle(titleCellStyle);

            if (dashboardList.get(0).getAchievementRate() != null){
                sheet.setColumnWidth(colNum, 17 * 256);
                addTitle = titleRow.createCell(colNum++);
                //addTitle.setCellValue(messageSource.getMessage("statistics.2nd.label.percent.completed",null, locale));
                addTitle.setCellValue("達成率");
                addTitle.setCellStyle(titleCellStyle);
            }
        }


        for (int i = 0, r = dashboardList.size(); i < r; i++) {

            colNum = 0;
            Row addListRow = sheet.createRow(rowNum);

            Cell cell = addListRow.createCell(colNum++);
            cell.setCellValue(dashboardList.get(i).getStoreName());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            if (dashboardList.get(i).getValue() != null){
                if (headerName.equals("D30T")){
                    cell.setCellValue(minusChkFilter(String.valueOf(dashboardList.get(i).getValue())));
                }else {
                    cell.setCellValue(String.format("%.2f", dashboardList.get(i).getValue()));
                }
            }else{
                cell.setCellValue(0);
            }
            cell.setCellStyle(dataCellStyle);

            if (dashboardList.get(i).getAchievementRate() != null){
                cell = addListRow.createCell(colNum++);
                if (dashboardList.get(i).getAchievementRate() != null){
                    cell.setCellValue(String.format("%.2f", dashboardList.get(i).getAchievementRate()) + "%");
                }else {
                    cell.setCellValue("0%");
                }

                cell.setCellStyle(dataCellStyle);
            }

            rowNum++;
        }
    }
}
