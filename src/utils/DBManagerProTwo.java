/***
 * This class implement RBAC
 * @author Yingli Duan
 * @version 1.1
 */
package utils;

import printServer.PasswordEncrypter;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Objects;


public class DBManagerProTwo {
    private static final Connection con = connect();
    private static Statement st;



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

    /**
     * add or delete a role
     * @param operation: "add","del","update"
     * @param access: like"1,2,3" or null
     */
    public void roleChange(String operation,String roleName, String access) throws SQLException {
        if (Objects.equals(operation, "add")){
            this.insertRole(roleName,access);
        }else if (Objects.equals(operation, "del")){
            String sql="delete from roleDB where roleName= '"+roleName+"'";
            st.executeUpdate(sql);
        }else{
            String sql="update roleDB set roleAccess = '"+access+"' where roleName = '"+roleName+"'";
            st.executeUpdate(sql);
        }
    }


    /**
     * New employee replace an old employee
     * @param newName can be 'null'
     */
    public void workerReplace( String oldName, String newName) throws SQLException {

        String sql = "Update userDB set userName = '" + newName + "' where userName = '" + oldName + "'";
        st.executeUpdate(sql);

        sql = "Update dutyDB set userName = '" +newName + "' where userName = '" + oldName + "'";
        st.executeUpdate(sql);
    }

    /**
     *
     * @param name: user name
     * @param roles: list type
     */
    public void updateWorkerRole(String name, String roles) throws SQLException {
        String sql = "update dutyDB  set role = '" + roles + "' where userName = '" + name + "' ";
        st.executeUpdate(sql);
    }


    public void insertRole(String roleName,String roleAccess) throws SQLException {
        String  sql="insert into roleDB values('" + roleName +"'"+ ",'"+roleAccess+"');";
        st.executeUpdate(sql);
    }

    public void insertUser(String name,String pw, String role) throws SQLException, NoSuchAlgorithmException {
        String sql ="\0";
        sql="insert into userDB values('" +name +"'"+ ",'"+String.valueOf(PasswordEncrypter.getEncryptedPassword(pw,null))+"','" +PasswordEncrypter.getSalt() + "',"+String.valueOf(1)+");";
        st.executeUpdate(sql);
        if(role == null){
            sql = "insert into dutyDB values('" + name + "','user')";
        }else{
            sql = "insert into dutyDB values('" + name + "','" + role+"')";
        }
        st.executeUpdate(sql);
    }

    /**
     * delete a user
     * @param name
     * @throws SQLException
     */
        public void deleteUser(String name) throws SQLException {
        String sql = "select * from dutyDB where userName = '"+name+"' ";
        ResultSet rs = st.executeQuery(sql);
        if(rs.next()){
            if(rs.getString("role")=="administrator"){

                    System.out.println("You can not delete administrator");
                    System.out.println("As I do not have time to implement this function");
                }else{
                    sql = "delete from userDB where username = '" + name+"'";
                    st.executeUpdate(sql);
                sql = "delete from dutyDB where username = '" + name+"'";
                st.executeUpdate(sql);
                }
            }else{
            System.out.println("No user");
        }
        }

    /**
     * search the access of a user
     * @param userName
     * @return boolean
     * @throws SQLException
     */
    public ResultSet searchPermission(String userName) throws SQLException {
        String sql = "select roleDB.roleAccess from userDB,roleDB,dutyDB where userDB.username = '"+userName + "' and dutyDB.userName = userDB.userName and dutyDB.role = roleDB.roleName";
        return st.executeQuery(sql);
    }
    public synchronized ResultSet search(String name) throws SQLException, NoSuchAlgorithmException {
        String sql="select * from " + "userDB"+ " where userName= '"+name+"'";

        Statement st=con.createStatement();
        return st.executeQuery(sql);
    }

}
