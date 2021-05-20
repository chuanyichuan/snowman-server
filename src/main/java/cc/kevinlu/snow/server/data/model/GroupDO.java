package cc.kevinlu.snow.server.data.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
* @author chuan
* @time 2021-04-27
*/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GroupDO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务组编号
     */
    private String groupCode;

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
    private String lastValue;

    /**
     * gmt_created
     */
    private Date gmtCreated;

    /**
     * gmt_updated
     */
    private Date gmtUpdated;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", groupCode=").append(groupCode);
        sb.append(", chunk=").append(chunk);
        sb.append(", mode=").append(mode);
        sb.append(", lastValue=").append(lastValue);
        sb.append(", gmtCreated=").append(gmtCreated);
        sb.append(", gmtUpdated=").append(gmtUpdated);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}