package cc.kevinlu.snow.server.config.sharding;

import java.util.Collection;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

/**
 * 分表策略
 * 
 * @author chuan
 */
public class SnowmanTableShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
        int status = shardingValue.getValue();
        String logicTableName = shardingValue.getLogicTableName();
        if (status >= 0) {
            return logicTableName;
        }
        return logicTableName + "_used";
    }
}
