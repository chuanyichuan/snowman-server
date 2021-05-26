package cc.kevinlu.snow.server.generate.alogrithm;

import java.util.List;

import cc.kevinlu.snow.client.enums.IdAlgorithmEnums;
import cc.kevinlu.snow.server.config.anno.AlgorithmAnno;
import cc.kevinlu.snow.server.generate.AbstractAlgorithm;
import cc.kevinlu.snow.server.generate.AlgorithmGenerator;
import cc.kevinlu.snow.server.processor.AlgorithmProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@AlgorithmAnno(IdAlgorithmEnums.TIMESTAMP)
public class TimeStampAlgorithm extends AbstractAlgorithm<Long> implements AlgorithmGenerator<Long> {

    public TimeStampAlgorithm() {
        super(null);
    }

    public TimeStampAlgorithm(AlgorithmProcessor algorithmProcessor) {
        super(algorithmProcessor);
    }

    @Override
    public void generateDistributedId(List<Long> idList, long groupId, long instanceId, long fromValue, int chunk) {
        for (int i = 0; i < chunk; i++) {
            idList.add(algorithmProcessor.timeStamp() + i);
        }
    }
}
