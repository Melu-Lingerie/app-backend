package ru.mellingerie.media.util;

import lombok.experimental.UtilityClass;
import ru.mellingerie.media.dto.CustomMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class FileHashingUtil {

    private static final String HASH_ALGORITHM = "SHA-256";

    public String calculateSHA256(CustomMultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        try (InputStream is = new ByteArrayInputStream(file.content());
             DigestInputStream dis = new DigestInputStream(is, md)) {
            while (dis.read() != -1) ;
        }
        return bytesToHex(md.digest());
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
