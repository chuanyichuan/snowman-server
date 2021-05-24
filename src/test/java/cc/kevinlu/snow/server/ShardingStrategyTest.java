package cc.kevinlu.snow.server;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cc.kevinlu.snow.server.data.mapper.UuidMapper;
import cc.kevinlu.snow.server.data.model.UuidDOExample;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingStrategyTest {

    @Resource
    private UuidMapper uuidMapper;

    @Test
    public void testUuiStrategy() {
        UuidDOExample example = new UuidDOExample();
        example.createCriteria().andStatusEqualTo(1);
        uuidMapper.selectByExample(example);
        System.out.println("----------------1~0----------------");
        example.clear();
        example.createCriteria().andStatusEqualTo(0);
        uuidMapper.selectByExample(example);
    }

}
