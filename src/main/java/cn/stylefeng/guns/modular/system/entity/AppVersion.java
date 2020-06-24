package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * app版本号
 * @author zf
 *
 */
@TableName("app_version")
public class AppVersion implements Serializable{

	private static final long serialVersionUID = -1441899780678390689L;

	/**
     * 主键id
     */
    @TableId(value = "VERSION_ID", type = IdType.AUTO)
	private Long versionId;
	
    /**
     * 版本号
     */
    @TableField("VERSION")
	private String version;
	
    /**
     * 更新内容
     */
    @TableField("CONTENT")
	private String content;
	/**
	 * 下载地址
	 */
    @TableField("ADDRESS")
	private String address;
    
    /**
     * 类型：1表示安卓，2表示苹果
     */
    @TableField("TYPE")
    private Integer type;
    
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;
    
    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private Date updateTime;

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
    
    

}
