package kr.co.deliverydispatch.View;

import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.deliverydispatch.View.twkfc.CommExcel;
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

@Component("ApprovalRiderListforExcelServiceImpl")
public class ApprovalRiderListforExcelServiceImpl extends CommExcel {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.ss HH:mm:ss", locale);
        Date currentTime = new Date();

        String strDate = format.format(currentTime);
        String fileName = "[" + strDate + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        if (request.getRequestURI().matches("/excelDownloadApprovalRiderList")){
            List<RiderApprovalInfo> approvalInfos =(List<RiderApprovalInfo>) model.get("getApprovalRiderList");
            // 본문 내용 세팅
            setApprovalRiderListforExcel(workbook, approvalInfos);
            fileName += "_RiderApprovalList.xlsx";
        }

        String encFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encFileName + ";filename*= UTF-8''" + encFileName);
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        OutputStream fs = response.getOutputStream();
        workbook.write(fs);
        if (fs!=null)fs.close();
        workbook.dispose();
    }

    public void setApprovalRiderListforExcel(SXSSFWorkbook wb, List<RiderApprovalInfo> approvalList){
        int rowNum = 0;
        int colNum = 0;

        Sheet sheet = wb.createSheet("ApprovalRiderList");

        // Title Area Cell Style
        CellStyle titleCellStyle = settTitleCell(wb);
        Font titleCellFont = setTitleCellFont(wb);
        titleCellStyle.setFont(titleCellFont);

        // Data Area Cell Style
        CellStyle dataCellStyle = setDataCell(wb);
        Font dataCellFont = setDataCellFont(wb);
        dataCellStyle.setFont(dataCellFont);

        /**
         * Title 내용 입력
         * */
        {
            Row titleRow = sheet.createRow(rowNum++);

            // 순번
            sheet.setColumnWidth(colNum, 15*256);
            Cell addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue("No");
            addTitle.setCellStyle(titleCellStyle);

            // 승인 ID
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.approval.id",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 라이더 Login ID
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.approval.login.id",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 라이더 이름
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.name",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 라이더 휴대폰번호
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.phone",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 요청 날짜
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.approval.response.date",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 유효기간
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.approval.detail.expiry.datetime",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            // 라이더 상태
            sheet.setColumnWidth(colNum, 15*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("rider.approval.status",null, locale));
            addTitle.setCellStyle(titleCellStyle);

        }

        // 본문 내용
        {
            for (int i = 0, r = approvalList.size(); i < r; i++) {
                colNum = 0;
                Row addListRow = sheet.createRow(rowNum);

                // 순번
                Cell cell = addListRow.createCell(colNum++);
                cell.setCellValue(1 + i);
                cell.setCellStyle(dataCellStyle);

                // 승인 ID
                cell = addListRow.createCell(colNum++);
                cell.setCellValue(changeType(String.class, approvalList.get(i).getId()));
                cell.setCellStyle(dataCellStyle);

                // 라이더 Login ID
                cell = addListRow.createCell(colNum++);
                cell.setCellValue(changeType(String.class, approvalList.get(i).getLoginId()));
                cell.setCellStyle(dataCellStyle);

                // 라이더 이름
                cell = addListRow.createCell(colNum++);
                cell.setCellValue(changeType(String.class, approvalList.get(i).getName()));
                cell.setCellStyle(dataCellStyle);

                // 라이더 휴대폰번호
                cell = addListRow.createCell(colNum++);
                cell.setCellValue(changeType(String.class, approvalList.get(i).getPhone()));
                cell.setCellStyle(dataCellStyle);

                // 요청 날짜
                cell = addListRow.createCell(colNum++);
                switch (approvalList.get(i).getApprovalStatus()) {
                    case "1":
                    case "4":
                    case "5":
                        cell.setCellValue(changeType(String.class, approvalList.get(i).getAcceptDatetime()));
                        break;
                    case "2":
                    case "3":
                        cell.setCellValue(changeType(String.class, approvalList.get(i).getRejectDatetime()));
                        break;
                    default:
                        cell.setCellValue("");
                }
                cell.setCellStyle(dataCellStyle);

                // 유효기간
                cell = addListRow.createCell(colNum++);
                if (approvalList.get(i).getSession() == null) {
                    cell.setCellValue("-");
                } else {
                    cell.setCellValue(changeType(String.class, approvalList.get(i).getSession().getExpiryDatetime()));
                }
                cell.setCellStyle(dataCellStyle);

                // 라이더 상태
                cell = addListRow.createCell(colNum++);

                switch (approvalList.get(i).getApprovalStatus()){
                    case "0":
                        cell.setCellValue(messageSource.getMessage("rider.approval.status.wait",null, locale));
                        break;
                    case "1":
                        cell.setCellValue(messageSource.getMessage("rider.approval.status.success",null, locale));
                        break;
                    case "2":
                    case "3":
                        cell.setCellValue(messageSource.getMessage("rider.approval.status.disapproval",null, locale));
                        break;
                    case "4":
                        cell.setCellValue(messageSource.getMessage("rider.approval.status.expiry",null, locale));
                        break;
                    case "5":
                        cell.setCellValue(messageSource.getMessage("rider.approval.status.pause",null, locale));
                        break;
                    default:
                        cell.setCellValue("");
                        break;
                }

                cell.setCellStyle(dataCellStyle);

                rowNum++;
            }
        }
    }
}
