package utils;
import printServer.PasswordEncrypter;

import java.security.NoSuchAlgorithmException;
import  java.sql.*;


public class userDatabase {
    private String db = "user";

    private Connection connect(){
        Connection con=null;
        try{
            Class.forName("org.sqlite.JDBC");
            String PATH_DB = "src\\db\\user.db";
            String url = "jdbc:sqlite:" + PATH_DB;
            con=DriverManager.getConnection(url);
            System.out.println();
        }catch(Exception e){
            System.out.println("数据库连接失败"+e.getMessage());;
        }
        return con;
    }

    private void __insert__(String name,String pw) throws SQLException, NoSuchAlgorithmException {
        Connection con = this.connect();
        Statement st=con.createStatement();
        String sql="insert into user values('" +name +"'"+ ",'"+String.valueOf(PasswordEncrypter.getEncryptedPassword(pw,null))+"');";

        st.executeUpdate(sql);
    }
    public void add() throws SQLException, NoSuchAlgorithmException {
        //this.__insert__("user1","000");
        this.__insert__("user2","111");
    }
    public synchronized ResultSet search(String name) throws SQLException, NoSuchAlgorithmException {
        Connection con = this.connect();
        String sql="select * from " + this.db+ " where user= '"+name+"'";

        Statement st=con.createStatement();
        return st.executeQuery(sql);
    }
}
