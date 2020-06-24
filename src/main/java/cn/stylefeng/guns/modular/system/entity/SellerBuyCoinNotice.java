package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 会员购买币通知表
 * @author zf
 *
 */
@TableName("seller_buy_coin_notice")
public class SellerBuyCoinNotice implements Serializable {

	private static final long serialVersionUID = 8748065874795148142L;
	/**
	 * 主键id
	 */
	@TableId(value="NOTICE_ID",type=IdType.AUTO)
	private Long noticeId;
	/**
	 * 会员id
	 */
	@TableField("SELLER_ID")
	private Long sellerId;
	
	/**
	 * 是否通知：0表示否，1表示已通知
	 */
	@TableField("IS_NOTICE")
	private Integer isNotice;

	public Long getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public Integer getIsNotice() {
		return isNotice;
	}

	public void setIsNotice(Integer isNotice) {
		this.isNotice = isNotice;
	}
	
	

}
