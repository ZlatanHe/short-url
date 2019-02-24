package com.zlatan.shorturl.repository;

import com.zlatan.shorturl.dal.ShortUrlDAO;
import com.zlatan.shorturl.domain.ShortUrl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Zlatan on 19/2/24.
 */
public class ShortUrlRepositoryTest {

    private ShortUrlRepository shortUrlRepository = new ShortUrlRepository();

    private StringRedisTemplate stringRedisTemplate = Mockito.mock(StringRedisTemplate.class);

    private ShortUrlDAO shortUrlDAO = Mockito.mock(ShortUrlDAO.class);

    @Before
    public void init() {
        ReflectionTestUtils.setField(shortUrlRepository, "shortUrlDAO", shortUrlDAO);
        ReflectionTestUtils.setField(shortUrlRepository, "stringRedisTemplate", stringRedisTemplate);
    }

    @Test
    public void testAdd() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setUrl("https://www.baidu.com");
        shortUrl.setCode("Hk89g5C");

        Mockito.when(shortUrlDAO.insert(Mockito.any())).thenReturn(0);
        Assert.assertFalse(shortUrlRepository.add(shortUrl));

        Mockito.when(shortUrlDAO.insert(Mockito.any())).thenReturn(1);
        Mockito.when(stringRedisTemplate.executePipelined(Mockito.any(RedisCallback.class)))
                .thenReturn(null);
        Assert.assertTrue(shortUrlRepository.add(shortUrl));

        Mockito.doThrow(new RuntimeException())
                .when(stringRedisTemplate)
                .executePipelined(Mockito.any(RedisCallback.class));
        Assert.assertTrue(shortUrlRepository.add(shortUrl));
    }
}
