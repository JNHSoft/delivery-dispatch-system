package kr.co.cntt.core.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import lombok.Setter;

@Setter
public class ServiceSupport extends LocaleContextHolder implements MessageSourceAware {

    Logger logger = LoggerFactory.getLogger(ServiceSupport.class);

    protected MessageSource messageService;


    /**
     * 프로퍼티의 메세지를 가져온다.
     * @param code : 메시지 key 값
     * @param args : 전달할 parameters / null
     * @return parameter 파싱 후 실제 메시지
     */
    public String getMessage(final ErrorCodeEnum code, final Object... args) {
        final String message = messageService.getMessage(code.name(), args, LocaleContextHolder.getLocale());
        return message;
    }

    /**
     * 해당 Locale의 메지시를 반환
     * @param code :메시지 key 값
     * @param locale : locale
     * @param args : 전달할 parameters / null
     * @return parameter String 파싱 후 실제 메시지
     */
    public String getMessage(final ErrorCodeEnum code, final Locale locale, final Object... args) {
        return messageService.getMessage(code.name(), args, locale);
    }

    /**
     * 프로퍼티의 메세지를 가져온다.
     * @param code : 메시지 key 값
     * @param args : 전달할 parameters / null
     * @return parameter String 파싱 후 실제 메시지
     */
    public String getMessage(final String code, final Object... args) {
        return messageService.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        messageService = messageSource;
    }
}
