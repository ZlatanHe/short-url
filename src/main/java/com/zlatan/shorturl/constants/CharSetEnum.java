package com.zlatan.shorturl.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Zlatan on 19/2/24.
 */
@AllArgsConstructor
public enum CharSetEnum {

    DEFAULT(0, "[0-9|a-z|A-Z]", Charsets.DEFAULT_CHARSET),
    LOWER_WITH_NUMBERS(1, "[0-9|a-z]", Charsets.LOWER_WITH_NUMBER),
    UPPER_WITH_NUMBERS(2, "[0-9|A-Z]", Charsets.UPPER_WITH_NUMBER),
    LOWER_WITHOUT_NUMBERS(3, "[a-z]", Charsets.LOWER_ONLY),
    UPPER_WITHOUT_NUMBERS(4, "[A-Z]", Charsets.UPPER_ONLY);

    @Getter
    int code;

    String desc;

    @Getter
    char[] charset;

    public static CharSetEnum findByCode(int code) {
        for (CharSetEnum charSetEnum : CharSetEnum.values()) {
            if (charSetEnum.code == code) {
                return charSetEnum;
            }
        }
        return DEFAULT;
    }
}
