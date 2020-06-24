package cn.stylefeng.guns.core.util.response;

public class TradeResponse {


    /**
     * status : ok
     * ch : market.btcusdt.trade.detail
     * ts : 1489473346905
     * tick : {"id":600848670,"ts":1489464451000,"data":[{"id":600848670,"price":7962.62,"amount":0.0122,"direction":"buy","ts":1489464451000}]}
     */

    private String status;
    private String ch;
    private long ts;
    public String errCode;
    public String errMsg;
    @SuppressWarnings("rawtypes")
	private Trade tick;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
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

    @SuppressWarnings("rawtypes")
	public Trade getTick() {
        return tick;
    }

    @SuppressWarnings("rawtypes")
	public void setTick(Trade tick) {
        this.tick = tick;
    }
}
