package cn.stylefeng.guns.core.util.response;
public class Kline {


    private long id;
    private float amount;
    private int count;
    private float open;
    private float close;
    private float low;
    private float high;
    private float vol;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public float getVol() {
		return vol;
	}
	public void setVol(float vol) {
		this.vol = vol;
	}
	@Override
	public String toString() {
		return "Kline [id=" + id + ", amount=" + amount + ", count=" + count + ", open=" + open + ", close=" + close
				+ ", low=" + low + ", high=" + high + ", vol=" + vol + "]";
	}

    
}
