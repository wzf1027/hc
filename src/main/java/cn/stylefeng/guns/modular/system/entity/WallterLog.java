package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 修改余额日志
 * @author zf
 *
 */
@TableName("wallter_log")
public class WallterLog implements Serializable {

	private static final long serialVersionUID = 7003613807125624990L;
	   /**
	  * 主键id
	  */
	@TableId(value = "LOG_ID", type = IdType.AUTO)
	private Long logId;
	
	@TableField("CONTENT")
	private String content;
	
	@TableField("LOG_USER")
	private String logUser;
	
	@TableField("SELLER_ID")
	private Long sellerId;
	
	 /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;
    
    

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public String getContent() {
		return content;
	}
	
	

	public String getLogUser() {
		return logUser;
	}

	public void setLogUser(String logUser) {
		this.logUser = logUser;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
    
    
	
	

}
