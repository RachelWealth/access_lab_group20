package printServer;
import utils.DBManagerProTwo;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;

public class redeploy_RBAC {
    DBManagerProTwo dbhelper = new DBManagerProTwo();
    private static Scanner scanner = new Scanner(System.in);
public String inputRole(){
    System.out.println("Please choose its role:(use 1,2,3...)");
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
            default:
                System.out.println("Wrong operation");
                break;
        }

    }
    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException, IOException {
        redeploy_RBAC rd = new redeploy_RBAC();
        int flag = 1;
        while(flag==1){
            rd.deploy();
            System.out.println("Please input what you want to do(input 1,2):");
            System.out.println("1. continue");
            System.out.println("2. exit");
            String flag0 = scanner.nextLine();
            if((flag0!="1") || (flag0 !="2")) {
                System.out.println("Please input 1 or 2");
            }
            else{
                flag = Integer.parseInt(flag0);
            }
        }
    }

}
