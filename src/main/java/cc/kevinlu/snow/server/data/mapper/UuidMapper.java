package cc.kevinlu.snow.server.data.mapper;

import cc.kevinlu.snow.server.data.model.UuidDO;
import cc.kevinlu.snow.server.data.model.UuidDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* @author chuan
* @time 2021-05-19
*/
public interface UuidMapper {
    long countByExample(UuidDOExample example);

    int deleteByExample(UuidDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UuidDO record);

    int insertSelective(UuidDO record);

    List<UuidDO> selectByExample(UuidDOExample example);

    UuidDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UuidDO record, @Param("example") UuidDOExample example);

    int updateByExample(@Param("record") UuidDO record, @Param("example") UuidDOExample example);

    int updateByPrimaryKeySelective(UuidDO record);

    int updateByPrimaryKey(UuidDO record);
}