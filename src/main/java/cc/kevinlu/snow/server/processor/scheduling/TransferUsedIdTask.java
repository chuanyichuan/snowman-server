package cc.kevinlu.snow.server.processor.scheduling;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

import cc.kevinlu.snow.server.data.mapper.TransferMapper;
import cc.kevinlu.snow.server.data.model.DigitDO;
import cc.kevinlu.snow.server.data.model.SnowflakeDO;
import cc.kevinlu.snow.server.data.model.UuidDO;
import cc.kevinlu.snow.server.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
public class TransferUsedIdTask implements Runnable {

    private int            size = 10;

    private String[]       tables;

    private TransferMapper transferMapper;

    public TransferUsedIdTask(int size, String[] tables, TransferMapper transferMapper) {
        this.size = size;
        this.tables = tables;
        this.transferMapper = transferMapper;
    }

    @Override
    public void run() {
        if (tables == null || tables.length == 0) {
            log.warn("tables is empty, skipped!");
            return;
        }
        for (String table : tables) {
            log.info("Start transfer table [{}]", table);
            try {
                List<Map> data = preTransfer(table, size);
                if (CollectionUtils.isEmpty(data)) {
                    continue;
                }
                doTransfer(table, data);
                postTransfer(table, data);
            } catch (Exception e) {
                log.error("The table [{}] occur error by transfer!", table, e);
            }
        }
    }

    private List<Map> preTransfer(String table, int size) {
        return transferMapper.preTransfer(table, size);
    }

    private void doTransfer(String table, List<Map> data) {
        JSONArray array = new JSONArray();
        array.addAll(data);

        if ("sm_digit".equalsIgnoreCase(table)) {
            List<DigitDO> list = array.toJavaList(DigitDO.class);
            transferMapper.transferDigit(list);
        } else if ("sm_uuid".equalsIgnoreCase(table)) {
            List<UuidDO> list = array.toJavaList(UuidDO.class);
            transferMapper.transferUuid(list);
        } else if ("sm_snowflake".equalsIgnoreCase(table)) {
            List<SnowflakeDO> list = array.toJavaList(SnowflakeDO.class);
            transferMapper.transferSnowflake(list);
        }
    }

    private void postTransfer(String table, List data) {
        transferMapper.postTransfer(table, data);
    }

}
