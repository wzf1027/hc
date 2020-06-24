package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 注册开关设置
 * @author Administrator
 *
 */
@TableName("seller_register_switch_setting")
public class SellerRegisterSwitchSetting implements Serializable{

	private static final long serialVersionUID = 270612511461500433L;
	
    /**
     * 主键id
     */
    @TableId(value = "SWITCH_SETTING_ID", type = IdType.AUTO)
	private Integer switchSettingId;
	
    /**
     * 开关：0表示开，1表示关
     */
    @TableField("IS_SWITCH")
	private Integer isSwitch;

	public Integer getSwitchSettingId() {
		return switchSettingId;
	}

	public void setSwitchSettingId(Integer switchSettingId) {
		this.switchSettingId = switchSettingId;
	}

	public Integer getIsSwitch() {
		return isSwitch;
	}

	public void setIsSwitch(Integer isSwitch) {
		this.isSwitch = isSwitch;
	}
    
    
	

}
