package de.unknowncity.astralib.common.util;

import java.util.Arrays;

public class AstraArrays {

    public static <T> T[] merge(T[] a1, T[] a2) {
        var result = Arrays.copyOf(a1, a1.length + a2.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
    }
}
