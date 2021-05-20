package cc.kevinlu.snow.server.data.mapper;

import cc.kevinlu.snow.server.data.model.ServiceInstanceDO;
import cc.kevinlu.snow.server.data.model.ServiceInstanceDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* @author chuan
* @time 2021-04-26
*/
public interface ServiceInstanceMapper {
    long countByExample(ServiceInstanceDOExample example);

    int deleteByExample(ServiceInstanceDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ServiceInstanceDO record);

    int insertSelective(ServiceInstanceDO record);

    List<ServiceInstanceDO> selectByExample(ServiceInstanceDOExample example);

    ServiceInstanceDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ServiceInstanceDO record, @Param("example") ServiceInstanceDOExample example);

    int updateByExample(@Param("record") ServiceInstanceDO record, @Param("example") ServiceInstanceDOExample example);

    int updateByPrimaryKeySelective(ServiceInstanceDO record);

    int updateByPrimaryKey(ServiceInstanceDO record);
}