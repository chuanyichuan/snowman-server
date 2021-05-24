package cc.kevinlu.snow.server.data.mapper;

import cc.kevinlu.snow.server.data.model.TimeStampDO;
import cc.kevinlu.snow.server.data.model.TimeStampDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* @author lucunyu
* @time 2021-05-24
*/
public interface TimeStampMapper {
    long countByExample(TimeStampDOExample example);

    int deleteByExample(TimeStampDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TimeStampDO record);

    int insertSelective(TimeStampDO record);

    List<TimeStampDO> selectByExample(TimeStampDOExample example);

    TimeStampDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TimeStampDO record, @Param("example") TimeStampDOExample example);

    int updateByExample(@Param("record") TimeStampDO record, @Param("example") TimeStampDOExample example);

    int updateByPrimaryKeySelective(TimeStampDO record);

    int updateByPrimaryKey(TimeStampDO record);
}