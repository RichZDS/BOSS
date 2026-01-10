package generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Boss处理投递（接受/拒绝）表
 * @TableName boss_application_decision
 */
@TableName(value ="boss_application_decision")
@Data
public class BossApplicationDecision implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 投递ID（无外键）
     */
    private Long applicationId;

    /**
     * BossID（无外键）
     */
    private Long bossId;

    /**
     * 处理结果：1接受/进入流程 2拒绝 3待定
     */
    private Integer decision;

    /**
     * 阶段：0筛选 1邀面 2面试 3Offer 4结束
     */
    private Integer stage;

    /**
     * 备注
     */
    private String note;

    /**
     * 处理时间
     */
    private Date decidedAt;

    /**
     * 
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BossApplicationDecision other = (BossApplicationDecision) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getApplicationId() == null ? other.getApplicationId() == null : this.getApplicationId().equals(other.getApplicationId()))
            && (this.getBossId() == null ? other.getBossId() == null : this.getBossId().equals(other.getBossId()))
            && (this.getDecision() == null ? other.getDecision() == null : this.getDecision().equals(other.getDecision()))
            && (this.getStage() == null ? other.getStage() == null : this.getStage().equals(other.getStage()))
            && (this.getNote() == null ? other.getNote() == null : this.getNote().equals(other.getNote()))
            && (this.getDecidedAt() == null ? other.getDecidedAt() == null : this.getDecidedAt().equals(other.getDecidedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getApplicationId() == null) ? 0 : getApplicationId().hashCode());
        result = prime * result + ((getBossId() == null) ? 0 : getBossId().hashCode());
        result = prime * result + ((getDecision() == null) ? 0 : getDecision().hashCode());
        result = prime * result + ((getStage() == null) ? 0 : getStage().hashCode());
        result = prime * result + ((getNote() == null) ? 0 : getNote().hashCode());
        result = prime * result + ((getDecidedAt() == null) ? 0 : getDecidedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", applicationId=").append(applicationId);
        sb.append(", bossId=").append(bossId);
        sb.append(", decision=").append(decision);
        sb.append(", stage=").append(stage);
        sb.append(", note=").append(note);
        sb.append(", decidedAt=").append(decidedAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}