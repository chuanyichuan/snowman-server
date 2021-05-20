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
package cc.kevinlu.snow.server.config;

/**
 * @author chuan
 */
public final class Constants {

    public static final int    DEFAULT_TIMEOUT                     = 3000;

    public static final int    BATCH_INSERT_SIZE                   = 500;

    public static final String CHECK_CHUNK_TOPIC                   = "check_chunk_queue";

    public static final String CHECK_CHUNK_LOCK_PATTERN            = "check_chunk_lock_%s";

    public static final String GROUP_RECENT_MAX_VALUE_QUEUE        = "group_recent_max_value_queue";
    public static final String GROUP_RECENT_MAX_VALUE_ITEM_PATTERN = "item_%d";

    /**
     * fill with groupId and instanceId and mode
     */
    public static final String CACHE_ID_LOCK_PATTERN               = "group_instance_id_%d_%d_%d";

    /**
     * fill with groupId
     */
    public static final String CACHE_GENERATE_LOCK_PATTERN         = "cache_generate_lock_%s";

    public static final String CACHE_GROUP_MAP                     = "group_id_info";
    public static final String CACHE_GROUP_INSTANT_MAP             = "group_instance_id_info";

}
