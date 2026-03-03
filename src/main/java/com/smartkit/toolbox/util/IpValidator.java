package com.smartkit.toolbox.util;

import java.util.regex.Pattern;

public final class IpValidator {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$"
    );

    private IpValidator() {
    }

    public static boolean isValid(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches();
    }
}