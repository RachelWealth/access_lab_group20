package printServer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class PasswordEncrypter
{
    private static String my_salt;
    public static byte[] getSHA(String input, String salt0) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        if(salt0==null){
            random.nextBytes(salt);
            String str_salt = new String(Base64.getEncoder().encode(salt));

            //System.out.println(str_salt);
            salt = str_salt.getBytes();
            my_salt = new String(salt);
            //System.out.println(str_salt0);
        }else{
            salt = salt0.getBytes();
        }
        md.update(salt);
        return md.digest(input.getBytes());
    }
    public static String getSalt(){
        return my_salt;
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

    public static String getEncryptedPassword(String password,String salt) throws NoSuchAlgorithmException {
        return toHexString(getSHA(password,salt));
    }
}