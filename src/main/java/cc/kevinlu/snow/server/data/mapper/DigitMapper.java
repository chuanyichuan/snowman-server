package cc.kevinlu.snow.server.data.mapper;

import cc.kevinlu.snow.server.data.model.DigitDO;
import cc.kevinlu.snow.server.data.model.DigitDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* @author chuan
* @time 2021-05-19
*/
public interface DigitMapper {
    long countByExample(DigitDOExample example);

    int deleteByExample(DigitDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DigitDO record);

    int insertSelective(DigitDO record);

    List<DigitDO> selectByExample(DigitDOExample example);

    DigitDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DigitDO record, @Param("example") DigitDOExample example);

    int updateByExample(@Param("record") DigitDO record, @Param("example") DigitDOExample example);

    int updateByPrimaryKeySelective(DigitDO record);

    int updateByPrimaryKey(DigitDO record);
}