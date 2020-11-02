package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String SIMPLE_FORMAT = "yyyyMMdd";
    public static final String SIMPLE_FORMAT_LINE = "yyyy-MM-dd";
    public static final String DETAIL_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DETAIL_FORMAT_NO_UNIT = "yyyyMMddhhmmss";
    /***
     * 获取延时MINUTES分钟后的时间
     * @param minutes
     * @return
     */
    public static Date getDelayMinutes(int minutes){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    /***
     * 格式化日期
     * @param myDate
     * @param fromatString
     * @return
     */
    public static String formatDate(Date myDate, String fromatString){
        SimpleDateFormat myFormat = new SimpleDateFormat(fromatString);
        return myFormat .format(myDate);
    }

    /***
     * 生成详细日期
     * @return
     */
    public static String getDetailTime(){
        SimpleDateFormat myFormat = new SimpleDateFormat(DETAIL_FORMAT);
        return myFormat .format(new Date());
    }

    /***
     * 生成详细日期，不要单位。格式YYMMDDhhmmss
     * @return
     */
    public static String getDetailTimeIgnoreUnit(){
        SimpleDateFormat myFormat = new SimpleDateFormat(DETAIL_FORMAT_NO_UNIT);
        return myFormat.format(new Date());
    }

    public static String getSimpleDate(){
        SimpleDateFormat myFormat = new SimpleDateFormat(SIMPLE_FORMAT);
        return myFormat.format(new Date());
    }

    public static void main(String args[]){
        System.out.println(getDetailTimeIgnoreUnit());
    }
}
