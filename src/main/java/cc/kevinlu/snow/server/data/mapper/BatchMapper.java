package cc.kevinlu.snow.server.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cc.kevinlu.snow.server.data.model.DigitDO;
import cc.kevinlu.snow.server.data.model.SnowflakeDO;
import cc.kevinlu.snow.server.data.model.UuidDO;

/**
* @author chuan
* @time 2021-04-27
*/
public interface BatchMapper {

    void insertDigit(@Param("records") List<DigitDO> records);

    void insertSnowflake(@Param("records") List<SnowflakeDO> records);

    void insertUuid(@Param("records") List<UuidDO> records);

    void updateSnowTimes(Long instanceId);

    List<Long> selectIdFromDigit(@Param("instanceId") Long instanceId, @Param("status") Integer status);

    List<Long> selectIdFromUuid(@Param("instanceId") Long instanceId, @Param("status") Integer status);

    List<Long> selectIdFromSnowflake(@Param("instanceId") Long instanceId, @Param("status") Integer status);
}
