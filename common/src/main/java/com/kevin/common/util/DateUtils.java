package com.kevin.common.util;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;


public abstract class DateUtils extends PropertyEditorSupport {

    public static final org.slf4j.Logger log              = LoggerFactory.getLogger(DateUtils.class);

    public static final String           DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String           DATE_FORMAT      = "yyyy-MM-dd";
    public static final String           YYYYMMDD         = "yyyyMMdd";

    private String                       dateFormat       = DATE_FORMAT;

    public static Date getCurrentDate() {
        return DateTime.now().toDate();
    }

    // private static SimpleDateFormat sDateFormat = new
    // SimpleDateFormat("yyyy-MM-dd");

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        log.info("set Text");
        SimpleDateFormat frm = new SimpleDateFormat(dateFormat);
        try {
            Date date = frm.parse(text);
            this.setValue(date);
        } catch (Exception exp) {
            log.error("parse date error ", exp);
        }
    }

    public static Date parse(String text) throws ParseException {
        SimpleDateFormat frm = new SimpleDateFormat(DATE_FORMAT);
        return frm.parse(text);
    }

    public static String parse(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }

    public static String parse(Date date, String formatString) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    /**
     * 指定时间的凌晨
     *
     * @param date
     * @return
     */
    public static Date getDateZero(Date date) {
        date = org.apache.commons.lang.time.DateUtils.setHours(date, 0);
        date = org.apache.commons.lang.time.DateUtils.setMinutes(date, 0);
        date = org.apache.commons.lang.time.DateUtils.setSeconds(date, 0);
        date = org.apache.commons.lang.time.DateUtils.setMilliseconds(date, 0);
        return date;
    }

    public static Calendar getDateZeroCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    public static Date add(Date date, int zoom, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(zoom, amount);
        return cal.getTime();
    }

    public static Date getDate(String date) {
        return getDate(date, DATE_TIME_FORMAT);
    }

    public static Date getDate(String date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            log.error("parse date error", e);
        }

        return null;
    }

    /**
     * 计算两个日期相隔的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDayCount(Date date1, Date date2) {
        Calendar cal1 = getDateZeroCalendar(date1);
        Calendar cal2 = getDateZeroCalendar(date2);
        long between_days = Math.abs((cal1.getTime().getTime() - cal2.getTime().getTime()) / (1000 * 3600 * 24));

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static String getDate(Date date) {
        return getDate(date, DATE_TIME_FORMAT);
    }

    public static String getDate(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 星期三为3，星期天为7
     *
     * @param date
     * @return
     */
    public static int getDayNumber(Date date) {
        int week = getDay(date) - 1;
        return week == 0 ? 7 : week;
    }

    /**
     * 获取前几个月
     *
     * @param date
     * @return
     * @version 1.0
     * @author like.cui
     * @created 2013年10月16日
     */
    public static Date getLastMonthStartDay(Date date, int lastMonthCount) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            int month = calendar.get(Calendar.MONTH);
            calendar.set(Calendar.MONTH, month - lastMonthCount);
            return calendar.getTime();
        } else {
            return null;
        }
    }

    /**
     * 获取月份起始日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getDayStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static boolean isToday(Date date) {
        Date d = new Date();
        return DateUtils.getDate(date, YYYYMMDD).equals(DateUtils.getDate(d, YYYYMMDD));
        // return (d.getYear()==date.getYear() && d.getMonth()==date.getMonth()
        // && d.getDate()==d.getDate());
    }

    /**
     * 获取月份最后日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getMaxMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 按照月份划分一段时间。 输入: 2013-02-18 到 2013-04-15 结果： 2013-02-18 2013-02-28 -------------- 2013-03-01 2013-03-31
     * -------------- 2013-04-01 2013-04-15 --------------
     *
     * @param start 开始时间
     * @param end 结束时间。
     * @return
     */
    public static List<Long[]> splitTime(long start, long end) {
        Preconditions.checkArgument(start > 0 && end > start);
        start = getDate(parse(new Date(start)), DATE_FORMAT).getTime();
        end = getDate(parse(new Date(end)), DATE_FORMAT).getTime();

        List<Long[]> result = Lists.newArrayList();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            long endTime = DateUtils.getMaxMonthDate(new Date(start)).getTime();
            if (start > end) {
                break;
            }
            if (endTime > end) {
                Long[] timePair = new Long[2];
                timePair[0] = start;
                timePair[1] = end;
                result.add(timePair);
                break;
            } else {
                Long[] timePair = new Long[2];
                timePair[0] = start;
                timePair[1] = endTime;
                result.add(timePair);
                start = DateUtils.getLastMonthStartDay(new Date(start), -1).getTime();
            }

        }

        return result;
    }

    /**
     * 得到某一天上一周的星期天，中国每周是以星期一开始。
     *
     * @return
     */
    public static Date getPreWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 如果当前日期不是周日则自动往后递增日期
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_WEEK, -1);
        }
        return cal.getTime();
    }

    /**
     * 转化字符串
     *
     * @param currentDate
     * @param dateTimeFormat
     * @return
     */
    public static String toString(Date currentDate, String dateTimeFormat) {
        DateTime dateTime = new DateTime(currentDate);
        return dateTime.toString(dateTimeFormat);
    }
}
