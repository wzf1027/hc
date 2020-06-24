package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 团队分红级差设置表
 * @author zf
 *
 */
@TableName("team_bonus_setting")
public class TeamBonusSetting implements Serializable {

	private static final long serialVersionUID = 6998642344921715230L;
	
	@TableId(type=IdType.AUTO,value="SETTING_ID")
	private Long settingId;
	/**
	 * 最大值
	 */
	@TableField("MAX_PRICE")
	private Double maxPrice;
	/**
	 * 最小值
	 */
	@TableField("MIN_PRICE")
	private Double minPrice;

	/**
	 * 返利比例（%）
	 */
	@TableField("BONUS_RATIO")
	private Double bonusRatio;
	
	/**
	 * 等级
	 */
	@TableField("LEVEL")
	private Integer level;
	
	/**
	 * 创建时间
	 */
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date creatTime;
	/**
	 * 
	 * 更新时间
	 */
	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;

	public Long getSettingId() {
		return settingId;
	}

	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getBonusRatio() {
		return bonusRatio;
	}

	public void setBonusRatio(Double bonusRatio) {
		this.bonusRatio = bonusRatio;
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
	
	
	
	
	
	

}
