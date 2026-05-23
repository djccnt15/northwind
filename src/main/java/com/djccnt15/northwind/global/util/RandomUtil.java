package com.djccnt15.northwind.global.util;

import lombok.experimental.UtilityClass;

import static java.util.UUID.randomUUID;

@UtilityClass
public class RandomUtil {
    
    public static String getRandUuidString() {
        return getRandUuidString(8);
    }
    
    public static String getRandUuidString(int length) {
        var uuidString = randomUUID().toString().replace("-", "");
        return uuidString.substring(0, Math.min(length, uuidString.length()));
    }
}
