package kr.co.cntt.core.util;

import static org.springframework.util.StringUtils.isEmpty;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NonNull;

/**
 * <p>
 * kr.co.cntt.core.util
 * <p>
 * StringUtil.java
 * <p>
 * 스트링
 * <p>
 * 유틸
 *
 * @author JIN
 */
public class StringUtil {
	/**
	 * <p>
	 * emailMasking
	 * <p>
	 * 이메일 마스킹 처리
	 *
	 * @param email 이메일
	 * @return 마스킹 처리한 이메일
	 * @author JIN
	 */
	public static String emailMasking(final String email) {
		final String regex = "\\b(\\S+)+@(\\S+.\\S+)";
		final Matcher matcher = Pattern.compile(regex).matcher(email);
		if (matcher.find()) {
			final String id = matcher.group(1);
			final int idLength = id.length();

			switch (idLength) {
			case 1:
				break;
			case 2:
			case 3:
				// 2글자, 3글자	==		뒤 한글자만 마스킹 처리
				return email.replaceAll("\\b(\\S+)[^@]+@(\\S+)", "$1*@$2");
			case 4:
			case 5:
				// 4글자, 5글자	==		뒤 2글자 마스킹 처리
				return email.replaceAll("\\b(\\S+)[^@][^@]+@(\\S+)", "$1**@$2");
			default:
				// 5글자	초과 		==		뒤 3글자 마스킹 처리
				return email.replaceAll("\\b(\\S+)[^@][^@][^@]+@(\\S+)", "$1***@$2");
			}
		}
		return email;
	}

	/**
	 * <p>
	 * nameMasking
	 * <p>
	 * 이름 마스킹 처리
	 *
	 * @param name 이름
	 * @return 마스킹 처리한 이름
	 * @author JIN
	 */
	public static String nameMasking(String name) {
		final String regex = "(^.)(.*)(.$)";
		final Matcher matcher = Pattern.compile(regex).matcher(name);

		if (matcher.find()) {
			final int nameLength = name.length();
			// 3글자 이상
			if (nameLength >= 3) {
				final char[] c = new char[matcher.group(2).length()];
				Arrays.fill(c, '*');
				name = name.replaceAll("(^.)(.*)(.$)", "$1" + String.valueOf(c) + "$3");

				// 2글자
			} else if (nameLength == 2) {
				name = name.replaceFirst("^.", "*");
			}
		}
		return name;
	}

	/**
	 * 한국 휴대폰번호 변환 ( 01012341234 -> 010-1234-1234 )<br>
	 * 한국의 전화번호는 7~11자리.
	 *
	 * @param phoneNumber 변환할 전화번호
	 * @return 변환 성공: "-"가 들어간 전화번호. 변환 실패: 파라메터로 입력한 전화번호
	 * @see "https://en.wikipedia.org/wiki/National_conventions_for_writing_telephone_numbers#South_Korea"
	 */
	public static String formatPhoneNumber(final String phoneNumber) {
		try {
			if (isEmpty(phoneNumber)) {
				return phoneNumber;
			}

			final String phoneNumberTrimmed = phoneNumber.replace("-", "").replace("(", "").replace(")", "").trim();

			String regEx = null;
			String input = null;
			switch (phoneNumberTrimmed.length()) {
			case 7:
				regEx = "(\\d{3})(\\d{4})";
				input = "$1-$2";
				break;
			case 8:
				regEx = "(\\d{4})(\\d{4})";
				input = "$1-$2";
				break;
			case 9:
				regEx = "(\\d{2})(\\d{3})(\\d{4})";
				input = "$1-$2-$3";
				break;
			case 10:
				regEx = phoneNumberTrimmed.indexOf("02") == 0 ? "(\\d{2})(\\d{4})(\\d{4})" : "(\\d{3})(\\d{3})(\\d{4})";
				input = "$1-$2-$3";
				break;
			case 11:
				regEx = "(\\d{3})(\\d{4})(\\d{4})";
				input = "$1-$2-$3";
				break;
			default:
				break;
			}

			if (!Pattern.matches(regEx, phoneNumberTrimmed)) {
				return phoneNumber;
			}

			return phoneNumberTrimmed.replaceAll(regEx, input);

		} catch (final Exception e) {
			return phoneNumber;
		}
	}

	/**
	 * 문자열이 숫자로 변환 가능한지 확인
	 *
	 * @param str
	 * @param forbidZeroPrefix true면 "0"으로 시작하는 숫자 문자열도 숫자로 인식
	 * @return 숫자로 변환 가능여부
	 * @author brad
	 * @see "https://stackoverflow.com/a/1102916/2652379"
	 */
	public static boolean isNumeric(final String str, final boolean forbidZeroPrefix) {
		try {
			if (!forbidZeroPrefix && str.indexOf("0") == 0 && str.length() > 1) {
				return false;
			}

			final NumberFormat formatter = NumberFormat.getInstance();
			final ParsePosition pos = new ParsePosition(0);
			formatter.parse(str, pos);
			return str.length() == pos.getIndex();

		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * 다수의 문자열을 하나의 문자열로 연결
	 *
	 * @param between 각 문자열 사이에 들어갈 문자열(구분자)
	 * @param defaultValue isEmpty(문자열)==true일 경우 value대신 사용될 값. 이 값도 empty일 경우 다음 문자열로 continue.
	 * @param values 연결할 문자열 배열. isEmpty(문자열)==true인 경우 무시.
	 * @return 연결된 문자열
	 */
	public static String concatStrings(final String between, final String defaultValue,
			@NonNull final String... values) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			String string = values[i];
			if (isEmpty(string)) {
				if (isEmpty(defaultValue)) {
					continue;
				}
				string = defaultValue;
			}

			if (i != 0 && !isEmpty(between)) {
				sb.append(between);
			}
			sb.append(string);
		}

		return sb.toString();
	}

	/**
	 * 문자열이 ASCII문자로만 이루어져있는지 확인
	 *
	 * @param str
	 * @return
	 */
	public static boolean isAscii(final String str) {
		return str.matches("\\A\\p{ASCII}*\\z");
	}
}
