package bj.restaurant.couts.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32;
    private static final int MIN_ITERATIONS = 100_000;
    private static final int MAX_ITERATIONS = 1_000_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hasher(char[] motDePasse) throws GeneralSecurityException {
        byte[] sel = new byte[SALT_BYTES];
        RANDOM.nextBytes(sel);
        byte[] hash = derive(motDePasse, sel, ITERATIONS);
        return "pbkdf2-sha256$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(sel) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifier(char[] motDePasse, String hashEncode) throws GeneralSecurityException {
        if (motDePasse == null || hashEncode == null) return false;
        String[] parties = hashEncode.split("\\$", -1);
        if (parties.length != 4 || !"pbkdf2-sha256".equals(parties[0])) return false;

        try {
            int iterations = Integer.parseInt(parties[1]);
            if (iterations < MIN_ITERATIONS || iterations > MAX_ITERATIONS) return false;
            byte[] sel = Base64.getDecoder().decode(parties[2]);
            byte[] hashAttendu = Base64.getDecoder().decode(parties[3]);
            if (sel.length < SALT_BYTES || hashAttendu.length != HASH_BYTES) return false;
            byte[] hashCalcule = derive(motDePasse, sel, iterations);
            return MessageDigest.isEqual(hashAttendu, hashCalcule);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static byte[] derive(char[] motDePasse, byte[] sel, int iterations) throws GeneralSecurityException {
        PBEKeySpec specification = new PBEKeySpec(motDePasse, sel, iterations, HASH_BYTES * Byte.SIZE);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(specification).getEncoded();
        } finally {
            specification.clearPassword();
        }
    }
}