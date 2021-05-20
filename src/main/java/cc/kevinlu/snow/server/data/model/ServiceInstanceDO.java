package cc.kevinlu.snow.server.data.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
* @author chuan
* @time 2021-04-26
*/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceDO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 服务组ID
     */
    private Long groupId;

    /**
     * 服务编号
     */
    private String serverCode;

    /**
     * 获取ID次数
     */
    private Integer snowTimes;

    /**
     * 服务状态(1:在线;2:下线;3:未知)
     */
    private Integer status;

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
        sb.append(", groupId=").append(groupId);
        sb.append(", serverCode=").append(serverCode);
        sb.append(", snowTimes=").append(snowTimes);
        sb.append(", status=").append(status);
        sb.append(", gmtCreated=").append(gmtCreated);
        sb.append(", gmtUpdated=").append(gmtUpdated);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}