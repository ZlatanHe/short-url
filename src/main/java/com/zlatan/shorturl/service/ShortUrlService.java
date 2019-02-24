package com.zlatan.shorturl.service;

import com.zlatan.shorturl.domain.ShortUrl;
import com.zlatan.shorturl.exception.ShortUrlGenerateException;
import com.zlatan.shorturl.repository.ShortUrlRepository;
import com.zlatan.shorturl.utils.ShortUrlConvertUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Zlatan on 19/2/24.
 */
@Service
public class ShortUrlService {

    @Resource
    private ShortUrlRepository shortUrlRepository;

    @Transactional(rollbackFor = Exception.class)
    public boolean generateShortUrl(String code,
                                    String url) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setUrl(url);
        shortUrl.setCode(code);
        return shortUrlRepository.add(shortUrl);
    }

    @Transactional(rollbackFor = Exception.class)
    public String generateShortUrl(String url,
                                   int targetLength,
                                   int charsetType) {
        String[] candidates = ShortUrlConvertUtils.convert(url, targetLength, charsetType);
        for (String candidate : candidates) {
            if (generateShortUrl(candidate, url)) {
                return candidate;
            }
        }
        throw new ShortUrlGenerateException("Generate failed due to hash conflicts. Please try again later.");
    }

    public String queryUrlByShortUrl(String shortUrl) {
        return shortUrlRepository.queryUrlByCode(shortUrl);
    }

    public long queryRequestCountByShortUrl(String shortUrl) {
        return shortUrlRepository.queryRequestCountByCode(shortUrl);
    }
}
