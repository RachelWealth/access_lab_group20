package utils;

import printServer.PasswordEncrypter;

import java.security.NoSuchAlgorithmException;

public class buildDatabase {
    userDatabase ud = new userDatabase();
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(PasswordEncrypter.getEncryptedPassword("000",null));
        System.out.println(PasswordEncrypter.getEncryptedPassword("111",null));
    }

}
