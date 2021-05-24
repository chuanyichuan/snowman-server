package cc.kevinlu.snow.server.generate.alogrithm;

import java.util.List;

import cc.kevinlu.snow.server.generate.AbstractAlgorithm;
import cc.kevinlu.snow.server.processor.AlgorithmProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
public class TimeStampAlgorithm extends AbstractAlgorithm<Long> {

    public TimeStampAlgorithm(AlgorithmProcessor algorithmProcessor) {
        super(algorithmProcessor);
    }

    @Override
    protected void generateDistributedId(List<Long> idList, long groupId, long instanceId, long fromValue, int chunk) {
        for (int i = 0; i < chunk; i++) {
            idList.add(algorithmProcessor.timeStamp() + i);
        }
    }
}
