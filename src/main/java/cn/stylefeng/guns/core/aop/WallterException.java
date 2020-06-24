package cn.stylefeng.guns.core.aop;

public class WallterException extends RuntimeException{

	private static final long serialVersionUID = 4284332295208656710L;

	public WallterException(String message) {
        super(message);
    }

    public WallterException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
