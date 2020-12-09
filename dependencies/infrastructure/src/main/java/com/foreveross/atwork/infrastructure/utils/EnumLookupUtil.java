package com.foreveross.atwork.infrastructure.utils;

import androidx.annotation.Nullable;

public class EnumLookupUtil {
    @Nullable
    public static <E extends Enum<E>> E lookup(Class<E> classE, String id) {
        E result = null;
        try {
            result = Enum.valueOf(classE, id);

        } catch (IllegalArgumentException e) {
            // log error or something here
            e.printStackTrace();
        }

        return result;
    }
}
