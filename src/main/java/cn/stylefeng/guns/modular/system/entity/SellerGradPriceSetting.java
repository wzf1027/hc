package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员押金设置表
 */
@TableName("seller_grad_price_setting")
public class SellerGradPriceSetting implements Serializable {

    @TableId(value = "ID",type= IdType.AUTO)
    private Long id;

    /**
     * 押金
     */
    @TableField("CASH")
    private Double cash;

    /**
     * 最大接单的金额比例
     */
    @TableField("PRICE_RATE")
    private Double priceRate;

    /**
     * 创建时间
     */
    @TableField(value="CREATE_TIME",fill= FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(Double priceRate) {
        this.priceRate = priceRate;
    }

    public Double getCash() {
        return cash;
    }

    public void setCash(Double cash) {
        this.cash = cash;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
