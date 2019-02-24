package com.zlatan.shorturl.dal;

import com.zlatan.shorturl.domain.ShortUrl;
import lombok.Data;

import java.util.Date;

/**
 * Created by Zlatan on 19/2/24.
 */
@Data
public class ShortUrlDO {

    private Long id;

    private String url;

    private String code;

    private Date createTime;

    private Date updateTime;

    private Integer count;

    public ShortUrl convert2Entity() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(id);
        shortUrl.setCode(code);
        shortUrl.setUrl(url);
        shortUrl.setCreateTime(createTime);
        shortUrl.setUpdateTime(updateTime);
        shortUrl.setCount(count);
        return shortUrl;
    }

    public static ShortUrlDO convertFromEntity(ShortUrl shortUrl) {
        ShortUrlDO shortUrlDO = new ShortUrlDO();
        shortUrlDO.setId(shortUrl.getId());
        shortUrlDO.setCode(shortUrl.getCode());
        shortUrlDO.setUrl(shortUrl.getUrl());
        shortUrlDO.setCreateTime(shortUrl.getCreateTime());
        shortUrlDO.setUpdateTime(shortUrl.getUpdateTime());
        shortUrlDO.setCount(shortUrl.getCount());
        return shortUrlDO;
    }
}
