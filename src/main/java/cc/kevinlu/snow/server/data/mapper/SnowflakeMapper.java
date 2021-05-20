package cc.kevinlu.snow.server.data.mapper;

import cc.kevinlu.snow.server.data.model.SnowflakeDO;
import cc.kevinlu.snow.server.data.model.SnowflakeDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* @author chuan
* @time 2021-05-19
*/
public interface SnowflakeMapper {
    long countByExample(SnowflakeDOExample example);

    int deleteByExample(SnowflakeDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SnowflakeDO record);

    int insertSelective(SnowflakeDO record);

    List<SnowflakeDO> selectByExample(SnowflakeDOExample example);

    SnowflakeDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SnowflakeDO record, @Param("example") SnowflakeDOExample example);

    int updateByExample(@Param("record") SnowflakeDO record, @Param("example") SnowflakeDOExample example);

    int updateByPrimaryKeySelective(SnowflakeDO record);

    int updateByPrimaryKey(SnowflakeDO record);
}