package printServer;
import utils.DBManagerProTwo;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class redeploy_RBAC {
    DBManagerProTwo dbhelper = new DBManagerProTwo();
    private static Scanner scanner = new Scanner(System.in);
public String inputRole(){
    System.out.println("Please choose its role");
    System.out.println("administrator");
    System.out.println("service");
    System.out.println("technician");
    System.out.println("pUser");
    System.out.println("user");
    String role = scanner.nextLine();
    return role;
}
public String inputName(){

    System.out.println("Please input the name employee:");
    String name = scanner.nextLine();
    return name;
}
    public void deploy() throws SQLException, NoSuchAlgorithmException {

        System.out.println("Please choose your operation:(use 1,2,3...)");
        System.out.println("add");
        System.out.println("delete");
        System.out.println("replace");
        System.out.println("update user role");
        System.out.println("update role permission");
        String ope = scanner.nextLine();

        String name = null;

        switch (ope){
            case "add":
                name=this.inputName();
                String role = this.inputRole();
                System.out.println("password");
                String pw = scanner.nextLine();
                dbhelper.insertUser(name,pw,role);
                break;
            case "delete":
                name = this.inputName();
                dbhelper.deleteUser(name);
                break;
            case "replace":
                System.out.println("Please input the old worker name");
                String oldname = scanner.nextLine();
                name = this.inputName();
                dbhelper.workerReplace(oldname,name);
                break;
            case "update user role":
                System.out.println("Please input the worker name");
                name = scanner.nextLine();
                System.out.println("Please input the new role(s)(like pUser,user)");
                System.out.println("administrator");
                System.out.println("service");
                System.out.println("technician");
                System.out.println("pUser");
                System.out.println("user");
                String roles = scanner.nextLine();
                //TODO check roles
                dbhelper.updateWorkerRole(name, roles);
                break;
            case"update role permission":
                String role0 = null;
                System.out.println("Please input your operation:(add,del,update)");
                String oper = scanner.nextLine();
                String ac = null;
                if(Objects.equals(oper, "add")){
                    System.out.println("Please input the new role");
                    role0 = scanner.nextLine();
                }
                else{
                    System.out.println("Please input the role you need to update");
                    role0 = scanner.nextLine();
                }
                if(!Objects.equals(oper, "del")){
                    System.out.println("Please input the new access of the role");
                    ac = scanner.nextLine();
                }
                dbhelper.roleChange(oper,role0,ac);
                    break;
                default:
                System.out.println("Wrong operation");
                break;
        }

    }
    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException, IOException {
        redeploy_RBAC rd = new redeploy_RBAC();
        String flag = "1";
        while(flag.equals("1")){
            rd.deploy();
            System.out.println("Please input what you want to do(input 1,2):");
            System.out.println("1. continue");
            System.out.println("2. exit");
             flag= scanner.nextLine();
            if((!Objects.equals(flag, "1")) || (!flag.equals("2"))) {
                System.out.println("Please input 1 or 2");
            }
            else{
                flag ="1";
            }
        }
    }

}
