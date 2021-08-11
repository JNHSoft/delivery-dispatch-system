package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.AdminByDate;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.statistic.IntervalAtTWKFC;
import kr.co.cntt.core.service.admin.ScheduleAdminService;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
import kr.co.cntt.core.service.admin.impl.Excel.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("scheduleAdminService")
public class ScheduleAdminServiceImpl implements ScheduleAdminService {

    @Value("${mail.id}")
    private String user;

    @Value("${mail.pwd}")
    private String pwd;

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.auth}")
    private String auth;

    @Value("${mail.enable}")
    private String enable;

    private String pAdmin = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d19hZG1pbiIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTUyMzgzOTY0ODExNH0.pBItlg9PbpWHNONuld5AXMWag6YohnOFBwiQj3sPg8A";
    private String kAdmin = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d19rZmNfYWRtaW4iLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE1ODA4Nzc1NDEzMjZ9.mx9V4RuBrF1DErKk2T2ZzypdnF3SbKZNW5d8Sy3tS48";

    /**
     * PizzaHut 통계
     * */
    @Autowired
    StatisticsAdminExcelBuilderServiceImpl orderListStatisctics;            // 1페이지 통계
    @Autowired
    StatisticsAdminOrderBuilderServiceImpl orderStatisctics;                // 2페이지 통계
    @Autowired
    StatisticsAdminByDateBuilderServiceImpl dateStatisctics;                // 3페이지
    @Autowired
    StatisticsAdminByIntervalExcelBuilderServiceImpl intervalStatisctis;    // 4페이지

    /**
     * KFC 통계
     * */
    @Autowired
    StatisticsAdminOrderAtTWKFCBuilderServiceImpl orderStatiscticsAtKFC;       // KFC 2페이지 통계
    @Autowired
    StatisticsAdminByDateAtTWKFCBuilderServiceImpl dateStatiscticsAtKFC;       // KFC 3페이지
    @Autowired
    StatisticsAdminByIntervalAtTWKFCExcelBuilderServiceImpl intervalStatisctisAtKFC;    // KFC 4페이지

    /**
     * 객체 주입
     */
    RiderMapper riderMapper;
    StatisticsAdminService statisticsAdminService;


    @Autowired
    public ScheduleAdminServiceImpl(RiderMapper riderMapper, StatisticsAdminService statisticsAdminService){
        this.riderMapper = riderMapper;
        this.statisticsAdminService = statisticsAdminService;
    }

    @Override
    public int resetRiderSharedStatusForStore(){
        return riderMapper.updateResetSharedRiderForStore();
    }

    @Override
    public boolean sendStatisticsByMail() {
        // PizzaHut 통계 엑셀 가져오기
        Map<String, DataSource> excelList = new HashMap<>();
        List<String> mailList = statisticsAdminService.selectReceivedStatisticsMailUser("1");

        // PizzaHut
        excelList.put("1", getStatisticsByOrderList(pAdmin, "0"));
        excelList.put("2", getStatisticsByOrder());
        excelList.put("3", getStatisticsByDate());
        excelList.put("4", getStatisticsByInterval());

        // 메일 발송
        return sendMail(excelList, mailList);
    }

    /**
     * 1페이지 통계
     * */
    private DataSource getStatisticsByOrderList(String token, String brandCode){
        SearchInfo searchInfo = new SearchInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        searchInfo.setCurrentDatetime(formatter.format(new Date()));
        searchInfo.setDays("1");

        searchInfo.setToken(token);

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        List<Order> orderStatisticsByAdminList = statisticsAdminService.selectAdminStatisticsExcel(searchInfo);
        orderListStatisctics.setOrderStatisticsByAdminExcel(wb, orderStatisticsByAdminList, brandCode);

        return getDataSourceForExcel(wb);
    }


    /**
    * 2페이지 통계 추출 (Pzh)
    * */
    private DataSource getStatisticsByOrder() {
        SearchInfo searchInfo = new SearchInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        searchInfo.setCurrentDatetime(formatter.format(new Date()));
        searchInfo.setDays("1");

        searchInfo.setToken(pAdmin);

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        List<Order> orderList = statisticsAdminService.selectStoreStatisticsByOrderForAdmin(searchInfo);
        orderStatisctics.setOrderStatisticsByOrderExcel(wb, orderList, 1);

        return getDataSourceForExcel(wb);
    }

    /**
     * 3페이지 통계 추출 (Pzh)
     * */
    private DataSource getStatisticsByDate() {
        SearchInfo searchInfo = new SearchInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        searchInfo.setCurrentDatetime(formatter.format(new Date()));
        searchInfo.setDays("1");

        searchInfo.setToken(pAdmin);

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        List<AdminByDate> dateList = statisticsAdminService.selectStoreStatisticsByDateForAdmin(searchInfo);
        dateStatisctics.setStoreStatisticsByDateExcel(wb, dateList, 1);

        return getDataSourceForExcel(wb);
    }

    /**
     * 4페이지 통계 추출 (Pzh)
     * */
    private DataSource getStatisticsByInterval() {
        Order order  = new Order();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        order.setCurrentDatetime(formatter.format(new Date()));
        order.setDays("1");

        order.setToken(pAdmin);

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        Interval interval = statisticsAdminService.selectAdminStatisticsByInterval(order);
        intervalStatisctis.setStoreStatisticsByIntervalExcel(wb, interval);

        return getDataSourceForExcel(wb);
    }

    @Override
    public boolean sendStatisticsByMailForKFC() {
        // PizzaHut 통계 엑셀 가져오기
        Map<String, DataSource> excelList = new HashMap<>();
        List<String> mailList = statisticsAdminService.selectReceivedStatisticsMailUser("4");

        // KFC
        excelList.put("1", getStatisticsByOrderList(kAdmin, "1"));
        excelList.put("2", getStatisticsByOrderAtKFC());
        excelList.put("3", getStatisticsByDateAtKFC());
        excelList.put("4", getStatisticsByIntervalAtKFC());

        return sendMail(excelList, mailList);
    }

    /**
     * 2페이지 통계 추출 (KFC)
     * */
    private DataSource getStatisticsByOrderAtKFC() {
        SearchInfo searchInfo = new SearchInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        searchInfo.setCurrentDatetime(formatter.format(new Date()));
        searchInfo.setDays("1");

        searchInfo.setToken(kAdmin);

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        List<Order> orderList = statisticsAdminService.selectStoreStatisticsByOrderForAdmin(searchInfo);

        List<Order> filterOrderList =
                orderList.stream().filter(a -> {
                    // 다음 4가지의 모든 시간이 NULL 이 아닌 경우만 가져온다
                    if (a.getAssignedDatetime() != null && a.getPickedUpDatetime() != null && a.getCompletedDatetime() != null && a.getReturnDatetime() != null){
                        // 예약 주문인 경우 30분을 제외한다.
                        if (a.getReservationStatus().equals("1")){
                            // 2020.05.18 예약시간 - 30분 시간이 실제 주문 시간보다 큰 경우에만 적용
                            LocalDateTime createDatetime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
                            LocalDateTime bookingDatetime = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));

                            if (createDatetime.isBefore(bookingDatetime.minusMinutes(30))){
                                LocalDateTime reserveToCreated = LocalDateTime.parse((a.getReservationDatetime()).replace(" ", "T"));
                                a.setCreatedDatetime((reserveToCreated.minusMinutes(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))));
                            }
                        }

                        // 픽업 시간
                        LocalDateTime pickupTime = LocalDateTime.parse((a.getPickedUpDatetime()).replace(" ", "T"));
                        // 배달 완료 시간
                        LocalDateTime completeTime = LocalDateTime.parse((a.getCompletedDatetime()).replace(" ", "T"));
                        // 기사 복귀 시간
                        LocalDateTime returnTime = LocalDateTime.parse((a.getReturnDatetime()).replace(" ", "T"));

                        // 주문 등록 시간
                        // LocalDateTime createdTime = LocalDateTime.parse((a.getCreatedDatetime()).replace(" ", "T"));
                        LocalDateTime assignTime = LocalDateTime.parse((a.getAssignedDatetime()).replace(" ", "T"));
                        // 다음 조건에 부합한 경우만 표기되도록 적용
                        //if (completeTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(createdTime.until(completeTime, ChronoUnit.SECONDS) < 0 || createdTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || createdTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                        if (completeTime.until(returnTime, ChronoUnit.SECONDS) >= 60 && !(assignTime.until(completeTime, ChronoUnit.SECONDS) < 0 || assignTime.until(pickupTime, ChronoUnit.SECONDS) < 0 || assignTime.until(returnTime, ChronoUnit.SECONDS) < 0)){
                            return  true;
                        }else {
                            return  false;
                        }
                    }else{
                        return  false;
                    }
                }).collect(Collectors.toList());


        orderStatiscticsAtKFC.setOrderStatisticsByOrderExcel(wb, filterOrderList, 1);

        return getDataSourceForExcel(wb);
    }

    /**
     * 3페이지 통계 추출 (KFC)
     * */
    private DataSource getStatisticsByDateAtKFC() {
        SearchInfo searchInfo = new SearchInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        searchInfo.setCurrentDatetime(formatter.format(new Date()));
        searchInfo.setDays("1");

        searchInfo.setToken(kAdmin);

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        List<AdminByDate> dateList = statisticsAdminService.selectStoreStatisticsByDateForAdmin(searchInfo);
        dateStatiscticsAtKFC.setStoreStatisticsByDateExcel(wb, dateList, 1);

        return getDataSourceForExcel(wb);
    }

    /**
     * 4페이지 통계 추출 (KFC)
     * */
    private DataSource getStatisticsByIntervalAtKFC() {
        Order order  = new Order();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        order.setCurrentDatetime(formatter.format(new Date()));
        order.setDays("1");

        order.setToken(kAdmin);

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        IntervalAtTWKFC interval = statisticsAdminService.selectAdminStatisticsByIntervalAtTWKFC(order);
        intervalStatisctisAtKFC.setStoreStatisticsByIntervalExcel(wb, interval);

        return getDataSourceForExcel(wb);
    }

    /**
     * 메일 발송 프로세스
     * */
    private boolean sendMail(Map<String, DataSource> sendData, List<String> mailList){

        if (sendData.isEmpty() || mailList.size() < 1){
            return false;
        }

        Properties props = new Properties();
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // 메일 발송을 위한 계정들 가져오기
        List<InternetAddress> addressList = new ArrayList<>();

        for (String mail:mailList
        ) {
            try{
                addressList.add(new InternetAddress(mail.toString()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", auth);
            props.put("mail.smtp.ssl.enable", enable);
            props.put("mail.smtp.ssl.trust", host);

            // ID & PWD
            Session session = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pwd);
                }
            });

            // 메일 발송 관련 내용 세팅
            try {
                MimeMessage message = new MimeMessage(session);
                Multipart mp = new MimeMultipart();

                // 발신자 설정
                message.setFrom(new InternetAddress(user, "System Manager"));

                // 수신자 설정
                InternetAddress[] mailArray = new InternetAddress[addressList.size()];
                addressList.toArray(mailArray);
                message.addRecipients(Message.RecipientType.TO, mailArray);

                // 본문 내용 설정
                message.setSubject("통계 자료 메일", "UTF-8");

                // Excel 첨부해보자.
                if (sendData.containsKey("1")){
                    MimeBodyPart orderExcel = new MimeBodyPart();
                    orderExcel.setDataHandler(new DataHandler(sendData.get("1")));
                    orderExcel.setFileName(MimeUtility.encodeText("Order_Report.xlsx", "UTF-8", "Q"));
                    mp.addBodyPart(orderExcel);
                }

                if (sendData.containsKey("2")){
                    MimeBodyPart orderListExcel = new MimeBodyPart();
                    orderListExcel.setDataHandler(new DataHandler(sendData.get("2")));
                    orderListExcel.setFileName(MimeUtility.encodeText("OrderList.xlsx", "UTF-8", "Q"));
                    mp.addBodyPart(orderListExcel);
                }

                if (sendData.containsKey("3")){
                    MimeBodyPart dateExcel = new MimeBodyPart();
                    dateExcel.setDataHandler(new DataHandler(sendData.get("3")));
                    dateExcel.setFileName(MimeUtility.encodeText("Date.xlsx", "UTF-8", "Q"));
                    mp.addBodyPart(dateExcel);
                }

                if (sendData.containsKey("4")){
                    MimeBodyPart intervalExcel = new MimeBodyPart();
                    intervalExcel.setDataHandler(new DataHandler(sendData.get("4")));
                    intervalExcel.setFileName(MimeUtility.encodeText("Interval.xlsx", "UTF-8", "Q"));
                    mp.addBodyPart(intervalExcel);
                }

                // 본문 내용
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText("본문내용", "UTF-8");
                mp.addBodyPart(bodyPart);

                //
                message.setContent(mp);

                // 메일 발송
                message.saveChanges();
                Transport.send(message);
            } catch (Exception e){
                e.printStackTrace();
                log.debug("메일 발송 오류 : " + e.getMessage());
            }

        } catch (Exception ex){
            ex.printStackTrace();
            log.debug("메일 발송 오류2 : " + ex.getMessage());
        }


        return true;
    }


    /**
     * 공통 프로세스
     * */
    private DataSource getDataSourceForExcel(SXSSFWorkbook wb){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            wb.write(bos);
            bos.close();
        }catch (Exception e){

        }finally {
            wb.dispose();
        }

        return new ByteArrayDataSource(bos.toByteArray(), "application/vnd.ms-excel");
    }
}
