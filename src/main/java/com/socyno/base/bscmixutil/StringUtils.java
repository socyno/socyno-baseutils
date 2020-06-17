package com.socyno.base.bscmixutil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.socyno.base.bscexec.MessageException;

import lombok.NonNull;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
    public final static int STR_TRIMED = 1;
    public final static int STR_LOWER = 2;
    public final static int STR_UPPER = 4;
    public final static int STR_NONBLANK = 8;
    public final static int STR_UNIQUE = 16;
    public final static int STR_PATHEND = 32;
    
    public static int stringBufferAppend(StringBuffer bf, byte[] bytes) {
        return stringBufferAppend(bf, bytes, null, 0, bytes.length);
    }
    
    public static int stringBufferAppend(StringBuffer bf, byte[] bytes, CharsetDecoder decoder) {
        return stringBufferAppend(bf, bytes, decoder, 0, bytes.length);
    }
    
    public static int stringBufferAppend(StringBuffer bf, byte[] bytes, int offset, int length) {
        return stringBufferAppend(bf, bytes, null, offset, length);
    }

    public static int stringBufferAppend(StringBuffer bf, byte[] bytes, CharsetDecoder decoder, int offset,
            int length) {
        int left = 0;
        if (decoder == null) {
            decoder = Charset.defaultCharset().newDecoder();
        }
        while (left < length) {
            try {
                bf.append(decoder.decode(ByteBuffer.wrap(bytes, offset, length - left)).toString());
                break;
            } catch (CharacterCodingException e) {
                left++;
                continue;
            }
        }
        return left;
    }
    
    /**
     * 仅当 buffer 内的没有数据时追加该数据，否则将被丢弃掉。
     */
    public static StringBuilder appendOnlyEmpty(@NonNull StringBuilder buffer, Object data) {
        return append(buffer, data, APPEND_ONLY_EMPTY);
    }
    
    /**
     * 仅当 buffer 内的有数据时追加该数据，否则将被丢弃掉。
     */
    public static StringBuilder appendIfNotEmpty(@NonNull StringBuilder buffer, Object data) {
        return append(buffer, data, APPEND_IFNOT_EMPTY);
    }
    
    /**
     * 无论 buffer 是否为空，均进行追加(buffer 的默认行为)
     */
    public static StringBuilder append(@NonNull StringBuilder buffer, Object data) {
        return append(buffer, data, APPEND_ALLWAYS);
    }
    
    /**
     * 仅当 buffer 内的有数据时在头部插入该数据，否则将被丢弃掉。
     */
    public static StringBuilder prependIfNotEmpty(@NonNull StringBuilder buffer, Object data) {
        return append(buffer, data, PREPEND_IFNOT_EMPTY);
    }
    
    private final static int APPEND_ALLWAYS = 0;
    
    private final static int APPEND_ONLY_EMPTY = 1;
    
    private final static int APPEND_IFNOT_EMPTY = 2;
    
    private final static int PREPEND_IFNOT_EMPTY = 4;
    
    private static StringBuilder append(@NonNull StringBuilder buffer, Object data, int appendPolicy) {
        
        if (data == null) {
            return buffer;
        }
        boolean prepend = false;
        int length = buffer.length();
        if ((appendPolicy & APPEND_ONLY_EMPTY) != 0 && length > 0) {
            return buffer;
        }
        
        if ((appendPolicy & APPEND_IFNOT_EMPTY) != 0 && length <= 0) {
            return buffer;
        }
        
        if ((prepend = (appendPolicy & PREPEND_IFNOT_EMPTY) != 0) && length <= 0) {
            return buffer;
        }
        
        Class<? extends Object> clazz = data.getClass();
        if (Boolean.class.equals(clazz)) {
            return prepend ? buffer.insert(0, ((Boolean) data).booleanValue())
                    : buffer.append(((Boolean) data).booleanValue());
        }
        if (Integer.class.equals(clazz)) {
            return prepend ? buffer.insert(0, ((Integer) data).intValue()) 
                    : buffer.append(((Integer) data).intValue());
        }
        if (Character.class.equals(clazz)) {
            return prepend ? buffer.insert(0, ((Character) data).charValue())
                    : buffer.append(((Character) data).charValue());
        }
        if (Double.class.equals(clazz)) {
            return prepend ? buffer.insert(0, ((Double) data).doubleValue())
                    : buffer.append(((Double) data).doubleValue());
        }
        if (Float.class.equals(clazz)) {
            return prepend ? buffer.insert(0, ((Float) data).floatValue()) 
                    : buffer.append(((Float) data).floatValue());
        }
        if (char[].class.equals(clazz)) {
            return prepend ? buffer.insert(0, (char[]) data) 
                    : buffer.append((char[]) data);
        }
        if (CharSequence.class.isAssignableFrom(clazz)) {
            return prepend ? buffer.insert(0, (CharSequence) data) 
                    : buffer.append((CharSequence) data);
        }
        return prepend ? buffer.insert(0, data) : buffer.append(data);
    }
    
    /**
     * 检查字串是否包含在自定的数组中，忽略大小写
     */
    public static boolean containsIgnoreCase(String[] names, String name) {
        if (names == null || names.length <= 0) {
            return false;
        }
        for (String n : names) {
            if (StringUtils.equalsIgnoreCase(n, name)) {
                return true;
            }
        }
        return false;
    }
    
    public static Date parseDate(String str) {
        return parseDate(str, null);
    }
    
    public static Date parseDate(String str, String format) {
        if (isBlank(str)) {
            return null;
        }
        if (isNotBlank(format)) {
            try {
                return new SimpleDateFormat(format).parse(str);
            } catch(Exception e) {}
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZ").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZZ").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.US).parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(str);
        } catch(Exception e) {}
        try {
            return new SimpleDateFormat("yyyy/MM/dd").parse(str);
        } catch(Exception e) {}
        throw new MessageException(String.format("Unknown date string : %s", str));
    }
    
    /**
     * 创建 Guid 唯一字串
     */
    public static String randomGuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 检查字符串是否为空。 是 ： 返回默认值； 否 ：返回当前值。
     */
    public static String ifBlank(String str, String ifBlank) {
        return StringUtils.isBlank(str) ? ifBlank : str;
    }
    
    /**
     * 检查字符串是否为空。 是 ： 返回 yesValue； 否 ：返回 noValue。
     */
    public static String ifBlank(String str, String yesValue, String noValue) {
        return StringUtils.isBlank(str) ? yesValue : noValue;
    }
    
    /**
     * 如果字串为 null， 返回空字串，否则返回字串本身 
     */
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * 尝试多种字符集对字节流进行字串转换，如果转换失败返回字串"<无法转换的数据流>"。 此函数仅用于日志打印时，业务逻辑请勿使用该函数。
     * 
     * @return
     */
    public static String bytesToDisplay(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException eu) {
            try {
                return new String(bytes, "GBK");
            } catch (UnsupportedEncodingException eg) {
                try {
                    return new String(bytes, "ISO-8859-1");
                } catch (UnsupportedEncodingException ei) {
                    return "<无法转换的数据流>";
                }
            }
        }
    }

    /**
     * 以逗号分隔连接重复的字符串
     */
    public static String join(CharSequence str, int length) {
        return join(str, length, ",", null, null);
    }

    /**
     * 以指定分隔符连接重复的字符串
     */
    public static String join(CharSequence str, int length,
            CharSequence seperator) {
        return join(str, length, seperator, null, null);
    }

    /**
     * 以指定分隔符连接重复的字符串，支持在连接后的字串前后添加额外的字符
     */
    public static String join(CharSequence str, int length,
            CharSequence seperator, CharSequence open, CharSequence close) {
        if (length <= 0) {
            return "";
        }
        StringBuffer joined = new StringBuffer();
        if (open != null) {
            joined.append(open);
        }
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                joined.append(seperator);
            }
            joined.append(str);
        }
        if (close != null) {
            joined.append(close);
        }
        return joined.toString();
    }

    /**
     * 字串分割：按照给定正则对字符串进行分割，可定义一些分割后的处理操作
     * 
     * @param str
     *            要分给的字串
     * @param regex
     *            分割正则表达式
     * @param flags
     *            分割后的处理操作， 可选常量定义如下：
     *                 STR_TRIMED 去掉前后空白；
     *                 STR_LOWER 转小写；
     *                 STR_UPPER 转大写；
     *                 STR_NONBLANK 忽略空白项
     *                 STR_UNIQUE 过滤重复项；
     *                 STR_PATHEND 确保已 / 结尾；
     * 
     * @return 如果 str 为 null 或 empty，返回空数组
     * */
    public static String[] split(String str, String regex, int flags) {
        str = nullToEmpty(str);
        if (str.isEmpty()) {
            return new String[0];
        }
        String[] values = regex == null ? new String[] { str } : str
                .split(regex);
        if (flags == 0) {
            return values;
        }
        List<String> processed = new ArrayList<String>();
        for (String v : values) {
            if ((flags & STR_TRIMED) != 0) {
                v = v.trim();
            }
            if ((flags & STR_LOWER) != 0) {
                v = v.toLowerCase();
            } else if ((flags & STR_UPPER) != 0) {
                v = v.toUpperCase();
            }
            if ((flags & STR_PATHEND) != 0) {
                if (!v.endsWith("/")) {
                    v = String.format("%s/", v);
                }
            }
            if ((flags & STR_NONBLANK) != 0 && StringUtils.isBlank(v)) {
                continue;
            }
            if ((flags & STR_UNIQUE) != 0 && processed.contains(v)) {
                continue;
            }
            processed.add(v);
        }
        return processed.toArray(new String[processed.size()]);
    }

    /**
     * 将 throwable 的 stacktrace 字串化
     */
    public static String stringifyStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 强制字符转转换(尝试使用多种字符集 UTF-8, GBK, ISO8859-1)
     * 
     * @param bytes
     * @param emptyIfFail
     *            如果字串无法转换，是返空字串还是null
     * @param nullToEmpty
     *            如果字节数据为 null， 是返回空字串还是null
     * @return 如果 bytes 为 null值，则返回 null； 如果在尝试以上编码后仍无法转换, 返回 null 或
     *         空(emptyIfFail=true)
     */
    public static String toString(byte[] bytes, boolean emptyIfFail,
            boolean nullToEmpty) {
        if (bytes == null) {
            return nullToEmpty ? "" : null;
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
        }
        try {
            return new String(bytes, "GBK");
        } catch (IOException e) {
        }
        try {
            return new String(bytes, "ISO8859-1");
        } catch (IOException e) {
        }
        return emptyIfFail ? "" : null;
    }

    /**
     * 以驼峰形式转换字串中的下划线
     */
    public static String applyFieldNamingPolicy(@NonNull String str) {
        StringBuffer dest = new StringBuffer();
        for (String sub : str.split("_")) {
            if (sub.length() == 0) {
                continue;
            }
            if (dest.length() == 0) {
                dest.append(sub);
                continue;
            }
            dest.append(Character.toUpperCase(sub.charAt(0))).append(
                    sub.substring(1));
        }
        return dest.toString();
    }
}
