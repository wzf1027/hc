package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 实名认证开关设置
 * @author zf
 *
 */
@TableName("seller_auth_switch_setting")
public class SellerAuthSwitchSetting implements Serializable{

	
    /**
	 * 
	 */
	private static final long serialVersionUID = -8790206924885452353L;

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
