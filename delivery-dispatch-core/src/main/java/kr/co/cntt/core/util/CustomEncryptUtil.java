package kr.co.cntt.core.util;

import org.apache.commons.codec.binary.Base64;

public class CustomEncryptUtil {

    public static String encodeBase64(String text){

        return new String(Base64.encodeBase64(text.getBytes()));
    }

    public static String decodeBase64(String text){

        return new String(Base64.decodeBase64(text));
    }

}
