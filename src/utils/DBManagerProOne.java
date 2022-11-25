package utils;

import printServer.PasswordEncrypter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

/**
 * This class implement access control list, and use public file to realize the storage of list
 * @author Yingli Duan
 * @version 1.1
 */
public class DBManagerProOne {
    static Dictionary<String, String> dict = new Hashtable<>();
    public DBManagerProOne(){
        dict.put("print","1");
        dict.put("queue","2");
        dict.put("topQueue","3");
        dict.put("start","4");
        dict.put("stop","5");
        dict.put("restart","6");
        dict.put("status","7");
        dict.put("readConfig","8");
        dict.put("setConfig","9");
    }
    private String filePath = "permission";

    private static Statement st;
    private static final Connection con = connect();
    private static Connection connect(){
        Connection con=null;
        try{
            Class.forName("org.sqlite.JDBC");
            String PATH_DB = "src\\db\\access.db";
            String url = "jdbc:sqlite:" + PATH_DB;
            con= DriverManager.getConnection(url);
            st=con.createStatement();
            System.out.println();
        }catch(Exception e){
            System.out.println("failed in connecting with database"+e.getMessage());;
        }
        return con;
    }

    public static void initial() throws IOException {
        try{
            FileWriter writer = new FileWriter("permission",false);
            writer.write("Alice:1,2,3,4,5,6,7,8,9\n");
            writer.write("Bob:4,5,6,7,8,9\n");
            writer.write("Cecilia:1,2,3,6\n");
            writer.write("David:1,2\n");
            writer.write("Erica:1,2\n");
            writer.write("Fres:1,2\n");
            writer.write("George:1,2\n");
            writer.flush();
            writer.close();
        }catch (Exception ignored){
            System.out.println("write failed");
        }
    }

    /**
     *
     * @param name: username
     * @param operator: function name
     * @return boolean
     * @throws IOException
     */
    public boolean searchPermission(String name, String operator) throws IOException {
        String ope = dict.get(operator);
        File file = new File("permission");
        BufferedReader reader = new BufferedReader(new FileReader("permission"));
        String str;
        while((str = reader.readLine())!=null){
            if(str.contains(name)){
                 String[] strs = str.split(":");
                 String[] opes = strs[1].split(",");
                 for(String op : opes){
                     if(Objects.equals(op, ope)){
                         return true;
                     }
                 }
            }
        }
        return false;
    }

    /**
     * insert a new user
     * @return
     */
    public boolean insertUser(String name,String pw, String permission) throws SQLException, NoSuchAlgorithmException {

        BufferedReader br = null;

        String line = null;

        StringBuilder bufAll = new StringBuilder();
        try{
            br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath)), "UTF-8"));
            while((line = br.readLine()) != null){
                StringBuffer buf = new StringBuffer();
                if(line.split(":")[0] == name){
                    System.out.println("user exist");
                    return false;
                }else{
                    buf.append(line);
                    buf.append(System.getProperty("line.separator"));
                    bufAll.append(buf);
                }
            }
            StringBuffer buf = new StringBuffer();
            line = name + ":" + permission + "\n";
            buf.append(line);
            bufAll.append(buf);

            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    br = null;
                }
            }
            writeFile(bufAll.toString());
        } catch (Exception e) {
            e.printStackTrace();
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ee) {
                    br = null;
                }
            }
            return false;
           // throw new RuntimeException(e);
        }
        String sql ="\0";
        sql="insert into userDB values('" +name +"'"+ ",'"+String.valueOf(PasswordEncrypter.getEncryptedPassword(pw,null))+"','" +PasswordEncrypter.getSalt() + "',"+String.valueOf(1)+");";
        st.executeUpdate(sql);

        return true;
    }


    public boolean deleteUser(String name) throws SQLException {


        BufferedReader br = null;

        String line = null;

        StringBuffer bufAll = new StringBuffer();
        try{
            br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath)), "UTF-8"));
            while((line = br.readLine()) != null){
                StringBuffer buf = new StringBuffer();
                if(Objects.equals(line.split(":")[0], name)){
                    continue;
                }else{
                    buf.append(line);
                    buf.append(System.getProperty("line.separator"));
                    bufAll.append(buf);
                }
            }


            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    br = null;
                }
            }
            writeFile(bufAll.toString());
        } catch (Exception e) {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ee) {
                    br = null;
                }
            }
            return false;
            // throw new RuntimeException(e);
        }
        String sql = "select * from dutyDB where userName = '"+name+"' ";
        ResultSet rs = st.executeQuery(sql);
        return true;


    }
    public boolean workerReplace(String oldName, String newName) throws SQLException {

        boolean flag = false;
        BufferedReader br = null;

        String line = null;

        StringBuffer bufAll = new StringBuffer();
        try{

            br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath)), "UTF-8"));
            while((line = br.readLine()) != null){
                StringBuffer buf = new StringBuffer();
                String[] a = line.split(":");
                if(Objects.equals(line.split(":")[0], oldName)){

                    line = newName+":" + line.split(":")[1];
                    flag = true;
                }
                    buf.append(line);
                    buf.append(System.getProperty("line.separator"));
                    bufAll.append(buf);

            }
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    br = null;
                }
            }
            writeFile(bufAll.toString());
        } catch (Exception e) {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ee) {
                    br = null;
                }
            }
            return false;
            // throw new RuntimeException(e);
        }
        String sql = "Update userDB set userName = '" + newName + "' where userName = '" + oldName + "'";
        st.executeUpdate(sql);
        return flag;
    }

    private boolean writeFile(String content) throws IOException {
        BufferedWriter bw = null;
        try{
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
            bw.write(content);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
        return true;
    }

}





