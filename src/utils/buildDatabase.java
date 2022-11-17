package utils;

import printServer.PasswordEncrypter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class buildDatabase {
    userDatabase ud = new userDatabase();

    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException, IOException {
        DBManagerProTwo dbhelper = new DBManagerProTwo();
//        dbhelper.insertRole("administrator","1,2,3,4,5,6,7,8,9");
//        dbhelper.insertRole("service","4,5,6");
//        dbhelper.insertRole("technician","7,8,9");
//        dbhelper.insertRole("pUser","1,2,3,6");
//        dbhelper.insertRole("user","1,2");
//
//        dbhelper.insertUser("Alice","000","administrator");
//        dbhelper.insertUser("Bob","001","service,technician");
//        dbhelper.insertUser("Cecilia","010","pUser");
//        dbhelper.insertUser("David","011","user");
//        dbhelper.insertUser("Erica","100",null);
//        dbhelper.insertUser("Fres","101","user");
//        dbhelper.insertUser("George","110",null);
//
//        ResultSet rs = dbhelper.searchPermission("Alice");
//        if (rs.next())
//            System.out.println(rs.getString("roleAccess"));

        //DBManagerProOne.initial();
        DBManagerProOne dbonehelper = new DBManagerProOne();
        System.out.println(dbonehelper.searchPermission("George","setConfig"));
    }
}
