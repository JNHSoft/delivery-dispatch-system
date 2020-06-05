package kr.co.deliverydispatch.View.twkfc;

import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.statistic.IntervalAtTWKFC;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
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
import java.util.Map;

@Component("StoreStatisticsByIntervalAtTWKFCExcelBuilderServiceImpl")
public class StoreStatisticsByIntervalAtTWKFCExcelBuilderServiceImpl extends CommExcel {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        locale = LocaleContextHolder.getLocale();
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", locale );
        Date currentTime = new Date ( );
        String dTime = formatter.format ( currentTime );
        String fileName = "[" + dTime + "]";
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        if(request.getRequestURI().matches("/excelDownloadByIntervalAtTWKFC")) {
            IntervalAtTWKFC storeStatisticsByInterval = (IntervalAtTWKFC) model.get("getStoreStatisticsByIntervalAtTWKFCExcel");
            setStoreStatisticsByIntervalExcel(workbook, storeStatisticsByInterval);
            fileName += " Interval_Report.xlsx";
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
    public void setStoreStatisticsByIntervalExcel(SXSSFWorkbook wb, IntervalAtTWKFC storeStatisticsByInterval) {
        int rowNum = 0;
        int colNum = 0;

        Sheet sheet = wb.createSheet("StoreStatisticsByInterval");

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
            addTitle.setCellValue(messageSource.getMessage("statistics.3rd.label.interval",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.3rd.label.count",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.3rd.label.percentage",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.3rd.label.cumulative",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            /// D7 데이터 추가
            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.3rd.label.d7count",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.3rd.label.d7percentage",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            sheet.setColumnWidth(colNum, 17*256);
            addTitle = titleRow.createCell(colNum++);
            addTitle.setCellValue(messageSource.getMessage("statistics.3rd.label.d7cumulative",null, locale));
            addTitle.setCellStyle(titleCellStyle);

            rowNum++;
        }

        Drawing drawing = sheet.createDrawingPatriarch();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

//        System.out.println("data = " + storeStatisticsByInterval.getIntervalMinuteCounts().size());

        // 내용 부분
        for(int i = 0, r = storeStatisticsByInterval.getIntervalMinuteCounts().size(); i<r; i++) {
            String time = null;

            /// D7 데이터로 인하여 세부 분류
            switch (i){
                case 0 :
                    dataset.addValue(Integer.parseInt(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[0].toString()), "TC", "~6:59");
                    dataset.addValue(Integer.parseInt(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[3].toString()), "D7TC", "~6:59");
                    time = "~6:59";
                    break;
                case 1:
                    dataset.addValue(Integer.parseInt(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[0].toString()), "TC", "~9:59");
                    dataset.addValue(Integer.parseInt(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[3].toString()), "D7TC", "~9:59");
                    time = "~9:59";
                    break;
                default:
                    if (i + 8 > 60){
                        dataset.addValue(Integer.parseInt(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[0].toString()), "TC", String.valueOf(i+8) + "~");
                        dataset.addValue(Integer.parseInt(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[3].toString()), "D7TC", String.valueOf(i+8) + "~");
                    }else{
                        dataset.addValue(Integer.parseInt(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[0].toString()), "TC", String.valueOf(i+8));
                        dataset.addValue(Integer.parseInt(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[3].toString()), "D7TC", String.valueOf(i+8));
                    }
                    if (i+8 == 60){
                        time = "1:00:00";
                    }else if (i+8 > 60){
                        time = "1:01:00~";
                    }else{
                        time = (i + 8) +":00";
                    }
                    break;
            }


            colNum = 0;
            Row addListRow = sheet.createRow(rowNum);

            Cell cell = addListRow.createCell(colNum++);
            cell.setCellValue(time);
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[0].toString());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[1].toString() + "%");
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[2].toString() + "%");
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[3].toString());
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[4].toString() + "%");
            cell.setCellStyle(dataCellStyle);

            cell = addListRow.createCell(colNum++);
            cell.setCellValue(storeStatisticsByInterval.getIntervalMinuteCounts().get(i)[5].toString() + "%");
            cell.setCellStyle(dataCellStyle);

            rowNum ++;
        }

        JFreeChart barChart = ChartFactory.createStackedBarChart(
                messageSource.getMessage("statistics.3rd.chart",null, locale)
                , messageSource.getMessage("statistics.minute",null, locale)
                , messageSource.getMessage("statistics.count",null, locale)
                , dataset, PlotOrientation.VERTICAL, true, true, false);

        barChart.setBorderVisible(true);
        barChart.setBorderPaint(java.awt.Color.GRAY);
        barChart.getLegend().setFrame(BlockBorder.NONE);
        barChart.getLegend().setMargin(0, 0, 10, 0);
        barChart.getLegend().setPosition(RectangleEdge.BOTTOM);

        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setDomainGridlinePaint(java.awt.Color.GRAY);
        plot.setRangeGridlinePaint(java.awt.Color.GRAY);

        barChart.getTitle().setFont(new java.awt.Font("맑은고딕", Font.BOLDWEIGHT_BOLD, 20));
        plot.getDomainAxis().setLabelFont(new java.awt.Font("맑은고딕", Font.BOLDWEIGHT_BOLD, 14));
        plot.getDomainAxis().setTickLabelFont(new java.awt.Font("맑은고딕", Font.BOLDWEIGHT_BOLD, 11));
        plot.getRangeAxis().setLabelFont(new java.awt.Font("맑은고딕", Font.BOLDWEIGHT_BOLD, 14));
        plot.getRangeAxis().setTickLabelFont(new java.awt.Font("맑은고딕", Font.BOLDWEIGHT_BOLD, 11));

        plot.getRangeAxis().setAutoRange(true);
        TickUnits tickUnits = new TickUnits();
        TickUnit unit = new NumberTickUnit(1);
        tickUnits.add(unit);
        plot.getRangeAxis().setStandardTickUnits(tickUnits);

        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setSeriesOutlineStroke(0, new BasicStroke(0.5f));
        renderer.setDefaultItemLabelPaint(Color.LIGHT_GRAY);
        renderer.setDefaultItemLabelsVisible(true);

//        for (int i = 0, r = storeStatisticsByInterval.getIntervalMinuteCounts().size(); i<r; i++) {
//            renderer.setSeriesPaint(i, Color.BLUE);
//        }

        int width = 16*100;
        int height = 9*100;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(bos, barChart, width, height);
            int barImgId = wb.addPicture(bos.toByteArray(), Workbook.PICTURE_TYPE_PNG);
            bos.close();

            ClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(8);
            anchor.setCol2(20);
            anchor.setRow1(1);
            anchor.setRow2(20);

            Picture picture = drawing.createPicture(anchor, barImgId);
            picture.resize(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
