package kr.co.deliverydispatch.View.twkfc;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.view.AbstractView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

public class CommExcel extends AbstractView {
    @Resource
    protected MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    protected String nullCheck(String str){
        return str!=null?str:"";
    }

    @Value("${spring.mvc.locale}")
    protected Locale locale;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //this.renderMergedOutputModel(model, request, response);
    }

    /**
     * Cell Font Style 설정
     * */
    public Font setDataCellFont(SXSSFWorkbook wb){
        Font dataCellFont = wb.createFont();
        dataCellFont.setFontHeightInPoints((short) 10);
        dataCellFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        dataCellFont.setFontName("맑은 고딕");

        return dataCellFont;
    }

    /**
     * Cell Font Style 설정 (FONT 변경)
     * */
    public Font setDataCellFont(SXSSFWorkbook wb, String strFontName){
        Font dataCellFont = wb.createFont();
        dataCellFont.setFontHeightInPoints((short) 10);
        dataCellFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        dataCellFont.setFontName(strFontName);

        return dataCellFont;
    }

    /**
     * Cell Style 설정
     * */
    public CellStyle setDataCell(SXSSFWorkbook wb){
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

    /**
     * Cell Title Style 설정
     * */
    public CellStyle settTitleCell(SXSSFWorkbook wb){
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

        return titleCellStyle;
    }

    /**
     * Cell Title Font Style 설정
     * */
    public Font setTitleCellFont(SXSSFWorkbook wb) {
        Font titleCellFont = wb.createFont();
        titleCellFont.setFontHeightInPoints((short) 10);
        titleCellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleCellFont.setFontName("맑은 고딕");

        return titleCellFont;
    }

    /**
     * Cell Title Font Style 설정 (Font 지정)
     * */
    public Font setTitleCellFont(SXSSFWorkbook wb, String strFontName) {
        Font titleCellFont = wb.createFont();
        titleCellFont.setFontHeightInPoints((short) 10);
        titleCellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleCellFont.setFontName(strFontName);

        return titleCellFont;
    }

    public String minusChkFilter(long mills){
        return mills>=0? DurationFormatUtils.formatDuration(mills,"HH:mm:ss"):"-"+DurationFormatUtils.formatDuration(Math.abs(mills),"HH:mm:ss");
    }

    public String minusChkFilter(String millsData){
        if(millsData !=null){
            long mills = Long.parseLong(Math.round(Double.parseDouble(millsData)*1000)+"");
            return mills>=0?DurationFormatUtils.formatDuration(mills,"HH:mm:ss"):"-"+DurationFormatUtils.formatDuration(Math.abs(mills),"HH:mm:ss");
        } else{
            return "-";
        }
    }

    public String changeType(Type type, String value){
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
}
