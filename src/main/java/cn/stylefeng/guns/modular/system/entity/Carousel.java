package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 	轮播图
 *
 */
@TableName("carousel")
public class Carousel implements Serializable {
	
	
	private static final long serialVersionUID = 2631868576726371912L;
	/**
	 * 主键
	 */
	@TableId(value="CAROUSEL_ID",type=IdType.AUTO)
	private Long carouselId;
	//图片名称
	@TableField("CAROUSEL_NAME")
	private String carouselName;
	
	//轮播图片
	@TableField("IMAGE")
	private String image;
	/**
	 * 外链
	 */
	@TableField("HREF")
	private String href;
	
	//排序
	@TableField("SORT")
	private Integer sort;
	
	//创建时间
	@TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date  createTime;
	
	/**
	 * 内容
	 */
	@TableField("CONTENT")
	private Object content;
	

	public Long getCarouselId() {
		return carouselId;
	}

	public void setCarouselId(Long carouselId) {
		this.carouselId = carouselId;
	}

	public String getCarouselName() {
		return carouselName;
	}

	public void setCarouselName(String carouselName) {
		this.carouselName = carouselName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getSort() {
		return sort;
	}

	
	
	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	

	
	
}
