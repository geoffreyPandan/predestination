package com.kevin.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public abstract class ValidUtil {

    // 保存每个月的天数
    private static final int[]  DAYS_OF_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    // 日历的起始年份
    public static final int     START_YEAR    = 1900;
    // 日历的结束年份
    public static final int     END_YEAR      = 2100;
    private static final String ZERO_STRING   = "0";

    public static String toString(Object str) {
        if (str == null) {
            return "";
        } else {
            return str.toString();
        }
    }

    public static int toInt(Object obj, int defaultValue) {
        if (obj == null) {
            return defaultValue;
        } else {
            try {
                return Integer.valueOf(obj.toString());
            } catch (Exception exp) {
                return defaultValue;
            }
        }
    }

    /***************************************************************************
     * 匹配英文字母 或者汉字 如"Shenzhen" "深圳"
     *
     * @param str 待匹配字符串
     * @return true 匹配通过 false 匹配失败
     */
    public static boolean isEnglishOrChinese(String str) {
        // 1、[A-Za-z]* 英文字母的匹配 一次或者多次
        // 2、[\u4E00-\u9FA5]* 汉字匹配 一次或者多次
        boolean flag = false;
        if (str != null) {
            Pattern p = Pattern.compile("^[A-Za-z]*|[\u4E00-\u9FA5]*$");
            Matcher match = p.matcher(str);
            flag = match.matches();
        }
        return flag;
    }

    /***************************************************************************
     * 匹配英中文 字母 数字格式为：姓与名之间用/隔开 例如Green/Jim King
     *
     * @param str 待匹配字符串
     * @return true 匹配通过 false 匹配失败
     */
    public static boolean isNick(Object str, boolean allowNull) {
        // 1、[A-Za-z]* 英文字母的匹配 一次或者多次
        // 1、[0-6]* 英文字母的匹配 一次或者多次
        // 2、[\u4E00-\u9FA5]* 汉字匹配 一次或者多次
        if (str == null) {
            return allowNull;
        } else {
            Pattern p = Pattern.compile("^[A-Za-z \u4E00-\u9FA5]{2,20}$");
            Matcher match = p.matcher(str.toString());
            return match.matches();
        }
    }

    public static boolean isBase64(Object str, boolean allowNull) {
        if (str == null) {
            return allowNull;
        } else {
            if (str.toString().length() < 100) return false;
            return true;
        }
    }

    /***
     * 日期时间格式
     *
     * @param str 待匹配字符串
     * @param allowNull
     * @return
     */
    public static boolean isDateTime(String str, boolean allowNull) {
        if (str == null || str == "") return allowNull;
        Pattern p = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})*$");
        Matcher match = p.matcher(str);
        return match.matches();
    }

    /***
     * 匹配车牌号
     *
     * @param str 待匹配字符串
     * @param allowNull
     * @return
     */
    public static boolean isCarNo(String str, boolean allowNull) {
        if (str == null || str == "") return allowNull;
        Pattern p = Pattern.compile("^[\u4E00-\u9FA5][A-Za-z0-9]{6}$");
        Matcher match = p.matcher(str);
        return match.matches();
    }

    /***************************************************************************
     * 匹配英中文姓名 与英文名 英文名格式为：姓与名之间用/隔开 例如Green/Jim King
     *
     * @param str 待匹配字符串
     * @return true 匹配通过 false 匹配失败
     */
    public static boolean isValidName(String str) {
        // 1、[A-Za-z]* 英文字母的匹配 一次或者多次
        // 2、[\u4E00-\u9FA5]* 汉字匹配 一次或者多次
        boolean flag = false;
        if (str != null) {
            Pattern p = Pattern.compile("^([A-Za-z]+[\\/][A-Za-z]+)|[\u4E00-\u9FA5]*");
            Matcher match = p.matcher(str);
            flag = match.matches();
        }
        return flag;
    }

    public static boolean isValidIp(String str, boolean allowNull) {
        if (StringUtils.isBlank(str)) {
            return allowNull;
        } else {
            String city = str;
            Pattern p = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
            Matcher match = p.matcher(city);
            return match.matches();
        }
    }

    /***************************************************************************
     * 匹配英中文姓名 与英文名 英文名格式为：姓与名之间用空格隔开
     *
     * @param str 待匹配字符串
     * @return true 匹配通过 false 匹配失败
     */
    public static boolean isCityName(String str, boolean allowNull) {
        // 1、[A-Za-z]* 英文字母的匹配 一次或者多次
        // 2、[\u4E00-\u9FA5]* 汉字匹配 一次或者多次

        if (StringUtils.isBlank(str)) {
            return allowNull;
        } else {
            String city = str;
            Pattern p = Pattern.compile("^[A-Za-z \u4E00-\u9FA5]*$");
            Matcher match = p.matcher(city);
            return match.matches();
        }
    }

    public static boolean isMobile(Object str, boolean allowNull) {
        // 13xxx, 18xxx,15xxx,14xxxx,
        if (str == null) {
            return allowNull;
        } else {
            Pattern p = Pattern.compile("^(\\+86|)(|0)1\\d{10}$");
            Matcher match = p.matcher(str.toString());
            return match.matches();
        }
    }

    public static boolean isCoodinate(Object str, boolean allowNull) {
        if (str == null) {
            return allowNull;
        } else {
            Pattern p = Pattern.compile("^(\\-|)\\d{6,9}$");
            Matcher match = p.matcher(str.toString());
            return match.matches();
        }
    }

    /***************************************************************************
     * 正整数验证
     *
     * @param str 待验证字符串
     * @return true 验证通过 false 验证失败
     */
    public static boolean isInteger(Object str, boolean allowNull) {
        if (str == null) {
            return allowNull;
        } else {
            String inte = str.toString();
            Pattern p = Pattern.compile("^(\\-|)\\d+$");
            Matcher match = p.matcher(inte);
            return match.matches();
        }
    }

    /***************************************************************************
     * QQ号码验证
     *
     * @param str 待验证字符串
     * @return true 验证通过 false 验证失败
     */
    public static boolean isQQ(String str) {
        boolean flag = false;
        if (str != null) {
            Pattern p = Pattern.compile("^[1-9]\\d{4,}$");
            Matcher match = p.matcher(str);
            flag = match.matches();
        }
        return flag;
    }

    /***************************************************************************
     * 整数验证(包括正整数与 负整数)
     *
     * @param str 待验证字符串
     * @return true 验证通过 false 验证失败
     */
    public static boolean isVerify(String str) {
        boolean flag = false;
        if (str != null) {
            Pattern p = Pattern.compile("^-?\\d*$");
            Matcher match = p.matcher(str);
            flag = match.matches();
        }
        return flag;
    }

    /**
     * 验证非负整数(正整数+0)
     *
     * @param str 待验证字符串
     * @return true 验证通过 false 验证失败
     */
    public static boolean isNonNegative(String str) {
        boolean flag = false;
        if (str != null) {
            Pattern p = Pattern.compile("^\\d+$");
            Matcher match = p.matcher(str);
            flag = match.matches();
        }
        return flag;
    }

    /**
     * 验证非负整数(正整数+0)
     *
     * @param str 待验证字符串
     * @return true 验证通过 false 验证失败
     */
    public static boolean isValidPositiveInteger(String str) {
        boolean flag = false;
        if (str != null) {
            Pattern p = Pattern.compile("^\\d+$");
            Matcher match = p.matcher(str);
            flag = match.matches();
            if (ZERO_STRING.equals(str)) {
                flag = false;
            }
        }
        return flag;
    }

    /***************************************************************************
     * 匹配英文字母(汉语拼音)
     *
     * @param str 待匹配字符串
     * @return true 匹配通过 false 匹配失败
     */
    public static boolean isValidEnglish(String str) {
        boolean flag = false;
        if (str != null) {
            Pattern p = Pattern.compile("^[A-Za-z]*$");
            Matcher match = p.matcher(str);
            flag = match.matches();
        }
        return flag;
    }

    /**
     * 检查Id
     *
     * @param value
     * @return
     */
    public static boolean isId(Object value, boolean allownull) {
        // boolean flag = false;
        if (value != null) {
            final String keys = "^[A-Za-z0-9]{8}$";
            Pattern p = Pattern.compile(keys);
            Matcher match = p.matcher(value.toString());
            return match.matches();
        } else return allownull;
    }

    /**
     * 检查身份证
     *
     * @param value
     * @return
     */
    public static boolean isIdentity(Object value, boolean allownull) {
        // boolean flag = false;
        if (value != null) {
            final String keys = "^(([0-9]{17}[0-9A-Za-z])|[0-9]{15})$";
            Pattern p = Pattern.compile(keys);
            Matcher match = p.matcher(value.toString());
            return match.matches();
        } else return allownull;
    }

    /**
     * 检查密码
     *
     * @param value
     * @param allowNull
     * @return
     */
    public static boolean isPassword(Object value, boolean allowNull) {
        if (value != null) {
            Pattern p = Pattern.compile("^[A-Za-z0-9\\@\\!\\~\\#\\%\\&\\*\\?\\,]{6,12}?$");
            Matcher match = p.matcher(value.toString());
            return match.matches();
        } else return allowNull;
    }

    /***************************************************************************
     * 匹配英文字母 或者汉字，数字 过滤特殊字符
     *
     * @param str 待匹配字符串
     * @return true 匹配通过 false 匹配失败
     */
    public static boolean isValidNonSpecialChar(String str) {
        boolean flag = false;
        if (str != null) {
            Pattern p = Pattern.compile("^[A-Za-z\u4E00-\u9FA5\\d]*$");
            Matcher match = p.matcher(str);
            flag = match.matches();
        }
        return flag;
    }

    /**
     * 验证HH时间格式的时间范围是否大于等于三小时 **注意此方法必须在isValidHour格式验证通过后调用
     *
     * @param startHour 开始时间 HH
     * @param endHour 结束时间HH
     * @return true 通过 false 不通过
     */
    public static boolean isHourZone(String startHour, String endHour) {
        boolean flag = false;
        if (startHour != null && endHour != null) {
            if (isHour(startHour) && isHour(endHour)) { // 格式验证，避免可能抛类型转换异常
                int sHour = Integer.parseInt(startHour);
                int eHour = Integer.parseInt(endHour);
                flag = (eHour - sHour >= 3);
            }
        }
        return flag;
    }

    /***************************************************************************
     * 验证电话号码 后可接分机号 区号3位或者4位 电话7位或者8位后 后面可加3位或者4位分机号
     *
     * @param telephoeNo 电话号码字符串
     * @return
     */
    public static boolean isValidTelephoeNo(String telephoeNo) {
        // 1、\\d{3,4} 区号 3位或者4位的匹配
        // 2、\\d{7,8} 号码 7味或者8位的匹配
        // 3、(\\d{3,4})? 分机号3位或者4位的匹配 ？可匹配一次或者两次
        boolean flag = false;
        if (telephoeNo != null) {
            Pattern p = Pattern.compile("^\\d{3,4}\\d{7,8}(\\d{3,4})?$");
            Matcher match = p.matcher(telephoeNo);
            flag = match.matches();
        }
        return flag;
    }

    /***************************************************************************
     * 验证是否是正确的邮箱格式
     *
     * @param email
     * @return true表示是正确的邮箱格式,false表示不是正确邮箱格式
     */
    public static boolean isEmail(String email) {
        // 1、\\w+表示@之前至少要输入一个匹配字母或数字或下划线 \\w 单词字符：[a-zA-Z_0-9]
        // 2、(\\w+\\.)表示域名. 如新浪邮箱域名是sina.com.cn
        // {1,3}表示可以出现一次或两次或者三次.
        boolean flag = false;
        if (email != null) {
            String reg = "\\w+[\\w\\.]+@(\\w+\\.){1,4}\\w+";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(email);
            flag = matcher.matches();
        }
        return flag;
    }

    public static boolean isUrl(String url) {
        // 1、\\w+表示@之前至少要输入一个匹配字母或数字或下划线 \\w 单词字符：[a-zA-Z_0-9]
        // 2、(\\w+\\.)表示域名. 如新浪邮箱域名是sina.com.cn
        // {1,3}表示可以出现一次或两次或者三次.
        boolean flag = false;
//        if (url != null) {
//            String reg = "^(http://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
//            Pattern pattern = Pattern.compile(reg);
//            Matcher matcher = pattern.matcher(url);
//            flag = matcher.matches();
//        }
        return flag;
    }

    /***************************************************************************
     * 验证整点时间格式是否正确 HH格式 时间范围00时~23时
     *
     * @param hour 时间格式字符串
     * @return true表示是正确的整点时间格式,false表示不是正确整点时间格式
     */
    public static boolean isHour(String hour) {
        boolean flag = false;
        if (hour != null) {
            Matcher matcher = Pattern.compile("^[0-2][0-9]$").matcher(hour);
            flag = matcher.matches();
            int firstNum = Integer.parseInt(hour.substring(0, 1));
            if (flag && firstNum == 2) {
                int secondNum = Integer.parseInt(hour.substring(1, 2));
                flag = secondNum < 4; // 时间小于24时
            }
        }
        return flag;
    }

    /***************************************************************************
     * 匹配日期格式 yyMMdd 并验证日期是否合法 此方法忽略了闰年的验证 用于15位身份证出生日期的验证
     *
     * @param dayStr 日期字符串
     * @return true 日期合法 false 日期非法
     */
    public static boolean isValidDay(String dayStr) {
        if (dayStr != null) {
            Matcher match = Pattern.compile("^\\d{2}\\d{2}\\d{2}$").matcher(dayStr);
            if (match.matches()) // 格式验证通过 yyMMdd
            {
                int month = Integer.parseInt(dayStr.substring(2, 4)); // 月
                int day = Integer.parseInt(dayStr.substring(4, 6)); // 日
                if (!isValidMonth(month)) {
                    return false; // 月份不合法
                }
                if (!(day >= 1 && day <= DAYS_OF_MONTH[month - 1])) {
                    return false; // 日期不合法
                }
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /***************************************************************************
     * 匹配日期格式 yyyyMMdd 并验证日期是否合法
     *
     * @param value 日期字符串
     * @allowNull 是否允许空
     * @return true 日期合法 false 日期非法
     */
    public static boolean isDate(Object value, boolean allowNull) {
        if (value == null) {
            return allowNull;
        } else {
            String date = value.toString();
            // 1、 \\d{4} 年，\\d{2}月，\\d{2}日匹配
            Matcher match = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$").matcher(date);
            if (match.matches()) { // 格式验证通过 yyyyMMdd
                int year = Integer.parseInt(date.substring(0, 4)); // 年
                int month = Integer.parseInt(date.substring(5, 7)); // 月
                int day = Integer.parseInt(date.substring(8, 10)); // 日
                if (!isValidYear((year))) {
                    return false; // 年份不在有效年份中
                }
                if (!isValidMonth(month)) {
                    return false; // 月份不合法
                }
                if (!isValidDay(year, month, day)) {
                    return false; // 日期不合法
                }
                return true;
            }
            return false;
        }
    }

    /**
     * 检查year是否在有效的年份范围内 此处验证大于1900年 小于2101年
     *
     * @param year
     * @return
     */
    public static boolean isValidYear(int year) {
        return year >= START_YEAR && year <= END_YEAR;
    }

    /**
     * 验证月份是否在有效月份内
     *
     * @param month
     * @return
     */
    public static boolean isValidMonth(int month) {
        return month >= 1 && month <= 12;
    }

    /**
     * 检查天数是否在有效的范围内，因为天数会根据年份和月份的不同而不同 所以必须依赖年份和月份进行检查
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static boolean isValidDay(int year, int month, int day) {
        if (month == 2 && isLeapYear(year)) {// 闰年且是2月份
            return day >= 1 && day <= 29;
        }
        return day >= 1 && day <= DAYS_OF_MONTH[month - 1];// 其他月份
    }

    /**
     * 验证是否是闰年
     *
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * 验证用户名注册是否合法-----------由数字、26个英文字母或者下划线组成的字符串
     *
     * @param userName
     * @return
     */
    public static boolean isRegUserName(String userName) {
        boolean flag = true;
        if (userName != null) {
            Matcher match = Pattern.compile("^\\w+$").matcher(userName);
            flag = match.matches();
        }
        return flag;
    }

    public static boolean isDigit(String value) {
        boolean flag = false;
        if (StringUtils.isNotBlank(value)) {
            Matcher match = Pattern.compile("^\\d+\\.\\d+$").matcher(value);
            flag = match.matches();
        }
        return flag;
    }

    public static void main(String[] args) {
        System.out.println(isDigit("9"));
        System.out.println(isDigit("9.3"));
        System.out.println(isDigit("9.999"));
        System.out.println(isDigit(".9"));
    }

}
