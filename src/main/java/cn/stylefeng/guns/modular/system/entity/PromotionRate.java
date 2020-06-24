package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 直推返利设置
 */
@TableName("promotion_rate")
public class PromotionRate implements Serializable {

    @TableId(type= IdType.AUTO,value="RATE_ID")
    private Long rateId;

    /**
     * 等级
     */
    @TableField("LEVEL")
    private Integer level;

    /**
     * 创建时间
     */
    @TableField(value="CREATE_TIME",fill= FieldFill.INSERT)
    private Date creatTime;
    /**
     *
     * 更新时间
     */
    @TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 返利比例（%）
     */
    @TableField("BONUS_RATIO")
    private Double bonusRatio;


    @TableField("NUMBER")
    private Double number;

    @TableField("TYPE")
    private Double type;

    public Double getType() {
        return type;
    }

    public void setType(Double type) {
        this.type = type;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Double getBonusRatio() {
        return bonusRatio;
    }

    public void setBonusRatio(Double bonusRatio) {
        this.bonusRatio = bonusRatio;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }
}
