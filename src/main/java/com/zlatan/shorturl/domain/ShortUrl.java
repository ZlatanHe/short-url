package com.zlatan.shorturl.domain;

import com.zlatan.shorturl.dal.ShortUrlDO;
import lombok.Data;

import java.util.Date;

/**
 * Created by Zlatan on 19/2/24.
 */
@Data
public class ShortUrl {

    private Long id;

    private String url;

    private String code;

    private Date createTime;

    private Date updateTime;

    private Integer count;
}
