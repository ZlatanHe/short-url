package com.zlatan.shorturl.dal;

import org.apache.ibatis.annotations.*;

/**
 * Created by Zlatan on 19/2/24.
 */
@Mapper
public interface ShortUrlDAO {

    @Select("select * from `short_url` where `code` = #{code}")
    @Results(
            id = "ShortUrlDO",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "code", column = "code"),
                    @Result(property = "url", column = "url"),
                    @Result(property = "createTime", column = "create_time"),
                    @Result(property = "updateTime", column = "update_time")
            }
    )
    ShortUrlDO queryByCode(@Param("code") String code);

    @Select("select `url` from `short_url` where `code` = #{code}")
    String queryUrlByCode(@Param("code") String code);

    @Insert("insert into `short_url` " +
            "(" +
            "`url`," +
            "`code`," +
            "`create_time`," +
            "`update_time`," +
            "`count`" +
            ")" +
            "values " +
            "(" +
            "#{url}," +
            "#{code}," +
            "now()," +
            "now()," +
            "0" +
            ")")
    int insert(ShortUrlDO shortUrlDO);

    @Update("update `short_url` set `count` = #{count} where `code` = #{code}")
    int updateCount(@Param("code") String code,
                    @Param("count") long count);
}
