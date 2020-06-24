package cn.stylefeng.guns.core.util.response;


import cn.stylefeng.guns.core.util.ApiException;

public class AccountsResponse<T> {

    /**
     * status : ok
     * data : [{"id":100009,"type":"spot","state":"working","user-id":1000}]
     */

    private String status;
    public String errCode;
    public String errMsg;
    private T data;

    public T checkAndReturn() {
        if ("ok".equals(status)) {
            return data;
        }
        throw new ApiException(errCode, errMsg);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
