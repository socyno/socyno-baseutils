package com.socyno.base.bscmixutil;

import java.util.HashMap;
import java.util.Map;

public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {
    
    public static boolean containsAll(long[] array, long... subarr) {
        return containsAll((Object[])toObject(array), (Object[])toObject(subarr));
    }
    
    public static boolean containsAll(int[] array, int... subarr) {
        return containsAll((Object[])toObject(array), (Object[])toObject(subarr));
    }
    
    public static boolean containsAll(Object[] array, Object... subarr) {
        if (array == null || array.length <= 0 || subarr == null || subarr.length <= 0) {
            return false;
        }
        Map<Object, Integer> arrmap = new HashMap<>();
        for (Object a : array) {
            arrmap.put(a, 1);
        }
        for (Object s : subarr) {
            if (!arrmap.containsKey(s)) {
                return false;
            }
        }
        return true;
    }
}
