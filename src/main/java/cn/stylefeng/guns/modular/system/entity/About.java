package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("about")
public class About implements Serializable {

	private static final long serialVersionUID = 2261801533461561400L;
	
	@TableId("ABOUT_ID")
	private Long aboutId;
	
	@TableField("CONTENT")
	private String content;

	public Long getAboutId() {
		return aboutId;
	}

	public void setAboutId(Long aboutId) {
		this.aboutId = aboutId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}  
	
	

}
