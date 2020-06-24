package cn.stylefeng.guns.core.util;

public class ResponseData {
	
	public static final String DEFAULT_SUCCESS_MESSAGE = "请求成功";

    public static final String DEFAULT_ERROR_MESSAGE = "网络异常";

    public static final Integer DEFAULT_SUCCESS_CODE = 200;

    public static final Integer DEFAULT_ERROR_CODE = 500;

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应对象
     */
    private Object data;

    public ResponseData() {
    }

    public ResponseData(Boolean success, Integer code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseData success() {
        return new ResponseData(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static ResponseData success(Object object) {
        return new ResponseData(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, object);
    }

    public static ResponseData success(Integer code, String message, Object object) {
        return new ResponseData(true, code, message, object);
    }

    public static ResponseData error(String message) {
        return new ResponseData(false, DEFAULT_ERROR_CODE, message, null);
    }

    public static ResponseData error(Integer code, String message) {
    	  return new ResponseData(false, code, message, null);
    }

    public static ResponseData error(Integer code, String message, Object object) {
    	  return new ResponseData(false, code, message,  object);
    }

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

    
    
}
