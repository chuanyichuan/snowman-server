package cc.kevinlu.snow.server.data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cc.kevinlu.snow.server.data.model.DigitDO;
import cc.kevinlu.snow.server.data.model.SnowflakeDO;
import cc.kevinlu.snow.server.data.model.TimeStampDO;
import cc.kevinlu.snow.server.data.model.UuidDO;

/**
* @author chuan
* @time 2021-05-23
*/
public interface TransferMapper {

    List<Map> preTransfer(@Param("table_name") String tableName, @Param("size") int size);

    void transferDigit(@Param("records") List<DigitDO> data);

    void transferUuid(@Param("records") List<UuidDO> list);

    void transferSnowflake(@Param("records") List<SnowflakeDO> list);

    void transferTimeStamp(@Param("records") List<TimeStampDO> list);

    void postTransfer(@Param("table_name") String table, @Param("records") List data);

}
