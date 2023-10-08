package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User createUser(User user) {

        String password = user.getPassword();
        String hashedPassword = getHashFromPassword(password);

        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User updateUser(User modifiedUser, User userToUpd) {
        userToUpd.setFirstName(modifiedUser.getFirstName());
        userToUpd.setLastName(modifiedUser.getLastName());
        userToUpd.setEmail(modifiedUser.getEmail());

        String password = getHashFromPassword(modifiedUser.getPassword());
        userToUpd.setPassword(password);

        User updatedUser = userRepository.save(userToUpd);

        return updatedUser;


    }

    private static String getHashFromPassword(String password) {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hash = new byte[1];

        try {
            hash = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        Base64.Encoder enc = Base64.getEncoder();
        String hashedPassword = salt + ":" + enc.encodeToString(hash);

        return hashedPassword;

    }
}
