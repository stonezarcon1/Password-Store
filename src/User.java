import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class User { //area where user info is stored
    private String username;
    private String password;
    private String salt;

    public User() {
    }

    //getters for user info
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getSalt() {
        return salt;
    }

    //setters for user info
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        try { //when password is set it automatically hashes the password and stores the salt
            String salt = newSalt();
            this.password = hashPass(salt, password);
            this.salt = salt;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public void setPlainPassword(String password) {
        this.password = password; //for storing a plaintext password. Use instead of setPassword()
    }

    private String newSalt() { //creating a custom user salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt); //generating a random salt that will be used to hash password
        String storeSalt = Base64.getEncoder().encodeToString(salt);
        return storeSalt;
    }
    public String hashPass(String salt, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] byteSalt = Base64.getDecoder().decode(salt); //re-encoding salt to byte
        KeySpec spec = new PBEKeySpec(password.toCharArray(), byteSalt, 65536, 128); //hash params
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); //hashing with SHA1
        byte[] hash = factory.generateSecret(spec).getEncoded(); //hashed password

        String hashedPass = Base64.getEncoder().encodeToString(hash); //encoding in storage friendly format
        return hashedPass;
    }
}