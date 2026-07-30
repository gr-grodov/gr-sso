package gr.grodov.grsso.service.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class IDGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    public static String randomID() {
        return randomID(16);
    }

    public static String randomID(int bytes) {
        byte[] buffer = new byte[bytes];
        RANDOM.nextBytes(buffer);
        return ENCODER.encodeToString(buffer);
    }

}
