package cc.kevinlu.snow.server.data.mapper;

import cc.kevinlu.snow.server.data.model.GroupDO;
import cc.kevinlu.snow.server.data.model.GroupDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* @author chuan
* @time 2021-04-27
*/
public interface GroupMapper {
    long countByExample(GroupDOExample example);

    int deleteByExample(GroupDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(GroupDO record);

    int insertSelective(GroupDO record);

    List<GroupDO> selectByExample(GroupDOExample example);

    GroupDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") GroupDO record, @Param("example") GroupDOExample example);

    int updateByExample(@Param("record") GroupDO record, @Param("example") GroupDOExample example);

    int updateByPrimaryKeySelective(GroupDO record);

    int updateByPrimaryKey(GroupDO record);
}