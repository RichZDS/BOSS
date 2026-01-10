package generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 岗位表
 * @TableName job_posting
 */
@TableName(value ="job_posting")
@Data
public class JobPosting implements Serializable {
    /**
     * 岗位ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * BossID（无外键）
     */
    private Long bossId;

    /**
     * 公司ID（无外键）
     */
    private Long companyId;

    /**
     * 岗位名称
     */
    private String title;

    /**
     * 地点
     */
    private String location;

    /**
     * 类型：全职/实习
     */
    private String jobType;

    /**
     * 
     */
    private Integer salaryMin;

    /**
     * 
     */
    private Integer salaryMax;

    /**
     * 描述
     */
    private String description;

    /**
     * 要求
     */
    private String requirement;

    /**
     * 状态：0草稿 1发布 2关闭
     */
    private Integer status;

    /**
     * 
     */
    private Date publishAt;

    /**
     * 
     */
    private Date createdAt;

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
        JobPosting other = (JobPosting) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getBossId() == null ? other.getBossId() == null : this.getBossId().equals(other.getBossId()))
            && (this.getCompanyId() == null ? other.getCompanyId() == null : this.getCompanyId().equals(other.getCompanyId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getJobType() == null ? other.getJobType() == null : this.getJobType().equals(other.getJobType()))
            && (this.getSalaryMin() == null ? other.getSalaryMin() == null : this.getSalaryMin().equals(other.getSalaryMin()))
            && (this.getSalaryMax() == null ? other.getSalaryMax() == null : this.getSalaryMax().equals(other.getSalaryMax()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getRequirement() == null ? other.getRequirement() == null : this.getRequirement().equals(other.getRequirement()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getPublishAt() == null ? other.getPublishAt() == null : this.getPublishAt().equals(other.getPublishAt()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBossId() == null) ? 0 : getBossId().hashCode());
        result = prime * result + ((getCompanyId() == null) ? 0 : getCompanyId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getJobType() == null) ? 0 : getJobType().hashCode());
        result = prime * result + ((getSalaryMin() == null) ? 0 : getSalaryMin().hashCode());
        result = prime * result + ((getSalaryMax() == null) ? 0 : getSalaryMax().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getRequirement() == null) ? 0 : getRequirement().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getPublishAt() == null) ? 0 : getPublishAt().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
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
        sb.append(", bossId=").append(bossId);
        sb.append(", companyId=").append(companyId);
        sb.append(", title=").append(title);
        sb.append(", location=").append(location);
        sb.append(", jobType=").append(jobType);
        sb.append(", salaryMin=").append(salaryMin);
        sb.append(", salaryMax=").append(salaryMax);
        sb.append(", description=").append(description);
        sb.append(", requirement=").append(requirement);
        sb.append(", status=").append(status);
        sb.append(", publishAt=").append(publishAt);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}