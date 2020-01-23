package main.java.utilities;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringHasher implements Serializable {
    private MessageDigest digest;

    public StringHasher(String algorithm){
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Ошибка: Алгоритм не найден!");
        }
    }

    public String getHash(String str){
        byte[] bytes = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append(String.format("%2x", aByte));
        }
        return builder.toString().replace(" ", "0");
    }
}

