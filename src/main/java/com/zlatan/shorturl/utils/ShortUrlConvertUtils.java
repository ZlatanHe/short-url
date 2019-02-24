package com.zlatan.shorturl.utils;

import com.zlatan.shorturl.constants.CharSetEnum;

/**
 * Created by Zlatan on 19/2/24.
 */
public class ShortUrlConvertUtils {

    public static String[] convert(String url,
                                   int targetLen,
                                   int charsetType) {
        CharSetEnum charSetEnum = CharSetEnum.findByCode(charsetType);
        char[] charset = charSetEnum.getCharset();

        int step = 32 / targetLen; // 位移步长
        int lastIndexOfCharset = charset.length - 1;

        String uuid32Bit = UUIDUtils.convertTo32Bit(url);

        String[] result = new String[4];
        for (int i = 0; i < 4; i++) {
            String subStr = uuid32Bit.substring(i * 8, i * 8 + 8); // 8位字符串
            long hexIndex = Long.parseLong(subStr, 16); // 16进值长整形，有效位8 * (log16/log2) = 32
            char[] currentResult = new char[targetLen];
            for (int j = 0; j < targetLen; j++) {
                int index = (int) (lastIndexOfCharset & hexIndex);
                currentResult[j] = charset[index];
                hexIndex = hexIndex >> step;
            }
            result[i] = new String(currentResult);
        }
        return result;
    }
}
