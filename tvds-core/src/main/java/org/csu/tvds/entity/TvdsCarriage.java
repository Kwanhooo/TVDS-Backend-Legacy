package org.csu.tvds.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @TableName tvds_carriage
 */
@TableName(value = "tvds_carriage")
@Data
public class TvdsCarriage implements Serializable {
    /**
     *
     */
    @TableId(value = "imageID")
    private String imageID;

    /**
     *
     */
    @TableField(value = "imageUrl")
    private String imageUrl;

    /**
     *
     */
    @TableField(value = "status")
    private Integer status;

    /**
     *
     */
    @TableField(value = "inspection")
    private Integer inspection;

    /**
     *
     */
    @TableField(value = "time")
    private LocalDate time;

    /**
     *
     */
    @TableField(value = "seat")
    private Integer seat;

    /**
     *
     */
    @TableField(value = "carriageNo")
    private Integer carriageNo;

    /**
     *
     */
    @TableField(value = "model")
    private String model;

    /**
     *
     */
    @TableField(value = "carriageID")
    private Integer carriageID;

    /**
     *
     */
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     *
     */
    @TableField(value = "updateTime", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     *
     */
    @TableField(value = "isDeleted")
    @TableLogic
    private Integer isDeleted;

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
        TvdsCarriage other = (TvdsCarriage) that;
        return (this.getImageID() == null ? other.getImageID() == null : this.getImageID().equals(other.getImageID()))
                && (this.getImageUrl() == null ? other.getImageUrl() == null : this.getImageUrl().equals(other.getImageUrl()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getInspection() == null ? other.getInspection() == null : this.getInspection().equals(other.getInspection()))
                && (this.getTime() == null ? other.getTime() == null : this.getTime().equals(other.getTime()))
                && (this.getSeat() == null ? other.getSeat() == null : this.getSeat().equals(other.getSeat()))
                && (this.getCarriageNo() == null ? other.getCarriageNo() == null : this.getCarriageNo().equals(other.getCarriageNo()))
                && (this.getModel() == null ? other.getModel() == null : this.getModel().equals(other.getModel()))
                && (this.getCarriageID() == null ? other.getCarriageID() == null : this.getCarriageID().equals(other.getCarriageID()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getImageID() == null) ? 0 : getImageID().hashCode());
        result = prime * result + ((getImageUrl() == null) ? 0 : getImageUrl().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getInspection() == null) ? 0 : getInspection().hashCode());
        result = prime * result + ((getTime() == null) ? 0 : getTime().hashCode());
        result = prime * result + ((getSeat() == null) ? 0 : getSeat().hashCode());
        result = prime * result + ((getCarriageNo() == null) ? 0 : getCarriageNo().hashCode());
        result = prime * result + ((getModel() == null) ? 0 : getModel().hashCode());
        result = prime * result + ((getCarriageID() == null) ? 0 : getCarriageID().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", imageID=").append(imageID);
        sb.append(", imageUrl=").append(imageUrl);
        sb.append(", status=").append(status);
        sb.append(", inspection=").append(inspection);
        sb.append(", time=").append(time);
        sb.append(", seat=").append(seat);
        sb.append(", carriageNo=").append(carriageNo);
        sb.append(", model=").append(model);
        sb.append(", carriageID=").append(carriageID);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}