package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 *会员收款码失败次数设置
 * @author zf
 *
 */
@TableName("seller_buy_sold_out_setting")
public class SellerBuySoldOutSetting implements Serializable {

	private static final long serialVersionUID = 4349932682760021618L;
	
	@TableId(value="ID",type=IdType.AUTO)
	private Long id;
	
	@TableField("NUMBER")
	private Integer number;

	@TableField("TIME")
	private Integer time;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}
}
