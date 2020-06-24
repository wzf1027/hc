package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 协议表
 * @author zf
 *
 */
@TableName("agreement")
public class Agreement implements Serializable {

	private static final long serialVersionUID = -5309600350016252135L;
	
	@TableId(value ="AGREEMENT_ID")
	private Long agreementId;
	
	/**
	 * 内容
	 */
	@TableField("CONTENT")
	private String content;

	public Long getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Long agreementId) {
		this.agreementId = agreementId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	

}
