package cn.stylefeng.guns.core.util;

import java.util.Date;

public class TimeUtil {

    /**判断是否超过多少小时 如：24
     *
     * @param tableTime 业务时间
     * @param hour 多少小时
     * @return boolean
     * @throws Exception
     */
    public static boolean judgmentDate(Date tableTime, Integer hour){
        if (tableTime==null){
            return true;
        }
        Date date = new Date();
        long cha = date.getTime()-tableTime.getTime();
        if (cha <=0){
            return false;
        }
        double result = cha * 1.0 / (1000 * 60 * 60);
        if (result <= hour) {
            return false;//是小于等于 hour 小时
        } else {
            return true;
        }
    }
}
