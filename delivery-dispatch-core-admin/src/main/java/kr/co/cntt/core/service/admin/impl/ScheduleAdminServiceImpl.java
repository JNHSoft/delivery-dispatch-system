package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.service.admin.ScheduleAdminService;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
import kr.co.cntt.core.service.admin.impl.Excel.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
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

    private String kAdmin = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d19hZG1pbiIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTUyMzgzOTY0ODExNH0.pBItlg9PbpWHNONuld5AXMWag6YohnOFBwiQj3sPg8A";
    private String pAdmin = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d19rZmNfYWRtaW4iLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE1ODA4Nzc1NDEzMjZ9.mx9V4RuBrF1DErKk2T2ZzypdnF3SbKZNW5d8Sy3tS48";

    @Resource
    private MessageSource messageSource;

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


    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 객체 주입
     */
    StatisticsAdminService statisticsAdminService;

    @Autowired
    public ScheduleAdminServiceImpl(StatisticsAdminService statisticsAdminService){
        this.statisticsAdminService = statisticsAdminService;
    }


    @Override
    public boolean sendStatisticsByMail() {
        // PizzaHut 통계 엑셀 가져오기
        Dictionary<String, DataSource> excelList = new Hashtable<>();

        // PizzaHut
        excelList.put("0-1", getStatisticsByOrderList(pAdmin, "0"));
        excelList.put("0-2", getStatisticsByOrder());

        // KFC
        excelList.put("1-1", getStatisticsByOrderList(kAdmin, "1"));
        excelList.put("1-2", getStatisticsByOrder());

        // 메일 발송
        sendMail(excelList);

        return false;
    }

    /**
     * 1페이지 통계
     * */
    private DataSource getStatisticsByOrderList(String token, String brandCode){
        Order order = new Order();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        order.setCurrentDatetime(formatter.format(new Date()));
        order.setDays("1");

        order.setToken(token);

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        List<Order> orderStatisticsByAdminList = statisticsAdminService.selectAdminStatisticsExcel(order);
        orderListStatisctics.setOrderStatisticsByAdminExcel(wb, orderStatisticsByAdminList, brandCode);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try{
            wb.write(bos);
            bos.close();
        } catch (Exception e){

        }

        return new ByteArrayDataSource(bos.toByteArray(), "application/vnd.ms-excel");
    }


    /**
    * 2페이지 통계 추출 (Pzh)
    * */
    private DataSource getStatisticsByOrder() {
        return null;
    }

    /**
     * 3페이지 통계 추출 (Pzh)
     * */
    private boolean sendStatisticsByDateByMail() {
        return false;
    }

    /**
     * 4페이지 통계 추출 (Pzh)
     * */
    private boolean sendStatisticsByIntervalByMail() {
        return false;
    }

    @Override
    public boolean sendStatisticsByMailForKFC() {
        return false;
    }

    /**
     * 2페이지 통계 추출 (KFC)
     * */
    private DataSource getStatisticsByOrderAtKFC() {
        return null;
    }

    /**
     * 3페이지 통계 추출 (KFC)
     * */
    private boolean sendStatisticsByDateByMailAtKFC() {
        return false;
    }

    /**
     * 4페이지 통계 추출 (KFC)
     * */
    private boolean sendStatisticsByIntervalByMailAtKFC() {
        return false;
    }

    /**
     * 메일 발송 프로세스
     * */
    private boolean sendMail(Dictionary<String, DataSource> sendData){

        Properties props = new Properties();

        // 메일 발송을 위한 계정들 가져오기
        List<InternetAddress> addressList = new ArrayList<>();
        List<String> mailList = new ArrayList<>();          // DB에서 가져올 것

        mailList.add("dlrmf3390@naver.com");

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

                // 발신자 설정
                message.setFrom(new InternetAddress(user, "System Manager"));

                // 수신자 설정
                InternetAddress[] mailArray = new InternetAddress[addressList.size()];
                addressList.toArray(mailArray);
                message.addRecipients(Message.RecipientType.TO, mailArray);

                // 본문 내용 설정
                message.setSubject("통계 자료 메일", "UTF-8");

                // Excel 첨부해보자.
                MimeBodyPart addExcel = new MimeBodyPart();
                //addExcel.setDataHandler(new DataHandler());
                addExcel.setFileName(MimeUtility.encodeText("엑셀 첨부.xlsx", "UTF-8", "Q"));

                // 본문 내용
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText("본문내용", "UTF-8");

                Multipart mp = new MimeMultipart();
                mp.addBodyPart(addExcel);
                mp.addBodyPart(bodyPart);

                //
                message.setContent(mp);

                // 메일 발송
                message.saveChanges();
                Transport.send(message);
            } catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }


        return true;
    }
}
