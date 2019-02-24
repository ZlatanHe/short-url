package com.zlatan.shorturl.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Zlatan on 19/2/24.
 */
@Data
public class ShortUrlGenerateRequestDTO {

    private static final Pattern CODE_PATTERN = Pattern.compile("[0-9a-zA-Z]{4,16}");

    private String url;

    /**
     * 指定短网址
     */
    private String code;

    /**
     * 短网址长度
     */
    private Integer length = 4;

    /**
     * 字符集
     * @see com.zlatan.shorturl.constants.CharSetEnum
     */
    private Integer charset = 0;

    public static void adapt(ShortUrlGenerateRequestDTO request) {
        if (StringUtils.isBlank(request.getUrl())) {
            throw new IllegalArgumentException("url is null");
        }
        if (StringUtils.isNotBlank(request.getCode())) {
            Matcher matcher = CODE_PATTERN.matcher(request.getCode());
            if(!matcher.matches()) {
                throw new IllegalArgumentException("code should match the pattern [0-9|a-z|A-Z]{4,16}");
            }
            return;
        }
        if (request.getLength() > 16) {
            request.setLength(16);
        }
        if (request.getLength() < 4) {
            request.setLength(4);
        }
    }
}
