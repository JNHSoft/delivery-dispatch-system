package kr.co.cntt.core.util;

import static org.springframework.util.StringUtils.isEmpty;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>kr.co.cntt.core.util
 * <p>DateUtil.java
 * <p>DATE
 * <p>유틸
 * @author JIN
 */
public class DateUtil {
	/**
	 * <p>getToday
	 * <p>오늘
	 * @param dateFormat 날짜 형식
	 * @return 오늘 날짜
	 * @author JIN
	 */
	public static String getToday(final String dateFormat) {
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		final Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}

	/**
	 * <p>getYesterday
	 * <p>어제
	 * @param dateFormat 날짜 형식
	 * @return 어제 날짜
	 * @author JIN
	 */
	public static String getYesterday(final String dateFormat) {
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return sdf.format(cal.getTime());
	}

	/**
	 * <p>getTomorrow
	 * <p>내일
	 * @param dateFormat 날짜 형식
	 * @return 내일 날짜
	 * @author JIN
	 */
	public static String getTomorrow(final String dateFormat) {
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		return sdf.format(cal.getTime());
	}

	/**
	 * 요일 구하기
	 * 일(1)~토(7)
	 */
	public static int getDay() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(cal.getTime());
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 현재 시간 구하기
	 */
	public static int getNowTime(final String dateFormat) {
		final long time = System.currentTimeMillis();
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return Integer.parseInt(sdf.format(new Date(time)));
	}

	/**
	 * 일시 객체를 특정 형식의 String으로 변형
	 *
	 * @param value 문자열로 변환할 일시
	 * @param format 원하는 일시 형식
	 *
	 * @return 변환된 문자열
	 * @author brad
	 */
	public static String localDatetimeToStr(final LocalDateTime value, final String format) {
		if (isEmpty(value)) {
			return null;
		}
		try {
			return value.format(DateTimeFormatter.ofPattern(format));
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * @param value
	 * @param format
	 *
	 * @return
	 * @see #localDatetimeToStr(LocalDateTime, String)
	 */
	public static String localDateToStr(final LocalDate value, final String format) {
		if (isEmpty(value)) {
			return null;
		}
		try {
			return value.format(DateTimeFormatter.ofPattern(format));
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * @param value
	 * @param format
	 *
	 * @return
	 * @see #localDatetimeToStr(LocalDateTime, String)
	 */
	public static String localTimeToStr(final LocalTime value, final String format) {
		if (isEmpty(value)) {
			return null;
		}
		try {
			return value.format(DateTimeFormatter.ofPattern(format));
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * 일시 문자열의 형식 검사 및 Calendar로 변환.
	 *
	 * @param value 일시 문자열
	 * @param formats 형식목록. 가장 먼저 일치하는 형식이 사용됨.
	 *
	 * @return parse 된 객체. parse 불가하면 null.
	 * @see "https://stackoverflow.com/a/20232680/2652379"
	 * @author brad
	 */
	public static LocalDateTime strToLocalDatetime(final String value, final String... formats) {
		if (isEmpty(value)) {
			return null;
		}

		for (final String format : formats) {
			try {
				return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
			} catch (final Exception e) {
			}
		}

		return null;
	}

	/**
	 * @param value
	 * @param formats
	 *
	 * @return
	 * @see #strToLocalDatetime(String, String...)
	 */
	public static LocalDate strToLocalDate(final String value, final String... formats) {
		if (isEmpty(value)) {
			return null;
		}

		for (final String format : formats) {
			try {
				return LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
			} catch (final Exception e) {
			}
		}

		return null;
	}

	/**
	 * @param value
	 * @param formats
	 *
	 * @return
	 * @see #strToLocalDatetime(String, String...)
	 */
	public static LocalTime strToLocalTime(final String value, final String... formats) {
		if (isEmpty(value)) {
			return null;
		}

		for (final String format : formats) {
			try {
				return LocalTime.parse(value, DateTimeFormatter.ofPattern(format));
			} catch (final Exception e) {
			}
		}

		return null;
	}

	/**
	 * 일시 문자열을 다른 형식의 일시 문자열로 변경
	 *
	 * @param value 일시 문자열
	 * @param outFormat return 형식
	 * @param inFormats value의 형식목록. 가장 먼저 일치하는 형식이 사용됨.
	 * @return 변환된 문자열. 불가 시 null.
	 */
	public static String strToStrDatetime(final String value, final String outFormat, final String... inFormats) {
		final LocalDateTime dateTime = strToLocalDatetime(value, inFormats);
		if (dateTime == null) {
			return null;
		}

		return localDatetimeToStr(dateTime, outFormat);
	}

	/**
	 * @param value
	 * @param outFormat
	 * @param inFormats
	 * @return
	 * @see #strToStrDatetime(String, String, String...)
	 */
	public static String strToStrDate(final String value, final String outFormat, final String... inFormats) {
		final LocalDate date = strToLocalDate(value, inFormats);
		if (date == null) {
			return null;
		}

		return localDateToStr(date, outFormat);
	}

	/**
	 * @param value
	 * @param outFormat
	 * @param inFormats
	 * @return
	 * @see #strToStrDatetime(String, String, String...)
	 */
	public static String strToStrTime(final String value, final String outFormat, final String... inFormats) {
		final LocalTime time = strToLocalTime(value, inFormats);
		if (time == null) {
			return null;
		}

		return localTimeToStr(time, outFormat);
	}

}
