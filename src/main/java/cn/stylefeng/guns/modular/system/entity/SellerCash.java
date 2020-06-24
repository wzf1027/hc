package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员押金表
 */
@TableName("seller_cash")
public class SellerCash implements Serializable {

    @TableId(value = "ID",type= IdType.AUTO)
    private Long id;

    /**
     * 会员id
     */
    @TableField("SELLER_ID")
    private Long sellerId;

    /**
     * 押金
     */
    @TableField("CASH")
    private Double cash;

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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
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
