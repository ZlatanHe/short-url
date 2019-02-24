package com.zlatan.shorturl.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * Created by Zlatan on 19/2/24.
 */
@Slf4j
public class UUIDUtils {

    public static String convertTo32Bit(String source) {
        if (StringUtils.isBlank(source)) {
            throw new IllegalArgumentException("source cannot be blank");
        }
        String uuid = UUID.randomUUID().toString();
        String encodeStr =  uuid.substring(0, 8) + uuid.substring(9, 13)
                + uuid.substring(14, 18) + uuid.substring(19, 23)
                + uuid.substring(24);
        log.info("[UUIDUtils-convert] source={}, encodedStr={}", source, encodeStr);
        return encodeStr;
    }
}
