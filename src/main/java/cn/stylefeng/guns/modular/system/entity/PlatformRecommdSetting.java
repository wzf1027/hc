package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 平台推荐码
 * @author zf
 *
 */
@TableName("platform_recommd_setting")
public class PlatformRecommdSetting implements Serializable {

	private static final long serialVersionUID = -5512152577448570818L;
    /**
     * 主键id
     */
    @TableId(value = "PLATFORM_RECOMMD_SETTING_ID", type = IdType.AUTO)
	private Long platformRecommdSettingId;
	
    /**
     * 推荐码
     */
    @TableField("CODE")
	private String code;

	public Long getPlatformRecommdSettingId() {
		return platformRecommdSettingId;
	}

	public void setPlatformRecommdSettingId(Long platformRecommdSettingId) {
		this.platformRecommdSettingId = platformRecommdSettingId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
    
    


}
