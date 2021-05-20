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
package cc.kevinlu.snow.server.processor.task.pojo;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

/**
 * @author chuan
 */
@Data
@Builder
@ToString
public class RegenerateBO implements Serializable {

    /**
     * id of group in db
     */
    private Long    groupId;

    /**
     * groupId of service
     */
    private String  group;

    /**
     * instanceID of service
     */
    private String  instance;

    /**
     * 服务组每次获取ID数量
     */
    private Integer chunk;

    /**
     * 1:数字;2:雪花算法;3:UUID
     */
    private Integer mode;

    /**
     * 服务组最近一次获取的ID最大值
     */
    private String  lastValue;

    /**
     * message times
     */
    private Integer times;

    @Tolerate
    public RegenerateBO() {
    }

}
