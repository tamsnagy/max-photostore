package com.max.photostore.service;

import com.max.photostore.domain.AppUser;
import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.SignupException;
import com.max.photostore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void signup(final String username, final String email, final String password) throws SignupException, InternalServerErrorException {
        if (!validateUsername(username))
            throw new SignupException("Invalid username");
        if (!validateEmail(email))
            throw new SignupException("Invalid email");
        if (!validatePassword(password))
            throw new SignupException("Invalid password");

        if (userRepository.findOneByUsername(username) != null)
            throw new SignupException("Username already in use");
        if (userRepository.findOneByEmail(email) != null)
            throw new SignupException("Email already in use");

        byte[] salt = generateSalt();
        byte[] passwordHash;
        try {
            passwordHash = hashPassword(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new InternalServerErrorException("An error occured during signup");
        }

        AppUser newUser = new AppUser(username, email, passwordHash, salt);
        userRepository.save(newUser);
    }

    @Override
    public boolean login(String username, String password) throws InternalServerErrorException {
        if (username == null || password == null)
            return false;

        AppUser user = userRepository.findOneByUsername(username);
        if (user == null)
            return false;

        try {
            boolean success = checkPassword(password.toCharArray(), user.getSalt(), user.getPassword());
            if (!success)
                return false;

            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("USER")); // TODO bszalai
            UserDetails userDetails = new User(username, password, authorities);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);
            return true;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new InternalServerErrorException(e);
        }
    }

    private boolean validateUsername(final String username) {
        if (username == null)
            return false;
        for (char c : username.toCharArray())
            if (!Character.isLetterOrDigit(c))
                return false;
        int length = username.length();
        return !(length < MIN_USERNAME_LENGTH || length > MAX_USERNAME_LENGTH);
    }

    private boolean validateEmail(final String email) {
        if (email == null)
            return false;
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    private boolean validatePassword(final String password) {
        if (password == null)
            return false;
        for (char c : password.toCharArray())
            if (!Character.isLetterOrDigit(c))
                return false;
        int length = password.length();
        return length >= MIN_PASSWORD_LENGTH;
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    private byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKey key = skf.generateSecret(spec);
        return key.getEncoded();
    }

    private boolean checkPassword(final char[] password, final byte[] salt, final byte[] expectedHash) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] passwordHash = hashPassword(password, salt, ITERATIONS, KEY_LENGTH);
        if (passwordHash.length != expectedHash.length)
            return false;
        for (int i = 0; i < passwordHash.length; i++)
            if (passwordHash[i] != expectedHash[i])
                return false;
        return true;
    }
}
