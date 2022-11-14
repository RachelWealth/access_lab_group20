package printServer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordEncrypter
{
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        SecureRandom random = new SecureRandom();
        byte[] salt1 = new byte[16];
        random.nextBytes(salt1);
        byte[] salt = "helloworld".getBytes();
        md.update(salt);
        return md.digest(input.getBytes());
    }
//        Byte to Hex
    public static String toHexString(byte[] encHash) {
        BigInteger signum = new BigInteger(1, encHash);
        StringBuilder hexString = new StringBuilder(signum.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static String getEncryptedPassword(String password) throws NoSuchAlgorithmException {
        return toHexString(getSHA(password));
    }
}
