package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 	商户充值配置
 * @author zf
 *
 */
@TableName("merchant_pay")
public class MerchantPay implements Serializable {

	private static final long serialVersionUID = 8152806181314965507L;
	
	@TableId(value="ID",type=IdType.AUTO)
	private Integer id;
	
	@TableField(value="VALUE")
	private String value;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
