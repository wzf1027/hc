package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 商户/承兑商的推荐关系表
 * @author zf
 *
 */
@TableName("sys_user_recommend_relation")
public class UserRecommendRelation implements Serializable {
	
	private static final long serialVersionUID = 5431107598382199540L;
	 /**
     * 主键id
     */
    @TableId(value = "RELATION_ID", type = IdType.ID_WORKER)
	private Long  relationId;
    
    /**
     * 商户id
     */
	@TableField("USER_ID")
	private Long userId;
	/**
	 * 推荐人id
	 */
	@TableField("RECOMMEND_ID")
	private Long recommendId;
	
	/**
	 * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;

	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(Long recommendId) {
		this.recommendId = recommendId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
    
    

}
