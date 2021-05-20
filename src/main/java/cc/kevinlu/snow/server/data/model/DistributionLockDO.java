package cc.kevinlu.snow.server.data.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
* @author chuan
* @time 2021-05-07
*/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DistributionLockDO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 分布式锁key
     */
    private String lockKey;

    /**
     * 分布式锁value
     */
    private String lockValue;

    /**
     * 分布式锁过期时间
     */
    private Long expireTime;

    /**
     * 锁拥有者ip
     */
    private String lockOwner;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 0:无锁;1:已锁
     */
    private Integer isLocked;

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
        sb.append(", lockKey=").append(lockKey);
        sb.append(", lockValue=").append(lockValue);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", lockOwner=").append(lockOwner);
        sb.append(", version=").append(version);
        sb.append(", isLocked=").append(isLocked);
        sb.append(", gmtCreated=").append(gmtCreated);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}