package com.zlatan.shorturl.repository;

import com.zlatan.shorturl.dal.ShortUrlDAO;
import com.zlatan.shorturl.dal.ShortUrlDO;
import com.zlatan.shorturl.domain.ShortUrl;
import com.zlatan.shorturl.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Zlatan on 19/2/24.
 */
@Repository
@Slf4j
public class ShortUrlRepository {

    private static final String CODE_REDIS_BLACK_LIST_KEY = "code-blacklist";

    private static final String SHORT_URL_REDIS_KEY_PREFIX = "short-url:";

    private static final String REQ_COUNT_REDIS_KEY = "req-cnt:";

    @Value("${invalid.code.request.limit:100}")
    private long invalidCodeRequestLimit;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShortUrlDAO shortUrlDAO;

    public boolean add(ShortUrl shortUrl) {
        ShortUrlDO shortUrlDO = ShortUrlDO.convertFromEntity(shortUrl);
        if (shortUrlDAO.insert(shortUrlDO) < 1) {
            return false;
        }

        String url = shortUrl.getUrl();
        String code = shortUrl.getCode();

        try {
            stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.openPipeline();
                    connection.stringCommands().set((SHORT_URL_REDIS_KEY_PREFIX + code).getBytes(), url.getBytes());
                    connection.setCommands().sRem(CODE_REDIS_BLACK_LIST_KEY.getBytes(), code.getBytes());
                    connection.hashCommands().hSet(REQ_COUNT_REDIS_KEY.getBytes(), code.getBytes(), "0".getBytes());
                    connection.closePipeline();
                    return null;
                }
            });
        } catch (Exception e) {
            log.error(
                    "[ShortUrlRepository-add] add to redis failed. url={}, code={}",
                    shortUrl.getUrl(), shortUrl.getCode(), e
            );
        }

        return true;
    }

    /**
     * 将访问统计同步到DB
     */
    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void saveCount2DB() {
        Date now = new Date();
        String taskKey = DateTimeUtils.format2Hour(now);
        if (stringRedisTemplate.opsForValue().increment(taskKey) > 1) {
            return;
        }
        BoundHashOperations<String, String, String> boundHashOps =
                stringRedisTemplate.boundHashOps(REQ_COUNT_REDIS_KEY);
        try (Cursor<Map.Entry<String, String>> cursor = boundHashOps.scan(
                ScanOptions.scanOptions().count(10).build())) {
            while (cursor.hasNext()) {
                Map.Entry<String, String> entry = cursor.next();
                shortUrlDAO.updateCount(entry.getKey(), Long.valueOf(entry.getValue()));
            }
        } catch (Exception e) {
            log.error("[ShortUrlRepository-saveCount2DB] failed.");
        }
    }

    public long queryRequestCountByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("code cannot be empty");
        }
        boolean hasCountInCache = stringRedisTemplate.opsForHash().hasKey(REQ_COUNT_REDIS_KEY, code);
        if (hasCountInCache) {
            return Long.parseLong(
                    Optional.ofNullable(stringRedisTemplate.opsForHash().get(REQ_COUNT_REDIS_KEY, code))
                    .orElse("0")
                    .toString()
            );
        }
        ShortUrlDO shortUrlDO = shortUrlDAO.queryByCode(code);
        return shortUrlDO == null ? 0L : shortUrlDO.getCount();
    }

    public String queryUrlByCode(final String code) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("code cannot be empty");
        }

        // 在黑名单中的code不访问
        if (stringRedisTemplate.opsForSet().isMember(CODE_REDIS_BLACK_LIST_KEY, code)) {
            return "";
        }

        // 高并发下redis做访问计数
        long reqCount = incrRequestCount(code);

        // 从redis读取缓存数据
        String url = stringRedisTemplate.opsForValue().get(SHORT_URL_REDIS_KEY_PREFIX + code);
        if (!StringUtils.isEmpty(url)) {
            return url;
        }

        // 从db读取持久化数据
        url = shortUrlDAO.queryUrlByCode(code);
        if (!StringUtils.isEmpty(url)) {
            // 存入redis
            stringRedisTemplate.opsForValue().set(SHORT_URL_REDIS_KEY_PREFIX + code, url);
        } else {
            // 访问次数超过阈值时将code设入黑名单防止恶意访问
            add2BlackListIfNeeded(code, reqCount);
        }

        return url;
    }

    /**
     * 访问计数加1
     * 访问计数对精度要求不高，计数失败不应该影响正常操作
     *
     * @param code 短链接code
     */
    private long incrRequestCount(String code) {
        try {
            return stringRedisTemplate.opsForHash().increment(REQ_COUNT_REDIS_KEY, code, 1L);
        } catch (Exception e) {
            log.error("[ShortUrlRepository-incrRequestCount] fail. code={}", code, e);
            return 0L;
        }
    }

    private void add2BlackListIfNeeded(String code,
                                       long reqCount) {
        try {
            if (reqCount > invalidCodeRequestLimit) {
                stringRedisTemplate.opsForSet().add(CODE_REDIS_BLACK_LIST_KEY, code);
                log.info("[ShortUrlRepository-add2BlackListIfNeeded] " +
                                "invalid code [{}] has been requested more than {} times. " +
                                "Add it to the blacklist.",
                        code, invalidCodeRequestLimit
                );
            }
        } catch (Exception e) {
            log.error("[ShortUrlRepository-add2BlackList] fail. code={}", code, e);
        }
    }
}
