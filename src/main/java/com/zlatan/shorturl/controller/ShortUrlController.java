package com.zlatan.shorturl.controller;

import com.zlatan.shorturl.dto.ShortUrlGenerateRequestDTO;
import com.zlatan.shorturl.exception.ShortUrlGenerateException;
import com.zlatan.shorturl.service.ShortUrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;

/**
 * Created by Zlatan on 19/2/24.
 */
@RestController
public class ShortUrlController {

    @Resource
    private ShortUrlService shortUrlService;

    @PostMapping("/generate")
    public String generate(@RequestBody ShortUrlGenerateRequestDTO request) {
        ShortUrlGenerateRequestDTO.adapt(request); // 参数检查及适配
        if (StringUtils.isBlank(request.getCode())) {
            return shortUrlService.generateShortUrl(
                    request.getUrl(),
                    request.getLength(),
                    request.getCharset()
            );
        }
        if (!shortUrlService.generateShortUrl(request.getCode(), request.getUrl())) {
            throw new ShortUrlGenerateException("Duplicated short url!");
        }
        return request.getCode();
    }

    @GetMapping("/{shortUrl:[0-9a-zA-Z]{4,16}}")
    public ModelAndView redirect(@PathVariable("shortUrl") String shortUrl) {
        String url = shortUrlService.queryUrlByShortUrl(shortUrl);

        ModelAndView modelAndView = new ModelAndView();
        if (StringUtils.isBlank(url)) {
            modelAndView.setViewName("forward:idx.html");
        }
        RedirectView redirectView = new RedirectView();
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirectView.setUrl(url);
        modelAndView.setView(redirectView);

        return modelAndView;
    }
}
