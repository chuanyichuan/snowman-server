package cc.kevinlu.snow.server.data.mapper;

import cc.kevinlu.snow.server.data.model.DistributionLockDO;
import cc.kevinlu.snow.server.data.model.DistributionLockDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* @author chuan
* @time 2021-05-07
*/
public interface DistributionLockMapper {
    long countByExample(DistributionLockDOExample example);

    int deleteByExample(DistributionLockDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DistributionLockDO record);

    int insertSelective(DistributionLockDO record);

    List<DistributionLockDO> selectByExample(DistributionLockDOExample example);

    DistributionLockDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DistributionLockDO record, @Param("example") DistributionLockDOExample example);

    int updateByExample(@Param("record") DistributionLockDO record, @Param("example") DistributionLockDOExample example);

    int updateByPrimaryKeySelective(DistributionLockDO record);

    int updateByPrimaryKey(DistributionLockDO record);
}