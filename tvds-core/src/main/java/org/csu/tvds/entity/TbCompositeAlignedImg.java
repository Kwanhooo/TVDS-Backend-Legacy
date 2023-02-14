package org.csu.tvds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName tb_composite_aligned_img
 */
@TableName(value ="tb_composite_aligned_img")
@Data
public class TbCompositeAlignedImg implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private String id;

    /**
     * 
     */
    @TableField(value = "inspectionSeq")
    private Integer inspectionSeq;

    /**
     * 
     */
    @TableField(value = "cameraNumber")
    private Integer cameraNumber;

    /**
     * 
     */
    @TableField(value = "carriageId")
    private Integer carriageId;

    /**
     * 
     */
    @TableField(value = "carriageNo")
    private Integer carriageNo;

    /**
     * 
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 
     */
    @TableField(value = "compositeUrl")
    private String compositeUrl;

    /**
     * 
     */
    @TableField(value = "alignedUrl")
    private String alignedUrl;

    /**
     * 
     */
    @TableField(value = "createTime")
    private LocalDateTime createTime;

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
        TbCompositeAlignedImg other = (TbCompositeAlignedImg) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getInspectionSeq() == null ? other.getInspectionSeq() == null : this.getInspectionSeq().equals(other.getInspectionSeq()))
            && (this.getCameraNumber() == null ? other.getCameraNumber() == null : this.getCameraNumber().equals(other.getCameraNumber()))
            && (this.getCarriageId() == null ? other.getCarriageId() == null : this.getCarriageId().equals(other.getCarriageId()))
            && (this.getCarriageNo() == null ? other.getCarriageNo() == null : this.getCarriageNo().equals(other.getCarriageNo()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCompositeUrl() == null ? other.getCompositeUrl() == null : this.getCompositeUrl().equals(other.getCompositeUrl()))
            && (this.getAlignedUrl() == null ? other.getAlignedUrl() == null : this.getAlignedUrl().equals(other.getAlignedUrl()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getInspectionSeq() == null) ? 0 : getInspectionSeq().hashCode());
        result = prime * result + ((getCameraNumber() == null) ? 0 : getCameraNumber().hashCode());
        result = prime * result + ((getCarriageId() == null) ? 0 : getCarriageId().hashCode());
        result = prime * result + ((getCarriageNo() == null) ? 0 : getCarriageNo().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCompositeUrl() == null) ? 0 : getCompositeUrl().hashCode());
        result = prime * result + ((getAlignedUrl() == null) ? 0 : getAlignedUrl().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", inspectionSeq=").append(inspectionSeq);
        sb.append(", cameraNumber=").append(cameraNumber);
        sb.append(", carriageId=").append(carriageId);
        sb.append(", carriageNo=").append(carriageNo);
        sb.append(", status=").append(status);
        sb.append(", compositeUrl=").append(compositeUrl);
        sb.append(", alignedUrl=").append(alignedUrl);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}