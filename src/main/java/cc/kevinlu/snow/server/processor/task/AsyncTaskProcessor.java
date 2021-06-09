/**
 * MIT License
 *
 * Copyright (c) 2021 chuanyichuan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cc.kevinlu.snow.server.processor.task;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author chuan
 */
@Component
@Async
public class AsyncTaskProcessor {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private JdbcTemplate           jdbcTemplate;

    public static final String     UUID_SQL      = "update sm_uuid set status = 1 where id = ?";
    public static final String     SNOWFLAKE_SQL = "update sm_snowflake set status = 1 where id = ?";
    public static final String     DIGIT_SQL     = "update sm_digit set status = 1 where id = ?";
    public static final String     TIMESTAMP_SQL = "update sm_timestamp set status = 1 where id = ?";

    public void uuidStatus(List records) {
        taskExecutor.execute(() -> {
            List<Object[]> batchSql = new ArrayList<>(records.size());
            for (Object id : records) {
                batchSql.add(new Object[] { id });
            }
            exec(UUID_SQL, batchSql);
        });
    }

    public void snowflakeStatus(List records) {
        taskExecutor.execute(() -> {
            List<Object[]> batchSql = new ArrayList<>(records.size());
            for (Object id : records) {
                batchSql.add(new Object[] { id });
            }
            exec(SNOWFLAKE_SQL, batchSql);
        });
    }

    public void digitStatus(Long id) {
        taskExecutor.execute(() -> {
            List<Object[]> batchSql = new ArrayList<>(1);
            batchSql.add(new Object[] { id });
            exec(DIGIT_SQL, batchSql);
        });
    }

    public void timestampStatus(List records) {
        taskExecutor.execute(() -> {
            List<Object[]> batchSql = new ArrayList<>(records.size());

            for (Object id : records) {
                batchSql.add(new Object[] { id });
            }
            exec(TIMESTAMP_SQL, batchSql);
        });
    }

    private void exec(String sql, List<Object[]> args) {
        jdbcTemplate.batchUpdate(sql, args);
    }
}
