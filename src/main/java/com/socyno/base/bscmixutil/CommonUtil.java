package com.socyno.base.bscmixutil;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import lombok.NonNull;
import org.apache.commons.lang3.time.DateFormatUtils;
import com.socyno.base.bscexec.MessageException;

public class CommonUtil {

    private static String[] regexpKeyworlds = { "\\", "$", "(", ")", "*", "+",
            ".", "[", "]", "?", "^", "{", "}", "|" };
    
    /**
     * 判断对象是否为 NULL，是则返回默认值，否则返回对象自身
     * */
    public static <T> T ifNull(T obj, T e) {
        return obj == null ? e : obj;
    }

    /**
     * 判断对象是否为 NULL，是则返回前值，否则返回后值
     * */
    public static <T> T ifNull(T obj, T yes, T no) {
        return obj == null ? yes : no;
    }

    /* 整数解析 */
    public static Integer parseInteger(Object intstr) {
        if (intstr == null) {
            return null;
        }
        if (intstr instanceof Boolean || boolean.class.equals(intstr.getClass())) {
            return ((boolean)intstr) ? 1 : 0;
        }
        try {
            Double dbl = Double.valueOf(intstr.toString());
            return dbl.intValue();
        } catch (NumberFormatException e) {

        }
        return null;
    }

    public static int parseInteger(Object str, int defaultValue) {
        Integer parsed;
        if ((parsed = parseInteger(str)) == null) {
            return defaultValue;
        }
        return parsed.intValue();
    }
    
    /**
     * 获取两者中较小的值
     **/
    public static int parseMinimalInteger(Object value, int minimal) {
        int parsed = parseInteger(value, minimal);
        return parsed > minimal ? minimal : parsed;
    }
    
    /**
     * 获取两者中较大的值
     **/
    public static int parseMaximalInteger(Object value, int maximal) {
        int parsed = parseInteger(value, maximal);
        return parsed < maximal ? maximal : parsed;
    }
    
    /**
     * 正整数解析(>0)，如不是正整数则返回默认值
     */
    public static int parsePositiveInteger(Object value, int defaultValue) {
        int parsed = parseInteger(value, 0);
        return parsed > 0 ? parsed : defaultValue;
    }
    
    /* 长整数解析 */
    public static Long parseLong(Object value) {
        return parseLong(value, null);
    }
    
    /* 长整数解析 */
    public static Long parseLong(Object value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean || boolean.class.equals(value.getClass())) {
            return ((boolean)value) ?1L : 0L;
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            Double parsed;
            if ((parsed = parseDouble(value)) != null) {
                defaultValue = parsed.longValue();
            }
        }
        return defaultValue;
    }

    /**
     * 数字转换， 返回 Double 类型
     */
    public static Double parseDouble(Object value, Double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Boolean || boolean.class.equals(value.getClass())) {
            return ((boolean)value) ? 1.0 : 0.0;
        }
        try {
            return Double.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 数字转换， 返回 Double 类型 如果无法转换， 返回 null值
     */
    public static Double parseDouble(Object value) {
        return parseDouble(value, null);
    }

    /* 长整数解析,获取两者中较小的值 */
    public static long parseMinimalLong(Object value, long minimal) {
        long parsed = parseLong(value, minimal);
        return parsed > minimal ? minimal : parsed;
    }

    /* 长整数解析,获取两者中较大的值 */
    public static long parseMaximalLong(Object value, long minimal) {
        long parsed = parseLong(value, minimal);
        return parsed < minimal ? minimal : parsed;
    }

    /* 长整数解析，如不是正整数（>0）则返回默认值 */
    public static long parsePositiveLong(Object value, long defaultValue) {
        long parsed = parseLong(value, 0L);
        return parsed > 0 ? parsed : defaultValue;
    }

    /**
     * 转换成数据库可识别的时间格式 即： yyy-MM-dd HH:mm:ss
     */
    public static String toSqlString(Date datetime) {
        if (datetime == null) {
            return null;
        }
        return DateFormatUtils.format(datetime, "yyyy-MM-dd HH:mm:ss");
    }
    
    /* 解析ISO08601 格式时间，否则返回默认值 */
    public static long timeMSFromISO8601(String dateTime, long defaultMS) {
        if (dateTime != null) {
            try {
                DateFormat df = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                return df.parse(
                        dateTime.replaceAll("\\+0([0-9]){1}\\:00", "+0$100"))
                        .getTime();
            } catch (ParseException e) {
            }
        }
        return defaultMS;
    }
    
    /**
     * 检查给定的字串是否为空白，如果是则抛出异常，否则返回首位去空白后的值
     * 
     * @param str
     *            要检查的字符串
     * @param errmsg
     *            空白时的异常信息
     * @return 移除首位空白后的字符串
     */
    public static String ensureNonBlank(String str, String errmsg)
            throws MessageException {
        if (StringUtils.isBlank(str)) {
            throw new MessageException(errmsg);
        }
        return str.trim();
    }
    
    /**
     * 检查给定的对象是否为Null值，如果是则抛出异常
     * 
     * @param object
     *            要检查的对象
     * @param errmsg
     *            空时的异常信息
     */
    public static void ensureNonNull(Object obj, String errmsg)
            throws MessageException {
        if (obj == null) {
            throw new MessageException(errmsg);
        }
        return;
    }
    
    /* 正则表达式逃逸 */
    public static String escapeRegexp(String str) {
        if (StringUtils.isNotBlank(str)) {
            for (String key : regexpKeyworlds) {
                if (str.contains(key)) {
                    str = str.replace(key, "\\" + key);
                }
            }
        }
        return str;
    }

    /* 数组去重 */
    public static <T> T[] uniqueArray(@NonNull T[] array) {
        return uniqueArray(array, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] uniqueArray(T[] array, boolean clearNull) {
        if (array == null || array.length < 2) {
            return array;
        }
        Collection<T> uniqued = uniqueCollection(Arrays.asList(array));
        return uniqued.toArray((T[]) Array.newInstance(array.getClass()
                .getComponentType(), uniqued.size()));
    }

    /* 集合去重 */
    public static <T> Collection<T> uniqueCollection(Collection<T> collection) {
        if (collection == null || collection.size() < 2) {
            return collection;
        }
        return new LinkedHashSet<T>(collection);
    }
    
    /*
     * 解析 boolean 值: null/[empty]/0/false/no/off => false, * => true
     */
    public static boolean parseBoolean(Object bool) {
        if (bool == null) {
            return false;
        }
        if (bool instanceof Boolean || boolean.class.equals(bool)) {
            return (boolean)bool;
        }
        String s = StringUtils.trimToEmpty(bool.toString()).toLowerCase();
        if (s.equals("") || s.equals("0") || s.equals("false")
                || s.equals("no") || s.equals("off")) {
            return false;
        }
        return true;
    }
    
    /*
     * 解析 boolean 值: null => null , [empty]/0/false/no/off => false, * => true
     */
    public static Boolean parseBooleanAllownNull(Object bool) {
        if (bool == null) {
            return null;
        }
        return parseBoolean(bool);
    }
    
    /**
     * 解析 Character : null/[empty] => null, * => toString().charAt(0)
     */
    public static Character parseCharacter(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Character || char.class.equals(val.getClass())) {
            return (char)val;
        }
        if (StringUtils.isEmpty((String)(val = val.toString()))) {
            return null;
        }
        return ((String)val).charAt(0);
    }

    /* 时间转换 */
    public static Date parseDate(Object date) {
        Long result;
        if ((result = parseDateMS(date)) == null) {
            return null;
        }
        return new Date(result);
    }

    public static Long parseDateMS(Object date) {
        if (date == null) {
            return null;
        }
        if (long.class.equals(date.getClass())) {
            return (long) date;
        } else if (date instanceof Long) {
            return (Long) date;
        } else if (date instanceof java.util.Date) {
            return ((java.util.Date) date).getTime();
        } else if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).getTime();
        } else if (date instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) date).getTime();
        }
        return parseLong(date.toString());
    }
}
