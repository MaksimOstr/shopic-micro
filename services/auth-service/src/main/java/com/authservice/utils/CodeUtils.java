package com.authservice.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public final class CodeUtils {
    private final static byte CODE_LENGTH = 8;
    private final static String chars = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private final SecureRandom random = new SecureRandom();

    public static String generateAlphanumericCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }

        return code.toString();
    }
}
