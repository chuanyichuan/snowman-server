package cc.kevinlu.snow.server.data.mapper;

import org.apache.ibatis.annotations.Param;

/**
* @author chuan
* @time 2021-05-07
*/
public interface SnowmanLockMapper {

    int lock(@Param("key") String key, @Param("value") String value, @Param("expireTime") Long expireTime,
             @Param("owner") String owner);

    int reLock(@Param("key") String key, @Param("value") String value, @Param("expireTime") Long expireTime,
               @Param("owner") String owner);

    int releaseLock(@Param("key") String key, @Param("value") String value);

}
