package com.chuangba.homeinn.util;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/11/2.
 */

public class HashUtil {
    public static final String CHARSET_DEFAULT = "UTF-8";

    /**
     * 已废除
     * @param inStr 仅支持英文字符,未处理中文
     * @return
     */
    @Deprecated
    public static String string2MD5_Old(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }
    /***
     * MD5加码 生成32个字符的md5码[UTF8编码]
     *
     */
    public static String string2MD5(String inStr,String charset) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");

            byte[] md5Bytes = md5.digest(inStr.getBytes(charset));
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)// 如果第一个字符ascii码小于16的话 那么它转16进制的话会忽略掉前面的0
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return "";
    }

    public static String string2MD5(String inStr) {
        return string2MD5(inStr,CHARSET_DEFAULT);
    }
}
