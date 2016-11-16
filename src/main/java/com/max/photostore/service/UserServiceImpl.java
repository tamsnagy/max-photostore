package com.max.photostore.service;

import com.max.photostore.exception.InternalServerErrorException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    @Override
    public void signup(final String username, final String email, final String password) {

    }

    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    private byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) throws InternalServerErrorException {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalServerErrorException("");
        }
    }

    private boolean checkPassword(final char[] password, final byte[] salt, final char[] expectedHash) throws InternalServerErrorException {
        byte[] passwordHash = hashPassword(password, salt, ITERATIONS, KEY_LENGTH);
        if (passwordHash.length != expectedHash.length)
            return false;
        for (int i = 0; i < passwordHash.length; i++)
            if (passwordHash[i] != expectedHash[i])
                return false;
        return true;
    }
}
