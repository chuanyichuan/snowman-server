package cc.kevinlu.snow.server.data.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
* @author chuan
* @time 2021-05-19
*/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UuidDO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 服务实例ID
     */
    private Long serviceInstanceId;

    /**
     * 服务实例本次获取ID的数量
     */
    private Integer chunk;

    /**
     * ID值
     */
    private String gValue;

    /**
     * 状态(0:未使用;1:已使用)
     */
    private Integer status;

    /**
     * gmt_created
     */
    private Date gmtCreated;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", serviceInstanceId=").append(serviceInstanceId);
        sb.append(", chunk=").append(chunk);
        sb.append(", gValue=").append(gValue);
        sb.append(", status=").append(status);
        sb.append(", gmtCreated=").append(gmtCreated);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}