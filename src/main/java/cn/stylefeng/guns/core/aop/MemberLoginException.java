package cn.stylefeng.guns.core.aop;


public class MemberLoginException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MemberLoginException(String message) {
        super(message);
    }

    public MemberLoginException(String message, Throwable cause) {
        super(message, cause);
    }
    
    

}
