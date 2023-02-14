package org.csu.tvds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName tb_templates_lib
 */
@TableName(value ="tb_templates_lib")
@Data
public class TbTemplatesLib implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private String id;

    /**
     * 
     */
    @TableField(value = "model")
    private String model;

    /**
     * 
     */
    @TableField(value = "cameraNumber")
    private Integer cameraNumber;

    /**
     * 
     */
    @TableField(value = "templateUrl")
    private String templateUrl;

    /**
     * 
     */
    @TableField(value = "createYear")
    private Integer createYear;

    /**
     * 
     */
    @TableField(value = "version")
    private String version;

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
        TbTemplatesLib other = (TbTemplatesLib) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getModel() == null ? other.getModel() == null : this.getModel().equals(other.getModel()))
            && (this.getCameraNumber() == null ? other.getCameraNumber() == null : this.getCameraNumber().equals(other.getCameraNumber()))
            && (this.getTemplateUrl() == null ? other.getTemplateUrl() == null : this.getTemplateUrl().equals(other.getTemplateUrl()))
            && (this.getCreateYear() == null ? other.getCreateYear() == null : this.getCreateYear().equals(other.getCreateYear()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getModel() == null) ? 0 : getModel().hashCode());
        result = prime * result + ((getCameraNumber() == null) ? 0 : getCameraNumber().hashCode());
        result = prime * result + ((getTemplateUrl() == null) ? 0 : getTemplateUrl().hashCode());
        result = prime * result + ((getCreateYear() == null) ? 0 : getCreateYear().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", model=").append(model);
        sb.append(", cameraNumber=").append(cameraNumber);
        sb.append(", templateUrl=").append(templateUrl);
        sb.append(", createYear=").append(createYear);
        sb.append(", version=").append(version);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}