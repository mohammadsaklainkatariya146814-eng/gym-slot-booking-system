package com.gymbooking.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashPassword(String plainText) {
        if (plainText == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(encoded.length * 2);
            for (byte b : encoded) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable.", e);
        }
    }

    public static boolean verifyPassword(String plainText, String storedHash) {
        if (plainText == null || storedHash == null) {
            return false;
        }
        return hashPassword(plainText).equalsIgnoreCase(storedHash);
    }
}
