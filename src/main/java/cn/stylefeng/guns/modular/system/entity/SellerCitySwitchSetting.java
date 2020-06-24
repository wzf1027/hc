package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

@TableName("seller_city_switch_setting")
public class SellerCitySwitchSetting implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("SELLER_ID")
    private Long sellerId;

    @TableField("IS_SWITCH")
    private Integer isSwitch;

    @TableField("IP")
    private String ip;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;


    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private Date updateTime;



    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getIsSwitch() {
        return isSwitch;
    }

    public void setIsSwitch(Integer isSwitch) {
        this.isSwitch = isSwitch;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
