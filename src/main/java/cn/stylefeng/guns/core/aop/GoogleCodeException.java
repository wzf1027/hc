package cn.stylefeng.guns.core.aop;

import org.apache.shiro.authc.AuthenticationException;

public class GoogleCodeException extends AuthenticationException {

	private static final long serialVersionUID = 4284332295208656710L;

	public GoogleCodeException(String message) {
        super(message);
    }

    public GoogleCodeException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
